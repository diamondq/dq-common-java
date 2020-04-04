package com.diamondq.common.vertx.processor.generators;

import com.diamondq.common.UtilMessages;
import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextExtendedCompletableFuture;
import com.diamondq.common.context.ContextExtendedCompletionStage;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.converters.ConverterManager;
import com.diamondq.common.errors.ExtendedIllegalStateException;
import com.diamondq.common.errors.Verify;
import com.diamondq.common.lambda.future.ExtendedCompletionStage;
import com.diamondq.common.lambda.future.FutureUtils;
import com.diamondq.common.lambda.interfaces.Function2;
import com.diamondq.common.lambda.interfaces.Function3;
import com.diamondq.common.security.acl.api.SecurityContext;
import com.diamondq.common.security.acl.api.SecurityContextManager;
import com.diamondq.common.vertx.VertxMessages;
import com.diamondq.common.vertx.VertxUtils;
import com.diamondq.common.vertx.annotations.ProxyGen;
import com.diamondq.common.vertx.processor.Generator;
import com.diamondq.common.vertx.processor.Messages;
import com.diamondq.common.vertx.processor.model.BaseParam;
import com.diamondq.common.vertx.processor.model.BaseType;
import com.diamondq.common.vertx.processor.model.ElementIllegalArgumentException;
import com.diamondq.common.vertx.processor.model.ImplClass;
import com.diamondq.common.vertx.processor.model.ProxyClass;
import com.diamondq.common.vertx.processor.model.ProxyMethod;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;

public class ImplGenerator implements Generator {

  @SuppressWarnings("unused")
  private static final Logger sLogger = LoggerFactory.getLogger(ImplGenerator.class);

  public ImplGenerator() {
  }

  /**
   * @see com.diamondq.common.vertx.processor.Generator#getAnnotation()
   */
  @Override
  public Class<? extends Annotation> getAnnotation() {
    return ProxyGen.class;
  }

  /**
   * @throws IOException
   * @see com.diamondq.common.vertx.processor.Generator#process(javax.lang.model.element.TypeElement,
   *      javax.annotation.processing.ProcessingEnvironment, javax.annotation.processing.RoundEnvironment)
   */
  @Override
  public void process(TypeElement pElement, ProcessingEnvironment pProcessingEnv, RoundEnvironment pRoundEnv)
    throws IOException {

    /* Make sure that they are only associated with interfaces */

    if (pElement.getKind() != ElementKind.INTERFACE)
      throw new ElementIllegalArgumentException(pElement, Messages.PROXYGENERATOR_ONLYINTERFACES,
        ProxyGen.class.getSimpleName());

    /* Build a model around the class */

    ImplClass implClass = new ImplClass(pElement, pProcessingEnv);

    /* Generate a new source file for the Proxy */

    JavaFileObject jfo = pProcessingEnv.getFiler().createSourceFile(implClass.getImplQualifiedName());
    Writer writer = jfo.openWriter();

    TypeSpec.Builder typeSpecBuilder = generateImplType(implClass);

    JavaFile javaFile = JavaFile.builder(implClass.getImplQualifiedPackage(), typeSpecBuilder.build()).build();

    javaFile.writeTo(writer);

    writer.close();

  }

  private TypeSpec.Builder generateImplType(ImplClass pImplClass) {

    /* Define the basic class */

    TypeSpec.Builder builder = TypeSpec.classBuilder(pImplClass.getImplSimpleName());

    /* And it will be a public class */

    builder = builder.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

    builder = builder.superclass(AbstractVerticle.class);

    builder = builder.addSuperinterface(pImplClass.getBaseQualifiedTypeName());

    /* Define some basic fields */

    /* **** ContextFactory */

    ClassName contextFactoryName = ClassName.get("com.diamondq.common.context", "ContextFactory");
    builder = builder
      .addField(FieldSpec.builder(contextFactoryName, "mContextFactory", Modifier.PROTECTED, Modifier.FINAL).build());

    if (pImplClass.isNeedsConverter() == true)
      builder = builder.addField(
        FieldSpec.builder(ConverterManager.class, "mConverterManager", Modifier.PROTECTED, Modifier.FINAL).build());

    builder = builder
      //
      // private SecurityContextManager mSecurityContextManager;
      .addField(FieldSpec
        .builder(SecurityContextManager.class, "mSecurityContextManager", Modifier.PROTECTED, Modifier.FINAL).build());

    builder = builder.addField(FieldSpec.builder(ParameterizedTypeName.get(MessageConsumer.class, JsonObject.class)
      .annotated(AnnotationSpec.builder(Nullable.class).build()), "mConsumer", Modifier.PRIVATE).build());

    builder = builder
      .addField(FieldSpec.builder(pImplClass.getBaseQualifiedTypeName(), "mSelfProxy", Modifier.PROTECTED).build());

    builder = builder.addField(FieldSpec.builder(ClassName.get(String.class), "mAddress", Modifier.PRIVATE).build());

    // /* **** Vertx */
    //
    // ClassName vertxName = ClassName.get("io.vertx.core", "Vertx");
    // builder = builder.addField(FieldSpec.builder(vertxName, "mVertx", Modifier.PRIVATE, Modifier.FINAL).build());

    builder = generateSetup(pImplClass, builder);

    builder = generateShutdown(pImplClass, builder);

    builder = generateStart(pImplClass, builder);

    builder = generateStop(pImplClass, builder);

    builder = generateOnMessage(pImplClass, builder);

    builder = generateGetAddress(pImplClass, builder);

    /* Add a constructor */

    MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC) //
      .addAnnotation(
        AnnotationSpec.builder(SuppressWarnings.class).addMember("value", CodeBlock.of("$S", "null")).build()) //
      .addParameter(contextFactoryName, "pContextFactory");
    if (pImplClass.isNeedsConverter() == true)
      constructorBuilder = constructorBuilder.addParameter(ConverterManager.class, "pConverterManager");
    constructorBuilder = constructorBuilder.addParameter(SecurityContextManager.class, "pSecurityContextManager");
    constructorBuilder = constructorBuilder.addStatement("$N = $N", "mContextFactory", "pContextFactory");
    if (pImplClass.isNeedsConverter() == true)
      constructorBuilder = constructorBuilder.addStatement("$N = $N", "mConverterManager", "pConverterManager");
    constructorBuilder =
      constructorBuilder.addStatement("$N = $N", "mSecurityContextManager", "pSecurityContextManager");

    builder = builder.addMethod(constructorBuilder.build());

    return builder;
  }

  private TypeSpec.Builder generateOnMessage(ImplClass pImplClass, TypeSpec.Builder pClassBuilder) {

    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("onMessage");

    // @Override
    // public void start(Future<@Nullable Void> pStartFuture) throws Exception {

    methodBuilder = methodBuilder.addModifiers(Modifier.PRIVATE) //
      .returns(TypeName.VOID) //
      .addParameter(ParameterSpec
        .builder(ParameterizedTypeName.get(ClassName.get(Message.class), ClassName.get(JsonObject.class)), "pMessage")
        .build()) //
    ;

    // try (Context ctx =
    // pContextFactory.newContext(SimpleProxyImpl.class, this)) {
    methodBuilder = methodBuilder
      .beginControlFlow("try ($T ctx = mContextFactory.newContext($T.class, this))", Context.class,
        pImplClass.getImplQualifiedTypeName())
      // try {
      .beginControlFlow("try")
      //
      // /* Get the action */
      //
      .addCode("\n/* Get the action */\n\n")
      // String action = m.headers().get("action");
      .addStatement("String action = $T.notNull(pMessage.headers().get($S))", Verify.class, "action")

      // /* Propagate the SecurityContext */
      //
      .addCode("\n/* Propagate the SecurityContext */\n\n")
      // @Nullable String securityContextStr = pMessage.headers().get("securityContext");
      .addStatement("@$T String securityContextStr = pMessage.headers().get($S)", Nullable.class, "securityContext")
      // if (securityContextStr != null) {
      .beginControlFlow("if (securityContextStr != null)")
      // SecurityContext securityContext =
      // mSecurityContextManager.deserialize(Base64.getDecoder().decode(securityContextStr));
      .addStatement(
        "$T securityContext = mSecurityContextManager.deserialize($T.getDecoder().decode(securityContextStr))",
        SecurityContext.class, Base64.class)
      // ctx.setData("securityContext", securityContext);
      .addStatement("ctx.setData($S, securityContext)", "securityContext")
      // }
      .endControlFlow();

    /* Make sure there is at least one parameter */

    boolean hasParams = false;
    for (ProxyMethod proxyMethod : pImplClass.getMethods()) {
      if (proxyMethod.getParameters().isEmpty() == false) {
        hasParams = true;
        break;
      }
    }

    if (hasParams == true)
      methodBuilder = methodBuilder
        //
        .addCode("\n")
        // JsonObject body = Verify.notNull(m.body());
        .addStatement("$T body = $T.notNull(pMessage.body())", JsonObject.class, Verify.class);
    //
    // /* Now handle each of the possible actions */
    //
    methodBuilder = methodBuilder.addCode("\n/* Now handle each of the possible actions */\n\n");

    boolean isFirstMethod = true;
    for (ProxyMethod proxyMethod : pImplClass.getMethods()) {

      if (isFirstMethod == true) {
        isFirstMethod = false;
        methodBuilder = methodBuilder
          // if ("setName".equals(action)) {
          .beginControlFlow("if ($S.equals(action))", proxyMethod.getMethodName());

      }
      else {
        methodBuilder = methodBuilder
          // if ("setName".equals(action)) {
          .nextControlFlow("else if ($S.equals(action))", proxyMethod.getMethodName());
      }
      methodBuilder = methodBuilder
        // try {
        .beginControlFlow("try");

      /* For each parameter */

      List<String> paramNames = new ArrayList<>();
      for (BaseParam param : proxyMethod.getParameters()) {
        BaseType type = param.getType();
        TypeName typeName = type.getTypeName();
        paramNames.add(param.getName());
        // String pValue = Verify.notNull(body.getString("pValue"));
        if (TypeName.BOOLEAN.equals(typeName)) {
          methodBuilder = methodBuilder //
            .addStatement("boolean $N = $T.notNullArg(body.getBoolean($S), $T.VERIFY_PARAM_NULL, $S)", param.getName(),
              Verify.class, param.getName(), UtilMessages.class, param.getName());
        }
        else if (TypeName.BOOLEAN.box().equals(typeName)) {
          methodBuilder = methodBuilder //
            .addStatement("Boolean $N = $T.notNullArg(body.getBoolean($S), $T.VERIFY_PARAM_NULL, $S)", param.getName(),
              Verify.class, param.getName(), UtilMessages.class, param.getName());
        }
        else if (TypeName.BOOLEAN.box().annotated(AnnotationSpec.builder(Nullable.class).build()).equals(typeName)) {
          methodBuilder = methodBuilder //
            .addStatement("@$T Boolean $N = body.getBoolean($S)", Nullable.class, param.getName(), param.getName());
        }
        else if (TypeName.BYTE.equals(typeName)) {
          methodBuilder = methodBuilder
            .addStatement("byte[] $N_array = $T.notNullArg(body.getBinary($S), $T.VERIFY_PARAM_NULL, $S)",
              param.getName(), Verify.class, param.getName(), UtilMessages.class, param.getName()) //
            .beginControlFlow("if ($N_array.length != 1)", param.getName())
            .addStatement("throw new IllegalArgumentException()") //
            .endControlFlow() //
            .addStatement("byte $N = $N_array[0]", param.getName(), param.getName());
        }
        else if (TypeName.CHAR.equals(typeName)) {
          methodBuilder = methodBuilder
            .addStatement("String $N_string = $T.notNullArg(body.getString($S), $T.VERIFY_PARAM_NULL, $S)",
              param.getName(), Verify.class, param.getName(), UtilMessages.class, param.getName()) //
            .beginControlFlow("if ($N_string.size() != 1)", param.getName())
            .addStatement("throw new IllegalArgumentException()") //
            .endControlFlow() //
            .addStatement("char $N = $N_string.charAt(0)", param.getName(), param.getName());
        }
        else if (TypeName.DOUBLE.equals(typeName)) {
          methodBuilder = methodBuilder //
            .addStatement("double $N = $T.notNullArg(body.getDouble($S), $T.VERIFY_PARAM_NULL, $S)", param.getName(),
              Verify.class, param.getName(), UtilMessages.class, param.getName());
        }
        else if (TypeName.DOUBLE.box().equals(typeName)) {
          methodBuilder = methodBuilder //
            .addStatement("Double $N = $T.notNullArg(body.getDouble($S), $T.VERIFY_PARAM_NULL, $S)", param.getName(),
              Verify.class, param.getName(), UtilMessages.class, param.getName());
        }
        else if (TypeName.DOUBLE.box().annotated(AnnotationSpec.builder(Nullable.class).build()).equals(typeName)) {
          methodBuilder = methodBuilder //
            .addStatement("@$T Double $N = body.getDouble($S)", Nullable.class, param.getName(), param.getName());
        }
        else if (TypeName.FLOAT.equals(typeName)) {
          methodBuilder = methodBuilder //
            .addStatement("float $N = $T.notNullArg(body.getFloat($S), $T.VERIFY_PARAM_NULL, $S)", param.getName(),
              Verify.class, param.getName(), UtilMessages.class, param.getName());
        }
        else if (TypeName.FLOAT.box().equals(typeName)) {
          methodBuilder = methodBuilder //
            .addStatement("Float $N = $T.notNullArg(body.getFloat($S), $T.VERIFY_PARAM_NULL, $S)", param.getName(),
              Verify.class, param.getName(), UtilMessages.class, param.getName());
        }
        else if (TypeName.FLOAT.box().annotated(AnnotationSpec.builder(Nullable.class).build()).equals(typeName)) {
          methodBuilder = methodBuilder //
            .addStatement("@$T Float $N = body.getFloat($S)", Nullable.class, param.getName(), param.getName());
        }
        else if (TypeName.INT.equals(typeName)) {
          methodBuilder = methodBuilder //
            .addStatement("int $N = $T.notNullArg(body.getInteger($S), $T.VERIFY_PARAM_NULL, $S)", param.getName(),
              Verify.class, param.getName(), UtilMessages.class, param.getName());
        }
        else if (TypeName.INT.box().equals(typeName)) {
          methodBuilder = methodBuilder //
            .addStatement("Integer $N = $T.notNullArg(body.getInteger($S), $T.VERIFY_PARAM_NULL, $S)", param.getName(),
              Verify.class, param.getName(), UtilMessages.class, param.getName());
        }
        else if (TypeName.INT.box().annotated(AnnotationSpec.builder(Nullable.class).build()).equals(typeName)) {
          methodBuilder = methodBuilder //
            .addStatement("@$T Integer $N = body.getInteger($S)", Nullable.class, param.getName(), param.getName());
        }
        else if (TypeName.LONG.equals(typeName)) {
          methodBuilder = methodBuilder //
            .addStatement("long $N = $T.notNullArg(body.getLong($S), $T.VERIFY_PARAM_NULL, $S)", param.getName(),
              Verify.class, param.getName(), UtilMessages.class, param.getName());
        }
        else if (TypeName.LONG.box().equals(typeName)) {
          methodBuilder = methodBuilder //
            .addStatement("Long $N = $T.notNullArg(body.getLong($S), $T.VERIFY_PARAM_NULL, $S)", param.getName(),
              Verify.class, param.getName(), UtilMessages.class, param.getName());
        }
        else if (TypeName.LONG.box().annotated(AnnotationSpec.builder(Nullable.class).build()).equals(typeName)) {
          methodBuilder = methodBuilder //
            .addStatement("@$T Long $N = body.getLong($S)", Nullable.class, param.getName(), param.getName());
        }
        else if (TypeName.SHORT.equals(typeName)) {
          methodBuilder = methodBuilder
            .addStatement("int $N_int = $T.notNullArg(body.getInteger($S), $T.VERIFY_PARAM_NULL, $S)", param.getName(),
              Verify.class, param.getName(), UtilMessages.class, param.getName()) //
            .beginControlFlow("if (($N_int > Short.MAX_VALUE) || ($N_int < Short.MIN_VALUE))", param.getName(),
              param.getName()) //
            .addStatement("throw new IllegalArgumentException()") //
            .endControlFlow() //
            .addStatement("short $N = (short)$N_int", param.getName(), param.getName());
        }
        else if (TypeName.SHORT.box().equals(typeName)) {
          methodBuilder = methodBuilder
            .addStatement("Integer $N_int = $T.notNullArg(body.getInteger($S), $T.VERIFY_PARAM_NULL, $S)",
              param.getName(), Verify.class, param.getName(), UtilMessages.class, param.getName()) //
            .beginControlFlow("if (($N_int > Short.MAX_VALUE) || ($N_int < Short.MIN_VALUE))", param.getName(),
              param.getName()) //
            .addStatement("throw new IllegalArgumentException()") //
            .endControlFlow() //
            .addStatement("Short $N = $N_int.shortValue()", param.getName(), param.getName());
        }
        else if (TypeName.SHORT.box().annotated(AnnotationSpec.builder(Nullable.class).build()).equals(typeName)) {
          methodBuilder = methodBuilder
            .addStatement("@$T Integer $N_int = body.getInteger($S)", Nullable.class, param.getName(), param.getName()) //
            .beginControlFlow("if (($N_int != null) && (($N_int > Short.MAX_VALUE) || ($N_int < Short.MIN_VALUE)))",
              param.getName(), param.getName(), param.getName()) //
            .addStatement("throw new IllegalArgumentException()") //
            .endControlFlow() //
            .addStatement("@$T Short $N = ($N_int == null ? null : $N_int.shortValue())", Nullable.class,
              param.getName(), param.getName(), param.getName());
        }
        /* Handle byte array */

        else if (ArrayTypeName.of(TypeName.BYTE).equals(typeName)) {
          methodBuilder =
            methodBuilder.addStatement("byte[] $N = $T.notNullArg(body.getBinary($S), $T.VERIFY_PARAM_NULL, $S)",
              param.getName(), Verify.class, param.getName(), UtilMessages.class, param.getName());
        }

        /* Handle string */

        else if (ClassName.get(String.class).equals(typeName)) {
          methodBuilder =
            methodBuilder.addStatement("String $N = $T.notNullArg(body.getString($S), $T.VERIFY_PARAM_NULL, $S)",
              param.getName(), Verify.class, param.getName(), UtilMessages.class, param.getName());
        }
        else if (ClassName.get(String.class).annotated(AnnotationSpec.builder(Nullable.class).build())
          .equals(typeName)) {
          methodBuilder = methodBuilder.addStatement("@$T String $N = body.getString($S)", Nullable.class,
            param.getName(), param.getName());
        }

        /* Handle UUID */

        else if (ClassName.get(UUID.class).equals(typeName)) {
          methodBuilder = methodBuilder.addStatement(
            "$T $N = $T.fromString($T.notNullArg(body.getString($S), $T.VERIFY_PARAM_NULL, $S))", UUID.class,
            param.getName(), UUID.class, Verify.class, param.getName(), UtilMessages.class, param.getName());
        }
        else if (ClassName.get(UUID.class).annotated(AnnotationSpec.builder(Nullable.class).build()).equals(typeName)) {
          methodBuilder = methodBuilder
            // @Nullable String str = body.getString("a");
            .addStatement("@$T String $N_str = body.getString($S)", Nullable.class, param.getName(), param.getName())
            // @Nullable UUID uuid = (str == null ? null : UUID.fromString(str));
            .addStatement("@$T $T $N = ($N_str == null ? null : $T.fromString($N_str))", Nullable.class, UUID.class,
              param.getName(), param.getName(), UUID.class, param.getName());
        }

        /* Handle Buffer */

        else if (ClassName.get(Buffer.class).equals(typeName)) {
          methodBuilder = methodBuilder //
            .addStatement("$T $N = $T.buffer($T.notNullArg(body.getBinary($S), $T.VERIFY_PARAM_NULL, $S))",
              Buffer.class, param.getName(), Buffer.class, Verify.class, param.getName(), UtilMessages.class,
              param.getName());
        }
        else if (ClassName.get(Buffer.class).annotated(AnnotationSpec.builder(Nullable.class).build())
          .equals(typeName)) {
          methodBuilder = methodBuilder //
            .addStatement("byte @$T[] $N_bytes = body.getBinary($S)", Nullable.class, param.getName(), param.getName())
            .addStatement("@$T $T $N = ($N_bytes == null ? null : $T.buffer($N_bytes))", Nullable.class, Buffer.class,
              param.getName(), param.getName(), Buffer.class, param.getName());
        }

        /* Handle list, set, collection */

        else if (typeName instanceof ParameterizedTypeName) {
          String basicTypeName = type.getNonGenericNonAnnotatedTypeName();
          if (("java.util.List".equals(basicTypeName) == true) || ("java.util.Collection".equals(basicTypeName) == true)
            || ("java.util.Set".equals(basicTypeName) == true)) {
            BaseType itemType = type.getParameterizedType(0);
            TypeName itemTypeName = itemType.getTypeName();
            TypeName listImplType;
            if ("java.util.List".equals(basicTypeName) == true)
              listImplType = ParameterizedTypeName.get(ClassName.get(ArrayList.class), itemTypeName);
            else if ("java.util.Collection".equals(basicTypeName) == true)
              listImplType = ParameterizedTypeName.get(ClassName.get(ArrayList.class), itemTypeName);
            else if ("java.util.Set".equals(basicTypeName) == true)
              listImplType = ParameterizedTypeName.get(ClassName.get(HashSet.class), itemTypeName);
            else
              throw new UnsupportedOperationException();
            methodBuilder = methodBuilder.addStatement(
              "$T $N_jsonarray = $T.notNullArg(body.getJsonArray($S), $T.VERIFY_PARAM_NULL, $S)", JsonArray.class,
              param.getName(), Verify.class, param.getName(), UtilMessages.class, param.getName());
            methodBuilder = methodBuilder.addStatement("$T $N = new $T()", typeName, param.getName(), listImplType);
            methodBuilder =
              methodBuilder.addStatement("int $N_size = $N_jsonarray.size()", param.getName(), param.getName());
            methodBuilder = methodBuilder.beginControlFlow("if ($N_size > 0)", param.getName());
            methodBuilder = methodBuilder.beginControlFlow("for (int i=0;i<$N_size;i++)", param.getName());
            if (TypeName.BOOLEAN.equals(itemTypeName)) {
              methodBuilder =
                methodBuilder.addStatement("$N.add($N_jsonarray.getBoolean(i))", param.getName(), param.getName());
            }
            else if (TypeName.BYTE.equals(itemTypeName)) {
              methodBuilder = methodBuilder //
                .addStatement("byte[] $N_byte_array = $N_jsonarray.getBinary(i)", param.getName(), param.getName()) //
                .addStatement("$N.add($N_byte_array[0])", param.getName(), param.getName());
            }
            else if (TypeName.CHAR.equals(itemTypeName)) {
              methodBuilder = methodBuilder //
                .addStatement("String $N_string = $N_jsonarray.getString(i)", param.getName(), param.getName()) //
                .addStatement("$N.add($N_string.charAt(0))", param.getName(), param.getName());
            }
            else if (TypeName.DOUBLE.equals(itemTypeName)) {
              methodBuilder =
                methodBuilder.addStatement("$N.add($N_jsonarray.getDouble(i))", param.getName(), param.getName());
            }
            else if (TypeName.FLOAT.equals(itemTypeName)) {
              methodBuilder =
                methodBuilder.addStatement("$N.add($N_jsonarray.getFloat(i))", param.getName(), param.getName());
            }
            else if (TypeName.INT.equals(itemTypeName)) {
              methodBuilder =
                methodBuilder.addStatement("$N.add($N_jsonarray.getInteger(i))", param.getName(), param.getName());
            }
            else if (TypeName.LONG.equals(itemTypeName)) {
              methodBuilder =
                methodBuilder.addStatement("$N.add($N_jsonarray.getLong(i))", param.getName(), param.getName());
            }
            else if (TypeName.SHORT.equals(itemTypeName)) {
              methodBuilder = methodBuilder //
                .addStatement("int $N_int = $N_jsonarray.getInteger(i)", param.getName(), param.getName()) //
                .addStatement("$N.add((short)$N_int)", param.getName(), param.getName());
            }

            /* Handle string */

            else if (ClassName.get(String.class).equals(itemTypeName)) {
              methodBuilder = methodBuilder.addStatement("$N.add($T.notNull($N_jsonarray.getString(i)))",
                param.getName(), Verify.class, param.getName());
            }
            else if (ClassName.get(String.class).annotated(AnnotationSpec.builder(Nullable.class).build())
              .equals(itemTypeName)) {
              methodBuilder =
                methodBuilder.addStatement("$N.add($N_jsonarray.getBoolean(i))", param.getName(), param.getName());
            }

            /* Handle UUID */

            else if (ClassName.get(UUID.class).equals(itemTypeName)) {
              methodBuilder = methodBuilder.addStatement("$N.add($T.fromString($T.notNull($N_jsonarray.getString(i))))",
                param.getName(), UUID.class, Verify.class, param.getName());
            }
            else if (ClassName.get(UUID.class).annotated(AnnotationSpec.builder(Nullable.class).build())
              .equals(itemTypeName)) {
              methodBuilder = methodBuilder //
                .addStatement("@$T String $N_string = $N_jsonarray.getString(i)", Nullable.class, param.getName(),
                  param.getName())
                .addStatement("$N.add($N_string == null ? null : $T.fromString($N_string))", param.getName(),
                  param.getName(), UUID.class, param.getName());
            }
            else if (itemType.isConverterAvailable() == true) {
              if (itemType.isNullable() == true) {
                methodBuilder = methodBuilder //
                  .addStatement("@$T $T $N_jsonobject = $N_jsonarray.getJsonObject(i)", Nullable.class,
                    JsonObject.class, param.getName(), param.getName())
                  .addStatement(
                    "@$T $T $N_obj = ($N_jsonobject == null ? null : mConverterManager.convert($N_json, $T.class))",
                    Nullable.class, itemTypeName.withoutAnnotations(), param.getName(), param.getName(),
                    param.getName(), itemTypeName.withoutAnnotations())
                  .addStatement("$N.add($N_obj)", param.getName(), param.getName())
                //
                ;
              }
              else {
                methodBuilder = methodBuilder // .
                  .addStatement(
                    "$T $N_jsonobject = $T.notNullArg($N_jsonarray.getJsonObject(i), $T.VERIFY_PARAM_NULL, $S)",
                    JsonObject.class, param.getName(), Verify.class, param.getName(), UtilMessages.class,
                    param.getName())
                  // MyObject obj = mConverterManager.convert(obj_json, MyObject.class);
                  .addStatement("$T $N_obj = mConverterManager.convert($N_jsonobject, $T.class)", itemTypeName,
                    param.getName(), param.getName(), itemTypeName)
                  .addStatement("$N.add($N_obj)", param.getName(), param.getName())
                //
                ;
              }
            }
            else
              throw new UnsupportedOperationException(
                "Method: " + proxyMethod.toString() + " Param: " + param.toString());
            methodBuilder = methodBuilder.endControlFlow();
            methodBuilder = methodBuilder.endControlFlow();
          }
          else if ("com.diamondq.common.context.ContextExtendedCompletionStage".equals(basicTypeName)) {
            methodBuilder = methodBuilder.addCode("\n/* CODE HERE 2 */\n\n");
          }
          else
            throw new UnsupportedOperationException(
              "Method: " + proxyMethod.toString() + " Param: " + param.toString());
        }

        else if (type.isProxyType() == true) {
          TypeElement declaredTypeElement = type.getDeclaredTypeElement();
          if (declaredTypeElement == null)
            throw new IllegalStateException();
          ProxyClass proxyClass = new ProxyClass(declaredTypeElement, pImplClass.getProcessingEnv());
          methodBuilder = methodBuilder //
            .addStatement("$T $N_json = $T.notNullArg(body.getJsonObject($S), $T.VERIFY_PARAM_NULL, $S)",
              JsonObject.class, param.getName(), Verify.class, param.getName(), UtilMessages.class, param.getName()) //
            .addStatement("String $N_address = $T.notNull($N_json.getString($S))", param.getName(), Verify.class,
              param.getName(), "address") //
            .addStatement(proxyClass.isNeedsConverter() == true
              ? "$T $N = new $TProxy(mContextFactory, mConverterManager, mSecurityContextManager, vertx, $N_address, Long.parseLong(System.getProperty($S, $S)) * 1000L)"
              : "$T $N = new $TProxy(mContextFactory, mSecurityContextManager, vertx, $N_address, Long.parseLong(System.getProperty($S, $S)) * 1000L)",
              typeName, param.getName(), typeName, param.getName(), "vertx-delivery-timeout", "30")
          //
          ;
        }

        /* Handle converter available objects */
        /*
         * IMPORTANT: The converter check must be last because things like a List, which, by itself may not need a
         * converter, the generic type within it might.
         */

        else if (type.isConverterAvailable() == true) {
          if (type.isNullable() == false) {
            methodBuilder =
              methodBuilder.addStatement("$T $N_json = $T.notNullArg(body.getJsonObject($S), $T.VERIFY_PARAM_NULL, $S)",
                JsonObject.class, param.getName(), Verify.class, param.getName(), UtilMessages.class, param.getName());
            // MyObject obj = mConverterManager.convert(obj_json, MyObject.class);
            methodBuilder = methodBuilder.addStatement("$T $N = mConverterManager.convert($N_json, $T.class)", typeName,
              param.getName(), param.getName(), typeName.withoutAnnotations());
          }
          else {
            methodBuilder = methodBuilder.addStatement("@$T $T $N_json = body.getJsonObject($S)", Nullable.class,
              JsonObject.class, param.getName(), param.getName());
            // MyObject obj = mConverterManager.convert(obj_json, MyObject.class);
            methodBuilder = methodBuilder.addStatement(
              "@$T $T $N = $N_json == null ? null : mConverterManager.convert($N_json, $T.class)", Nullable.class,
              typeName.withoutAnnotations(), param.getName(), param.getName(), param.getName(),
              typeName.withoutAnnotations());
          }
        }

        else
          throw new UnsupportedOperationException("Method: " + proxyMethod.toString() + " Param: " + param.toString());
      }

      BaseType returnType = proxyMethod.getActualReturn();
      TypeName returnTypeName = returnType.getTypeName();
      TypeName nullableReturnTypeName =
        returnTypeName.withoutAnnotations().annotated(AnnotationSpec.builder(Nullable.class).build());

      // (r, ex, ctx2) -> {
      MethodSpec.Builder completionMethod = MethodSpec.methodBuilder("apply").addModifiers(Modifier.PUBLIC)
        .addAnnotation(Override.class).addParameter(nullableReturnTypeName, "r") //
        .addParameter(ClassName.get(Throwable.class).annotated(AnnotationSpec.builder(Nullable.class).build()), "ex") //
        .addParameter(ClassName.get(Context.class), "ctx2") //
        .returns(ClassName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build())) //
        // try {
        .beginControlFlow("try")
        // if (ex != null) {
        .beginControlFlow("if (ex != null)")
        // ExtendedIllegalStateException exception = new ExtendedIllegalStateException(ex,
        // VertxMessages.CALLPROXY_FAILED, "getName", "SimpleProxy", ex.getMessage());
        .addStatement(
          "$T exception = new $T(ex, $T.CALLPROXY_FAILED, $S, $S, ex.getClass().getSimpleName(), ex.getMessage())",
          ExtendedIllegalStateException.class, ExtendedIllegalStateException.class, VertxMessages.class,
          proxyMethod.getMethodName(), pImplClass.getBaseSimpleName())
        // pMessage.fail(-1, exception.getMessage());
        .addStatement("pMessage.fail(-1, exception.getMessage())")
        // ctx2.reportThrowable(exception);
        .addStatement("ctx2.reportThrowable(exception)")
        // } else {
        .nextControlFlow("else");

      completionMethod = generateMessageReply(returnType, proxyMethod, pImplClass, completionMethod);

      completionMethod = completionMethod
        // }
        .endControlFlow()
        // return null;
        .addStatement("return null")
        // } catch (RuntimeException ex2) {
        .nextControlFlow("catch(RuntimeException ex2)")
        // ctx2.reportThrowable(ex2);
        .addStatement("ctx2.reportThrowable(ex2)")
        // pMessage.fail(-2, ex2.getMessage());
        .addStatement("pMessage.fail(-2, ex2.getMessage())")
        // return null;
        .addStatement("return null")
        // }
        .endControlFlow()
      // });
      ;

      TypeSpec methodResultHandler = TypeSpec.anonymousClassBuilder("")
        .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Function3.class), //
          nullableReturnTypeName,
          ClassName.get(Throwable.class).annotated(AnnotationSpec.builder(Nullable.class).build()),
          ClassName.get(Context.class),
          ClassName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build())))
        .addMethod(completionMethod.build()) //
        .build();

      String paramNameStr = String.join(", ", paramNames);
      methodBuilder = methodBuilder
        // ContextExtendedCompletionStage<String> result = getName();
        .addStatement("$T<$T> result = $L(" + paramNameStr + ")", ContextExtendedCompletionStage.class, returnTypeName,
          proxyMethod.getMethodName())
        // result.handle(
        .addStatement("result.handle($L)", methodResultHandler);

      methodBuilder = methodBuilder
        // } catch (RuntimeException ex) {
        .nextControlFlow("catch (RuntimeException ex)")
        // ExtendedIllegalStateException exception = new ExtendedIllegalStateException(ex,
        // VertxMessages.CALLPROXY_FAILED, "setName", "SimpleProxy", ex.getMessage());
        .addStatement(
          "$T exception = new $T(ex, $T.CALLPROXY_FAILED, $S, $S, ex.getClass().getSimpleName(), ex.getMessage())",
          ExtendedIllegalStateException.class, ExtendedIllegalStateException.class, VertxMessages.class,
          proxyMethod.getMethodName(), pImplClass.getBaseSimpleName())
        // pMessage.fail(-1, exception.getMessage());
        .addStatement("pMessage.fail(-1, exception.getMessage())")
        // ctx.reportThrowable(exception);
        .addStatement("ctx.reportThrowable(exception)")
        // }
        .endControlFlow();
    }

    if (pImplClass.getMethods().isEmpty() == false) {
      methodBuilder = methodBuilder.endControlFlow();
    }

    // } catch (RuntimeException ex) {
    methodBuilder = methodBuilder.nextControlFlow("catch (RuntimeException ex)")
      // pMessage.fail(-1, ex.getMessage());
      .addStatement("pMessage.fail(-1, ex.getMessage())")
      // ctx.reportThrowable(ex);
      .addStatement("ctx.reportThrowable(ex)")
      // }
      .endControlFlow()
      // }
      .endControlFlow();

    return pClassBuilder.addMethod(methodBuilder.build());

  }

  private MethodSpec.Builder generateMessageReply(BaseType pReturnType, ProxyMethod pProxyMethod, ImplClass pImplClass,
    MethodSpec.Builder pBuilder) {

    TypeName typeName = pReturnType.getTypeName();
    // sLogger.info("generateMessageReply-> typeName: {} without: {} match: {}", typeName,
    // typeName.withoutAnnotations(), ClassName.get("java.lang", "Void"));
    if (ClassName.get("java.lang", "Void").equals(typeName.withoutAnnotations())) {
      pBuilder = pBuilder //
        .addStatement("pMessage.reply(r)");
    }
    else if (TypeName.INT.box().equals(typeName.withoutAnnotations())) {
      pBuilder = pBuilder //
        .addStatement("pMessage.reply(r)");
    }
    else if (TypeName.LONG.box().equals(typeName.withoutAnnotations())) {
      pBuilder = pBuilder //
        .addStatement("pMessage.reply(r)");
    }
    else if (TypeName.FLOAT.box().equals(typeName.withoutAnnotations())) {
      pBuilder = pBuilder //
        .addStatement("pMessage.reply(r)");
    }
    else if (TypeName.DOUBLE.box().equals(typeName.withoutAnnotations())) {
      pBuilder = pBuilder //
        .addStatement("pMessage.reply(r)");
    }
    else if (TypeName.BOOLEAN.box().equals(typeName.withoutAnnotations())) {
      pBuilder = pBuilder //
        .addStatement("pMessage.reply(r)");
    }
    else if (TypeName.SHORT.box().equals(typeName.withoutAnnotations())) {
      pBuilder = pBuilder //
        .addStatement("pMessage.reply(r)");
    }
    else if (TypeName.CHAR.box().equals(typeName.withoutAnnotations())) {
      pBuilder = pBuilder //
        .addStatement("pMessage.reply(r)");
    }
    else if (TypeName.BYTE.box().equals(typeName.withoutAnnotations())) {
      pBuilder = pBuilder //
        .addStatement("pMessage.reply(r)");
    }
    else if (ClassName.get(String.class).equals(typeName.withoutAnnotations())) {
      pBuilder = pBuilder //
        .addStatement("pMessage.reply(r)");
    }
    else if (ClassName.get(Buffer.class).equals(typeName.withoutAnnotations())) {
      pBuilder = pBuilder //
        .addStatement("pMessage.reply(r)");
    }
    else if (ClassName.get(JsonObject.class).equals(typeName.withoutAnnotations())) {
      pBuilder = pBuilder //
        .addStatement("pMessage.reply(r)");
    }
    else if (ClassName.get(JsonArray.class).equals(typeName.withoutAnnotations())) {
      pBuilder = pBuilder //
        .addStatement("pMessage.reply(r)");
    }
    else if (ArrayTypeName.of(TypeName.BYTE).equals(typeName.withoutAnnotations())) {
      pBuilder = pBuilder //
        .addStatement("pMessage.reply(r)");
    }
    /* Handle UUID */

    else if (ClassName.get(UUID.class).equals(typeName)) {
      pBuilder = pBuilder //
        .addStatement("pMessage.reply($T.notNull(r).toString())", Verify.class);
    }
    else if (ClassName.get(UUID.class).annotated(AnnotationSpec.builder(Nullable.class).build()).equals(typeName)) {
      pBuilder = pBuilder //
        .addStatement("pMessage.reply(r == null ? null : r.toString())");
    }

    /* Handle list, set, collection */

    else if (typeName instanceof ParameterizedTypeName) {
      String basicTypeName = pReturnType.getNonGenericNonAnnotatedTypeName();
      if (("java.util.List".equals(basicTypeName) == true) || ("java.util.Collection".equals(basicTypeName) == true)
        || ("java.util.Set".equals(basicTypeName) == true)) {
        BaseType itemType = pReturnType.getParameterizedType(0);
        TypeName itemTypeName = itemType.getTypeName();
        if (pReturnType.isNullable() == true)
          pBuilder = pBuilder.addStatement("@$T $T r_array = (r == null ? null : new $T())", Nullable.class,
            JsonArray.class, JsonArray.class);
        else
          pBuilder = pBuilder.addStatement("$T r_array = new $T()", JsonArray.class, JsonArray.class);
        if (pReturnType.isNullable()) {
          pBuilder = pBuilder.beginControlFlow("if (r != null)");
          pBuilder = pBuilder.beginControlFlow("for ($T item : r)", itemType.getTypeName());
        }
        else
          pBuilder = pBuilder.beginControlFlow("for ($T item : $T.notNull(r))", itemType.getTypeName(), Verify.class);

        pBuilder =
          handleElement(pProxyMethod, pReturnType, pBuilder, itemType, itemTypeName, "r_array.add(", ")", "item");
        pBuilder = pBuilder.endControlFlow();
        if (pReturnType.isNullable())
          pBuilder = pBuilder.endControlFlow();
        pBuilder = pBuilder.addStatement("pMessage.reply(r_array)");

      }
      else if ("com.diamondq.common.context.ContextExtendedCompletionStage".equals(basicTypeName)) {

        BaseType actualReturnType = pReturnType.getParameterizedType(0);
        TypeName actualTypeName = actualReturnType.getTypeName();

        // @Override
        // public @Nullable Void apply(@Nullable Void r2, @Nullable Throwable ex2, Context ctx3) {
        MethodSpec.Builder replyMethod = MethodSpec.methodBuilder("apply") //
          .addAnnotation(Override.class) //
          .addModifiers(Modifier.PUBLIC) //
          .returns(TypeName.VOID.box().annotated(AnnotationSpec.builder(Nullable.class).build())) //
          .addParameter(ParameterSpec
            .builder(TypeName.VOID.box().annotated(AnnotationSpec.builder(Nullable.class).build()), "r2").build()) //
          .addParameter(ParameterSpec
            .builder(ClassName.get(Throwable.class).annotated(AnnotationSpec.builder(Nullable.class).build()), "ex2")
            .build()) //
          .addParameter(ParameterSpec.builder(Context.class, "ctx3").build()) //
          // try {
          .beginControlFlow("try")
          // if (ex2 != null) {
          .beginControlFlow("if (ex2 != null)")
          // ExtendedIllegalStateException exception = new ExtendedIllegalStateException(ex2,
          // VertxMessages.CALLPROXY_FAILED, "scanFolderAndInjest", "LocalFileEngine", ex2.getClass().getSimpleName(),
          // ex2.getMessage());
          .addStatement(
            "$T exception = new $T(ex2, $T.CALLPROXY_FAILED, $S, $S, ex2.getClass().getSimpleName(), ex2.getMessage())",
            ExtendedIllegalStateException.class, ExtendedIllegalStateException.class, VertxMessages.class,
            pProxyMethod.getMethodName(), pImplClass.getBaseSimpleName())
          // vertx.eventBus().send(replyAddress,
          // new ReplyException(ReplyFailure.RECIPIENT_FAILURE, -1, exception.getMessage()));
          .addStatement("vertx.eventBus().send(replyAddress, new $T($T.RECIPIENT_FAILURE, -1, exception.getMessage()))",
            ReplyException.class, ReplyFailure.class)
          // ctx3.reportThrowable(exception);
          .addStatement("ctx3.reportThrowable(exception)")
          // } else {
          .nextControlFlow("else")
          // vertx.eventBus().send(replyAddress, r2);
          .addStatement("vertx.eventBus().send(replyAddress, r2)")
          // }
          .endControlFlow()
          // } catch (RuntimeException ex3) {
          .nextControlFlow("catch (RuntimeException ex3)")
          // ctx3.reportThrowable(ex3);
          .addStatement("ctx3.reportThrowable(ex3)")
          // vertx.eventBus().send(replyAddress,
          // new ReplyException(ReplyFailure.RECIPIENT_FAILURE, -1, ex3.getMessage()));
          .addStatement("vertx.eventBus().send(replyAddress, new $T($T.RECIPIENT_FAILURE, -1, ex3.getMessage()))",
            ReplyException.class, ReplyFailure.class)
          // }
          .endControlFlow()
          // return null;
          .addStatement("return null")
        // }
        // });
        ;

        TypeSpec replyHandler = TypeSpec.anonymousClassBuilder("")
          .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Function3.class), actualTypeName,
            ClassName.get(Throwable.class).annotated(AnnotationSpec.builder(Nullable.class).build()),
            ClassName.get(Context.class),
            TypeName.VOID.box().annotated(AnnotationSpec.builder(Nullable.class).build())))
          .addMethod(replyMethod.build()) //
          .build();

        pBuilder = pBuilder //
          // String replyAddress = Verify.notNull(body.getString("__replyAddress"));
          .addStatement("String replyAddress = $T.notNull(body.getString($S))", Verify.class, "__replyAddress")
          // Verify.notNull(r).handle(
          .addStatement("$T.notNull(r).handle($L)", Verify.class, replyHandler)
          // pMessage.reply(null);
          .addStatement("pMessage.reply(null)");
      }
      else if (basicTypeName.startsWith("org.javatuples.")) {
        pBuilder = pBuilder //
          .addStatement("$T replyArray = new $T()", JsonArray.class, JsonArray.class)
          .addStatement("r = $T.notNull(r)", Verify.class);
        int typeCount = pReturnType.getParameterizedTypeSize();
        for (int i = 0; i < typeCount; i++) {
          BaseType varType = pReturnType.getParameterizedType(i);
          TypeName varTypeName = varType.getTypeName();
          pBuilder = pBuilder.beginControlFlow("") //
            .addStatement("$T item = r.getValue" + String.valueOf(i) + "()", varTypeName);
          pBuilder =
            handleElement(pProxyMethod, pReturnType, pBuilder, varType, varTypeName, "replyArray.add(", ")", "item");
          pBuilder = pBuilder.endControlFlow();
        }
        pBuilder = pBuilder //
          .addStatement("pMessage.reply(replyArray)");
      }
      else
        throw new UnsupportedOperationException("Unrecognized Return ??<??> -> Method: |" + pProxyMethod.toString()
          + "| Return: |" + pReturnType.toString() + "|");
    }

    else if (pReturnType.isProxyType() == true) {
      if (pReturnType.isNullable() == true) {
        pBuilder = pBuilder //
          .addStatement("$T r_obj", JsonObject.class) //
          .beginControlFlow("if (r != null)") //
          .addStatement("r_obj = new $T()", JsonObject.class) //
          .beginControlFlow("if (r instanceof $TProxy)", typeName.withoutAnnotations()) //
          .addStatement("r_obj.put($S, (($TProxy)r).getAddress())", "address", typeName.withoutAnnotations()) //
          .nextControlFlow("else") //
          .addStatement("throw new $T()", IllegalStateException.class) //
          .endControlFlow() //
          .nextControlFlow("else") //
          .addStatement("r_obj = null") //
          .endControlFlow() //
          .addStatement("pMessage.reply(r_obj)");
      }
      else {
        pBuilder = pBuilder //
          .addStatement("$T r_obj = new $T()", JsonObject.class, JsonObject.class) //
          .addStatement("$T.notNull(r)", Verify.class) //
          .beginControlFlow("if (r instanceof $TProxy)", typeName.withoutAnnotations()) //
          .addStatement("r_obj.put($S, (($TProxy)r).getAddress())", "address", typeName) //
          .nextControlFlow("else") //
          .addStatement("throw new $T()", IllegalStateException.class) //
          .endControlFlow() //
          .addStatement("pMessage.reply(r_obj)");
      }
    }

    else if (pReturnType.isConverterAvailable() == true) {
      if (pReturnType.isNullable() == true) {
        pBuilder = pBuilder
          // JsonObject r_obj = mConverterManager.convert(r, JsonObject.class);
          .addStatement("@$T $T r_obj = (r == null ? null : mConverterManager.convert(r, $T.class))", Nullable.class,
            JsonObject.class, JsonObject.class)
          //
          .addStatement("pMessage.reply(r_obj)");
      }
      else {
        pBuilder = pBuilder
          // JsonObject r_obj = mConverterManager.convert(r, JsonObject.class);
          .addStatement("$T r_obj = mConverterManager.convert($T.notNull(r), $T.class)", JsonObject.class, Verify.class,
            JsonObject.class)
          //
          .addStatement("pMessage.reply(r_obj)");
      }
    }

    else
      throw new UnsupportedOperationException(
        "Unrecognized Return ?? -> Method: |" + pProxyMethod.toString() + "| Return: |" + pReturnType.toString() + "|");

    return pBuilder;
  }

  private MethodSpec.Builder handleElement(ProxyMethod pProxyMethod, BaseType pReturnType, MethodSpec.Builder pBuilder,
    BaseType itemType, TypeName itemTypeName, String pPrefix, String pSuffix, String pItemName) {
    if (TypeName.BOOLEAN.box().equals(itemTypeName.withoutAnnotations())) {
      pBuilder = pBuilder.addStatement(pPrefix + pItemName + pSuffix);
    }
    else if (TypeName.BYTE.box().equals(itemTypeName.withoutAnnotations())) {
      pBuilder = pBuilder //
        .addStatement("byte[] r_byte_array = new byte[1]") //
        .addStatement("r_byte_array[0] = " + pItemName) //
        .addStatement(pPrefix + "r_byte_array" + pSuffix);
    }
    else if (TypeName.CHAR.box().equals(itemTypeName.withoutAnnotations())) {
      pBuilder = pBuilder //
        .addStatement("String r_string = Character.toString(" + pItemName + ")") //
        .addStatement(pPrefix + "r_string" + pSuffix);
    }
    else if (TypeName.DOUBLE.box().equals(itemTypeName.withoutAnnotations())) {
      pBuilder = pBuilder.addStatement(pPrefix + pItemName + pSuffix);
    }
    else if (TypeName.FLOAT.box().equals(itemTypeName.withoutAnnotations())) {
      pBuilder = pBuilder.addStatement(pPrefix + pItemName + pSuffix);
    }
    else if (TypeName.INT.box().equals(itemTypeName.withoutAnnotations())) {
      pBuilder = pBuilder.addStatement(pPrefix + pItemName + pSuffix);
    }
    else if (TypeName.LONG.box().equals(itemTypeName.withoutAnnotations())) {
      pBuilder = pBuilder.addStatement(pPrefix + pItemName + pSuffix);
    }
    else if (TypeName.SHORT.box().equals(itemTypeName.withoutAnnotations())) {
      pBuilder = pBuilder //
        .addStatement("int r_int = " + pItemName) //
        .addStatement(pPrefix + "r_int" + pSuffix);
    }

    /* Handle string */

    else if (ClassName.get(String.class).equals(itemTypeName.withoutAnnotations())) {
      pBuilder = pBuilder.addStatement(pPrefix + pItemName + pSuffix);
    }

    /* Handle UUID */

    else if (ClassName.get(UUID.class).equals(itemTypeName)) {
      pBuilder = pBuilder.addStatement(pPrefix + pItemName + ".toString()" + pSuffix);
    }
    else if (ClassName.get(UUID.class).annotated(AnnotationSpec.builder(Nullable.class).build()).equals(itemTypeName)) {
      pBuilder =
        pBuilder.addStatement(pPrefix + pItemName + " == null ? null : " + pItemName + ".toString()" + pSuffix);
    }

    else if (itemTypeName instanceof ParameterizedTypeName) {
      String basicTypeName = itemType.getNonGenericNonAnnotatedTypeName();
      if (("java.util.List".equals(basicTypeName) == true) || ("java.util.Collection".equals(basicTypeName) == true)
        || ("java.util.Set".equals(basicTypeName) == true)) {
        BaseType collItemType = itemType.getParameterizedType(0);
        TypeName collItemTypeName = collItemType.getTypeName();
        if (itemType.isNullable() == true)
          pBuilder = pBuilder.addStatement("@$T $T r_array = (" + pItemName + " == null ? null : new $T())",
            Nullable.class, JsonArray.class, JsonArray.class);
        else
          pBuilder = pBuilder.addStatement("$T r_array = new $T()", JsonArray.class, JsonArray.class);
        if (itemType.isNullable()) {
          pBuilder = pBuilder.beginControlFlow("if (" + pItemName + " != null)");
          pBuilder = pBuilder.beginControlFlow("for ($T subItem : " + pItemName + ")", collItemType.getTypeName());
        }
        else
          pBuilder = pBuilder.beginControlFlow("for ($T subItem : $T.notNull(" + pItemName + "))",
            collItemType.getTypeName(), Verify.class);

        pBuilder = handleElement(pProxyMethod, pReturnType, pBuilder, collItemType, collItemTypeName, "r_array.add(",
          ")", "subItem");

        pBuilder = pBuilder.endControlFlow();
        if (pReturnType.isNullable())
          pBuilder = pBuilder.endControlFlow();
        pBuilder = pBuilder.addStatement(pPrefix + "r_array" + pSuffix);

      }
      else
        throw new UnsupportedOperationException("Unrecognized Return of ??<??> -> Method: |" + pProxyMethod.toString()
          + "| Return: |" + pReturnType.toString() + "|");
    }

    else if (itemType.isConverterAvailable() == true) {
      if (itemType.isNullable() == true) {
        pBuilder = pBuilder
          // JsonObject r_obj = mConverterManager.convert(r, JsonObject.class);
          .addStatement("@$T $T r_obj = (r == null ? null : mConverterManager.convert(" + pItemName + ", $T.class))",
            Nullable.class, JsonObject.class, JsonObject.class) //
          .addStatement(pPrefix + "r_obj" + pSuffix);
      }
      else {
        pBuilder = pBuilder
          // JsonObject r_obj = mConverterManager.convert(r, JsonObject.class);
          .addStatement("$T r_obj = mConverterManager.convert(" + pItemName + ", $T.class)", JsonObject.class,
            JsonObject.class) //
          .addStatement(pPrefix + "r_obj" + pSuffix);
      }
    }
    else
      throw new UnsupportedOperationException("Unrecognized Return List<??> -> Method: |" + pProxyMethod.toString()
        + "| Return: |" + pReturnType.toString() + "|");
    return pBuilder;
  }

  private TypeSpec.Builder generateGetAddress(ImplClass pImplClass, TypeSpec.Builder pClassBuilder) {

    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getAddress") //
      .addModifiers(Modifier.PUBLIC) //
      .returns(ClassName.get(String.class)) //
      .addStatement("return mAddress") //
    ;
    return pClassBuilder.addMethod(methodBuilder.build());

  }

  private TypeSpec.Builder generateStop(ImplClass pImplClass, TypeSpec.Builder pClassBuilder) {

    MethodSpec.Builder completionMethod =
      MethodSpec.methodBuilder("handle").addModifiers(Modifier.PUBLIC).addAnnotation(Override.class)
        .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(AsyncResult.class),
          ClassName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build())), "ar").build())
        .beginControlFlow("try ($T ctx2 = ctx.activateOnThread($S))", Context.class, "")
        // if (ar.failed() == true) {
        .beginControlFlow("if (ar.failed() == true)")
        //
        // /* The unregistration has failed. Report but continue */
        //
        .addCode("\n/* The unregistration has failed. Report but continue */\n\n")
        // ctx2.reportThrowable(Verify.notNull(ar.cause()));
        .addStatement("ctx2.reportThrowable($T.notNull(ar.cause()))", Verify.class)
        // }
        .endControlFlow()
        // pStopFuture.complete();
        .addStatement("pStopFuture.complete()")
        // }
        .endControlFlow();

    TypeSpec completionHandler = TypeSpec.anonymousClassBuilder("")
      .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Handler.class),
        ParameterizedTypeName.get(ClassName.get(AsyncResult.class),
          ClassName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build()))))
      .addMethod(completionMethod.build()) //
      .build();

    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("stop");

    // @Override
    // public void stop(Future<@Nullable Void> pStopFuture) throws Exception {

    methodBuilder = methodBuilder.addModifiers(Modifier.PUBLIC) //
      .addAnnotation(Override.class) //
      .returns(TypeName.VOID) //
      .addParameter(
        ParameterSpec
          .builder(ParameterizedTypeName.get(ClassName.get(Future.class),
            ClassName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build())), "pStopFuture")
          .build()) //
      .addException(ClassName.get(Exception.class));

    // /**
    // * @see io.vertx.core.AbstractVerticle#stop(io.vertx.core.Future)
    // */
    methodBuilder = methodBuilder.addJavadoc("@see io.vertx.core.AbstractVerticle#stop(io.vertx.core.Future)");

    // try (Context ctx =
    // pContextFactory.newContext(SimpleProxyImpl.class, this)) {
    methodBuilder = methodBuilder
      .beginControlFlow("try ($T ctx = mContextFactory.newContext($T.class, this))", Context.class,
        pImplClass.getImplQualifiedTypeName())

      // stop();
      .addStatement("stop()")
      // MessageConsumer<JsonObject> consumer = mConsumer;
      .addStatement("$T<$T> consumer = mConsumer", MessageConsumer.class, JsonObject.class)
      // if (consumer != null) {
      .beginControlFlow("if (consumer != null)")
      // ctx.prepareForAlternateThreads();
      .addStatement("ctx.prepareForAlternateThreads()")
      // consumer.unregister(
      .addStatement("consumer.unregister($L)", completionHandler)
      // }
      .endControlFlow()
      // }
      .endControlFlow();

    return pClassBuilder.addMethod(methodBuilder.build());

  }

  private TypeSpec.Builder generateStart(ImplClass pImplClass, TypeSpec.Builder pClassBuilder) {

    MethodSpec.Builder completionMethod =
      MethodSpec.methodBuilder("handle").addModifiers(Modifier.PUBLIC).addAnnotation(Override.class)
        .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(AsyncResult.class),
          ClassName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build())), "ar").build())
        .beginControlFlow("try ($T ctx2 = ctx.activateOnThread($S))", Context.class, "")
        // /* The registration of the consumer is complete */
        //
        .addCode("\n/* The registration of the consumer is complete */\n\n")
        // if (ar.succeeded() == true)
        .beginControlFlow("if (ar.succeeded() == true)")
        // mConsumer = consumer;
        .addStatement("mConsumer = consumer")
        // pStartFuture.complete();
        .addStatement("pStartFuture.complete()")
        // } else {
        .nextControlFlow("else")
        //
        // /* Indicate that there was a problem */
        //
        .addCode("\n/* Indicate that there was a problem */\n\n")
        // pStartFuture.fail(Verify.notNull(ar.cause()));
        .addStatement("pStartFuture.fail($T.notNull(ar.cause()))", Verify.class)
        // }
        .endControlFlow()
        // }
        .endControlFlow();

    TypeSpec completionHandler = TypeSpec.anonymousClassBuilder("")
      .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Handler.class),
        ParameterizedTypeName.get(ClassName.get(AsyncResult.class),
          ClassName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build()))))
      .addMethod(completionMethod.build()) //
      .build();

    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("start");

    // @Override
    // public void start(Future<@Nullable Void> pStartFuture) throws Exception {

    methodBuilder = methodBuilder.addModifiers(Modifier.PUBLIC) //
      .addAnnotation(Override.class) //
      .returns(TypeName.VOID) //
      .addParameter(
        ParameterSpec
          .builder(ParameterizedTypeName.get(ClassName.get(Future.class),
            ClassName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build())), "pStartFuture")
          .build()) //
      .addException(ClassName.get(Exception.class));

    // /**
    // * @see io.vertx.core.AbstractVerticle#start(io.vertx.core.Future)
    // */
    methodBuilder = methodBuilder.addJavadoc("@see io.vertx.core.AbstractVerticle#start(io.vertx.core.Future)");

    // try (Context ctx =
    // pContextFactory.newContext(SimpleProxyImpl.class, null)) {
    methodBuilder = methodBuilder
      .beginControlFlow("try ($T ctx = mContextFactory.newContext($T.class, this))", Context.class,
        pImplClass.getImplQualifiedTypeName())

      // start();
      .addStatement("start()")
      // String address = Verify.notNull(context.config().getString("address"));
      .addStatement("mAddress = $T.notNull($T.notNull(context.config()).getString($S))", Verify.class, Verify.class,
        "address")
      // MessageConsumer<JsonObject> consumer = vertx.eventBus().<JsonObject> consumer(address);
      .addStatement("$T<$T> consumer = vertx.eventBus().<$T>consumer(mAddress)", MessageConsumer.class,
        JsonObject.class, JsonObject.class)
      // mSelfProxy = new LocalFileEngineProxy(mContextFactory, vertx, address,
      // Long.parseLong(System.getProperty("vertx-delivery-timeout", "30")) * 1000L);
      .addStatement(pImplClass.isNeedsConverter()
        ? "mSelfProxy = new $NProxy(mContextFactory, mConverterManager, mSecurityContextManager, vertx, mAddress, Long.parseLong(System.getProperty($S, $S)) * 1000L)"
        : "mSelfProxy = new $NProxy(mContextFactory, mSecurityContextManager, vertx, mAddress, Long.parseLong(System.getProperty($S, $S)) * 1000L)",
        pImplClass.getBaseSimpleName(), "vertx-delivery-timeout", "30")
      //
      // /* Register a handler to callback when the consumer is fully registered */
      //
      .addCode("\n/* Register a handler to callback when the consumer is fully registered */\n\n")
      // ctx.prepareForAlternateThreads()
      .addStatement("ctx.prepareForAlternateThreads()")
      // consumer.completionHandler((ar) -> {
      .addStatement("consumer.completionHandler($L)", completionHandler)
      //
      // /* Register a handler to callback on each message from the EventBus */
      //
      .addCode("\n/* Register a handler to callback on each message from the EventBus */\n\n")
      // consumer.handler((m) -> {
      .addStatement("consumer.handler(this::onMessage)")
      // }
      .endControlFlow();

    return pClassBuilder.addMethod(methodBuilder.build());

  }

  private TypeSpec.Builder generateShutdown(ImplClass pImplClass, TypeSpec.Builder pClassBuilder) {

    MethodSpec.Builder undeployMethod =
      MethodSpec.methodBuilder("apply").addModifiers(Modifier.PUBLIC).addAnnotation(Override.class)
        .addParameter(ClassName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build()), "v")
        .addParameter(ClassName.get(Context.class), "ctx2")
        .returns(ParameterizedTypeName.get(ClassName.get(ContextExtendedCompletionStage.class),
          ClassName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build())))
        // return VertxUtils.<String, @Nullable Void> callReturnsNullable(pVertx::undeploy, pReg.getValue1());
        .addStatement("return $T.<String, @$T Void> callReturnsNullable(pVertx::undeploy, pReg.getValue1())",
          VertxUtils.class, Nullable.class)
    // });
    ;
    TypeSpec undeployHandler = TypeSpec.anonymousClassBuilder("")
      .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Function2.class),
        ClassName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build()),
        ClassName.get(Context.class),
        ParameterizedTypeName.get(ClassName.get(ExtendedCompletionStage.class),
          ClassName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build()))))
      .addMethod(undeployMethod.build()) //
      .build();

    MethodSpec.Builder exceptionallyMethod =
      MethodSpec.methodBuilder("apply").addModifiers(Modifier.PUBLIC).addAnnotation(Override.class)
        .addParameter(ClassName.get(Throwable.class), "ex").addParameter(ClassName.get(Context.class), "ctx2")
        .returns(ClassName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build()))
        // ctx2.reportThrowable(ex);
        .addStatement("ctx2.reportThrowable(ex)")
        // return null;
        .addStatement("return null")
    // });
    ;
    TypeSpec exceptionallyHandler = TypeSpec.anonymousClassBuilder("")
      .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Function2.class), ClassName.get(Throwable.class),
        ClassName.get(Context.class),
        ClassName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build())))
      .addMethod(exceptionallyMethod.build()) //
      .build();

    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("shutdown");

    // public static ContextExtendedCompletionStage<@Nullable Void> shutdown(ContextFactory pContextFactory,
    // ServiceDiscovery pServiceDiscovery, Vertx pVertx, Pair<String, String> pReg) {

    TypeName returnType = ParameterizedTypeName.get(ClassName.get(ContextExtendedCompletionStage.class),
      ClassName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build()));

    methodBuilder = methodBuilder.addModifiers(Modifier.STATIC, Modifier.PUBLIC) //
      .returns(returnType) //
      .addParameter(ParameterSpec.builder(ContextFactory.class, "pContextFactory").build()) //
      .addParameter(ParameterSpec.builder(ServiceDiscovery.class, "pServiceDiscovery").build()) //
      .addParameter(ParameterSpec.builder(Vertx.class, "pVertx").build()) //
      .addParameter(ParameterSpec.builder(
        ParameterizedTypeName.get(ClassName.get(Pair.class), ClassName.get(String.class), ClassName.get(String.class)),
        "pReg").build()) //
    ;

    methodBuilder = methodBuilder
      .addJavadoc("Stops the verticles to support the $T interface\n\n", pImplClass.getBaseQualifiedTypeName()) //
      // * @param pContextFactory
      .addJavadoc("@param pContextFactory the context factory\n")
      // * @param pServiceDiscovery
      .addJavadoc("@param pServiceDiscovery the service discovery\n")
      // * @param pVertx
      .addJavadoc("@param pVertx the Vert.x\n")
      // * @param pReg
      .addJavadoc("@param pReg registration information provided by the setup call\n")
      // * @return
      .addJavadoc("@return a future indicating that the verticles are shutdown\n\n");

    // try (Context ctx =
    // pContextFactory.newContext(SimpleProxyImpl.class, null, pReg)) {
    methodBuilder = methodBuilder
      .beginControlFlow("try ($T ctx = pContextFactory.newContext($T.class, null, pReg))", Context.class,
        pImplClass.getImplQualifiedTypeName())

      //
      // /* First, unpublish the record */
      //
      .addCode("\n/* First, unpublish the record */\n\n")
      // return VertxUtils.<String, @Nullable Void> callReturnsNullable(pServiceDiscovery::unpublish, pReg.getValue0())
      // //
      .addCode("return $T.<String, @$T Void> callReturnsNullable(pServiceDiscovery::unpublish, pReg.getValue0())",
        VertxUtils.class, Nullable.class)

      //
      // /* Even during an unpublish failure, we're still going to undeploy, so just report the error */
      //
      .addCode(
        "\n\n/* Even during an unpublish failure, we're still going to undeploy, so just report the error */\n\n")
      // .exceptionally((ex, ctx2) -> {
      .addCode(".exceptionally($L)", exceptionallyHandler)
      //
      // /* Now attempt to undeploy */
      //
      .addCode("\n\n/* Now attempt to undeploy */\n\n") //
      .addStatement(".thenCompose($L)", undeployHandler)
      // }
      .endControlFlow();

    return pClassBuilder.addMethod(methodBuilder.build());

  }

  private TypeSpec.Builder generateSetup(ImplClass pImplClass, TypeSpec.Builder pClassBuilder) {

    MethodSpec.Builder undeployMethod =
      MethodSpec.methodBuilder("handle").addModifiers(Modifier.PUBLIC).addAnnotation(Override.class)
        .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(AsyncResult.class),
          ClassName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build())), "far").build())
        // (far) -> {
        // try (Context ctx3 = ctx2.activateOnThread("")) {
        .beginControlFlow("try ($T ctx3 = ctx2.activateOnThread($S))", Context.class, "")
        // if (far.failed()) {
        .beginControlFlow("if (far.failed())")
        //
        // /* The undeploy failed. Report the failure, but continue with the original error */
        //
        .addCode("\n/* The undeploy failed. Report the failure, but continue with the original error */\n\n")
        // ctx3.reportThrowable(Verify.getRuntimeException(far.cause()));
        .addStatement("ctx3.reportThrowable($T.getRuntimeException(far.cause()))", Verify.class)
        // }
        .endControlFlow()
        //
        // /* Attempt to pass the error back to the caller */
        //
        .addCode("\n/* Attempt to pass the error back to the caller */\n\n")
        // if (result.completeExceptionally(ex) == false) {
        .beginControlFlow("if (result.completeExceptionally(ex) == false)")
        //
        // /* The result has already been completed. At this point, just report the error */
        //
        .addCode("\n/* The result has already been completed. At this point, just report the error */\n\n")
        // ctx3.reportThrowable(ex);
        .addStatement("ctx3.reportThrowable(ex)")
        // }
        .endControlFlow()
        // }
        .endControlFlow()
    // });
    ;
    TypeSpec undeployHandler = TypeSpec.anonymousClassBuilder("")
      .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Handler.class),
        ParameterizedTypeName.get(ClassName.get(AsyncResult.class),
          ClassName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build()))))
      .addMethod(undeployMethod.build()) //
      .build();

    MethodSpec.Builder secondUndeployMethod =
      MethodSpec.methodBuilder("handle").addModifiers(Modifier.PUBLIC).addAnnotation(Override.class)
        .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(AsyncResult.class),
          ClassName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build())), "far").build())

        // (far) -> {
        // try (Context ctx4 = ctx3.activateOnThread("")) {
        .beginControlFlow("try($T ctx4 = ctx3.activateOnThread($S))", Context.class, "")
        // if (far.failed()) {
        .beginControlFlow("if (far.failed() == true)")
        //
        // /* The undeploy failed. Report the failure, but continue with the original error */
        //
        .addCode("\n/* The undeploy failed. Report the failure, but continue with the original error */\n\n")
        // ctx4.reportThrowable(Verify.getRuntimeException(far.cause()));
        .addStatement("ctx4.reportThrowable($T.getRuntimeException(far.cause()))", Verify.class)
        // }
        .endControlFlow()
        //
        // /* Attempt to pass the error back to the caller */
        //
        .addCode("\n/* Attempt to pass the error back to the caller */\n\n")
        // if (result.completeExceptionally(ex) == false) {
        .beginControlFlow("if (result.completeExceptionally(ex) == false)")
        //
        // /* The result has already been completed. At this point, just report the error */
        //
        .addCode("\n/* The result has already been completed. At this point, just report the error */\n\n")
        // ctx4.reportThrowable(ex);
        .addStatement("ctx4.reportThrowable(ex)")
        // }
        .endControlFlow()
        // }
        .endControlFlow()
    // });
    ;
    TypeSpec secondUndeployHandler = TypeSpec.anonymousClassBuilder("")
      .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Handler.class),
        ParameterizedTypeName.get(ClassName.get(AsyncResult.class),
          ClassName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build()))))
      .addMethod(secondUndeployMethod.build()) //
      .build();

    MethodSpec.Builder publishMethod =
      MethodSpec.methodBuilder("handle").addModifiers(Modifier.PUBLIC).addAnnotation(Override.class)
        .addParameter(ParameterSpec
          .builder(ParameterizedTypeName.get(ClassName.get(AsyncResult.class), ClassName.get(Record.class)), "par")
          .build())
        // (par) -> {
        //
        // /* Reactivate the context */
        //
        .addCode("\n/* Reactivate the context */\n\n")
        // try (Context ctx3 = ctx2.activateOnThread("")) {
        .beginControlFlow("try (Context ctx3 = ctx2.activateOnThread($S))", "")
        // try {
        .beginControlFlow("try")
        // if (par.succeeded() == true) {
        .beginControlFlow("if (par.succeeded() == true)")
        //
        // /* Inform the caller that we're done */
        //
        .addCode("\n/* Inform the caller that we're done */\n\n")
        // Record publishedRecord = Verify.notNull(par.result());
        .addStatement("$T publishedRecord = $T.notNull(par.result())", Record.class, Verify.class)
        // String registrationId = publishedRecord.getRegistration();
        .addStatement("String registrationId = $T.notNull(publishedRecord.getRegistration())", Verify.class)
        // if (result.complete(Pair.with(registrationId, wasDeployedId)) == false) {
        .beginControlFlow("if (result.complete($T.with(registrationId, wasDeployedId)) == false)", Pair.class)
        // throw new IllegalStateException();
        .addStatement("throw new $T()", IllegalStateException.class)
        // }
        .endControlFlow()
        // } else {
        .nextControlFlow("else")
        // Verify.throwRuntimeException(par.cause());
        .addStatement("$T.throwRuntimeException(par.cause())", Verify.class)
        // }
        .endControlFlow()
        // } catch (RuntimeException ex) {
        .nextControlFlow("catch(RuntimeException ex)")
        //
        // /* An error has occurred while publishing the record. Undeploy the verticle */
        //
        .addCode("\n/* An error has occurred while publishing the record. Undeploy the verticle */\n\n")
        // ctx3.prepareForAlternateThreads();
        .addStatement("ctx3.prepareForAlternateThreads()")
        // pVertx.undeploy(wasDeployedId,
        .addStatement("pVertx.undeploy(wasDeployedId, $L)", secondUndeployHandler)

        // }
        .endControlFlow()
        // }
        .endControlFlow()
    // });
    ;

    TypeSpec publishHandler = TypeSpec.anonymousClassBuilder("")
      .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Handler.class),
        ParameterizedTypeName.get(ClassName.get(AsyncResult.class), ClassName.get(Record.class))))
      .addMethod(publishMethod.build()) //
      .build();

    MethodSpec.Builder deployMethod =
      MethodSpec.methodBuilder("handle").addModifiers(Modifier.PUBLIC).addAnnotation(Override.class)
        .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(AsyncResult.class, String.class), "ar").build())
        // String deploymentId = null;
        .addStatement("String deploymentId = null")
        //
        // /* Reactivate the context */
        //
        .addCode("\n/* Reactivate the context */\n\n")
        // try (Context ctx2 = ctx.activateOnThread("")) {
        .beginControlFlow("try($T ctx2 = ctx.activateOnThread($S))", Context.class, "")
        // try {
        .beginControlFlow("try")
        // if (ar.succeeded() == true) {
        .beginControlFlow("if (ar.succeeded() == true)")
        //
        // /* All verticles were deployed successfully. Next step is publish a record for lookup */
        //
        .addCode("\n/* All verticles were deployed successfully. Next step is publish a record for lookup */\n\n")
        // deploymentId = Verify.notNull(ar.result());
        .addStatement("deploymentId = $T.notNull(ar.result())", Verify.class)
        // final String wasDeployedId = deploymentId;
        .addStatement("final String wasDeployedId = deploymentId")
        //
        // /* Define a record */
        //
        .addCode("\n/* Define a record */\n\n")
        // JsonObject metadata = new JsonObject();
        .addStatement("$T metadata = new $T()", JsonObject.class, JsonObject.class)
        // if (pMetaData != null)
        .beginControlFlow("if (pMetaData != null)")
        // for (Map.Entry<String, String> pair : pMetaData.entrySet()) {
        .beginControlFlow("for($T.Entry<String, String> pair : pMetaData.entrySet())", Map.class)
        // metadata.put(pair.getKey(), pair.getValue());
        .addStatement("metadata.put(pair.getKey(), pair.getValue())")
        // }
        .endControlFlow().endControlFlow()
        // Record record = EventBusService.createRecord(pName, address, SimpleProxy.class, metadata);
        .addStatement("$T record = $T.createRecord(pName, address, $T.class, metadata)", Record.class,
          EventBusService.class, pImplClass.getBaseTypeElement())
        //
        // /* Now publish the record to the discovery service */
        //
        .addCode("\n/* Now publish the record to the discovery service */\n\n")
        // ctx2.prepareForAlternateThreads();
        .addStatement("ctx2.prepareForAlternateThreads()")
        // pServiceDiscovery.publish(record,
        .addStatement("pServiceDiscovery.publish(record, $L)", publishHandler)
        // } else {
        .nextControlFlow("else")
        // Verify.throwRuntimeException(ar.cause());
        .addStatement("$T.throwRuntimeException(ar.cause())", Verify.class)
        // }
        .endControlFlow()
        // }
        .endControlFlow()
        // catch (RuntimeException ex) {
        .beginControlFlow("catch ($T ex)", RuntimeException.class)
        //
        // /* If the deployment was a success, but an error has occurred, then we need to undeploy */
        //
        .addCode("\n/* If the deployment was a success, but an error has occurred, then we need to undeploy */\n\n")
        // if (deploymentId != null) {
        .beginControlFlow("if (deploymentId != null)")
        // ctx2.prepareForAlternateThreads();
        .addStatement("ctx2.prepareForAlternateThreads()")
        // pVertx.undeploy(deploymentId,
        .addStatement("pVertx.undeploy(deploymentId, $L)", undeployHandler)

        // } else {
        .nextControlFlow("else")
        //
        // /* Attempt to pass the error back to the caller */
        //
        .addCode("\n/* Attempt to pass the error back to the caller */\n\n")
        // if (result.completeExceptionally(ex) == false) {
        .beginControlFlow("if (result.completeExceptionally(ex) == false)")
        //
        // /* The result has already been completed. At this point, just report the error */
        //
        .addCode("\n/* The result has already been completed. At this point, just report the error */\n\n")
        // ctx2.reportThrowable(ex);
        .addStatement("ctx2.reportThrowable(ex)")
        // }
        .endControlFlow()
        // }
        .endControlFlow()
        // }
        .endControlFlow()
        // }
        .endControlFlow();

    TypeSpec deployHandler = TypeSpec.anonymousClassBuilder("")
      .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Handler.class),
        ParameterizedTypeName.get(ClassName.get(AsyncResult.class), ClassName.get(String.class))))
      .addMethod(deployMethod.build()) //
      .build();

    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("setup");

    // public static ContextExtendedCompletionStage<Pair<String, String>> setup(ContextFactory pContextFactory,
    // ServiceDiscovery pServiceDiscovery, Vertx pVertx, int pInstanceCount, Supplier<Verticle> pSupplier, String pName,
    // @Nullable String pAddress, @Nullable Map<@NonNull String, @NonNull String> pMetaData) {

    TypeName returnType = ParameterizedTypeName.get(ClassName.get(ContextExtendedCompletionStage.class),
      ParameterizedTypeName.get(ClassName.get(Pair.class), ClassName.get(String.class), ClassName.get(String.class)));

    methodBuilder = methodBuilder.addModifiers(Modifier.STATIC, Modifier.PUBLIC) //
      .returns(returnType) //
      .addParameter(ParameterSpec.builder(ContextFactory.class, "pContextFactory").build()) //
      .addParameter(ParameterSpec.builder(ServiceDiscovery.class, "pServiceDiscovery").build()) //
      .addParameter(ParameterSpec.builder(Vertx.class, "pVertx").build()) //
      .addParameter(ParameterSpec.builder(Integer.TYPE, "pInstanceCount").build()) //
      .addParameter(ParameterSpec
        .builder(ParameterizedTypeName.get(ClassName.get(Supplier.class), ClassName.get(Verticle.class)), "pSupplier")
        .build()) //
      .addParameter(ParameterSpec.builder(String.class, "pName").build()) //
      .addParameter(ParameterSpec
        .builder(ClassName.get(String.class).annotated(AnnotationSpec.builder(Nullable.class).build()), "pAddress")
        .build()) //
      .addParameter(ParameterSpec.builder(
        ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), ClassName.get(String.class))
          .annotated(AnnotationSpec.builder(Nullable.class).build()),
        "pMetaData").build()) //
    ;

    methodBuilder = methodBuilder
      .addJavadoc("Sets verticles to support the $T interface\n\n", pImplClass.getBaseQualifiedTypeName()) //
      // * @param pContextFactory
      .addJavadoc("@param pContextFactory the context factory\n")
      // * @param pServiceDiscovery
      .addJavadoc("@param pServiceDiscovery the service discovery\n")
      // * @param pVertx
      .addJavadoc("@param pVertx the Vert.x\n")
      // * @param pInstanceCount
      .addJavadoc(
        "@param pInstanceCount the number of instances to create. Pass -1 to use the default number of twice the number of processors\n")
      // * @param pSupplier
      .addJavadoc("@param pSupplier a supplier of verticle objects\n")
      // * @param pName
      .addJavadoc("@param pName the name of the verticle (used for Registration)\n")
      // * @param pAddress
      .addJavadoc("@param pAddress the optional address (if null, then a random UUID is generated)\n")
      // * @param pMetaData
      .addJavadoc("@param pMetaData the optional metadata for registration (simple (String, String) key/value pairs)\n")
      // * @return
      .addJavadoc(
        "@return a future indicating that the verticles are setup with the registration information needed for shutdown\n\n");

    // try (Context ctx =
    // pContextFactory.newContext(SimpleProxyImpl.class, null, pInstanceCount, pName, pAddress, pMetaData)) {
    methodBuilder = methodBuilder
      .beginControlFlow(
        "try ($T ctx = pContextFactory.newContext($T.class, null, pInstanceCount, pName, pAddress, pMetaData))",
        Context.class, pImplClass.getImplQualifiedTypeName())

      //
      // /* Define a completable future to indicate when deployment is complete */
      //
      .addCode("\n/* Define a completable future to indicate when deployment is complete */\n\n")

      // ContextExtendedCompletableFuture<Pair<String, String>> result = FutureUtils.newCompletableFuture();
      .addStatement("$T<$T<String, String>> result = $T.newCompletableFuture()", ContextExtendedCompletableFuture.class,
        Pair.class, FutureUtils.class)

      //
      // /* Define the deployment options */
      //
      .addCode("\n/* Define the deployment options */\n\n")

      // DeploymentOptions options = new DeploymentOptions();
      .addStatement("$T options = new $T()", DeploymentOptions.class, DeploymentOptions.class)

      //
      // /* Set the number of instances */
      //
      .addCode("\n/* Set the number of instances */\n\n")

      // if (pInstanceCount == -1)
      .beginControlFlow("if (pInstanceCount == -1)")

      // options = options.setInstances(Runtime.getRuntime().availableProcessors() * 2);
      .addStatement("options = options.setInstances($T.getRuntime().availableProcessors() * 2)", Runtime.class)
      // else
      .nextControlFlow("else")
      // options = options.setInstances(pInstanceCount);
      .addStatement("options = options.setInstances(pInstanceCount)").endControlFlow()
      //
      .addCode("\n")
      // String address;
      .addStatement("String address")
      // if (pAddress == null)
      .beginControlFlow("if (pAddress == null)")
      // address = UUID.randomUUID().toString();
      .addStatement("address = $T.randomUUID().toString()", UUID.class)
      // else
      .nextControlFlow("else")
      // address = pAddress;
      .addStatement("address = pAddress").endControlFlow()
      //
      .addCode("\n")
      // options = options.setConfig(new JsonObject().put("address", address));
      .addStatement("options = options.setConfig(new $T().put($S, address))", JsonObject.class, "address")
      //
      // /* Start the deployment */
      //
      .addCode("\n/* Start the deployment */\n\n")
      // ctx.prepareForAlternateThreads();
      .addStatement("ctx.prepareForAlternateThreads()")
      // pVertx.deployVerticle(pSupplier, options, (ar) -> {
      .addStatement("pVertx.deployVerticle(pSupplier, options, $L)", deployHandler)
      // return result;
      .addStatement("return result")
      // }
      .endControlFlow();

    return pClassBuilder.addMethod(methodBuilder.build());
  }

}

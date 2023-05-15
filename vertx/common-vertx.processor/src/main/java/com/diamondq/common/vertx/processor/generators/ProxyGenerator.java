package com.diamondq.common.vertx.processor.generators;

import com.diamondq.common.UtilMessages;
import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextExtendedCompletableFuture;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.converters.ConverterManager;
import com.diamondq.common.errors.Verify;
import com.diamondq.common.lambda.future.FutureUtils;
import com.diamondq.common.security.acl.api.SecurityContext;
import com.diamondq.common.security.acl.api.SecurityContextManager;
import com.diamondq.common.utils.parsing.properties.PropertiesParsing;
import com.diamondq.common.vertx.annotations.ProxyGen;
import com.diamondq.common.vertx.processor.Generator;
import com.diamondq.common.vertx.processor.Messages;
import com.diamondq.common.vertx.processor.model.BaseParam;
import com.diamondq.common.vertx.processor.model.BaseType;
import com.diamondq.common.vertx.processor.model.ElementIllegalArgumentException;
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
import com.squareup.javapoet.TypeVariableName;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.Status;
import org.javatuples.Decade;
import org.javatuples.Ennead;
import org.javatuples.Octet;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Quintet;
import org.javatuples.Septet;
import org.javatuples.Sextet;
import org.javatuples.Triplet;
import org.javatuples.Unit;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class ProxyGenerator implements Generator {

  @SuppressWarnings("unused") private static final Logger sLogger = LoggerFactory.getLogger(ProxyGenerator.class);

  public ProxyGenerator() {
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
   *   javax.annotation.processing.ProcessingEnvironment, javax.annotation.processing.RoundEnvironment)
   */
  @Override
  public void process(TypeElement pElement, ProcessingEnvironment pProcessingEnv, RoundEnvironment pRoundEnv)
    throws IOException {

    /* Make sure that they are only associated with interfaces */

    if (pElement.getKind() != ElementKind.INTERFACE) throw new ElementIllegalArgumentException(pElement,
      Messages.PROXYGENERATOR_ONLYINTERFACES,
      ProxyGen.class.getSimpleName()
    );

    /* Build a model around the class */

    ProxyClass proxyClass = new ProxyClass(pElement, pProcessingEnv);

    /* Generate a new source file for the Proxy */

    JavaFileObject jfo = pProcessingEnv.getFiler().createSourceFile(proxyClass.getProxyQualifiedName());
    Writer writer = jfo.openWriter();

    TypeSpec.Builder typeSpecBuilder = generateProxyType(proxyClass);

    typeSpecBuilder = generateProxyOsgi(proxyClass, typeSpecBuilder);

    /* So, we need to build each method that matches a method in the interface */

    for (ProxyMethod proxyMethod : proxyClass.getMethods()) {
      typeSpecBuilder = typeSpecBuilder.addMethod(generateProxyMethod(proxyClass, proxyMethod, pProcessingEnv));
    }

    JavaFile javaFile = JavaFile.builder(proxyClass.getProxyQualifiedPackage(), typeSpecBuilder.build()).build();

    javaFile.writeTo(writer);

    writer.close();

  }

  private TypeSpec.Builder generateProxyOsgi(ProxyClass pProxyClass, TypeSpec.Builder pTypeSpecBuilder) {

    // (m) -> {
    MethodSpec.Builder announceMethod = MethodSpec.methodBuilder("handle")
      .addModifiers(Modifier.PUBLIC)
      .addAnnotation(Override.class)
      .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(Message.class),
        ClassName.get(JsonObject.class)
      ), "m").build())
      // Record record = Verify.notNull(m.body());
      .addStatement("$T record = new $T($T.notNull(m.body()))", Record.class, Record.class, Verify.class)
      // String interfaceStr = record.getMetadata().getString("service.interface");
      .addStatement("String interfaceStr = record.getMetadata().getString($S)", "service.interface")
      // if ("x".equals(interfaceStr) == true) {
      .beginControlFlow("if ($S.equals(interfaceStr))", pProxyClass.getBaseQualifiedName())
      // String address = Verify.notNull(Verify.notNull(record.getLocation()).getString("endpoint"));
      .addStatement("String address = $T.notNull($T.notNull(record.getLocation()).getString($S))",
        Verify.class,
        Verify.class,
        "endpoint"
      )
      // if (record.getStatus() == Status.UP) {
      .beginControlFlow("if (record.getStatus() == $T.UP)", Status.class)
      //
      // /* This is a new record. Create an register a proxy */
      //
      .addCode("\n/* This is a new record. Create and register a proxy */\n\n");
    if (pProxyClass.isNeedsConverter()) {
      announceMethod = announceMethod
        // Engine instance = new EngineProxy(mContextFactory, mVertx, address);
        .addStatement(
          "$T instance = new $T(mContextFactory, mConverterManager, mSecurityContextManager, mVertx, address, mDeliveryTimeout)",
          pProxyClass.getBaseQualifiedTypeName(),
          pProxyClass.getProxyQualifiedTypeName()
        );
    } else {
      announceMethod = announceMethod
        // Engine instance = new EngineProxy(mContextFactory, mVertx, address);
        .addStatement(
          "$T instance = new $T(mContextFactory, mSecurityContextManager, mVertx, address, mDeliveryTimeout)",
          pProxyClass.getBaseQualifiedTypeName(),
          pProxyClass.getProxyQualifiedTypeName()
        );
    }
    announceMethod = announceMethod
      // Dictionary<String, ?> properties = new Hashtable<>();
      .addStatement("$T<String, ?> properties = new $T<>()", Dictionary.class, Hashtable.class)
      // ServiceRegistration<Engine> registration = pContext.getBundleContext().registerService(Engine.class,
      // instance, properties);
      .addStatement("$T<$T> registration = pContext.getBundleContext().registerService($T.class, instance, properties)",
        ServiceRegistration.class,
        pProxyClass.getBaseQualifiedTypeName(),
        pProxyClass.getBaseQualifiedTypeName()
      )
      // mRegistrations.put(address, registration);
      .addStatement("mRegistrations.put(address, registration)")
      // } else {
      .nextControlFlow("else")
      //
      // /* The record has been removed. Remove any existing proxy */
      //
      .addCode("\n/* The record has been removed. Remove any existing proxy */\n\n")
      // ServiceRegistration<Engine> registration = mRegistrations.remove(address);
      .addStatement("$T<$T> registration = mRegistrations.remove(address)",
        ServiceRegistration.class,
        pProxyClass.getBaseQualifiedTypeName()
      )
      // if (registration != null) {
      .beginControlFlow("if (registration != null)")
      // registration.unregister();
      .addStatement("registration.unregister()")
      // }
      .endControlFlow()
      // }
      .endControlFlow()
      // }
      .endControlFlow()
    // }
    ;

    TypeSpec announceHandler = TypeSpec.anonymousClassBuilder("")
      .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Handler.class),
        ParameterizedTypeName.get(ClassName.get(Message.class), ClassName.get(JsonObject.class))
      ))
      .addMethod(announceMethod.build()) //
      .build();

    // (ar) -> {
    MethodSpec.Builder unregisterMethod = MethodSpec.methodBuilder("handle")
      .addModifiers(Modifier.PUBLIC)
      .addAnnotation(Override.class)
      .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(AsyncResult.class),
        ClassName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build())
      ), "ar").build())
      // try (Context ctx2 = ctx.activateOnThread("")) {
      .beginControlFlow("try ($T ctx2 = ctx.activateOnThread($S))", Context.class, "")
      // if (ar.failed() == true) {
      .beginControlFlow("if (ar.failed() == true)")
      // ctx2.reportThrowable(Verify.notNull(ar.cause()));
      .addStatement("ctx2.reportThrowable($T.notNull(ar.cause()))", Verify.class)
      // }
      .endControlFlow()
      //
      // /* Now deregister any remaining registrations */
      //
      .addCode("\n/* Now deregister any remaining registrations */\n\n")
      // Collection<ServiceRegistration<Engine>> regs = new ArrayList<>(mRegistrations.values());
      .addStatement("$T<$T<$T>> regs = new $T<>(mRegistrations.values())",
        Collection.class,
        ServiceRegistration.class,
        pProxyClass.getBaseQualifiedTypeName(),
        ArrayList.class
      )
      // mRegistrations.clear();
      .addStatement("mRegistrations.clear()")
      // for (ServiceRegistration<Engine> reg : regs) {
      .beginControlFlow("for ($T<$T> reg : regs)", ServiceRegistration.class, pProxyClass.getBaseQualifiedTypeName())
      // reg.unregister();
      .addStatement("reg.unregister()")
      // }
      .endControlFlow()
      // }
      .endControlFlow()
      // });
      ;

    TypeSpec unregisterHandler = TypeSpec.anonymousClassBuilder("")
      .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Handler.class),
        ParameterizedTypeName.get(ClassName.get(AsyncResult.class),
          ClassName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build())
        )
      ))
      .addMethod(unregisterMethod.build()) //
      .build();

    // public static class OsgiManager {
    TypeSpec.Builder builder = TypeSpec.classBuilder("OsgiManager").addModifiers(Modifier.STATIC, Modifier.PUBLIC)
      //
      // private ContextFactory mContextFactory;
      .addField(FieldSpec.builder(ContextFactory.class, "mContextFactory", Modifier.PRIVATE).build());

    if (pProxyClass.isNeedsConverter())
      builder = builder.addField(FieldSpec.builder(ConverterManager.class, "mConverterManager", Modifier.PRIVATE)
        .build());

    builder = builder
      //
      // private SecurityContextManager mSecurityContextManager;
      .addField(FieldSpec.builder(SecurityContextManager.class, "mSecurityContextManager", Modifier.PRIVATE).build())
      //
      // private ServiceDiscovery mServiceDiscovery;
      .addField(FieldSpec.builder(ServiceDiscovery.class, "mServiceDiscovery", Modifier.PRIVATE).build())
      //
      // private Vertx mVertx;
      .addField(FieldSpec.builder(Vertx.class, "mVertx", Modifier.PRIVATE).build())
      //
      // private long mDeliveryTimeout;
      .addField(FieldSpec.builder(Long.TYPE, "mDeliveryTimeout", Modifier.PRIVATE).build())
      //
      // private @Nullable MessageConsumer<Record> mListener;
      .addField(FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(MessageConsumer.class),
        ClassName.get(JsonObject.class)
      ).annotated(AnnotationSpec.builder(Nullable.class).build()), "mListener", Modifier.PRIVATE).build())
      //
      // private final ConcurrentMap<String, ServiceRegistration<Engine>> mRegistrations;
      .addField(FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(ConcurrentMap.class),
        ClassName.get(String.class),
        ParameterizedTypeName.get(ClassName.get(ServiceRegistration.class), pProxyClass.getBaseQualifiedTypeName())
      ), "mRegistrations", Modifier.PRIVATE, Modifier.FINAL).build())
      //
      // @SuppressWarnings("null")
      // public OsgiManager() {
      .addMethod(MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
          .addMember("value", CodeBlock.of("$S", "null"))
          .build())
        // mRegistrations = new ConcurrentHashMap<>();
        .addStatement("mRegistrations = new $T<>()", ConcurrentHashMap.class)
        // }
        .build())
      //
      // public void setContextFactory(ContextFactory pContextFactory) {
      .addMethod(MethodSpec.methodBuilder("setContextFactory")
        .addParameter(ParameterSpec.builder(ContextFactory.class, "pContextFactory").build())
        // ContextFactory.staticReportTrace(EngineProxy.OsgiManager.class, this, pContextFactory);
        .addStatement("$T.staticReportTrace($N.OsgiManager.class, this, pContextFactory)",
          ContextFactory.class,
          pProxyClass.getProxySimpleName()
        )
        // mContextFactory = pContextFactory;
        .addStatement("mContextFactory = pContextFactory")
        // }
        .build());
    if (pProxyClass.isNeedsConverter()) builder = builder
      //
      // public void setConverterManager(ConverterManager pConverterManager) {
      .addMethod(MethodSpec.methodBuilder("setConverterManager")
        .addParameter(ParameterSpec.builder(ConverterManager.class, "pConverterManager").build())
        // ContextFactory.staticReportTrace(EngineProxy.OsgiManager.class, this, pConverterManager);
        .addStatement("$T.staticReportTrace($N.OsgiManager.class, this, pConverterManager)",
          ContextFactory.class,
          pProxyClass.getProxySimpleName()
        )
        // mConverterManager = pConverterManager;
        .addStatement("mConverterManager = pConverterManager")
        // }
        .build());
    builder = builder
      //
      // public void setSecurityContextManager(SecurityContextManager pSecurityContextManager) {
      .addMethod(MethodSpec.methodBuilder("setSecurityContextManager")
        .addParameter(ParameterSpec.builder(SecurityContextManager.class, "pSecurityContextManager").build())
        // ContextFactory.staticReportTrace(EngineProxy.OsgiManager.class, this, pSecurityContextManager);
        .addStatement("$T.staticReportTrace($N.OsgiManager.class, this, pSecurityContextManager)",
          ContextFactory.class,
          pProxyClass.getProxySimpleName()
        )
        // mSecurityContextManager = pSecurityContextManager;
        .addStatement("mSecurityContextManager = pSecurityContextManager")
        // }
        .build())
      //
      //
      // public void setVertx(Vertx pVertx) {
      .addMethod(MethodSpec.methodBuilder("setVertx").addParameter(ParameterSpec.builder(Vertx.class, "pVertx").build())
        // ContextFactory.staticReportTrace(EngineProxy.OsgiManager.class, this, pVertx);
        .addStatement("$T.staticReportTrace($N.OsgiManager.class, this, pVertx)",
          ContextFactory.class,
          pProxyClass.getProxySimpleName()
        )
        // mVertx = pVertx;
        .addStatement("mVertx = pVertx")
        // }
        .build())
      //
      // public void setServiceDiscovery(ServiceDiscovery pServiceDiscovery) {
      .addMethod(MethodSpec.methodBuilder("setServiceDiscovery")
        .addParameter(ParameterSpec.builder(ServiceDiscovery.class, "pServiceDiscovery").build())
        // ContextFactory.staticReportTrace(EngineProxy.OsgiManager.class, this, pServiceDiscovery);
        .addStatement("$T.staticReportTrace($N.OsgiManager.class, this, pServiceDiscovery)",
          ContextFactory.class,
          pProxyClass.getProxySimpleName()
        )
        // mServiceDiscovery = pServiceDiscovery;
        .addStatement("mServiceDiscovery = pServiceDiscovery")
        // }
        .build());
    //
    // public void onActivate(ComponentContext pContext) {
    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("onActivate")
      .addParameter(ParameterSpec.builder(ComponentContext.class, "pContext").build())
      // Dictionary<String, Object> contextProperties = pContext.getProperties();
      .addStatement("$T<String, Object> contextProperties = pContext.getProperties()", Dictionary.class)
      // Object servicePid = contextProperties.get(Constants.SERVICE_PID);
      .addStatement("Object servicePid = contextProperties.get($T.SERVICE_PID)", Constants.class)
      // Verify.notNullArg(mContextFactory, UtilMessages.VERIFY_DEPENDENCY_MISSING, "contextFactory", servicePid);
      .addStatement("$T.notNullArg(mContextFactory, $T.VERIFY_DEPENDENCY_MISSING, $S, servicePid)",
        Verify.class,
        UtilMessages.class,
        "contextFactory"
      )
      // try (Context ctx = mContextFactory.newContext(EngineProxy.OsgiManager.class, this)) {
      .beginControlFlow("try ($T ctx = mContextFactory.newContext($N.OsgiManager.class, this))",
        Context.class,
        pProxyClass.getProxySimpleName()
      );
    if (pProxyClass.isNeedsConverter()) methodBuilder = methodBuilder //
      // Verify.notNullArg(mConverterManager, UtilMessages.VERIFY_DEPENDENCY_MISSING, "converterManager", servicePid);
      .addStatement("$T.notNullArg(mConverterManager, $T.VERIFY_DEPENDENCY_MISSING, $S, servicePid)",
        Verify.class,
        UtilMessages.class,
        "converterManager"
      );
    methodBuilder = methodBuilder
      // Verify.notNullArg(mVertx, UtilMessages.VERIFY_DEPENDENCY_MISSING, "vertx", servicePid);
      .addStatement("$T.notNullArg(mVertx, $T.VERIFY_DEPENDENCY_MISSING, $S, servicePid)",
        Verify.class,
        UtilMessages.class,
        "vertx"
      )
      // Verify.notNullArg(mServiceDiscovery, UtilMessages.VERIFY_DEPENDENCY_MISSING, "serviceDiscovery", servicePid);
      .addStatement("$T.notNullArg(mServiceDiscovery, $T.VERIFY_DEPENDENCY_MISSING, $S, servicePid)",
        Verify.class,
        UtilMessages.class,
        "serviceDiscovery"
      )
      // Verify.notNullArg(mSecurityContextManager, UtilMessages.VERIFY_DEPENDENCY_MISSING, "securityContextManager",
      // servicePid);
      .addStatement("$T.notNullArg(mSecurityContextManager, $T.VERIFY_DEPENDENCY_MISSING, $S, servicePid)",
        Verify.class,
        UtilMessages.class,
        "securityContextManager"
      )
      //
      // /* Parse configuration properties */
      //
      .addCode("\n/* Parse configuration properties */\n\n")
      // long deliveryTime = PropertiesParsing.getNonNullLong(contextProperties, ".delivery-timeout",
      // Long.parseLong(System.getProperty("vertx-delivery-timeout", "30")));
      .addStatement(
        "long deliveryTime = $T.getNonNullLong(contextProperties, $S, Long.parseLong(System.getProperty($S, $S)))",
        PropertiesParsing.class,
        ".delivery-timeout",
        "vertx-delivery-timeout",
        "30"
      )
      // TimeUnit deliveryUnit = TimeUnit.valueOf(PropertiesParsing.getNonNullString(contextProperties,
      // ".delivery-timeout-unit", "SECONDS").toUpperCase(Locale.ENGLISH));
      .addStatement(
        "$T deliveryUnit = $T.valueOf($T.getNonNullString(contextProperties, $S, $S).toUpperCase($T.ENGLISH))",
        TimeUnit.class,
        TimeUnit.class,
        PropertiesParsing.class,
        ".delivery-timeout-unit",
        "SECONDS",
        Locale.class
      )
      // mDeliveryTimeout = TimeUnit.MILLISECONDS.convert(deliveryTime, deliveryUnit);
      .addStatement("mDeliveryTimeout = $T.MILLISECONDS.convert(deliveryTime, deliveryUnit)", TimeUnit.class)
      //
      // /* Register to get notified whenever the record is registered */
      //
      .addCode("\n/* Register to get notified whenever the record is registered */\n\n")
      // mListener = mVertx.eventBus().consumer("vertx.discovery.announce",
      .addStatement("mListener = mVertx.eventBus().consumer($S, $L)", "vertx.discovery.announce", announceHandler)
      // }
      .endControlFlow();
    // }
    builder = builder.addMethod(methodBuilder.build())
      //
      // public void onDeactivate() {
      .addMethod(MethodSpec.methodBuilder("onDeactivate").addModifiers(Modifier.PUBLIC)
        // try (Context ctx = mContextFactory.newContext(EngineProxy.OsgiManager.class, this)) {
        .beginControlFlow("try ($T ctx = mContextFactory.newContext($N.OsgiManager.class, this))",
          Context.class,
          pProxyClass.getProxySimpleName()
        )
        //
        // /* First, unregister the consumer so we can't get any additional registrations */
        //
        .addCode("\n/* First, unregister the consumer, so we can't get any additional registrations */\n\n")
        // MessageConsumer<Record> listener = mListener;
        .addStatement("$T<$T> listener = mListener", MessageConsumer.class, JsonObject.class)
        // if (listener != null) {
        .beginControlFlow("if (listener != null)")
        // ctx.prepareForAlternateThreads();
        .addStatement("ctx.prepareForAlternateThreads()")
        // listener.unregister(
        .addStatement("listener.unregister($L)", unregisterHandler)
        // }
        .endControlFlow()
        // }
        .endControlFlow()
        // }
        .build())
    // }
    ;
    pTypeSpecBuilder = pTypeSpecBuilder.addType(builder.build());
    return pTypeSpecBuilder;
  }

  private TypeSpec.Builder generateProxyType(ProxyClass pAnnotatedClass) {

    /* Define the basic class */

    TypeSpec.Builder builder = TypeSpec.classBuilder(pAnnotatedClass.getProxySimpleName());

    /* It must implement the annotated interface */

    builder = builder.addSuperinterface(ClassName.get(pAnnotatedClass.getBaseTypeElement()));

    /* And it will be a public class */

    builder = builder.addModifiers(Modifier.PUBLIC);

    /* Define some basic fields */

    /* **** ContextFactory */

    ClassName contextFactoryName = ClassName.get("com.diamondq.common.context", "ContextFactory");
    builder = builder.addField(FieldSpec.builder(contextFactoryName,
      "mContextFactory",
      Modifier.PRIVATE,
      Modifier.FINAL
    ).build());

    if (pAnnotatedClass.isNeedsConverter() == true) builder = builder.addField(FieldSpec.builder(ConverterManager.class,
      "mConverterManager",
      Modifier.PROTECTED,
      Modifier.FINAL
    ).build());

    /* **** SecurityContextManager */

    builder = builder.addField(FieldSpec.builder(ClassName.get(SecurityContextManager.class),
      "mSecurityContextManager",
      Modifier.PROTECTED,
      Modifier.FINAL
    ).build());

    /* **** Vertx */

    ClassName vertxName = ClassName.get("io.vertx.core", "Vertx");
    builder = builder.addField(FieldSpec.builder(vertxName, "mVertx", Modifier.PRIVATE, Modifier.FINAL).build());

    /* **** Delivery Timeout */

    builder = builder.addField(FieldSpec.builder(Long.TYPE, "mDeliveryTimeout", Modifier.PRIVATE, Modifier.FINAL)
      .build());

    /* **** Address */

    builder = builder.addField(FieldSpec.builder(String.class, "mAddress", Modifier.PRIVATE, Modifier.VOLATILE)
      .build());

    /* Add a constructor */

    MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC) //
      .addParameter(contextFactoryName, "pContextFactory");
    if (pAnnotatedClass.isNeedsConverter())
      constructorBuilder = constructorBuilder.addParameter(ConverterManager.class, "pConverterManager");
    constructorBuilder = constructorBuilder //
      .addParameter(SecurityContextManager.class, "pSecurityContextManager") //
      .addParameter(vertxName, "pVertx") //
      .addParameter(String.class, "pAddress")//
      .addParameter(Long.TYPE, "pDeliveryTimeout") //
      .addStatement("mContextFactory = $T.notNull(pContextFactory)", Verify.class);
    if (pAnnotatedClass.isNeedsConverter()) constructorBuilder = constructorBuilder //
      .addStatement("mConverterManager = $T.notNull(pConverterManager)", Verify.class);
    constructorBuilder = constructorBuilder //
      .addStatement("mSecurityContextManager = $T.notNull(pSecurityContextManager)", Verify.class)
      .addStatement("mVertx = $T.notNull(pVertx)", Verify.class)
      .addStatement("mAddress = $T.notNull(pAddress)", Verify.class)
      .addStatement("mDeliveryTimeout = pDeliveryTimeout");

    builder = builder.addMethod(constructorBuilder.build());

    /* Get the address */

    MethodSpec.Builder getAddressBuilder = MethodSpec.methodBuilder("getAddress").addModifiers(Modifier.PUBLIC) //
      .returns(ClassName.get(String.class)) //
      .addStatement("return mAddress")
      //
      ;
    builder = builder.addMethod(getAddressBuilder.build());

    return builder;
  }

  private MethodSpec.Builder addParameters(ProxyMethod pProxyMethod, MethodSpec.Builder pBuilder) {
    // message.put("aParam", aValue);
    for (BaseParam param : pProxyMethod.getParameters()) {
      BaseType type = param.getType();
      TypeName typeName = type.getTypeName();
      if ((TypeName.BOOLEAN.equals(typeName)) || (TypeName.BOOLEAN.box().equals(typeName.withoutAnnotations()))) {
        pBuilder = pBuilder.addStatement("message.put($S, $N)", param.getName(), param.getName());
      } else if ((TypeName.BYTE.equals(typeName)) || (TypeName.BYTE.box().equals(typeName))) {
        pBuilder = pBuilder //
          .addStatement("byte[] $N_array = new byte[1]", param.getName()) //
          .addStatement("$N_array[0] = $N", param.getName(), param.getName()) //
          .addStatement("message.put($S, $N_array)", param.getName(), param.getName());
      } else if (TypeName.BYTE.box().annotated(AnnotationSpec.builder(Nullable.class).build()).equals(typeName)) {
        pBuilder = pBuilder //
          .beginControlFlow("if ($N != null)", param.getName()) //
          .addStatement("byte[] $N_array = new byte[1]", param.getName()) //
          .addStatement("$N_array[0] = $N", param.getName(), param.getName()) //
          .addStatement("message.put($S, $N_array)", param.getName(), param.getName()) //
          .nextControlFlow("else") //
          .addStatement("message.put($S, (byte[])null)", param.getName()) //
          .endControlFlow();
      } else if ((TypeName.CHAR.equals(typeName)) || (TypeName.CHAR.box().equals(typeName))) {
        pBuilder = pBuilder //
          .addStatement("String $N_string = Character.toString($N)", param.getName(), param.getName()) //
          .addStatement("message.put($S, $N_string)", param.getName(), param.getName());
      } else if (TypeName.CHAR.box().annotated(AnnotationSpec.builder(Nullable.class).build()).equals(typeName)) {
        pBuilder = pBuilder //
          .beginControlFlow("if ($N != null)", param.getName()) //
          .addStatement("String $N_string = Character.toString($N)", param.getName(), param.getName()) //
          .addStatement("message.put($S, $N_string)", param.getName(), param.getName()) //
          .nextControlFlow("else") //
          .addStatement("message.put($S, (String) null)", param.getName()) //
          .endControlFlow();
      } else if ((TypeName.DOUBLE.equals(typeName)) || (TypeName.DOUBLE.box().equals(typeName.withoutAnnotations()))) {
        pBuilder = pBuilder.addStatement("message.put($S, $N)", param.getName(), param.getName());
      } else if ((TypeName.FLOAT.equals(typeName)) || (TypeName.FLOAT.box().equals(typeName.withoutAnnotations()))) {
        pBuilder = pBuilder.addStatement("message.put($S, $N)", param.getName(), param.getName());
      } else if ((TypeName.INT.equals(typeName)) || (TypeName.INT.box().equals(typeName.withoutAnnotations()))) {
        pBuilder = pBuilder.addStatement("message.put($S, $N)", param.getName(), param.getName());
      } else if ((TypeName.LONG.equals(typeName)) || (TypeName.LONG.box().equals(typeName.withoutAnnotations()))) {
        pBuilder = pBuilder.addStatement("message.put($S, $N)", param.getName(), param.getName());
      } else if ((TypeName.SHORT.equals(typeName)) || (TypeName.DOUBLE.box().equals(typeName))) {
        pBuilder = pBuilder //
          .addStatement("int $N_int = $N", param.getName(), param.getName()) //
          .addStatement("message.put($S, $N_int)", param.getName(), param.getName());
      } else if (TypeName.SHORT.box().annotated(AnnotationSpec.builder(Nullable.class).build()).equals(typeName)) {
        pBuilder = pBuilder //
          .beginControlFlow("if ($N != null)", param.getName()) //
          .addStatement("int $N_int = $N", param.getName(), param.getName()) //
          .addStatement("message.put($S, $N_int)", param.getName(), param.getName()) //
          .nextControlFlow("else") //
          .addStatement("message.put($S, (Integer) null)", param.getName()) //
          .endControlFlow();
      }

      /* Handle byte array */

      else if (ArrayTypeName.of(TypeName.BYTE).equals(typeName.withoutAnnotations())) {
        pBuilder = pBuilder.addStatement("message.put($S, $N)", param.getName(), param.getName());
      }

      /* Handle string */

      else if (ClassName.get(String.class).equals(typeName)) {
        pBuilder = pBuilder.addStatement("message.put($S, $N)", param.getName(), param.getName());
      } else if (ClassName.get(String.class)
        .annotated(AnnotationSpec.builder(Nullable.class).build())
        .equals(typeName)) {
        pBuilder = pBuilder.addStatement("message.put($S, $N)", param.getName(), param.getName());
      }

      /* Handle UUID */

      else if (ClassName.get(UUID.class).equals(typeName.withoutAnnotations())) {
        if (type.isNullable() == true) pBuilder = pBuilder //
          .addStatement("@$T String $N_str = ($N == null ? null : $N.toString())",
            Nullable.class,
            param.getName(),
            param.getName(),
            param.getName()
          );
        else pBuilder = pBuilder //
          .addStatement("@$T($S) @$T String $N_str = ($N == null ? null : $N.toString())",
            SuppressWarnings.class,
            "null",
            Nullable.class,
            param.getName(),
            param.getName(),
            param.getName()
          );
        pBuilder = pBuilder //
          .addStatement("message.put($S, $N_str)", param.getName(), param.getName());
      }

      /* Handle Buffer */

      else if (ClassName.get(Buffer.class).equals(typeName.withoutAnnotations())) {
        pBuilder = pBuilder //
          .addStatement("@$T($S) byte @$T[] $N_bytes = ($N == null ? null : $N.getBytes())",
            SuppressWarnings.class,
            "null",
            Nullable.class,
            param.getName(),
            param.getName(),
            param.getName()
          ).addStatement("message.put($S, $N_bytes)", param.getName(), param.getName());
      }

      /* Handle list, set, collection */

      else if (typeName instanceof ParameterizedTypeName) {
        String basicTypeName = type.getNonGenericNonAnnotatedTypeName();
        if (("java.util.List".equals(basicTypeName) == true) || ("java.util.Collection".equals(basicTypeName) == true)
          || ("java.util.Set".equals(basicTypeName) == true)) {
          BaseType itemType = type.getParameterizedType(0);
          TypeName itemTypeName = itemType.getTypeName();
          pBuilder = pBuilder.addStatement("$T $N_array = new $T()", JsonArray.class, param.getName(), JsonArray.class);
          pBuilder = pBuilder.beginControlFlow("for ($T item : $N)", itemType.getTypeName(), param.getName());
          if (TypeName.BOOLEAN.equals(itemTypeName)) {
            pBuilder = pBuilder.addStatement("$N_array.add(item)", param.getName());
          } else if (TypeName.BYTE.equals(itemTypeName)) {
            pBuilder = pBuilder //
              .addStatement("byte[] $N_byte_array = new byte[1]", param.getName()) //
              .addStatement("$N_byte_array[0] = item", param.getName()) //
              .addStatement("$N_array.add($N_byte_array)", param.getName(), param.getName());
          } else if (TypeName.CHAR.equals(itemTypeName)) {
            pBuilder = pBuilder //
              .addStatement("String $N_string = Character.toString(item)", param.getName()) //
              .addStatement("$N_array.add($N_string)", param.getName(), param.getName());
          } else if (TypeName.DOUBLE.equals(itemTypeName)) {
            pBuilder = pBuilder.addStatement("$N_array.add(item)", param.getName());
          } else if (TypeName.FLOAT.equals(itemTypeName)) {
            pBuilder = pBuilder.addStatement("$N_array.add(item)", param.getName());
          } else if (TypeName.INT.equals(itemTypeName)) {
            pBuilder = pBuilder.addStatement("$N_array.add(item)", param.getName());
          } else if (TypeName.LONG.equals(itemTypeName)) {
            pBuilder = pBuilder.addStatement("$N_array.add(item)", param.getName());
          } else if (TypeName.SHORT.equals(itemTypeName)) {
            pBuilder = pBuilder //
              .addStatement("int $N_int = item", param.getName()) //
              .addStatement("$N_array.add($N_int)", param.getName(), param.getName());
          }

          /* Handle string */

          else if (ClassName.get(String.class).equals(itemTypeName)) {
            pBuilder = pBuilder.addStatement("$N_array.add(item)", param.getName());
          } else if (ClassName.get(String.class)
            .annotated(AnnotationSpec.builder(Nullable.class).build())
            .equals(itemTypeName)) {
            pBuilder = pBuilder.addStatement("$N_array.add(item)", param.getName());
          }

          /* Handle UUID */

          else if (ClassName.get(UUID.class).equals(itemTypeName)) {
            pBuilder = pBuilder.addStatement("$N_array.add(item.toString())", param.getName());
          } else if (ClassName.get(UUID.class)
            .annotated(AnnotationSpec.builder(Nullable.class).build())
            .equals(itemTypeName)) {
            pBuilder = pBuilder.addStatement("$N_array.add(item == null ? null : item.toString())", param.getName());
          } else if (itemType.isConverterAvailable() == true) {
            pBuilder = pBuilder //
              .addStatement(
                "@$T($S) @$T $T $N_jsonobject = item == null ? null : mConverterManager.convert(item, $T.class)",
                SuppressWarnings.class,
                "null",
                Nullable.class,
                JsonObject.class,
                param.getName(),
                JsonObject.class
              ).addStatement("$N_array.add($N_jsonobject)", param.getName(), param.getName()) //
            ;
          } else throw new UnsupportedOperationException(
            "Method: " + pProxyMethod.toString() + " Param: " + param.toString());
          pBuilder = pBuilder.endControlFlow();
          pBuilder = pBuilder.addStatement("message.put($S, $N_array)", param.getName(), param.getName());

        } else
          throw new UnsupportedOperationException("Method: " + pProxyMethod.toString() + " Param: " + param.toString());
      } else if (type.isProxyType() == true) {
        pBuilder = pBuilder //
          .addStatement("$T $N_jsonobject = new $T()", JsonObject.class, param.getName(), JsonObject.class)
          .beginControlFlow("if ($N instanceof $TProxy)", param.getName(), type.getTypeName()) //
          .addStatement("$N_jsonobject.put($S, (($TProxy)$N).getAddress())",
            param.getName(),
            "address",
            type.getTypeName(),
            param.getName()
          )
          .nextControlFlow("else if ($N instanceof $TAbstractImpl)", param.getName(), type.getTypeName()) //
          .addStatement("$N_jsonobject.put($S, (($TAbstractImpl)$N).getAddress())",
            param.getName(),
            "address",
            type.getTypeName(),
            param.getName()
          )
          .endControlFlow() //
          // @SuppressWarnings("null")
          // JsonObject pStorageServer_nullable = (pStorageServer == null ? null : pStorageServer_jsonobject);
          .addStatement("@$T($S) @$T $T $N_nullable = ($N == null ? null : $N_jsonobject)",
            SuppressWarnings.class,
            "null",
            Nullable.class,
            JsonObject.class,
            param.getName(),
            param.getName(),
            param.getName()
          )
          .addStatement("message.put($S, $N_nullable)", param.getName(), param.getName());
      }

      /* Handle converter available objects */

      else if (type.isConverterAvailable() == true) {
        // JsonObject obj = mConverterManager.convert(pObj, JsonObject.class)
        if (type.isNullable() == true) pBuilder = pBuilder.addStatement(
          "@$T $T $N_jsonobject = $N == null ? null : mConverterManager.convert($N, $T.class)",
          Nullable.class,
          JsonObject.class,
          param.getName(),
          param.getName(),
          param.getName(),
          JsonObject.class
        );
        else pBuilder = pBuilder.addStatement(
          "@$T($S) @$T $T $N_jsonobject = $N == null ? null : mConverterManager.convert($N, $T.class)",
          SuppressWarnings.class,
          "null",
          Nullable.class,
          JsonObject.class,
          param.getName(),
          param.getName(),
          param.getName(),
          JsonObject.class
        );
        pBuilder = pBuilder.addStatement("message.put($S, $N_jsonobject)", param.getName(), param.getName());
      } else throw new UnsupportedOperationException(
        "TypeName: " + typeName.toString() + " Param: |" + param.toString() + "| Method: |" + pProxyMethod.toString()
          + "|");
    }

    /* Handle the special ContextExtendableStage return type */

    BaseType returnType = pProxyMethod.getReturnType();
    if (returnType.getNonGenericNonAnnotatedTypeName()
      .equals("com.diamondq.common.context.ContextExtendedCompletionStage")) {
      BaseType paramType = returnType.getParameterizedType(0);
      if (paramType.getNonGenericNonAnnotatedTypeName()
        .equals("com.diamondq.common.context.ContextExtendedCompletionStage")) {

        BaseType actualReturnType = paramType.getParameterizedType(0);
        TypeName actualTypeName = actualReturnType.getTypeName();

        MethodSpec.Builder replyMethod = MethodSpec.methodBuilder("handle") //
          .addAnnotation(Override.class) //
          .addModifiers(Modifier.PUBLIC) //
          .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(Message.class), actualTypeName),
            "pEvent"
          ).build()) //
          .addStatement("$T body = pEvent.body()", actualTypeName) //
          .addStatement("finalResult.complete(body)");
        ;

        TypeSpec replyHandler = TypeSpec.anonymousClassBuilder("")
          .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Handler.class),
            ParameterizedTypeName.get(ClassName.get(Message.class), actualTypeName)
          )) //
          .addMethod(replyMethod.build()) //
          .build();

        pBuilder = pBuilder //
          // String returnReplyAddress = UUID.randomUUID().toString();
          .addStatement("String returnReplyAddress = $T.randomUUID().toString()", UUID.class)
          // message.put("__replyAddress", returnReplyAddress);
          .addStatement("message.put($S, returnReplyAddress)", "__replyAddress")
          // ContextExtendedCompletableFuture<@Nullable Void> finalResult = FutureUtils.newCompletableFuture();
          .addStatement("$T<$T> finalResult = $T.newCompletableFuture()",
            ContextExtendedCompletableFuture.class,
            actualTypeName,
            FutureUtils.class
          )
          // MessageConsumer<@Nullable Void> replyConsumer = mVertx.eventBus().consumer(returnReplyAddress);
          .addStatement("$T<$T> replyConsumer = mVertx.eventBus().consumer(returnReplyAddress)",
            MessageConsumer.class,
            actualTypeName
          )
          // replyConsumer.handler(new Handler<Message<@Nullable Void>>() {
          .addStatement("replyConsumer.handler($L)", replyHandler)
        // @Override
        // public void handle(Message<@Nullable Void> pEvent) {
        // Void body = pEvent.body();
        // finalResult.complete(body);
        // }
        // });
        ;
      }

    }

    return pBuilder;
  }

  private MethodSpec generateProxyMethod(ProxyClass pProxyClass, ProxyMethod pProxyMethod,
    ProcessingEnvironment pProcessingEnv) {

    BaseType returnType = pProxyMethod.getReturnType();

    if (returnType.getTypeName().equals(TypeName.VOID)) {
      MethodSpec.Builder builder = override(pProxyMethod)

        // try (Context ctx = mContextFactory.newContext(SimpleProxyProxy.class, this)) {
        .beginControlFlow("try ($T ctx = mContextFactory.newContext($T.class, this))",
          Context.class,
          pProxyClass.getProxyQualifiedTypeName()
        )

        // JsonObject message = new JsonObject();
        .addStatement("$T message = new $T()", JsonObject.class, JsonObject.class);

      builder = addParameters(pProxyMethod, builder);

      // DeliveryOptions options = new DeliveryOptions().addHeader("action", "getName");
      builder = builder.addStatement("$T options = new $T().addHeader($S, $S).setSendTimeout(mDeliveryTimeout)",
          DeliveryOptions.class,
          DeliveryOptions.class,
          "action",
          pProxyMethod.getMethodName()
        ) //

        // mVertx.eventBus().<String> send(mAddress, message, options,
        .addStatement("$N.eventBus().send($N, $N, $N)", "mVertx", "mAddress", "message", "options")

        // }
        .endControlFlow();

      return builder.build();
    } else {
      BaseType actualReturnType = returnType.getParameterizedType(0);
      TypeName returnTypeName = actualReturnType.getTypeName();
      TypeName replyReturnType;

      Boolean isReturnTypeNullable = null;
      boolean isReplyUsed = true;

      if (ClassName.get("java.lang", "Void").equals(returnTypeName.withoutAnnotations())) {
        replyReturnType = returnTypeName;
      } else if (TypeName.INT.box().equals(returnTypeName)) {
        replyReturnType = returnTypeName;
      } else if (TypeName.LONG.box().equals(returnTypeName)) {
        replyReturnType = returnTypeName;
      } else if (TypeName.FLOAT.box().equals(returnTypeName)) {
        replyReturnType = returnTypeName;
      } else if (TypeName.DOUBLE.box().equals(returnTypeName)) {
        replyReturnType = returnTypeName;
      } else if (TypeName.BOOLEAN.box().equals(returnTypeName)) {
        replyReturnType = returnTypeName;
      } else if (TypeName.SHORT.box().equals(returnTypeName)) {
        replyReturnType = returnTypeName;
      } else if (TypeName.CHAR.box().equals(returnTypeName)) {
        replyReturnType = returnTypeName;
      } else if (TypeName.BYTE.box().equals(returnTypeName)) {
        replyReturnType = returnTypeName;
      } else if (ClassName.get(String.class).equals(returnTypeName.withoutAnnotations())) {
        replyReturnType = returnTypeName;
      } else if (ClassName.get(Buffer.class).equals(returnTypeName.withoutAnnotations())) {
        replyReturnType = returnTypeName;
      } else if (ClassName.get(JsonObject.class).equals(returnTypeName.withoutAnnotations())) {
        replyReturnType = returnTypeName;
      } else if (ClassName.get(JsonArray.class).equals(returnTypeName.withoutAnnotations())) {
        replyReturnType = returnTypeName;
      } else if (ArrayTypeName.of(TypeName.BYTE).equals(returnTypeName.withoutAnnotations())) {
        replyReturnType = returnTypeName;
      }
      /* Handle UUID */

      else if (ClassName.get(UUID.class).equals(returnTypeName)) {
        replyReturnType = TypeName.get(String.class);
      } else if (ClassName.get(UUID.class)
        .annotated(AnnotationSpec.builder(Nullable.class).build())
        .equals(returnTypeName)) {
        replyReturnType = TypeName.get(String.class).annotated(AnnotationSpec.builder(Nullable.class).build());
      }

      /* Handle list, set, collection */

      else if (returnTypeName instanceof ParameterizedTypeName) {
        String basicTypeName = actualReturnType.getNonGenericNonAnnotatedTypeName();
        if (("java.util.List".equals(basicTypeName) == true) || ("java.util.Collection".equals(basicTypeName) == true)
          || ("java.util.Set".equals(basicTypeName) == true)) {
          replyReturnType = TypeName.get(JsonArray.class);
        } else if ("com.diamondq.common.context.ContextExtendedCompletionStage".equals(basicTypeName)) {
          replyReturnType = TypeName.get(Void.class).annotated(AnnotationSpec.builder(Nullable.class).build());
          isReturnTypeNullable = true;
          isReplyUsed = false;
        } else if (basicTypeName.startsWith("org.javatuples.")) {
          replyReturnType = TypeName.get(JsonArray.class);
        } else throw new UnsupportedOperationException(
          "Method: " + pProxyMethod.toString() + " Return: " + actualReturnType.toString());
      } else if (actualReturnType.isProxyType() == true) {
        replyReturnType = TypeName.get(JsonObject.class);
      } else if (actualReturnType.isConverterAvailable() == true) {
        replyReturnType = TypeName.get(JsonObject.class);
      } else throw new UnsupportedOperationException(
        "Method: " + pProxyMethod.toString() + " Return: " + actualReturnType.toString());

      if (actualReturnType.isNullable()) {
        if (isReturnTypeNullable == null) isReturnTypeNullable = true;
        boolean needsNullable = true;
        for (AnnotationSpec spec : replyReturnType.annotations) {
          if (spec.type.equals(TypeName.get(Nullable.class))) needsNullable = false;
        }
        if (needsNullable == true)
          replyReturnType = replyReturnType.annotated(AnnotationSpec.builder(Nullable.class).build());
      } else {
        if (isReturnTypeNullable == null) isReturnTypeNullable = false;
      }

      // (ar) -> {
      MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("handle") //
        .addModifiers(Modifier.PUBLIC) //
        .addAnnotation(Override.class) //
        .returns(Void.TYPE) //
        .addParameter(ParameterizedTypeName.get(ClassName.get(AsyncResult.class),
          ParameterizedTypeName.get(ClassName.get(Message.class), replyReturnType)
        ), "ar") //
        // try (Context ctx2 = ctx.activateOnThread("")) {
        .beginControlFlow("try (Context ctx2 = ctx.activateOnThread($S))", "")
        // try {
        .beginControlFlow("try")
        // if (ar.succeeded() == true) {
        .beginControlFlow("if (ar.succeeded() == true)")
        // Message<String> replyMessage = ar.result();
        .addStatement("$T<$T> replyMessage = ar.result()", Message.class, replyReturnType)
        // if (replyMessage == null)
        .beginControlFlow("if (replyMessage == null)")
        // result.completeExceptionally(new IllegalStateException());
        .addStatement("result.completeExceptionally(new IllegalStateException())")
        // else {
        .endControlFlow().beginControlFlow("else");
      if (isReplyUsed == true) {
        methodBuilder = methodBuilder
          // String replyResult = replyMessage.body();
          .addStatement("$T replyResult = replyMessage.body()", replyReturnType);
      }
      if (isReturnTypeNullable == false) {
        methodBuilder = methodBuilder.beginControlFlow("if (replyResult == null)") //
          .addStatement("result.completeExceptionally(new IllegalStateException())") //
          .endControlFlow().beginControlFlow("else");
      }
      // result.complete(replyResult);

      if (ClassName.get("java.lang", "Void").equals(returnTypeName.withoutAnnotations())) {
        methodBuilder = methodBuilder.addStatement("result.complete(replyResult)");
      } else if (TypeName.INT.box().equals(returnTypeName)) {
        methodBuilder = methodBuilder.addStatement("result.complete(replyResult)");
      } else if (TypeName.LONG.box().equals(returnTypeName)) {
        methodBuilder = methodBuilder.addStatement("result.complete(replyResult)");
      } else if (TypeName.FLOAT.box().equals(returnTypeName)) {
        methodBuilder = methodBuilder.addStatement("result.complete(replyResult)");
      } else if (TypeName.DOUBLE.box().equals(returnTypeName)) {
        methodBuilder = methodBuilder.addStatement("result.complete(replyResult)");
      } else if (TypeName.BOOLEAN.box().equals(returnTypeName)) {
        methodBuilder = methodBuilder.addStatement("result.complete(replyResult)");
      } else if (TypeName.SHORT.box().equals(returnTypeName)) {
        methodBuilder = methodBuilder.addStatement("result.complete(replyResult)");
      } else if (TypeName.CHAR.box().equals(returnTypeName)) {
        methodBuilder = methodBuilder.addStatement("result.complete(replyResult)");
      } else if (TypeName.BYTE.box().equals(returnTypeName)) {
        methodBuilder = methodBuilder.addStatement("result.complete(replyResult)");
      } else if (ClassName.get(String.class).equals(returnTypeName.withoutAnnotations())) {
        methodBuilder = methodBuilder.addStatement("result.complete(replyResult)");
      } else if (ClassName.get(Buffer.class).equals(returnTypeName.withoutAnnotations())) {
        methodBuilder = methodBuilder.addStatement("result.complete(replyResult)");
      } else if (ClassName.get(JsonObject.class).equals(returnTypeName.withoutAnnotations())) {
        methodBuilder = methodBuilder.addStatement("result.complete(replyResult)");
      } else if (ClassName.get(JsonArray.class).equals(returnTypeName.withoutAnnotations())) {
        methodBuilder = methodBuilder.addStatement("result.complete(replyResult)");
      } else if (ArrayTypeName.of(TypeName.BYTE).equals(returnTypeName.withoutAnnotations())) {
        methodBuilder = methodBuilder.addStatement("result.complete(replyResult)");
      }

      /* Handle UUID */

      else if (ClassName.get(UUID.class).equals(returnTypeName)) {
        methodBuilder = methodBuilder //
          .addStatement("$T replyResult_obj = $T.fromString(replyResult)", returnTypeName, UUID.class)
          .addStatement("result.complete(replyResult_obj)");
      } else if (ClassName.get(UUID.class)
        .annotated(AnnotationSpec.builder(Nullable.class).build())
        .equals(returnTypeName)) {
        methodBuilder = methodBuilder //
          .addStatement("$T replyResult_obj = (replyResult == null ? null : $T.fromString(replyResult))",
            returnTypeName,
            UUID.class
          ).addStatement("result.complete(replyResult_obj)");
      }

      /* Handle list, set, collection */

      else if (returnTypeName instanceof ParameterizedTypeName) {
        String basicTypeName = actualReturnType.getNonGenericNonAnnotatedTypeName();
        if (("java.util.List".equals(basicTypeName) == true) || ("java.util.Collection".equals(basicTypeName) == true)
          || ("java.util.Set".equals(basicTypeName) == true)) {

          TypeName concreteTypeName;
          if (("java.util.List".equals(basicTypeName) == true) || ("java.util.Collection".equals(basicTypeName)
            == true)) concreteTypeName = ClassName.get(ArrayList.class);
          else concreteTypeName = ClassName.get(HashSet.class);

          BaseType itemType = actualReturnType.getParameterizedType(0);
          TypeName itemTypeName = itemType.getTypeName();
          if (actualReturnType.isNullable() == true) methodBuilder = methodBuilder.addStatement(
            "$T r_array = (replyResult == null ? null : new $T<>())",
            returnTypeName,
            concreteTypeName
          );
          else methodBuilder = methodBuilder.addStatement("$T r_array = new $T<>()", returnTypeName, concreteTypeName);
          if (actualReturnType.isNullable()) methodBuilder = methodBuilder.beginControlFlow("if (replyResult != null)");
          methodBuilder = methodBuilder.addStatement("int r_size = replyResult.size()");
          methodBuilder = methodBuilder.beginControlFlow("if (r_size > 0)");
          methodBuilder = methodBuilder.beginControlFlow("for (int i=0;i<r_size;i++)");

          methodBuilder = handleElement(pProxyMethod,
            actualReturnType,
            methodBuilder,
            itemType,
            itemTypeName,
            "r_array.add(",
            ")",
            "replyResult",
            "i"
          );

          methodBuilder = methodBuilder.endControlFlow();
          methodBuilder = methodBuilder.endControlFlow();
          if (actualReturnType.isNullable()) methodBuilder = methodBuilder.endControlFlow();
          methodBuilder = methodBuilder.addStatement("result.complete(r_array)");

        } else if ("com.diamondq.common.context.ContextExtendedCompletionStage".equals(basicTypeName)) {
          methodBuilder = methodBuilder //
            // result.complete(finalResult);
            .addStatement("result.complete(finalResult)");
        } else if (basicTypeName.startsWith("org.javatuples.")) {

          int typeSize = actualReturnType.getParameterizedTypeSize();
          Class<?> tupleClass;
          switch (typeSize) {
            case 1:
              tupleClass = Unit.class;
              break;
            case 2:
              tupleClass = Pair.class;
              break;
            case 3:
              tupleClass = Triplet.class;
              break;
            case 4:
              tupleClass = Quartet.class;
              break;
            case 5:
              tupleClass = Quintet.class;
              break;
            case 6:
              tupleClass = Sextet.class;
              break;
            case 7:
              tupleClass = Septet.class;
              break;
            case 8:
              tupleClass = Octet.class;
              break;
            case 9:
              tupleClass = Ennead.class;
              break;
            case 10:
              tupleClass = Decade.class;
              break;
            default:
              throw new UnsupportedOperationException();
          }

          /* Define the variables */

          List<String> argNames = new ArrayList<>();
          for (int i = 0; i < typeSize; i++) {
            BaseType varType = actualReturnType.getParameterizedType(i);
            TypeName varTypeName = varType.getTypeName();
            String argName = "arg" + String.valueOf(i);
            argNames.add(argName);
            methodBuilder = methodBuilder //
              .addStatement("$T " + argName, varTypeName);
          }

          /* Extract the variables */

          for (int i = 0; i < typeSize; i++) {
            BaseType varType = actualReturnType.getParameterizedType(i);
            TypeName varTypeName = varType.getTypeName();
            methodBuilder = methodBuilder //
              .beginControlFlow("");
            methodBuilder = handleElement(pProxyMethod,
              actualReturnType,
              methodBuilder,
              varType,
              varTypeName,
              argNames.get(i) + " = ",
              "",
              "replyResult",
              String.valueOf(i)
            );
            methodBuilder = methodBuilder //
              .endControlFlow();
          }

          /* Finish */

          StringBuilder sb = new StringBuilder();
          sb.append("result.complete($T.with(");
          sb.append(String.join(", ", argNames));
          sb.append("))");
          methodBuilder = methodBuilder //
            .addStatement(sb.toString(), ClassName.get(tupleClass));

        } else throw new UnsupportedOperationException(
          "Unrecognized Return of ??<??> -> Method: |" + pProxyMethod.toString() + "| Return: |"
            + actualReturnType.toString() + "|");
      } else if (actualReturnType.isProxyType() == true) {
        TypeElement typeElement = actualReturnType.getDeclaredTypeElement();
        if (typeElement == null) throw new IllegalStateException();
        ProxyClass returnProxy = new ProxyClass(typeElement, pProcessingEnv);
        if (actualReturnType.isNullable() == true) methodBuilder = methodBuilder //
          .beginControlFlow("if (replyResult != null)");
        methodBuilder = methodBuilder //
          .addStatement("String address = $T.notNull(replyResult.getString($S))", Verify.class, "address") //
          .addStatement(returnProxy.isNeedsConverter()
              == true ? "result.complete(new $TProxy(mContextFactory, mConverterManager, mSecurityContextManager, mVertx, address, mDeliveryTimeout))" : "result.complete(new $TProxy(mContextFactory, mSecurityContextManager, mVertx, address, mDeliveryTimeout))",
            returnTypeName.withoutAnnotations()
          );
        if (actualReturnType.isNullable() == true) methodBuilder = methodBuilder //
          .nextControlFlow("else") //
          .addStatement("result.complete(null)") //
          .endControlFlow();
      } else if (actualReturnType.isConverterAvailable() == true) {
        if (actualReturnType.isNullable() == true) {
          methodBuilder = methodBuilder //
            .addStatement(
              "result.complete((replyResult == null ? null : mConverterManager.convert(replyResult, $T.class)))",
              returnTypeName.withoutAnnotations()
            );
        } else {
          methodBuilder = methodBuilder //
            .addStatement("result.complete(mConverterManager.convert(replyResult, $T.class))",
              returnTypeName.withoutAnnotations()
            );
        }
      } else throw new UnsupportedOperationException(
        "Unrecognized Return of ?? -> Method: |" + pProxyMethod.toString() + "| Return: |" + actualReturnType.toString()
          + "|");

      if (isReturnTypeNullable == false) {
        methodBuilder = methodBuilder.endControlFlow();
      }
      // }
      methodBuilder = methodBuilder.endControlFlow()
        // }
        // else {
        .endControlFlow().beginControlFlow("else")
        // Throwable cause = ar.cause();
        .addStatement("$T cause = ar.cause()", Throwable.class)
        // if (cause == null)
        .beginControlFlow("if (cause == null)")
        // result.completeExceptionally(new IllegalStateException());
        .addStatement("result.completeExceptionally(new IllegalStateException())")
        // else
        .endControlFlow().beginControlFlow("else")
        // result.completeExceptionally(cause);
        .addStatement("result.completeExceptionally(cause)")
        // }
        .endControlFlow()
        // }
        .endControlFlow()
        // } catch (RuntimeException ex) {
        .nextControlFlow("catch (RuntimeException ex)")
        // if (result.completeExceptionally(ex) == false)
        .beginControlFlow("if (result.completeExceptionally(ex) == false)")
        // ctx2.reportThrowable(ex);
        .addStatement("ctx2.reportThrowable(ex)").endControlFlow() //
        // }
        .endControlFlow()
        // });
        .endControlFlow();
      TypeSpec handler = TypeSpec.anonymousClassBuilder("")
        .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Handler.class),
          ParameterizedTypeName.get(ClassName.get(AsyncResult.class),
            ParameterizedTypeName.get(ClassName.get(Message.class), replyReturnType)
          )
        )) //
        .addMethod(methodBuilder.build()) //
        .build();

      /* Override this method */

      MethodSpec.Builder builder = override(pProxyMethod)

        .addCode("\n/* Create a new context */\n\n")

        // try (Context ctx = mContextFactory.newContext(SimpleProxyProxy.class, this)) {
        .beginControlFlow("try ($T ctx = mContextFactory.newContext($T.class, this))",
          Context.class,
          pProxyClass.getProxyQualifiedTypeName()
        )

        .addCode("\n/* Define the message to send */\n\n")
        // JsonObject message = new JsonObject();
        .addStatement("$T message = new $T()", JsonObject.class, JsonObject.class);

      builder = addParameters(pProxyMethod, builder);

      // DeliveryOptions options = new DeliveryOptions().addHeader("action", "getName");
      builder = builder.addCode("\n/* Add the options including the method to call */\n\n")
        .addStatement("$T options = new $T().addHeader($S, $S).setSendTimeout(mDeliveryTimeout)",
          DeliveryOptions.class,
          DeliveryOptions.class,
          "action",
          pProxyMethod.getMethodName()
        ) //

        // SecurityContext securityContext = ctx.getData("securityContext", true, SecurityContext.class);
        .addStatement("$T securityContext = ctx.getData($S, true, $T.class)",
          SecurityContext.class,
          "securityContext",
          SecurityContext.class
        )

        // if (securityContext != null) {
        .beginControlFlow("if (securityContext != null)")
        // options = options.addHeader("securityContext",
        // Base64.getEncoder().encodeToString(mSecurityContextManager.serialize(securityContext)));
        .addStatement(
          "options = options.addHeader($S, $T.getEncoder().encodeToString(mSecurityContextManager.serialize(securityContext)))",
          "securityContext",
          Base64.class
        )
        // }
        .endControlFlow()
        //

        .addCode("\n/* Define the return future */\n\n")

        // ContextExtendedCompletableFuture<String> result = FutureUtils.newCompletableFuture();
        .addStatement("$T<$T> result = $T.newCompletableFuture()",
          ContextExtendedCompletableFuture.class,
          returnTypeName,
          FutureUtils.class
        ) //

        .addCode("\n/* Inform the context to extend beyond this method call */\n\n")

        // ctx.prepareForAlternateThreads();
        .addStatement("ctx.prepareForAlternateThreads()")

        .addCode("\n/* Send the message */\n\n")

        // mVertx.eventBus().<String> send(mAddress, message, options,
        .addStatement("$N.eventBus().<$T>request($N, $N, $N, $L)",
          "mVertx",
          replyReturnType,
          "mAddress",
          "message",
          "options",
          handler
        )

        // return result;
        .addStatement("return result")

        // }
        .endControlFlow();

      return builder.build();
    }

  }

  private MethodSpec.Builder handleElement(ProxyMethod pProxyMethod, BaseType actualReturnType,
    MethodSpec.Builder methodBuilder, BaseType itemType, TypeName itemTypeName, String pPrefix, String pSuffix,
    String pResultName, String pOffset) {
    if (TypeName.BOOLEAN.box().annotated(AnnotationSpec.builder(Nullable.class).build()).equals(itemTypeName)) {
      methodBuilder = methodBuilder.addStatement(pPrefix + "" + pResultName + ".getBoolean(" + pOffset + ")" + pSuffix);
    } else if (TypeName.BOOLEAN.box().equals(itemTypeName)) {
      methodBuilder = methodBuilder.addStatement(
        pPrefix + "$T.notNull(" + pResultName + ".getBoolean(" + pOffset + "))" + pSuffix, Verify.class);
    } else if (TypeName.BYTE.box().annotated(AnnotationSpec.builder(Nullable.class).build()).equals(itemTypeName)) {
      methodBuilder = methodBuilder //
        .addStatement("byte[] r_byte_array = " + pResultName + ".getBinary(" + pOffset + ")") //
        .addStatement(pPrefix + "r_byte_array[0]" + pSuffix);
    } else if (TypeName.BYTE.box().equals(itemTypeName)) {
      methodBuilder = methodBuilder //
        .addStatement("byte[] r_byte_array = $T.notNull(" + pResultName + ".getBinary(" + pOffset + "))",
          Verify.class
        ) //
        .addStatement(pPrefix + "r_byte_array[0]" + pSuffix);
    } else if (TypeName.CHAR.box().annotated(AnnotationSpec.builder(Nullable.class).build()).equals(itemTypeName)) {
      methodBuilder = methodBuilder //
        .addStatement("String r_string = " + pResultName + ".getString(" + pOffset + ")") //
        .addStatement(pPrefix + "r_string.charAt(0)" + pSuffix);
    } else if (TypeName.CHAR.box().equals(itemTypeName)) {
      methodBuilder = methodBuilder //
        .addStatement("String r_string = $T.notNull(" + pResultName + ".getString(" + pOffset + "))", Verify.class) //
        .addStatement(pPrefix + "r_string.charAt(0)" + pSuffix);
    } else if (TypeName.DOUBLE.box().annotated(AnnotationSpec.builder(Nullable.class).build()).equals(itemTypeName)) {
      methodBuilder = methodBuilder.addStatement(pPrefix + "" + pResultName + ".getDouble(" + pOffset + ")" + pSuffix);
    } else if (TypeName.DOUBLE.box().equals(itemTypeName)) {
      methodBuilder = methodBuilder.addStatement(
        pPrefix + "$T.notNull(" + pResultName + ".getDouble(" + pOffset + "))" + pSuffix, Verify.class);
    } else if (TypeName.FLOAT.box().annotated(AnnotationSpec.builder(Nullable.class).build()).equals(itemTypeName)) {
      methodBuilder = methodBuilder.addStatement(pPrefix + "" + pResultName + ".getFloat(" + pOffset + ")" + pSuffix);
    } else if (TypeName.FLOAT.box().equals(itemTypeName)) {
      methodBuilder = methodBuilder.addStatement(
        pPrefix + "$T.notNull(" + pResultName + ".getFloat(" + pOffset + "))" + pSuffix, Verify.class);
    } else if (TypeName.INT.box().annotated(AnnotationSpec.builder(Nullable.class).build()).equals(itemTypeName)) {
      methodBuilder = methodBuilder.addStatement(pPrefix + "" + pResultName + ".getInteger(" + pOffset + ")" + pSuffix);
    } else if (TypeName.INT.box().equals(itemTypeName)) {
      methodBuilder = methodBuilder.addStatement(
        pPrefix + "$T.notNull(" + pResultName + ".getInteger(" + pOffset + "))" + pSuffix, Verify.class);
    } else if (TypeName.LONG.box().annotated(AnnotationSpec.builder(Nullable.class).build()).equals(itemTypeName)) {
      methodBuilder = methodBuilder.addStatement(pPrefix + "" + pResultName + ".getLong(" + pOffset + ")" + pSuffix);
    } else if (TypeName.LONG.box().equals(itemTypeName)) {
      methodBuilder = methodBuilder.addStatement(
        pPrefix + "$T.notNull(" + pResultName + ".getLong(" + pOffset + "))" + pSuffix, Verify.class);
    } else if (TypeName.SHORT.box().annotated(AnnotationSpec.builder(Nullable.class).build()).equals(itemTypeName)) {
      methodBuilder = methodBuilder //
        .addStatement("Integer r_int = " + pResultName + ".getInt(" + pOffset + ")") //
        .addStatement(pPrefix + "r_int.shortValue()" + pSuffix);
    } else if (TypeName.SHORT.box().equals(itemTypeName)) {
      methodBuilder = methodBuilder //
        .addStatement("Integer r_int = $T.notNull(" + pResultName + ".getInt(" + pOffset + "))", Verify.class) //
        .addStatement(pPrefix + "r_int.shortValue()" + pSuffix);
    }

    /* Handle string */

    else if (ClassName.get(String.class).equals(itemTypeName)) {
      methodBuilder = methodBuilder //
        .addStatement("$T r_obj = " + pResultName + ".getString(" + pOffset + ")", String.class) //
        .beginControlFlow("if (r_obj == null)") //
        .addStatement("throw new $T()", IllegalStateException.class) //
        .endControlFlow() //
        .addStatement(pPrefix + "r_obj" + pSuffix);
    } else if (ClassName.get(String.class)
      .annotated(AnnotationSpec.builder(Nullable.class).build())
      .equals(itemTypeName)) {
      methodBuilder = methodBuilder.addStatement(pPrefix + "" + pResultName + ".getString(" + pOffset + ")" + pSuffix);
    }

    /* Handle UUID */

    else if (ClassName.get(UUID.class).equals(itemTypeName)) {
      methodBuilder = methodBuilder //
        .addStatement("$T r_obj = " + pResultName + ".getString(" + pOffset + ")", String.class) //
        .beginControlFlow("if (r_obj == null)") //
        .addStatement("throw new $T()", IllegalStateException.class) //
        .endControlFlow() //
        .addStatement(pPrefix + "$T.fromString(r_obj)" + pSuffix, UUID.class);
    } else if (ClassName.get(UUID.class)
      .annotated(AnnotationSpec.builder(Nullable.class).build())
      .equals(itemTypeName)) {
      methodBuilder = methodBuilder //
        .addStatement("$T r_obj = " + pResultName + ".getString(" + pOffset + ")", String.class) //
        .addStatement(pPrefix + "r_obj == null ? null : $T.fromString(r_obj)" + pSuffix, UUID.class);
    }

    /* Handle list, set, collection */

    else if (itemTypeName instanceof ParameterizedTypeName) {
      String basicTypeName = itemType.getNonGenericNonAnnotatedTypeName();
      if (("java.util.List".equals(basicTypeName) == true) || ("java.util.Collection".equals(basicTypeName) == true)
        || ("java.util.Set".equals(basicTypeName) == true)) {

        TypeName concreteTypeName;
        if (("java.util.List".equals(basicTypeName) == true) || ("java.util.Collection".equals(basicTypeName) == true))
          concreteTypeName = ClassName.get(ArrayList.class);
        else concreteTypeName = ClassName.get(HashSet.class);

        BaseType collItemType = itemType.getParameterizedType(0);
        TypeName collItemTypeName = collItemType.getTypeName();
        if (itemType.isNullable() == true) {
          methodBuilder = methodBuilder //
            .addStatement("$T replyArray = " + pResultName + ".getJsonArray(" + pOffset + ")", JsonArray.class)
            .addStatement("$T r_array = (replyArray == null ? null : new $T<>())", itemTypeName, concreteTypeName);
        } else {
          methodBuilder = methodBuilder //
            .addStatement("$T replyArray = $T.notNull(" + pResultName + ".getJsonArray(" + pOffset + "))",
              JsonArray.class,
              Verify.class
            ).addStatement("$T r_array = new $T<>()", itemTypeName, concreteTypeName);
        }
        if (itemType.isNullable()) methodBuilder = methodBuilder.beginControlFlow("if (replyArray != null)");
        methodBuilder = methodBuilder.addStatement("int r_size = replyArray.size()");
        methodBuilder = methodBuilder.beginControlFlow("if (r_size > 0)");
        methodBuilder = methodBuilder.beginControlFlow("for (int i=0;i<r_size;i++)");

        methodBuilder = handleElement(pProxyMethod,
          actualReturnType,
          methodBuilder,
          collItemType,
          collItemTypeName,
          "r_array.add(",
          ")",
          "replyArray",
          "i"
        );

        methodBuilder = methodBuilder.endControlFlow();
        methodBuilder = methodBuilder.endControlFlow();
        if (itemType.isNullable()) methodBuilder = methodBuilder.endControlFlow();
        methodBuilder = methodBuilder.addStatement(pPrefix + "r_array" + pSuffix);

      } else throw new UnsupportedOperationException(
        "Unrecognized Return of ??<??> -> Method: |" + pProxyMethod.toString() + "| Return: |"
          + actualReturnType.toString() + "|");
    } else if (itemType.isConverterAvailable() == true) {
      if (itemType.isNullable() == true) {
        methodBuilder = methodBuilder //
          .addStatement("$T r_obj = " + pResultName + ".getJsonObject(" + pOffset + ")", JsonObject.class) //
          .addStatement(pPrefix + "r_obj == null ? null : mConverterManager.convert(r_obj, $T.class)" + pSuffix,
            itemTypeName.withoutAnnotations()
          );
      } else {
        methodBuilder = methodBuilder //
          .addStatement("$T r_obj = " + pResultName + ".getJsonObject(" + pOffset + ")", JsonObject.class) //
          .beginControlFlow("if (r_obj == null)") //
          .addStatement("throw new $T()", IllegalStateException.class) //
          .endControlFlow() //
          .addStatement(pPrefix + "mConverterManager.convert(" + pResultName + ", $T.class)" + pSuffix,
            itemTypeName.withoutAnnotations()
          );
      }
    } else throw new UnsupportedOperationException(
      "Unrecognized Return of List<??> -> Method: |" + pProxyMethod.toString() + "| Return: |"
        + actualReturnType.toString() + "|");
    return methodBuilder;
  }

  /**
   * This is a modification of the MethodSpec.overriding method because it explicitly does not put annotations on the
   * parameters or return types, but we need the @Nullable annotations to be passed along
   *
   * @param method the method
   * @return the builder
   */
  private MethodSpec.Builder override(ProxyMethod pMethod) {

    ExecutableElement method = pMethod.getExecutableElement();

    Element enclosingClass = method.getEnclosingElement();
    if (enclosingClass.getModifiers().contains(Modifier.FINAL)) {
      throw new IllegalArgumentException("Cannot override method on final class " + enclosingClass);
    }

    Set<Modifier> modifiers = method.getModifiers();
    if (modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.FINAL)
      || modifiers.contains(Modifier.STATIC)) {
      throw new IllegalArgumentException("cannot override method with modifiers: " + modifiers);
    }

    String methodName = method.getSimpleName().toString();
    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName);

    methodBuilder.addAnnotation(Override.class);

    modifiers = new LinkedHashSet<>(modifiers);
    modifiers.remove(Modifier.ABSTRACT);
    modifiers.remove(Modifier.DEFAULT);
    methodBuilder.addModifiers(modifiers);

    for (TypeParameterElement typeParameterElement : method.getTypeParameters()) {
      TypeVariable var = (TypeVariable) typeParameterElement.asType();
      methodBuilder.addTypeVariable(TypeVariableName.get(var));
    }

    methodBuilder.returns(pMethod.getReturnType().getTypeName());
    List<ParameterSpec> parameterSpecs = new ArrayList<>();
    for (BaseParam param : pMethod.getParameters()) {
      BaseType paramType = param.getType();
      TypeName type = paramType.getTypeName();
      String name = param.getName();
      ParameterSpec.Builder paramBuilder = ParameterSpec.builder(type, name);
      parameterSpecs.add(paramBuilder.build());
    }
    methodBuilder.addParameters(parameterSpecs);
    methodBuilder.varargs(method.isVarArgs());

    for (TypeMirror thrownType : method.getThrownTypes()) {
      methodBuilder.addException(TypeName.get(thrownType));
    }

    return methodBuilder;
  }
}

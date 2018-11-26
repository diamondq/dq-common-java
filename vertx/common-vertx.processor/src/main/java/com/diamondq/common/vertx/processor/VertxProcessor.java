package com.diamondq.common.vertx.processor;

import com.diamondq.common.vertx.annotations.ProxyGen;
import com.diamondq.common.vertx.processor.generators.ImplGenerator;
import com.diamondq.common.vertx.processor.generators.ProxyGenerator;
import com.diamondq.common.vertx.processor.model.ElementIllegalArgumentException;
import com.google.auto.service.AutoService;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AutoService(Processor.class)
public class VertxProcessor extends AbstractProcessor {

  private static final Logger                                     sLogger =
    LoggerFactory.getLogger(VertxProcessor.class);

  private final Set<String>                                       mSupportedAnnotationTypes;

  private final Map<Class<? extends Annotation>, List<Generator>> mProcessors;

  public VertxProcessor() {
    Map<Class<? extends Annotation>, List<Generator>> processors = new HashMap<>();
    Generator[] generators = new Generator[] {new ProxyGenerator(), new ImplGenerator()};
    Set<String> types = new HashSet<>();
    for (Generator generator : generators) {
      Class<? extends Annotation> annotation = generator.getAnnotation();
      types.add(annotation.getCanonicalName());
      List<Generator> list = processors.get(annotation);
      if (list == null) {
        list = new ArrayList<>();
        processors.put(annotation, list);
      }
      list.add(generator);
    }
    mProcessors = Collections.unmodifiableMap(processors);
    mSupportedAnnotationTypes = Collections.unmodifiableSet(types);
  }

  /**
   * @see javax.annotation.processing.AbstractProcessor#getSupportedSourceVersion()
   */
  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  /**
   * @see javax.annotation.processing.AbstractProcessor#getSupportedAnnotationTypes()
   */
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return mSupportedAnnotationTypes;
  }

  @Override
  public boolean process(Set<? extends TypeElement> pAnnotations, RoundEnvironment pRoundEnv) {
    try {

      for (Map.Entry<Class<? extends Annotation>, List<Generator>> pair : mProcessors.entrySet()) {

        /* Find all the objects annotated with the annotation */

        for (Element annotatedElement : pRoundEnv.getElementsAnnotatedWith(pair.getKey())) {

          try {
            try {
              /* Make sure that they are only associated with interfaces */

              if (annotatedElement.getKind() != ElementKind.INTERFACE)
                throw new ElementIllegalArgumentException(annotatedElement, Messages.PROXYGENERATOR_ONLYINTERFACES,
                  pair.getKey().getSimpleName());

              TypeElement typeElement = (TypeElement) annotatedElement;

              for (Generator generator : pair.getValue()) {

                /* Process */

                generator.process(typeElement, processingEnv, pRoundEnv);
              }

            }
            catch (Exception ex) {
              sLogger.error("Error on: {}", annotatedElement.getSimpleName());
              throw new ElementIllegalArgumentException(annotatedElement, ex, Messages.PROXYGENERATOR_ERROR,
                ProxyGen.class.getSimpleName());
            }
          }
          catch (ElementIllegalArgumentException ex) {
            sLogger.error("", ex);
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, ex.getLocalizedMessage(), ex.getElement());
          }
        }
      }
    }
    catch (Throwable ex) {
      sLogger.error("RE: ", ex);
      throw ex;
    }
    return true;
  }
}

package com.diamondq.common.vertx.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.lang.annotation.Annotation;

public interface Generator {

  /**
   * The annotation that needs to be present for this generator to work
   *
   * @return the annotation
   */
  Class<? extends Annotation> getAnnotation();

  void process(TypeElement pTypeElement, ProcessingEnvironment pProcessingEnv, RoundEnvironment pRoundEnv)
    throws IOException;

}

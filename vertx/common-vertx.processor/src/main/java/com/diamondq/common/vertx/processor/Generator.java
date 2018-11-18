package com.diamondq.common.vertx.processor;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface Generator {

  /**
   * The annotation that needs to be present for this generator to work
   * 
   * @return the annotation
   */
  public Class<@NonNull ? extends @NonNull Annotation> getAnnotation();

  public void process(TypeElement pTypeElement, ProcessingEnvironment pProcessingEnv, RoundEnvironment pRoundEnv)
    throws IOException;

}

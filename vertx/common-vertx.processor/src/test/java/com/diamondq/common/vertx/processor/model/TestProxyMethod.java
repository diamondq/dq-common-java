package com.diamondq.common.vertx.processor.model;

import java.util.Collections;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class TestProxyMethod {
  public @Rule ExtendedCompilationRule rule = new ExtendedCompilationRule();

  private Elements                     elements;

  private ProcessingEnvironment        processingEnv;

  @SuppressWarnings("null")
  public TestProxyMethod() {
  }

  @Before
  public void setup() {
    elements = rule.getElements();
    processingEnv = rule.getProcessingEnv();
  }

  @Test
  public void validMethods() {
    TypeElement typeElement = elements.getTypeElement(SmallSample.class.getCanonicalName());
    Assert.assertNotNull(typeElement);
    for (Element element : typeElement.getEnclosedElements()) {
      if (element.getKind() == ElementKind.METHOD) {
        ExecutableElement ee = (ExecutableElement) element;
        ProxyMethod method =
          new ProxyMethod(ee, BaseParam.constructor(), BaseType.constructor(), processingEnv, Collections.emptyMap());
        Assert.assertNotNull(method);
      }
    }
  }

}

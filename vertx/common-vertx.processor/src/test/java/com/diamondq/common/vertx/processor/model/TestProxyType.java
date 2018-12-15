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

public class TestProxyType {

  public @Rule ExtendedCompilationRule rule = new ExtendedCompilationRule();

  private Elements                     elements;

  private ProcessingEnvironment        processingEnv;

  @SuppressWarnings("null")
  public TestProxyType() {
  }

  @Before
  public void setup() {
    elements = rule.getElements();
    processingEnv = rule.getProcessingEnv();
  }

  @Test
  public void validTypes() {
    TypeElement typeElement = elements.getTypeElement(SampleWithDifferentReturnTypes.class.getCanonicalName());
    Assert.assertNotNull(typeElement);
    for (Element element : typeElement.getEnclosedElements()) {
      if (element.getKind() == ElementKind.METHOD) {
        ExecutableElement ee = (ExecutableElement) element;
        ValueString valueString = ee.getAnnotation(ValueString.class);
        Assert.assertNotNull("Missing valueString on " + ee.toString(), valueString);

        try {
          BaseType type = new BaseType(ee.getReturnType(), BaseType.constructor(), processingEnv, Collections.emptyMap());
          Assert.assertNotNull(type);
          String value = type.getTypeName().toString();
          Assert.assertEquals("Return type does not match for " + ee.toString(), valueString.value(), value);
        }
        catch (Exception ex) {
          Assert.fail("Unable to process return type for " + ee.toString());
          if (ex instanceof RuntimeException)
            throw (RuntimeException) ex;
          throw new RuntimeException(ex);
        }
      }
    }
  }
}

package com.diamondq.common.bpm.camunda;

import org.camunda.bpm.impl.juel.ExpressionNode;
import org.camunda.bpm.impl.juel.TreeValueExpression;

import java.lang.reflect.Field;
import java.util.Objects;

public class ExpressionUtil {

  private static final Field nodeField = getNodeField();

  private static Field getNodeField() {
    try {
      Field field = TreeValueExpression.class.getDeclaredField("node");
      field.setAccessible(true);
      return field;
    }
    catch (NoSuchFieldException | SecurityException ex) {
      throw new IllegalStateException(ex);
    }
  }

  public static ExpressionNode getExpressionNode(TreeValueExpression pExpression) {
    try {
      return (ExpressionNode) Objects.requireNonNull(nodeField.get(pExpression));
    }
    catch (IllegalArgumentException | IllegalAccessException ex) {
      throw new IllegalStateException(ex);
    }
  }
}

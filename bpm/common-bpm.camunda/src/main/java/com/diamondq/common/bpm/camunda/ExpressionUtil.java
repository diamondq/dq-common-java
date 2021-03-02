package com.diamondq.common.bpm.camunda;

import java.lang.reflect.Field;
import java.util.Objects;

import de.odysseus.el.TreeValueExpression;
import de.odysseus.el.tree.ExpressionNode;

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
      ExpressionNode node = (ExpressionNode) Objects.requireNonNull(nodeField.get(pExpression));
      return node;
    }
    catch (IllegalArgumentException | IllegalAccessException ex) {
      throw new IllegalStateException(ex);
    }
  }
}

package com.diamondq.common.i18n.tests;

import com.diamondq.common.i18n.LayeredResourceBundle;
import org.junit.Test;

import java.util.Collections;
import java.util.ListResourceBundle;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class TestLayeredResourceBundle {

  private static ResourceBundle bundleOf(Object[][] pContents) {
    return new ListResourceBundle() {
      @Override
      protected Object[][] getContents() {
        return pContents;
      }
    };
  }

  private final ResourceBundle mParent = bundleOf(new Object[][] {{"a", "parent a"}, {"b", "parent b"}});

  @Test
  public void ownMessagesWinOverTheParent() {
    ResourceBundle bundle = new LayeredResourceBundle(Map.of("a", "own a"), mParent);

    assertEquals("own a", bundle.getString("a"));
  }

  @Test
  public void parentIsUsedForOtherMessages() {
    ResourceBundle bundle = new LayeredResourceBundle(Map.of("a", "own a"), mParent);

    assertEquals("parent b", bundle.getString("b"));
  }

  @Test
  public void unknownMessageIsMissing() {
    ResourceBundle bundle = new LayeredResourceBundle(Map.of("a", "own a"), mParent);

    assertThrows(MissingResourceException.class, () -> bundle.getString("c"));
  }

  @Test
  public void keysIncludeBothOwnAndParentMessages() {
    ResourceBundle bundle = new LayeredResourceBundle(Map.of("a", "own a", "c", "own c"), mParent);

    assertEquals(Set.of("a", "b", "c"), Set.copyOf(Collections.list(bundle.getKeys())));
    assertEquals(Set.of("a", "b", "c"), bundle.keySet());
  }

  @Test
  public void canBeCreatedFromAnotherBundle() {
    ResourceBundle overrides = bundleOf(new Object[][] {{"b", "override b"}});
    ResourceBundle bundle = new LayeredResourceBundle(overrides, mParent);

    assertEquals("parent a", bundle.getString("a"));
    assertEquals("override b", bundle.getString("b"));
  }

  @Test
  public void layersCanBeStacked() {
    ResourceBundle lower = new LayeredResourceBundle(Map.of("a", "lower a"), mParent);
    ResourceBundle upper = new LayeredResourceBundle(Map.of("b", "upper b"), lower);

    assertEquals("lower a", upper.getString("a"));
    assertEquals("upper b", upper.getString("b"));
  }
}

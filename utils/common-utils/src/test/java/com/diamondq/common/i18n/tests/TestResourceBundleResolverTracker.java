package com.diamondq.common.i18n.tests;

import com.diamondq.common.i18n.I18N;
import com.diamondq.common.i18n.ResourceBundleResolver;
import com.diamondq.common.i18n.impl.ResourceBundleResolverTracker;
import org.junit.Test;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;

public class TestResourceBundleResolverTracker {

  private static final String BASENAME = "com.diamondq.common.i18n.tests.ResolverMessages";

  @Test
  public void boundResolverIsUsedUntilUnbound() {
    ResourceBundle override = new ListResourceBundle() {
      @Override
      protected Object[][] getContents() {
        return new Object[][] {{"greeting", "Overridden"}};
      }
    };
    ResourceBundleResolver resolver = (pBasename, pLocale, pClassLoader, pCurrent) -> override;
    ResourceBundleResolverTracker tracker = new ResourceBundleResolverTracker();

    tracker.addResolver(resolver);
    try {
      assertEquals("Overridden",
        I18N.getBundle(TestResourceBundleResolver.ResolverMessages.class, BASENAME, Locale.US).getString("greeting")
      );
    }
    finally {
      tracker.removeResolver(resolver);
    }

    assertEquals("Hello",
      I18N.getBundle(TestResourceBundleResolver.ResolverMessages.class, BASENAME, Locale.US).getString("greeting")
    );
  }
}

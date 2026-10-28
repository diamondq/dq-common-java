package com.diamondq.common.i18n.tests;

import com.diamondq.common.i18n.ResourceBundleResolver;
import com.diamondq.common.i18n.I18N;
import com.diamondq.common.i18n.MessagesEnum;
import org.jspecify.annotations.Nullable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class TestResourceBundleResolver {

  private static final String BASENAME = "com.diamondq.common.i18n.tests.ResolverMessages";

  /**
   * A message enum that looks up its bundle through I18N.getBundle, the way newly generated enums are expected to. The
   * bundles are the ResolverMessages*.properties files in the test resources
   */
  public enum ResolverMessages implements MessagesEnum {
    GREETING("greeting"), ONLY_DEFAULT("onlyDefault");

    private final String mCode;

    ResolverMessages(String pCode) {
      mCode = pCode;
    }

    @Override
    public String getCode() {
      return mCode;
    }

    @Override
    public ResourceBundle getBundle(Locale pLocale) {
      return I18N.getBundle(ResolverMessages.class, BASENAME, pLocale);
    }
  }

  private static ResourceBundle bundleOf(String pGreeting) {
    return new ListResourceBundle() {
      @Override
      protected Object[][] getContents() {
        return new Object[][] {{"greeting", pGreeting}};
      }
    };
  }

  private final List<ResourceBundleResolver> mAdded = new ArrayList<>();

  private Locale mOriginalDefault;

  private ResourceBundleResolver add(ResourceBundleResolver pResolver) {
    I18N.addResourceBundleResolver(pResolver);
    mAdded.add(pResolver);
    return pResolver;
  }

  @Before
  public void setup() {
    mOriginalDefault = Locale.getDefault();
    Locale.setDefault(Locale.US);
  }

  @After
  public void teardown() {
    for (ResourceBundleResolver resolver : mAdded)
      I18N.removeResourceBundleResolver(resolver);
    Locale.setDefault(mOriginalDefault);
  }

  @Test
  public void standardLookupIsUsedWhenThereAreNoResolvers() {
    assertEquals("Hello", ResolverMessages.GREETING.with().format(Locale.US));
    assertEquals("Hallo", ResolverMessages.GREETING.with().format(Locale.GERMANY));
    assertEquals("Default only", ResolverMessages.ONLY_DEFAULT.with().format(Locale.GERMANY));
  }

  @Test
  public void missingBundleStillThrows() {
    assertThrows(MissingResourceException.class,
      () -> I18N.getBundle(ResolverMessages.class, "com.diamondq.common.i18n.tests.DoesNotExist", Locale.US));
  }

  @Test
  public void resolverOverridesStandardLookup() {
    add((pBasename, pLocale, pClassLoader) -> bundleOf("Overridden"));

    assertEquals("Overridden", ResolverMessages.GREETING.with().format(Locale.US));
  }

  @Test
  public void resolverReturningNullFallsThrough() {
    add((pBasename, pLocale, pClassLoader) -> null);

    assertEquals("Hallo", ResolverMessages.GREETING.with().format(Locale.GERMANY));
  }

  @Test
  public void firstResolverWithAnAnswerWins() {
    add((pBasename, pLocale, pClassLoader) -> null);
    add((pBasename, pLocale, pClassLoader) -> bundleOf("Second"));
    add((pBasename, pLocale, pClassLoader) -> bundleOf("Third"));

    assertEquals("Second", ResolverMessages.GREETING.with().format(Locale.US));
  }

  @Test
  public void removedResolverIsNoLongerUsed() {
    ResourceBundleResolver resolver = add((pBasename, pLocale, pClassLoader) -> bundleOf("Overridden"));

    assertTrue(I18N.removeResourceBundleResolver(resolver));
    assertFalse(I18N.removeResourceBundleResolver(resolver));
    assertEquals("Hello", ResolverMessages.GREETING.with().format(Locale.US));
  }

  @Test
  public void resolverIsToldWhatIsBeingLookedUp() {
    AtomicReference<@Nullable String> basename = new AtomicReference<>();
    AtomicReference<@Nullable Locale> locale = new AtomicReference<>();
    AtomicReference<@Nullable ClassLoader> classLoader = new AtomicReference<>();
    add((pBasename, pLocale, pClassLoader) -> {
      basename.set(pBasename);
      locale.set(pLocale);
      classLoader.set(pClassLoader);
      return null;
    });

    ResolverMessages.GREETING.with().format(Locale.GERMANY);

    assertEquals(BASENAME, basename.get());
    assertEquals(Locale.GERMANY, locale.get());
    assertSame(I18N.getClassLoader(ResolverMessages.class, BASENAME), classLoader.get());
  }
}

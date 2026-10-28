package com.diamondq.common.i18n.tests;

import com.diamondq.common.i18n.I18N;
import com.diamondq.common.i18n.LayeredResourceBundle;
import com.diamondq.common.i18n.MessagesEnum;
import com.diamondq.common.i18n.ResourceBundleResolver;
import org.jspecify.annotations.Nullable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.ListResourceBundle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class TestResourceBundleResolver {

  private static final String BASENAME = "com.diamondq.common.i18n.tests.ResolverMessages";

  private static final String MISSING_BASENAME = "com.diamondq.common.i18n.tests.DoesNotExist";

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

  private static ResourceBundle lookup() {
    return I18N.getBundle(ResolverMessages.class, BASENAME, Locale.US);
  }

  private static ResourceBundle layer(Map<String, String> pOverrides, @Nullable ResourceBundle pCurrent) {
    return new LayeredResourceBundle(pOverrides, Objects.requireNonNull(pCurrent));
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
      () -> I18N.getBundle(ResolverMessages.class, MISSING_BASENAME, Locale.US));
  }

  @Test
  public void missingBundleStillThrowsWhenResolversDoNotSupplyOne() {
    add((pBasename, pLocale, pClassLoader, pCurrent) -> null);

    assertThrows(MissingResourceException.class,
      () -> I18N.getBundle(ResolverMessages.class, MISSING_BASENAME, Locale.US));
  }

  @Test
  public void resolverCanSupplyABundleThatDoesNotExist() {
    add((pBasename, pLocale, pClassLoader, pCurrent) -> {
      if (!MISSING_BASENAME.equals(pBasename)) return null;
      assertNull(pCurrent);
      return bundleOf("Supplied");
    });

    assertEquals("Supplied",
      I18N.getBundle(ResolverMessages.class, MISSING_BASENAME, Locale.US).getString("greeting")
    );
  }

  @Test
  public void resolverReplacesStandardLookup() {
    add((pBasename, pLocale, pClassLoader, pCurrent) -> bundleOf("Overridden"));

    assertEquals("Overridden", ResolverMessages.GREETING.with().format(Locale.US));
  }

  @Test
  public void resolverReturningNullLeavesTheBundleAlone() {
    add((pBasename, pLocale, pClassLoader, pCurrent) -> null);

    assertEquals("Hallo", ResolverMessages.GREETING.with().format(Locale.GERMANY));
  }

  @Test
  public void layeredResolverOverridesSomeMessagesAndFallsBackForTheRest() {
    add((pBasename, pLocale, pClassLoader, pCurrent) -> layer(Map.of("greeting", "Overridden"), pCurrent));

    assertEquals("Overridden", ResolverMessages.GREETING.with().format(Locale.GERMANY));
    assertEquals("Default only", ResolverMessages.ONLY_DEFAULT.with().format(Locale.GERMANY));
  }

  @Test
  public void severalResolversEachOverrideDifferentMessages() {
    add((pBasename, pLocale, pClassLoader, pCurrent) -> layer(Map.of("greeting", "First"), pCurrent));
    add((pBasename, pLocale, pClassLoader, pCurrent) -> layer(Map.of("onlyDefault", "Second"), pCurrent));

    assertEquals("First", ResolverMessages.GREETING.with().format(Locale.US));
    assertEquals("Second", ResolverMessages.ONLY_DEFAULT.with().format(Locale.US));
  }

  @Test
  public void laterResolverWinsWhenBothOverrideTheSameMessage() {
    add((pBasename, pLocale, pClassLoader, pCurrent) -> layer(Map.of("greeting", "First"), pCurrent));
    add((pBasename, pLocale, pClassLoader, pCurrent) -> layer(Map.of("greeting", "Second"), pCurrent));

    assertEquals("Second", ResolverMessages.GREETING.with().format(Locale.US));
  }

  @Test
  public void resolverReceivesTheBundleProducedSoFar() {
    AtomicReference<@Nullable ResourceBundle> firstSaw = new AtomicReference<>();
    AtomicReference<@Nullable ResourceBundle> secondSaw = new AtomicReference<>();
    ResourceBundle firstResult = bundleOf("First");
    add((pBasename, pLocale, pClassLoader, pCurrent) -> {
      firstSaw.set(pCurrent);
      return firstResult;
    });
    add((pBasename, pLocale, pClassLoader, pCurrent) -> {
      secondSaw.set(pCurrent);
      return null;
    });

    lookup();

    ResourceBundle standard = firstSaw.get();
    assertNotNull(standard);
    assertEquals("Hello", standard.getString("greeting"));
    assertSame(firstResult, secondSaw.get());
  }

  @Test
  public void removedResolverIsNoLongerUsed() {
    ResourceBundleResolver resolver = add(
      (pBasename, pLocale, pClassLoader, pCurrent) -> bundleOf("Overridden"));

    assertEquals("Overridden", ResolverMessages.GREETING.with().format(Locale.US));
    assertTrue(I18N.removeResourceBundleResolver(resolver));
    assertFalse(I18N.removeResourceBundleResolver(resolver));
    assertEquals("Hello", ResolverMessages.GREETING.with().format(Locale.US));
  }

  @Test
  public void addedResolverTakesEffectAfterAnEarlierLookup() {
    assertEquals("Hello", ResolverMessages.GREETING.with().format(Locale.US));
    add((pBasename, pLocale, pClassLoader, pCurrent) -> bundleOf("Overridden"));
    assertEquals("Overridden", ResolverMessages.GREETING.with().format(Locale.US));
  }

  @Test
  public void resultIsCachedWhileResolversAreUnchanged() {
    AtomicReference<Integer> calls = new AtomicReference<>(0);
    add((pBasename, pLocale, pClassLoader, pCurrent) -> {
      calls.updateAndGet(c -> c + 1);
      return layer(Map.of("greeting", "Overridden"), pCurrent);
    });

    ResourceBundle first = lookup();
    ResourceBundle second = lookup();

    assertSame(first, second);
    assertEquals(Integer.valueOf(1), calls.get());
  }

  @Test
  public void resolverIsToldWhatIsBeingLookedUp() {
    AtomicReference<@Nullable String> basename = new AtomicReference<>();
    AtomicReference<@Nullable Locale> locale = new AtomicReference<>();
    AtomicReference<@Nullable ClassLoader> classLoader = new AtomicReference<>();
    add((pBasename, pLocale, pClassLoader, pCurrent) -> {
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

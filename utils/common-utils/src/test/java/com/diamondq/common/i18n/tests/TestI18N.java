package com.diamondq.common.i18n.tests;

import com.diamondq.common.i18n.I18N;
import com.diamondq.common.i18n.I18NString;
import com.diamondq.common.i18n.MessagesEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestI18N {

  private static ResourceBundle bundle(Object[][] pContents) {
    return new ListResourceBundle() {
      @Override
      protected Object[][] getContents() {
        return pContents;
      }
    };
  }

  private static final ResourceBundle sEnglish = bundle(new Object[][] { //
    { "greeting", "Hello" }, //
    { "outer", "Outer({0})" }, //
    { "number", "{0,number}" }, //
    { "bad", "{0,bogus}" }, //
    { "same", "English from TestMessages" } });

  private static final ResourceBundle sGerman = bundle(new Object[][] { //
    { "greeting", "Hallo" }, //
    { "outer", "Aussen({0})" }, //
    { "number", "{0,number}" }, //
    { "bad", "{0,bogus}" }, //
    { "same", "Deutsch aus TestMessages" } });

  private static final ResourceBundle sOtherEnglish = bundle(new Object[][] { { "same", "English from OtherMessages" } });

  private static final ResourceBundle sOtherGerman = bundle(new Object[][] { { "same", "Deutsch aus OtherMessages" } });

  /**
   * Mimics a real bundle lookup: German-language locales get the German bundle, everything else falls back to English
   */
  private static ResourceBundle pick(Locale pLocale, ResourceBundle pEnglish, ResourceBundle pGerman) {
    return "de".equals(pLocale.getLanguage()) ? pGerman : pEnglish;
  }

  public enum TestMessages implements MessagesEnum {
    GREETING("greeting"),
    OUTER("outer"),
    NUMBER("number"),
    BAD("bad"),
    MISSING("missing"),
    SAME("same");

    private final String mCode;

    TestMessages(String pCode) {
      mCode = pCode;
    }

    @Override
    public String getCode() {
      return mCode;
    }

    @Override
    public ResourceBundle getBundle(Locale pLocale) {
      return pick(pLocale, sEnglish, sGerman);
    }
  }

  /**
   * Uses the same code as TestMessages.SAME but a different bundle
   */
  public enum OtherMessages implements MessagesEnum {
    SAME("same");

    private final String mCode;

    OtherMessages(String pCode) {
      mCode = pCode;
    }

    @Override
    public String getCode() {
      return mCode;
    }

    @Override
    public ResourceBundle getBundle(Locale pLocale) {
      return pick(pLocale, sOtherEnglish, sOtherGerman);
    }
  }

  private Locale mOriginalDefault;

  @Before
  public void setup() {
    mOriginalDefault = Locale.getDefault();
    Locale.setDefault(Locale.US);
  }

  @After
  public void teardown() {
    Locale.setDefault(mOriginalDefault);
  }

  /* MessageFormat locale */

  @Test
  public void getFormatUsesRequestedLocaleForSubformats() {
    assertEquals("1,234,567.5", I18N.getFormat(Locale.US, TestMessages.NUMBER, 1234567.5));
    assertEquals("1.234.567,5", I18N.getFormat(Locale.GERMANY, TestMessages.NUMBER, 1234567.5));
  }

  @Test
  public void getAllFormatsUsesEachLocaleForSubformats() {
    Map<Locale, String> all = I18N.getAllFormats(TestMessages.NUMBER, 1234567.5);
    assertEquals("1,234,567.5", all.get(Locale.US));
    assertEquals("1.234.567,5", all.get(Locale.GERMANY));
  }

  /* getAllFormats cache key */

  @Test
  public void getAllFormatsDoesNotShareCacheBetweenEnumsWithSameCode() {
    Map<Locale, String> first = I18N.getAllFormats(TestMessages.SAME);
    Map<Locale, String> second = I18N.getAllFormats(OtherMessages.SAME);

    assertEquals("English from TestMessages", first.get(Locale.US));
    assertEquals("Deutsch aus TestMessages", first.get(Locale.GERMANY));
    assertEquals("English from OtherMessages", second.get(Locale.US));
    assertEquals("Deutsch aus OtherMessages", second.get(Locale.GERMANY));
  }

  @Test
  public void getAllFormatsOrderDoesNotMatter() {
    /* Same as above but with the enums used in the opposite order, in case the first one cached wins */
    Map<Locale, String> second = I18N.getAllFormats(OtherMessages.SAME);
    Map<Locale, String> first = I18N.getAllFormats(TestMessages.SAME);

    assertEquals("English from OtherMessages", second.get(Locale.US));
    assertEquals("English from TestMessages", first.get(Locale.US));
  }

  /* toString never throws */

  @Test
  public void toStringFormatsNormally() {
    assertEquals("Hello", TestMessages.GREETING.with().toString());
  }

  @Test
  public void toStringFallsBackWhenKeyIsMissing() {
    assertEquals("missing[a, 2]", TestMessages.MISSING.with("a", 2).toString());
  }

  @Test
  public void toStringFallsBackWhenPatternIsInvalid() {
    assertEquals("bad[x]", TestMessages.BAD.with("x").toString());
  }

  /* Nested I18NString */

  @Test
  public void nestedI18NStringUsesRequestedLocale() {
    I18NString inner = TestMessages.GREETING.with();
    I18NString outer = TestMessages.OUTER.with(inner);

    assertEquals("Outer(Hello)", I18N.getFormat(Locale.US, TestMessages.OUTER, inner));
    assertEquals("Aussen(Hallo)", I18N.getFormat(Locale.GERMANY, TestMessages.OUTER, inner));
    assertEquals("Aussen(Hallo)", outer.format(Locale.GERMANY));
  }

  @Test
  public void nestedI18NStringIsLocalizedInGetAllFormats() {
    I18NString inner = TestMessages.GREETING.with();
    Map<Locale, String> all = I18N.getAllFormats(TestMessages.OUTER, inner);

    assertEquals("Outer(Hello)", all.get(Locale.US));
    assertEquals("Aussen(Hallo)", all.get(Locale.GERMANY));
  }

  @Test
  public void nestedI18NStringDoesNotMutateCallerArguments() {
    I18NString inner = TestMessages.GREETING.with();
    Object[] args = new Object[] { inner };

    I18N.getFormat(Locale.GERMANY, TestMessages.OUTER, args);

    assertEquals(inner, args[0]);
  }

  @Test
  public void formatDiffersFromDefaultLocaleToString() {
    I18NString str = TestMessages.GREETING.with();
    assertEquals("Hello", str.toString());
    assertNotEquals(str.toString(), str.format(Locale.GERMANY));
  }
}

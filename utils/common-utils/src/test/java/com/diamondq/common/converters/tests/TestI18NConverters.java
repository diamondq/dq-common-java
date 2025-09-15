package com.diamondq.common.converters.tests;

import com.diamondq.common.UtilMessages;
import com.diamondq.common.converters.ConverterManager;
import com.diamondq.common.converters.impl.ConverterManagerImpl;
import com.diamondq.common.converters.impl.I18NStringToMapConverter;
import com.diamondq.common.converters.impl.MapToI18NStringConverter;
import com.diamondq.common.i18n.I18NString;
import com.diamondq.common.types.Types;
import org.jspecify.annotations.Nullable;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestI18NConverters {
  @Test
  public void simpleConverter() {
    ConverterManager converterManager = new ConverterManagerImpl(Collections.emptyList());
    converterManager.addConverter(new I18NStringToMapConverter());
    converterManager.addConverter(new MapToI18NStringConverter());
    I18NString str = UtilMessages.CONVERTERMANAGER_NO_MATCH.with("A", 2);
    String formattedString = str.toString();
    Map<String, @Nullable Object> result = converterManager.convert(str, Types.MAP_OF_STRING_TO_NULLABLE_OBJECT);
    assertNotNull(result);

    I18NString unconverted = converterManager.convert(result, Types.MAP_OF_STRING_TO_NULLABLE_OBJECT, Types.I18NSTRING);
    String unconvertedFormatted = unconverted.toString();

    assertEquals(formattedString, unconvertedFormatted);
  }

}

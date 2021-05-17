package com.diamondq.common.converters.tests;

import static org.junit.Assert.assertEquals;

import com.diamondq.common.converters.ConverterManager;
import com.diamondq.common.converters.impl.CommonConverters;
import com.diamondq.common.converters.impl.ConverterManagerImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;

import org.junit.Test;
import org.osgi.framework.InvalidSyntaxException;

public class TestBasicConverter {

  @Test
  public void simpleConverter() throws IOException, InvalidSyntaxException {
    ConverterManager converterManager = new ConverterManagerImpl(Collections.emptyList());
    converterManager.addConverter(new CommonConverters().getStringToPathConverter());
    Path result = converterManager.convert(File.pathSeparator, Path.class);
    assertEquals(File.pathSeparator, result.toString());
  }

}

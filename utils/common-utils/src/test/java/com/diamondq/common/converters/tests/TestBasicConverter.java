package com.diamondq.common.converters.tests;

import static org.junit.Assert.assertEquals;

import com.diamondq.common.converters.AbstractConverter;
import com.diamondq.common.converters.ConverterManager;
import com.diamondq.common.converters.impl.CommonConverters;
import com.diamondq.common.converters.impl.ConverterManagerImpl;
import com.diamondq.common.types.Types;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

  private static class ListStringConverter extends AbstractConverter<List<String>, String> {

    public ListStringConverter() {
      super(Types.LIST_STRING, String.class);
    }

    @Override
    public String convert(List<String> pInput) {
      return String.join(",", pInput);
    };
  }

  private static class ListLongConverter extends AbstractConverter<List<Long>, String> {

    public ListLongConverter() {
      super(Types.LIST_LONG, String.class);
    }

    @Override
    public String convert(List<Long> pInput) {
      return String.join(",", pInput.stream().map(String::valueOf).collect(Collectors.toList()));
    };
  }

  @Test
  public void genericConverter() throws IOException, InvalidSyntaxException {
    ConverterManager converterManager = new ConverterManagerImpl(Collections.emptyList());
    converterManager.addConverter(new ListStringConverter());
    converterManager.addConverter(new ListLongConverter());
    List<String> ls = new ArrayList<>();
    ls.add("a");
    ls.add("b");
    ls.add("c");
    List<Long> ll = new ArrayList<>();
    ll.add(1L);
    ll.add(2L);
    ll.add(3L);
    String str1 = converterManager.convert(ls, Types.LIST_STRING, String.class);
    assertEquals("a,b,c", str1);
    String str2 = converterManager.convert(ll, Types.LIST_LONG, String.class);
    assertEquals("1,2,3", str2);
  }

}

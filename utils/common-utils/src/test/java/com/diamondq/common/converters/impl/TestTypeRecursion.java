package com.diamondq.common.converters.impl;

import static org.junit.Assert.assertArrayEquals;

import com.diamondq.common.TypeReference;
import com.diamondq.common.types.Types;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Before;
import org.junit.Test;

public class TestTypeRecursion {

  @SuppressWarnings("null")
  private ConverterManagerImpl converterManager;

  @Before
  public void setup() {
    converterManager = new ConverterManagerImpl(Collections.emptyList());
  }

  @SuppressWarnings("null")
  private @NonNull String[] setToArray(Set<Type> pSet) {
    return pSet.stream().map((type) -> type.getTypeName()).toArray(String[]::new);
  }

  @SuppressWarnings("unused")
  private void printSet(Set<Type> pSet) {
    System.out.println(String.join(",", setToArray(pSet)));
  }

  @Test
  public void testSimple() {
    LinkedHashSet<Type> set = new LinkedHashSet<>();
    converterManager.calculateTypes(Types.LIST_OF_STRING.getType(), set);
    assertArrayEquals(new String[] {"java.util.List<java.lang.String>", "java.util.Collection<java.lang.String>",
        "java.lang.Iterable<java.lang.String>", "java.util.List<?>", "java.util.Collection<?>",
        "java.lang.Iterable<?>"},
      setToArray(set));

    set = new LinkedHashSet<>();
    converterManager.calculateTypes(Types.MAP_OF_STRING_TO_STRING.getType(), set);
    assertArrayEquals(new String[] {"java.util.Map<java.lang.String, java.lang.String>", "java.util.Map<?, ?>"},
      setToArray(set));
  }

  @Test
  public void testFixedInterface() {
    LinkedHashSet<Type> set = new LinkedHashSet<>();
    converterManager.calculateTypes(new TypeReference<StringMap>() {
    }.getType(), set);
    assertArrayEquals(new String[] {"com.diamondq.common.converters.impl.StringMap",
        "java.util.Map<java.lang.String, java.lang.String>", "java.util.Map<?, ?>"},
      setToArray(set));
  }

  @Test
  public <O> void testUnboundedInterface() {
    LinkedHashSet<Type> set = new LinkedHashSet<>();
    converterManager.calculateTypes(new TypeReference<WildcardMap<String, O>>() {
    }.getType(), set);
    printSet(set);
    assertArrayEquals(new String[] {"com.diamondq.common.converters.impl.WildcardMap<java.lang.String, ?>",
        "java.util.Map<java.lang.String, ?>", "com.diamondq.common.converters.impl.WildcardMap<?, ?>",
        "java.util.Map<?, ?>"},
      setToArray(set));
  }

  @Test
  public <O> void testNonGeneric() {
    LinkedHashSet<Type> set = new LinkedHashSet<>();
    converterManager.calculateTypes(List.class, set);
    assertArrayEquals(new String[] {"java.util.List<?>", "java.util.Collection<?>", "java.lang.Iterable<?>"},
      setToArray(set));
  }
}

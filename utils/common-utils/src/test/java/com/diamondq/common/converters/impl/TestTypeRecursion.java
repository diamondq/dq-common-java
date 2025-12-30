package com.diamondq.common.converters.impl;

import com.diamondq.common.TypeReference;
import com.diamondq.common.types.Types;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;

public class TestTypeRecursion {

  @SuppressWarnings("null") private ConverterManagerImpl converterManager;

  @Before
  public void setup() {
    converterManager = new ConverterManagerImpl(Collections.emptyList());
  }

  @SuppressWarnings("null")
  private String[] setToArray(Set<Type> pSet) {
    return pSet.stream().map(Type::getTypeName).sorted().toArray(String[]::new);
  }

  @SuppressWarnings("unused")
  private void printSet(Set<Type> pSet) {
    System.out.println(String.join(",", setToArray(pSet)));
  }

  @Test
  public void testSimple() {
    LinkedHashSet<Type> set = new LinkedHashSet<>();
    converterManager.calculateTypes(Types.LIST_OF_STRING.getType(), set);
    assertArrayEquals(new String[] { "java.lang.Iterable<?>", "java.lang.Iterable<java.lang.String>", "java.util.Collection<?>", "java.util.Collection<java.lang.String>", "java.util.List<?>", "java.util.List<java.lang.String>", "java.util.SequencedCollection<?>", "java.util.SequencedCollection<java.lang.String>" },
      setToArray(set)
    );

    set = new LinkedHashSet<>();
    converterManager.calculateTypes(Types.MAP_OF_STRING_TO_STRING.getType(), set);
    assertArrayEquals(new String[] { "java.util.Map<?, ?>", "java.util.Map<java.lang.String, java.lang.String>" },
      setToArray(set)
    );
  }

  @Test
  public void testFixedInterface() {
    LinkedHashSet<Type> set = new LinkedHashSet<>();
    converterManager.calculateTypes(new TypeReference<StringMap>() {
      }.getType(), set
    );
    assertArrayEquals(new String[] { "com.diamondq.common.converters.impl.StringMap", "java.util.Map<?, ?>", "java.util.Map<java.lang.String, java.lang.String>" },
      setToArray(set)
    );
  }

  @Test
  public <O> void testUnboundedInterface() {
    LinkedHashSet<Type> set = new LinkedHashSet<>();
    converterManager.calculateTypes(new TypeReference<WildcardMap<String, O>>() {
      }.getType(), set
    );
    printSet(set);
    assertArrayEquals(new String[] { "com.diamondq.common.converters.impl.WildcardMap<?, ?>", "com.diamondq.common.converters.impl.WildcardMap<java.lang.String, ?>", "java.util.Map<?, ?>", "java.util.Map<java.lang.String, ?>" },
      setToArray(set)
    );
  }

  @Test
  public <O> void testNonGeneric() {
    LinkedHashSet<Type> set = new LinkedHashSet<>();
    converterManager.calculateTypes(List.class, set);
    System.err.println("Value: " + set);
    assertArrayEquals(new String[] { "java.lang.Iterable<?>", "java.util.Collection<?>", "java.util.List<?>", "java.util.SequencedCollection<?>" },
      setToArray(set)
    );
  }
}

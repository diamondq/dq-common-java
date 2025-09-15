package com.diamondq.common;

import org.jspecify.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * This generic abstract class is used for obtaining full generics type information by sub-classing. Class is based on
 * ideas from <a href="http://gafter.blogspot.com/2006/12/super-type-tokens.html"
 * >http://gafter.blogspot.com/2006/12/super-type-tokens.html</a>, Additional idea (from a suggestion made in comments
 * of the article) is to require bogus implementation of <code>Comparable</code> (any such generic interface would do,
 * as long as it forces a method with generic type to be implemented). to ensure that a Type argument is indeed given.
 * <p>
 * Usage is by sub-classing: here is one way to instantiate reference to generic type <code>List&lt;Integer&gt;</code>:
 * <p>
 * Adapted from Jackson library which is under Apache 2.0 license.
 *
 * <pre>
 * TypeReference ref = new TypeReference&lt;List&lt;Integer&gt;&gt;() {
 * };
 * </pre>
 * <p>
 * which can be passed to methods that accept TypeReference
 *
 * @param <T> the type to capture
 */
public abstract class TypeReference<T extends @Nullable Object> implements Comparable<TypeReference<T>> {
  protected final Type mType;

  protected TypeReference(Type pType) {
    mType = pType;
  }

  protected TypeReference() {
    Type superClass = getClass().getGenericSuperclass();
    if (superClass instanceof Class<?>) { // sanity check, should never happen
      throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
    }
    if (superClass == null) {
      throw new IllegalArgumentException("Internal error: No generic superclass");
    }
    /*
     * 22-Dec-2008, tatu: Not sure if this case is safe -- I suspect it is possible to make it fail? But let's deal with
     * specific case when we know an actual use case, and thereby suitable workarounds for valid case(s) and/or error to
     * throw on invalid one(s).
     */
    mType = ((ParameterizedType) superClass).getActualTypeArguments()[0];
  }

  public Type getType() {
    return mType;
  }

  private static class SimpleTypeReference<C> extends TypeReference<C> {
    public SimpleTypeReference(Class<C> pClass) {
      super(pClass);
    }
  }

  public static <C extends @Nullable Object> TypeReference<C> of(Class<C> pClass) {
    return new SimpleTypeReference<>(pClass);
  }

  /**
   * The only reason we define this method (and require implementation of <code>Comparable</code>) is to prevent
   * constructing a reference without type information.
   */
  @Override
  public int compareTo(TypeReference<T> o) {
    return 0;
  }
  // just need an implementation, not a good one... hence ^^^
}

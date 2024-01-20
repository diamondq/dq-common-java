package com.diamondq.common.storage.kv.impl;

import com.diamondq.common.storage.kv.IObjectWithId;
import com.diamondq.common.storage.kv.IObjectWithIdAndRev;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

/**
 * Helper for Primitive Wrappers
 */
public class PrimitiveWrappers {

  /**
   * If the provided class is a primitive, then returns a wrapper around that primitive to make it persistable
   *
   * @param pClass
   * @param pMustHaveRevision true if the wrapper (or main object) must support both id and revision
   * @return the wrapper class that should be used or null if this is not a primitive
   */
  public static <O> @Nullable Class<?> getIfPrimitive(Class<O> pClass, boolean pMustHaveRevision) {
    assert (pClass != null);

    /* If this is an object with an id and revision, then it's definitely not a primitive */

    if (pClass.isAssignableFrom(IObjectWithIdAndRev.class)) return null;

    /* If we don't need the revision, but it is an object with an id, then it's definitely not a primitive */

    if ((pMustHaveRevision == false) && (pClass.isAssignableFrom(IObjectWithId.class))) return null;

    /* We need to create a wrapper. Determine the best possible wrapper, based on the class */

    if (pMustHaveRevision == true) {
      if (pClass.isAssignableFrom(IObjectWithId.class)) return RevisionOnlyWrapper.class;
      else return IdAndRevisionWrapper.class;
    } else {
      return IdWrapper.class;
    }
  }

  /**
   * Unwraps a wrapper
   *
   * @param pObj the wrapper
   * @param pClass the actual class
   * @return the result
   */
  @SuppressWarnings("unchecked")
  public static <O> @Nullable O unwrap(@Nullable Object pObj, Class<O> pClass) {
    if (pObj == null) {

      /* If it's a pure primitive, then we need to return the 'default' value */

      if (pClass == Boolean.TYPE) return (O) Boolean.FALSE;
      else if (pClass == Integer.TYPE) return (O) Integer.valueOf(0);
      else if (pClass == Short.TYPE) return (O) Short.valueOf((short) 0);
      else if (pClass == Long.TYPE) return (O) Long.valueOf(0L);
      else if (pClass == Float.TYPE) return (O) Float.valueOf(0.0f);
      else if (pClass == Double.TYPE) return (O) Double.valueOf(0.0d);
      else return null;
    }

    if (pClass.isInstance(pObj)) return (O) pObj;

    if (pObj instanceof IdAndRevisionWrapper<?>) return ((IdAndRevisionWrapper<O>) pObj).getData();

    if (pObj instanceof IdWrapper<?>) return ((IdWrapper<O>) pObj).getData();

    if (pObj instanceof RevisionOnlyWrapper<?>) return (O) ((RevisionOnlyWrapper<?>) pObj).getData();

    throw new IllegalArgumentException(
      "The object " + pObj.getClass().getName() + " cannot be unwrapped to " + pClass.getName());
  }

  /**
   * Wraps an object
   *
   * @param pObj the object
   * @param pPrimitiveWrapperClass the wrapper class to use
   * @param pKey the key
   * @param pRevision the revision
   * @return the wrapped object (or the object itself if wrapping isn't necessary)
   */
  @SuppressWarnings("unchecked")
  public static <O> @Nullable Object wrap(@Nullable O pObj, Class<?> pPrimitiveWrapperClass, String pKey,
    @Nullable String pRevision) {
    try {
      Object obj = pPrimitiveWrapperClass.getDeclaredConstructor().newInstance();
      if (IObjectWithId.class.isAssignableFrom(pPrimitiveWrapperClass)) {
        obj = ((IObjectWithId<O>) obj).setObjectId(pKey);
        if (obj == null) throw new IllegalStateException();
      }
      if ((IObjectWithIdAndRev.class.isAssignableFrom(pPrimitiveWrapperClass)) && (pRevision != null)) {
        obj = ((IObjectWithIdAndRev<O>) obj).setObjectRevision(pRevision);
        if (obj == null) throw new IllegalStateException();
      }

      /* Now set the data */

      if (pObj == null) return null;

      if (RevisionOnlyWrapper.class.isAssignableFrom(pPrimitiveWrapperClass))
        obj = ((RevisionOnlyWrapper<O>) obj).setData(pObj);
      else if (IdAndRevisionWrapper.class.isAssignableFrom(pPrimitiveWrapperClass))
        obj = ((IdAndRevisionWrapper<O>) obj).setData(pObj);
      else if (IdWrapper.class.isAssignableFrom(pPrimitiveWrapperClass)) obj = ((IdWrapper<O>) obj).setData(pObj);
      return obj;
    }
    catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }

}

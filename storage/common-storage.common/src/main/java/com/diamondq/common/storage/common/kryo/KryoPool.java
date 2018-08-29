package com.diamondq.common.storage.common.kryo;

import com.esotericsoftware.kryo.Kryo;

import java.util.LinkedList;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * CDI helper for managing the Kryo Pool
 */
@ApplicationScoped
public class KryoPool {

  private final LinkedList<@NonNull Kryo> mKryoPool;

  /**
   * Default constructor
   */
  @Inject
  public KryoPool() {
    mKryoPool = new LinkedList<>();
  }

  /**
   * Gets a value from the pool
   * 
   * @return the pool
   */
  public Kryo getFromPool() {
    synchronized (this) {
      if (mKryoPool.isEmpty() == false)
        return mKryoPool.remove();
      return new Kryo();
    }
  }

  /**
   * Returns a value to the pool
   * 
   * @param pKryo the value to return
   */
  public void returnToPool(Kryo pKryo) {
    synchronized (this) {
      mKryoPool.add(pKryo);
    }
  }
}

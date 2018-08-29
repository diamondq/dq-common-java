package com.diamondq.common.utils.sync.abstracts;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;

public abstract class AbstractRecordChangesSimpleSyncInfo<T, T_KEY> extends AbstractOneWaySyncSimpleSyncInfo<T, T_KEY> {

  protected List<Pair<T_KEY, T>> mBToCreate = new ArrayList<>();

  protected List<Pair<T_KEY, T>> mBToDelete = new ArrayList<>();

  protected List<Pair<T_KEY, T>> mBToModify = new ArrayList<>();

  @Override
  public ExtendedCompletableFuture<@Nullable Void> createB(Stream<Pair<T_KEY, T>> pStream) {
    synchronized (this) {
      pStream.map((p) -> {
        mBToCreate.add(p);
        return null;
      });
      return ExtendedCompletableFuture.completedFuture(null);
    }
  }

  @Override
  public ExtendedCompletableFuture<@Nullable Void> deleteB(Stream<Pair<T_KEY, T>> pStream) {
    synchronized (this) {
      pStream.map((p) -> {
        mBToDelete.add(p);
        return null;
      });
      return ExtendedCompletableFuture.completedFuture(null);
    }
  }

  @Override
  public ExtendedCompletableFuture<@Nullable Void> modifyB(Stream<Pair<T_KEY, T>> pStream) {
    synchronized (this) {
      pStream.map((p) -> {
        mBToModify.add(p);
        return null;
      });
      return ExtendedCompletableFuture.completedFuture(null);
    }
  }

  public List<Pair<T_KEY, T>> getBToCreate() {
    return mBToCreate;
  }

  public List<Pair<T_KEY, T>> getBToDelete() {
    return mBToDelete;
  }

  public List<Pair<T_KEY, T>> getBToModify() {
    return mBToModify;
  }

}

package com.diamondq.common.lambda.future;

import com.diamondq.common.tracing.opentracing.wrappers.TracerBiConsumer;
import com.diamondq.common.tracing.opentracing.wrappers.TracerBiFunction;
import com.diamondq.common.tracing.opentracing.wrappers.TracerConsumer;
import com.diamondq.common.tracing.opentracing.wrappers.TracerFunction;
import com.diamondq.common.tracing.opentracing.wrappers.TracerRunnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An extension to the standard CompletableFuture to add a few extra functions
 *
 * @param <T>
 */
public class ExtendedCompletionStageImpl<T> implements ExtendedCompletionStage<T> {

	private CompletionStage<T> mDelegate;

	static <U> CompletionStage<U> decomposeToCompletionStage(CompletionStage<U> pStage) {
		if (pStage instanceof ExtendedCompletionStageImpl)
			return ((ExtendedCompletionStageImpl<U>) pStage).mDelegate;
		return pStage;
	}

	@Override
	public int hashCode() {
		return mDelegate.hashCode();
	}

	@Override
	public boolean equals(@Nullable Object pObj) {
		return mDelegate.equals(pObj);
	}

	@Override
	public <U> ExtendedCompletionStage<U> thenApply(Function<? super T, ? extends U> pFn) {
		return handle((t, ex) -> {
			if (ex != null) {
				if (ex instanceof RuntimeException)
					throw (RuntimeException)ex;
				throw new RuntimeException(ex);
			}
			return pFn.apply(t);
		});
	}

	@Override
	public <U> ExtendedCompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> pFn) {
		return handleAsync((t, ex) -> {
			if (ex != null) {
				if (ex instanceof RuntimeException)
					throw (RuntimeException)ex;
				throw new RuntimeException(ex);
			}
			return pFn.apply(t);
		});
	}

	@Override
	public <U> ExtendedCompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> pFn,
		@Nullable Executor pExecutor) {
		return handleAsync((t, ex) -> {
			if (ex != null) {
				if (ex instanceof RuntimeException)
					throw (RuntimeException)ex;
				throw new RuntimeException(ex);
			}
			return pFn.apply(t);
		}, pExecutor);
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> thenAccept(Consumer<? super T> pAction) {
		return handle((t, ex) -> {
			if (ex != null) {
				if (ex instanceof RuntimeException)
					throw (RuntimeException)ex;
				throw new RuntimeException(ex);
			}
			pAction.accept(t);
			return null;
		});
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer<? super T> pAction) {
		return handleAsync((t, ex) -> {
			if (ex != null) {
				if (ex instanceof RuntimeException)
					throw (RuntimeException)ex;
				throw new RuntimeException(ex);
			}
			pAction.accept(t);
			return null;
		});
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer<? super T> pAction, Executor pExecutor) {
		return handleAsync((t, ex) -> {
			if (ex != null) {
				if (ex instanceof RuntimeException)
					throw (RuntimeException)ex;
				throw new RuntimeException(ex);
			}
			pAction.accept(t);
			return null;
		}, pExecutor);
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> thenRun(Runnable pAction) {
		return handle((t, ex) -> {
			if (ex != null) {
				if (ex instanceof RuntimeException)
					throw (RuntimeException)ex;
				throw new RuntimeException(ex);
			}
			pAction.run();
			return null;
		});
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> thenRunAsync(Runnable pAction) {
		return handleAsync((t, ex) -> {
			if (ex != null) {
				if (ex instanceof RuntimeException)
					throw (RuntimeException)ex;
				throw new RuntimeException(ex);
			}
			pAction.run();
			return null;
		});
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> thenRunAsync(Runnable pAction, Executor pExecutor) {
		return handleAsync((t, ex) -> {
			if (ex != null) {
				if (ex instanceof RuntimeException)
					throw (RuntimeException)ex;
				throw new RuntimeException(ex);
			}
			pAction.run();
			return null;
		}, pExecutor);
	}

	@Override
	public <U, V> ExtendedCompletionStage<V> thenCombine(CompletionStage<? extends U> pOther,
		BiFunction<? super T, ? super U, ? extends V> pFn) {
		TracerBiFunction<? super T, ? super U, ? extends V> ab = new TracerBiFunction<>(pFn);
		try {
			ExtendedCompletionStage<V> result = ExtendedCompletionStage
				.of(mDelegate.thenCombine(decomposeToCompletionStage(pOther), ab));
			final TracerBiFunction<? super T, ? super U, ? extends V> cleanup = ab;
			result = result.exceptionally((ex) -> {
				cleanup.abortContinuation();
				if (ex instanceof RuntimeException)
					throw (RuntimeException)ex;
				throw new RuntimeException(ex);
			});
			ab = null;
			return result;
		}
		finally {
			if (ab != null)
				ab.abortContinuation();
		}
	}

	@Override
	public <U, V> ExtendedCompletionStage<V> thenCombineAsync(CompletionStage<? extends U> pOther,
		BiFunction<? super T, ? super U, ? extends V> pFn) {
		TracerBiFunction<? super T, ? super U, ? extends V> ab = new TracerBiFunction<>(pFn);
		try {
			ExtendedCompletionStage<V> result = ExtendedCompletionStage
				.of(mDelegate.thenCombineAsync(decomposeToCompletionStage(pOther), ab));
			final TracerBiFunction<? super T, ? super U, ? extends V> cleanup = ab;
			result = result.exceptionally((ex) -> {
				cleanup.abortContinuation();
				if (ex instanceof RuntimeException)
					throw (RuntimeException)ex;
				throw new RuntimeException(ex);
			});
			ab = null;
			return result;
		}
		finally {
			if (ab != null)
				ab.abortContinuation();
		}
	}

	@Override
	public <U, V> ExtendedCompletionStage<V> thenCombineAsync(CompletionStage<? extends U> pOther,
		BiFunction<? super T, ? super U, ? extends V> pFn, @Nullable Executor pExecutor) {
		TracerBiFunction<? super T, ? super U, ? extends V> ab = new TracerBiFunction<>(pFn);
		try {
			ExtendedCompletionStage<V> result = ExtendedCompletionStage
				.of(mDelegate.thenCombineAsync(decomposeToCompletionStage(pOther), ab, pExecutor));
			final TracerBiFunction<? super T, ? super U, ? extends V> cleanup = ab;
			result = result.exceptionally((ex) -> {
				cleanup.abortContinuation();
				if (ex instanceof RuntimeException)
					throw (RuntimeException)ex;
				throw new RuntimeException(ex);
			});
			ab = null;
			return result;
		}
		finally {
			if (ab != null)
				ab.abortContinuation();
		}
	}

	@Override
	public <U> ExtendedCompletionStage<@Nullable Void> thenAcceptBoth(CompletionStage<? extends U> pOther,
		BiConsumer<? super T, ? super U> pAction) {
		return thenCombine(pOther, (a, b) -> {
			pAction.accept(a, b);
			return null;
		});
	}

	@Override
	public <U> ExtendedCompletionStage<@Nullable Void> thenAcceptBothAsync(CompletionStage<? extends U> pOther,
		BiConsumer<? super T, ? super U> pAction) {
		return thenCombineAsync(pOther, (a, b) -> {
			pAction.accept(a, b);
			return null;
		});
	}

	@Override
	public <U> ExtendedCompletionStage<@Nullable Void> thenAcceptBothAsync(CompletionStage<? extends U> pOther,
		BiConsumer<? super T, ? super U> pAction, @Nullable Executor pExecutor) {
		return thenCombineAsync(pOther, (a, b) -> {
			pAction.accept(a, b);
			return null;
		}, pExecutor);
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> runAfterBoth(CompletionStage<?> pOther, Runnable pAction) {
		return thenCombine(pOther, (a, b) -> {
			pAction.run();
			return null;
		});
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> runAfterBothAsync(CompletionStage<?> pOther, Runnable pAction) {
		return thenCombineAsync(pOther, (a, b) -> {
			pAction.run();
			return null;
		});
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> runAfterBothAsync(CompletionStage<?> pOther, Runnable pAction,
		Executor pExecutor) {
		return thenCombineAsync(pOther, (a, b) -> {
			pAction.run();
			return null;
		}, pExecutor);
	}

	@Override
	public <U> ExtendedCompletionStage<U> applyToEither(CompletionStage<? extends T> pOther,
		Function<? super T, U> pFn) {
		TracerFunction<? super T, U> ab = new TracerFunction<>(pFn);
		try {
			ExtendedCompletionStage<U> result = ExtendedCompletionStage
				.of(mDelegate.applyToEither(decomposeToCompletionStage(pOther), ab));
			final TracerFunction<? super T, U> cleanup = ab;
			result = result.exceptionally((ex) -> {
				cleanup.abortContinuation();
				if (ex instanceof RuntimeException)
					throw (RuntimeException)ex;
				throw new RuntimeException(ex);
			});
			ab = null;
			return result;
		}
		finally {
			if (ab != null)
				ab.abortContinuation();
		}
	}

	@Override
	public <U> ExtendedCompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> pOther,
		Function<? super T, U> pFn) {
		TracerFunction<? super T, U> ab = new TracerFunction<>(pFn);
		try {
			ExtendedCompletionStage<U> result = ExtendedCompletionStage
				.of(mDelegate.applyToEitherAsync(decomposeToCompletionStage(pOther), ab));
			final TracerFunction<? super T, U> cleanup = ab;
			result = result.exceptionally((ex) -> {
				cleanup.abortContinuation();
				if (ex instanceof RuntimeException)
					throw (RuntimeException)ex;
				throw new RuntimeException(ex);
			});
			ab = null;
			return result;
		}
		finally {
			if (ab != null)
				ab.abortContinuation();
		}
	}

	@Override
	public <U> ExtendedCompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> pOther,
		Function<? super T, U> pFn, @Nullable Executor pExecutor) {
		TracerFunction<? super T, U> ab = new TracerFunction<>(pFn);
		try {
			ExtendedCompletionStage<U> result = ExtendedCompletionStage
				.of(mDelegate.applyToEitherAsync(decomposeToCompletionStage(pOther), ab, pExecutor));
			final TracerFunction<? super T, U> cleanup = ab;
			result = result.exceptionally((ex) -> {
				cleanup.abortContinuation();
				if (ex instanceof RuntimeException)
					throw (RuntimeException)ex;
				throw new RuntimeException(ex);
			});
			ab = null;
			return result;
		}
		finally {
			if (ab != null)
				ab.abortContinuation();
		}
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> acceptEither(CompletionStage<? extends T> pOther,
		Consumer<? super T> pAction) {
		return applyToEither(pOther, (t)-> {
			pAction.accept(t);
			return null;
		});
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> acceptEitherAsync(CompletionStage<? extends T> pOther,
		Consumer<? super T> pAction) {
		return applyToEitherAsync(pOther, (t)-> {
			pAction.accept(t);
			return null;
		});
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> acceptEitherAsync(CompletionStage<? extends T> pOther,
		Consumer<? super T> pAction, Executor pExecutor) {
		return applyToEitherAsync(pOther, (t)-> {
			pAction.accept(t);
			return null;
		}, pExecutor);
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> runAfterEither(CompletionStage<?> pOther, Runnable pAction) {
		TracerRunnable ab = new TracerRunnable(pAction);
		try {
			ExtendedCompletionStage<@Nullable Void> result = ExtendedCompletionStage
				.of(mDelegate.runAfterEither(decomposeToCompletionStage(pOther), ab));
			final TracerRunnable cleanup = ab;
			result = result.exceptionally((ex) -> {
				cleanup.abortContinuation();
				if (ex instanceof RuntimeException)
					throw (RuntimeException)ex;
				throw new RuntimeException(ex);
			});
			ab = null;
			return result;
		}
		finally {
			if (ab != null)
				ab.abortContinuation();
		}
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> runAfterEitherAsync(CompletionStage<?> pOther, Runnable pAction) {
		TracerRunnable ab = new TracerRunnable(pAction);
		try {
			ExtendedCompletionStage<@Nullable Void> result = ExtendedCompletionStage
				.of(mDelegate.runAfterEitherAsync(decomposeToCompletionStage(pOther), ab));
			final TracerRunnable cleanup = ab;
			result = result.exceptionally((ex) -> {
				cleanup.abortContinuation();
				if (ex instanceof RuntimeException)
					throw (RuntimeException)ex;
				throw new RuntimeException(ex);
			});
			ab = null;
			return result;
		}
		finally {
			if (ab != null)
				ab.abortContinuation();
		}
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> runAfterEitherAsync(CompletionStage<?> pOther, Runnable pAction,
		Executor pExecutor) {
		TracerRunnable ab = new TracerRunnable(pAction);
		try {
			ExtendedCompletionStage<@Nullable Void> result = ExtendedCompletionStage
				.of(mDelegate.runAfterEitherAsync(decomposeToCompletionStage(pOther), ab, pExecutor));
			final TracerRunnable cleanup = ab;
			result = result.exceptionally((ex) -> {
				cleanup.abortContinuation();
				if (ex instanceof RuntimeException)
					throw (RuntimeException)ex;
				throw new RuntimeException(ex);
			});
			ab = null;
			return result;
		}
		finally {
			if (ab != null)
				ab.abortContinuation();
		}
	}

	@Override
	public <U> ExtendedCompletionStage<U> thenCompose(
		Function<? super T, @NonNull ? extends @NonNull CompletionStage<U>> pFn) {
		TracerFunction<@Nullable ? super T, @NonNull ? extends @NonNull CompletionStage<U>> ab = new TracerFunction<>(pFn);
		try {
			ExtendedCompletionStage<U> result = ExtendedCompletionStage.of(mDelegate.thenCompose(ab));
			final TracerFunction<@Nullable ? super T, @NonNull ? extends @NonNull CompletionStage<U>> cleanup = ab;
			result = result.exceptionally((ex) -> {
				cleanup.abortContinuation();
				if (ex instanceof RuntimeException)
					throw (RuntimeException)ex;
				throw new RuntimeException(ex);
			});
			ab = null;
			return result;
		}
		finally {
			if (ab != null)
				ab.abortContinuation();
		}
	}

	@Override
	public <U> ExtendedCompletionStage<U> thenComposeAsync(
		Function<? super T, @NonNull ? extends @NonNull CompletionStage<U>> pFn) {
		TracerFunction<@Nullable ? super T, @NonNull ? extends @NonNull CompletionStage<U>> ab = new TracerFunction<>(pFn);
		try {
			ExtendedCompletionStage<U> result = ExtendedCompletionStage.of(mDelegate.thenComposeAsync(ab));
			final TracerFunction<@Nullable ? super T, @NonNull ? extends @NonNull CompletionStage<U>> cleanup = ab;
			result = result.exceptionally((ex) -> {
				cleanup.abortContinuation();
				if (ex instanceof RuntimeException)
					throw (RuntimeException)ex;
				throw new RuntimeException(ex);
			});
			ab = null;
			return result;
		}
		finally {
			if (ab != null)
				ab.abortContinuation();
		}
	}

	@Override
	public <U> ExtendedCompletionStage<U> thenComposeAsync(
		Function<? super T, @NonNull ? extends @NonNull CompletionStage<U>> pFn, Executor pExecutor) {
		TracerFunction<@Nullable ? super T, @NonNull ? extends @NonNull CompletionStage<U>> ab = new TracerFunction<>(pFn);
		try {
			ExtendedCompletionStage<U> result = ExtendedCompletionStage.of(mDelegate.thenComposeAsync(ab, pExecutor));
			final TracerFunction<@Nullable ? super T, @NonNull ? extends @NonNull CompletionStage<U>> cleanup = ab;
			result = result.exceptionally((ex) -> {
				cleanup.abortContinuation();
				if (ex instanceof RuntimeException)
					throw (RuntimeException)ex;
				throw new RuntimeException(ex);
			});
			ab = null;
			return result;
		}
		finally {
			if (ab != null)
				ab.abortContinuation();
		}
	}

	@Override
	public ExtendedCompletionStage<T> whenComplete(
		BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction) {
		return handle((t, ex) -> {
			pAction.accept(t, ex);
			return t;
		});
	}

	@Override
	public ExtendedCompletionStage<T> whenCompleteAsync(
		BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction) {
		return handleAsync((t, ex) -> {
			pAction.accept(t, ex);
			return t;
		});
	}

	@Override
	public ExtendedCompletionStage<T> whenCompleteAsync(
		BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction, @Nullable Executor pExecutor) {
		return handleAsync((t, ex) -> {
			pAction.accept(t, ex);
			return t;
		}, pExecutor);
	}

	@Override
	public <U> ExtendedCompletionStage<U> handle(BiFunction<? super T, @Nullable Throwable, ? extends @NonNull U> pFn) {
		TracerBiFunction<? super T, @Nullable Throwable, ? extends @NonNull U> ab = new TracerBiFunction<>(pFn);
		try {
			ExtendedCompletionStage<U> result = ExtendedCompletionStage.of(mDelegate.handle(ab));
			ab = null;
			return result;
		}
		finally {
			if (ab != null)
				ab.abortContinuation();
		}
	}

	@Override
	public <U> ExtendedCompletionStage<U> handleAsync(BiFunction<? super T, @Nullable Throwable, ? extends U> pFn) {
		TracerBiFunction<? super T, @Nullable Throwable, ? extends @NonNull U> ab = new TracerBiFunction<>(pFn);
		try {
			ExtendedCompletionStage<U> result = ExtendedCompletionStage.of(mDelegate.handleAsync(ab));
			ab = null;
			return result;
		}
		finally {
			if (ab != null)
				ab.abortContinuation();
		}
	}

	@Override
	public <U> ExtendedCompletionStage<U> handleAsync(BiFunction<? super T, @Nullable Throwable, ? extends U> pFn,
		@Nullable Executor pExecutor) {
		TracerBiFunction<? super T, @Nullable Throwable, ? extends @NonNull U> ab = new TracerBiFunction<>(pFn);
		try {
			ExtendedCompletionStage<U> result = ExtendedCompletionStage.of(mDelegate.handleAsync(ab, pExecutor));
			ab = null;
			return result;
		}
		finally {
			if (ab != null)
				ab.abortContinuation();
		}
	}

	@Override
	public CompletableFuture<T> toCompletableFuture() {
		return mDelegate.toCompletableFuture();
	}

	@Override
	public ExtendedCompletionStage<T> exceptionally(Function<Throwable, ? extends T> pFn) {
		return handle((t, ex) -> {
			if (ex != null)
				return pFn.apply(ex);
			return t;
		});
	}

	@Override
	public String toString() {
		return mDelegate.toString();
	}

	/**
	 * Default constructor
	 */
	public ExtendedCompletionStageImpl() {
		mDelegate = new CompletableFuture<T>();
	}

	ExtendedCompletionStageImpl(CompletionStage<T> pFuture) {
		mDelegate = pFuture;
	}

	/**
	 * Continues if the result is null
	 *
	 * @param pFunc the function
	 * @return the future
	 */
	@Override
	public ExtendedCompletionStage<T> continueIfNull(Supplier<T> pFunc) {
		return thenApply((result) -> {
			if (result != null)
				return result;
			return pFunc.get();
		});
	}

	/**
	 * Continues the compose if null
	 *
	 * @param pFunc the function
	 * @return the future
	 */
	@SuppressWarnings("null")
	@Override
	public ExtendedCompletionStage<T> continueComposeIfNull(Supplier<CompletionStage<T>> pFunc) {
		return thenCompose((result) -> {
			if (result != null)
				return ExtendedCompletableFuture.completedFuture(result);
			return pFunc.get();
		});
	}

	/**
	 * Continues async if null
	 *
	 * @param pFunc the function
	 * @return the future
	 */
	@Override
	public ExtendedCompletionStage<T> continueAsyncIfNull(Supplier<T> pFunc) {
		return thenApplyAsync((result) -> {
			if (result != null)
				return result;
			return pFunc.get();
		});
	}

	/**
	 * Marks a completed failure
	 *
	 * @param pEx the exception
	 * @return the future
	 */
	public static <U> ExtendedCompletionStage<U> completedFailure(Throwable pEx) {
		CompletableFuture<U> result = new CompletableFuture<U>();
		result.completeExceptionally(pEx);
		return ExtendedCompletionStage.of(result);
	}

	/**
	 * Continues to compose if
	 *
	 * @param pClass the class
	 * @param pFunc the function
	 * @return the future
	 */
	@Override
	@SuppressWarnings({"unchecked", "null"})
	public <C, U> ExtendedCompletionStage<?> continueComposeIf(Class<C> pClass,
		Function<C, @NonNull ? extends @NonNull CompletionStage<U>> pFunc) {
		return thenCompose(result -> {
			if (result != null) {
				if (pClass.isInstance(result) == true) {
					C input = (C) result;
					return (CompletionStage<Object>) pFunc.apply(input);
				}
			}
			return (CompletionStage<Object>) result;
		});
	}

	/**
	 * Continues if
	 *
	 * @param pClass the class
	 * @param pFunc the function
	 * @return the future
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <C, U> ExtendedCompletionStage<?> continueIf(Class<C> pClass, Function<C, U> pFunc) {
		return thenApply(result -> {
			if (result != null) {
				if (pClass.isInstance(result) == true) {
					C input = (C) result;
					return pFunc.apply(input);
				}
			}
			return result;
		});
	}

	/**
	 * Splits a compose into two tracks
	 *
	 * @param pBoolFunc the boolean function
	 * @param pTrueFunc the true side
	 * @param pFalseFunc the false side
	 * @return the future
	 */
	@Override
	public <R> ExtendedCompletionStage<R> splitCompose(Predicate<T> pBoolFunc,
		Function<T, @NonNull ? extends @NonNull CompletionStage<R>> pTrueFunc,
		Function<T, @NonNull ? extends @NonNull CompletionStage<R>> pFalseFunc) {
		return thenCompose((input) -> {
			if (pBoolFunc.test(input) == true)
				return pTrueFunc.apply(input);
			else
				return pFalseFunc.apply(input);
		});
	}

	/**
	 * Split based apply
	 *
	 * @param pBoolFunc the boolean function
	 * @param pTrueFunc the true result
	 * @param pFalseFunc the false result
	 * @return the future
	 */
	@Override
	public <R> ExtendedCompletionStage<R> splitApply(Predicate<T> pBoolFunc, Function<T, ? extends R> pTrueFunc,
		Function<T, ? extends R> pFalseFunc) {
		return thenApply((input) -> {
			if (pBoolFunc.test(input) == true)
				return pTrueFunc.apply(input);
			else
				return pFalseFunc.apply(input);
		});
	}
}
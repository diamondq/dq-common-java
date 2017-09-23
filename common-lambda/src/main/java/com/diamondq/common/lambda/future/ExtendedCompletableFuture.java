package com.diamondq.common.lambda.future;

import com.diamondq.common.tracing.opentracing.wrappers.TracerBiConsumer;
import com.diamondq.common.tracing.opentracing.wrappers.TracerBiFunction;
import com.diamondq.common.tracing.opentracing.wrappers.TracerConsumer;
import com.diamondq.common.tracing.opentracing.wrappers.TracerFunction;
import com.diamondq.common.tracing.opentracing.wrappers.TracerRunnable;
import com.diamondq.common.tracing.opentracing.wrappers.TracerSupplier;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
public class ExtendedCompletableFuture<T> extends CompletableFuture<T> implements ExtendedCompletionStage<T> {

	private final CompletableFuture<T> mDelegate;

	private static <U> CompletionStage<U> decomposeToCompletionStage(CompletionStage<U> pStage) {
		if (pStage instanceof ExtendedCompletableFuture)
			return ((ExtendedCompletableFuture<U>) pStage).mDelegate;
		return pStage;
	}

	private static <U> CompletableFuture<U> decomposeToCompletableFuture(CompletableFuture<U> pFuture) {
		if (pFuture instanceof ExtendedCompletableFuture)
			return ((ExtendedCompletableFuture<U>) pFuture).mDelegate;
		return pFuture;
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
	public boolean isDone() {
		return mDelegate.isDone();
	}

	@Override
	public @Nullable T get() throws InterruptedException, ExecutionException {
		return mDelegate.get();
	}

	@Override
	public T get(long pTimeout, TimeUnit pUnit) throws InterruptedException, ExecutionException, TimeoutException {
		return mDelegate.get(pTimeout, pUnit);
	}

	@Override
	public T join() {
		return mDelegate.join();
	}

	@Override
	public T getNow(T pValueIfAbsent) {
		return mDelegate.getNow(pValueIfAbsent);
	}

	@Override
	public boolean complete(T pValue) {
		return mDelegate.complete(pValue);
	}

	@Override
	public boolean completeExceptionally(Throwable pEx) {
		return mDelegate.completeExceptionally(pEx);
	}

	@Override
	public <U> ExtendedCompletableFuture<U> thenApply(Function<? super T, ? extends U> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.thenApply(new TracerFunction<>(pFn)));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.thenApplyAsync(new TracerFunction<>(pFn)));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> pFn,
		@Nullable Executor pExecutor) {
		return ExtendedCompletableFuture.of(mDelegate.thenApplyAsync(new TracerFunction<>(pFn), pExecutor));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> thenAccept(Consumer<? super T> pAction) {
		return ExtendedCompletableFuture.of(mDelegate.thenAccept(new TracerConsumer<>(pAction)));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> thenAcceptAsync(Consumer<? super T> pAction) {
		return ExtendedCompletableFuture.of(mDelegate.thenAcceptAsync(new TracerConsumer<>(pAction)));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> thenAcceptAsync(Consumer<? super T> pAction, Executor pExecutor) {
		return ExtendedCompletableFuture.of(mDelegate.thenAcceptAsync(new TracerConsumer<>(pAction), pExecutor));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> thenRun(Runnable pAction) {
		return ExtendedCompletableFuture.of(mDelegate.thenRun(new TracerRunnable(pAction)));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> thenRunAsync(Runnable pAction) {
		return ExtendedCompletableFuture.of(mDelegate.thenRunAsync(new TracerRunnable(pAction)));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> thenRunAsync(Runnable pAction, Executor pExecutor) {
		return ExtendedCompletableFuture.of(mDelegate.thenRunAsync(new TracerRunnable(pAction), pExecutor));
	}

	@Override
	public <U, V> ExtendedCompletableFuture<V> thenCombine(CompletionStage<? extends U> pOther,
		BiFunction<? super T, ? super U, ? extends V> pFn) {
		return ExtendedCompletableFuture
			.of(mDelegate.thenCombine(decomposeToCompletionStage(pOther), new TracerBiFunction<>(pFn)));
	}

	@Override
	public <U, V> ExtendedCompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> pOther,
		BiFunction<? super T, ? super U, ? extends V> pFn) {
		return ExtendedCompletableFuture
			.of(mDelegate.thenCombineAsync(decomposeToCompletionStage(pOther), new TracerBiFunction<>(pFn)));
	}

	@Override
	public <U, V> ExtendedCompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> pOther,
		BiFunction<? super T, ? super U, ? extends V> pFn, @Nullable Executor pExecutor) {
		return ExtendedCompletableFuture
			.of(mDelegate.thenCombineAsync(decomposeToCompletionStage(pOther), new TracerBiFunction<>(pFn), pExecutor));
	}

	@Override
	public <U> ExtendedCompletableFuture<@Nullable Void> thenAcceptBoth(CompletionStage<? extends U> pOther,
		BiConsumer<? super T, ? super U> pAction) {
		return ExtendedCompletableFuture
			.of(mDelegate.thenAcceptBoth(decomposeToCompletionStage(pOther), new TracerBiConsumer<>(pAction)));
	}

	@Override
	public <U> ExtendedCompletableFuture<@Nullable Void> thenAcceptBothAsync(CompletionStage<? extends U> pOther,
		BiConsumer<? super T, ? super U> pAction) {
		return ExtendedCompletableFuture
			.of(mDelegate.thenAcceptBothAsync(decomposeToCompletionStage(pOther), new TracerBiConsumer<>(pAction)));
	}

	@Override
	public <U> ExtendedCompletableFuture<@Nullable Void> thenAcceptBothAsync(CompletionStage<? extends U> pOther,
		BiConsumer<? super T, ? super U> pAction, @Nullable Executor pExecutor) {
		return ExtendedCompletableFuture.of(mDelegate.thenAcceptBothAsync(decomposeToCompletionStage(pOther),
			new TracerBiConsumer<>(pAction), pExecutor));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> runAfterBoth(CompletionStage<?> pOther, Runnable pAction) {
		return ExtendedCompletableFuture
			.of(mDelegate.runAfterBoth(decomposeToCompletionStage(pOther), new TracerRunnable(pAction)));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> runAfterBothAsync(CompletionStage<?> pOther, Runnable pAction) {
		return ExtendedCompletableFuture
			.of(mDelegate.runAfterBothAsync(decomposeToCompletionStage(pOther), new TracerRunnable(pAction)));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> runAfterBothAsync(CompletionStage<?> pOther, Runnable pAction,
		Executor pExecutor) {
		return ExtendedCompletableFuture.of(
			mDelegate.runAfterBothAsync(decomposeToCompletionStage(pOther), new TracerRunnable(pAction), pExecutor));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> applyToEither(CompletionStage<? extends T> pOther,
		Function<? super T, U> pFn) {
		return ExtendedCompletableFuture
			.of(mDelegate.applyToEither(decomposeToCompletionStage(pOther), new TracerFunction<>(pFn)));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> pOther,
		Function<? super T, U> pFn) {
		return ExtendedCompletableFuture
			.of(mDelegate.applyToEitherAsync(decomposeToCompletionStage(pOther), new TracerFunction<>(pFn)));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> pOther,
		Function<? super T, U> pFn, @Nullable Executor pExecutor) {
		return ExtendedCompletableFuture
			.of(mDelegate.applyToEitherAsync(decomposeToCompletionStage(pOther), new TracerFunction<>(pFn), pExecutor));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> acceptEither(CompletionStage<? extends T> pOther,
		Consumer<? super T> pAction) {
		return ExtendedCompletableFuture
			.of(mDelegate.acceptEither(decomposeToCompletionStage(pOther), new TracerConsumer<>(pAction)));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> acceptEitherAsync(CompletionStage<? extends T> pOther,
		Consumer<? super T> pAction) {
		return ExtendedCompletableFuture
			.of(mDelegate.acceptEitherAsync(decomposeToCompletionStage(pOther), new TracerConsumer<>(pAction)));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> acceptEitherAsync(CompletionStage<? extends T> pOther,
		Consumer<? super T> pAction, Executor pExecutor) {
		return ExtendedCompletableFuture.of(
			mDelegate.acceptEitherAsync(decomposeToCompletionStage(pOther), new TracerConsumer<>(pAction), pExecutor));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> runAfterEither(CompletionStage<?> pOther, Runnable pAction) {
		return ExtendedCompletableFuture
			.of(mDelegate.runAfterEither(decomposeToCompletionStage(pOther), new TracerRunnable(pAction)));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> runAfterEitherAsync(CompletionStage<?> pOther, Runnable pAction) {
		return ExtendedCompletableFuture
			.of(mDelegate.runAfterEitherAsync(decomposeToCompletionStage(pOther), new TracerRunnable(pAction)));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> runAfterEitherAsync(CompletionStage<?> pOther, Runnable pAction,
		Executor pExecutor) {
		return ExtendedCompletableFuture.of(
			mDelegate.runAfterEitherAsync(decomposeToCompletionStage(pOther), new TracerRunnable(pAction), pExecutor));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> thenCompose(
		Function<@Nullable ? super T, @NonNull ? extends @NonNull CompletionStage<U>> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.thenCompose(new TracerFunction<>(pFn)));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> thenComposeAsync(
		Function<? super T, @NonNull ? extends @NonNull CompletionStage<U>> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.thenComposeAsync(new TracerFunction<>(pFn)));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> thenComposeAsync(
		Function<? super T, @NonNull ? extends @NonNull CompletionStage<U>> pFn, Executor pExecutor) {
		return ExtendedCompletableFuture.of(mDelegate.thenComposeAsync(new TracerFunction<>(pFn), pExecutor));
	}

	@Override
	public ExtendedCompletableFuture<T> whenComplete(
		BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction) {
		return ExtendedCompletableFuture.of(mDelegate.whenComplete(new TracerBiConsumer<>(pAction)));
	}

	@Override
	public ExtendedCompletableFuture<T> whenCompleteAsync(
		BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction) {
		return ExtendedCompletableFuture.of(mDelegate.whenCompleteAsync(new TracerBiConsumer<>(pAction)));
	}

	@Override
	public ExtendedCompletableFuture<T> whenCompleteAsync(
		BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction, @Nullable Executor pExecutor) {
		return ExtendedCompletableFuture.of(mDelegate.whenCompleteAsync(new TracerBiConsumer<>(pAction), pExecutor));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> handle(
		BiFunction<? super T, @Nullable Throwable, ? extends @NonNull U> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.handle(new TracerBiFunction<>(pFn)));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> handleAsync(BiFunction<? super T, @Nullable Throwable, ? extends U> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.handleAsync(new TracerBiFunction<>(pFn)));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> handleAsync(BiFunction<? super T, @Nullable Throwable, ? extends U> pFn,
		@Nullable Executor pExecutor) {
		return ExtendedCompletableFuture.of(mDelegate.handleAsync(new TracerBiFunction<>(pFn), pExecutor));
	}

	@Override
	public CompletableFuture<T> toCompletableFuture() {
		return mDelegate.toCompletableFuture();
	}

	@Override
	public ExtendedCompletableFuture<T> exceptionally(Function<Throwable, ? extends T> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.exceptionally(new TracerFunction<>(pFn)));
	}

	@Override
	public boolean cancel(boolean pMayInterruptIfRunning) {
		return mDelegate.cancel(pMayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return mDelegate.isCancelled();
	}

	@Override
	public boolean isCompletedExceptionally() {
		return mDelegate.isCompletedExceptionally();
	}

	@Override
	public void obtrudeValue(T pValue) {
		mDelegate.obtrudeValue(pValue);
	}

	@Override
	public void obtrudeException(Throwable pEx) {
		mDelegate.obtrudeException(pEx);
	}

	@Override
	public int getNumberOfDependents() {
		return mDelegate.getNumberOfDependents();
	}

	@Override
	public String toString() {
		return mDelegate.toString();
	}

	/**
	 * Default constructor
	 */
	public ExtendedCompletableFuture() {
		mDelegate = new CompletableFuture<T>();
	}

	private ExtendedCompletableFuture(CompletableFuture<T> pFuture) {
		mDelegate = pFuture;
	}

	/**
	 * Returns a new CompletableFuture that is asynchronously completed by a task running in the
	 * {@link ForkJoinPool#commonPool()} with the value obtained by calling the given Supplier.
	 *
	 * @param supplier a function returning the value to be used to complete the returned CompletableFuture
	 * @param <U> the function's return type
	 * @return the new CompletableFuture
	 */
	public static <U> ExtendedCompletableFuture<U> supplyAsync(Supplier<U> supplier) {
		return ExtendedCompletableFuture.of(CompletableFuture.supplyAsync(new TracerSupplier<>(supplier)));
	}

	/**
	 * Returns a new CompletableFuture that is asynchronously completed by a task running in the given executor with the
	 * value obtained by calling the given Supplier.
	 *
	 * @param supplier a function returning the value to be used to complete the returned CompletableFuture
	 * @param executor the executor to use for asynchronous execution
	 * @param <U> the function's return type
	 * @return the new CompletableFuture
	 */
	public static <U> ExtendedCompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor) {
		return ExtendedCompletableFuture.of(CompletableFuture.supplyAsync(new TracerSupplier<>(supplier), executor));
	}

	/**
	 * Returns a new CompletableFuture that is asynchronously completed by a task running in the
	 * {@link ForkJoinPool#commonPool()} after it runs the given action.
	 *
	 * @param runnable the action to run before completing the returned CompletableFuture
	 * @return the new CompletableFuture
	 */
	public static ExtendedCompletableFuture<@Nullable Void> runAsync(Runnable runnable) {
		return ExtendedCompletableFuture.of(CompletableFuture.runAsync(new TracerRunnable(runnable)));
	}

	/**
	 * Returns a new CompletableFuture that is asynchronously completed by a task running in the given executor after it
	 * runs the given action.
	 *
	 * @param runnable the action to run before completing the returned CompletableFuture
	 * @param executor the executor to use for asynchronous execution
	 * @return the new CompletableFuture
	 */
	public static ExtendedCompletableFuture<@Nullable Void> runAsync(Runnable runnable, Executor executor) {
		return ExtendedCompletableFuture.of(CompletableFuture.runAsync(new TracerRunnable(runnable), executor));
	}

	public static ExtendedCompletableFuture<@Nullable Void> allOf(@NonNull CompletableFuture<?>... cfs) {
		@NonNull
		CompletableFuture<?>[] args = new @NonNull CompletableFuture<?>[cfs.length];
		for (int i = 0; i < cfs.length; i++)
			args[i] = decomposeToCompletableFuture(cfs[i]);
		return ExtendedCompletableFuture.of(CompletableFuture.allOf(args));
	}

	/**
	 * Generates an allOf future
	 *
	 * @param cfs the collection of futures
	 * @return the future
	 */
	public static ExtendedCompletableFuture<@Nullable Void> allOf(
		Collection<@NonNull ? extends @NonNull CompletableFuture<?>> cfs) {
		CompletableFuture<?>[] args = new CompletableFuture<?>[cfs.size()];
		int count = 0;
		for (Iterator<@NonNull ? extends @NonNull CompletableFuture<?>> i = cfs.iterator(); i.hasNext();) {
			CompletableFuture<?> next = i.next();
			args[count++] = decomposeToCompletableFuture(next);
		}
		return ExtendedCompletableFuture.of(CompletableFuture.allOf(args));
	}

	public static ExtendedCompletableFuture<@Nullable Object> anyOf(@NonNull CompletableFuture<?>... cfs) {
		CompletableFuture<?>[] args = new CompletableFuture<?>[cfs.length];
		for (int i = 0; i < cfs.length; i++)
			args[i] = decomposeToCompletableFuture(cfs[i]);
		return ExtendedCompletableFuture.of(CompletableFuture.anyOf(args));
	}

	public static <U> ExtendedCompletableFuture<U> completedFuture(U value) {
		return ExtendedCompletableFuture.of(CompletableFuture.completedFuture(value));
	}

	/**
	 * Generates a new ExtendedCompletableFuture from an existing CompletableFuture
	 *
	 * @param pFuture the existing CompletableFuture
	 * @return the new ExtendedCompletableFuture
	 */
	public static <U> ExtendedCompletableFuture<U> of(CompletableFuture<U> pFuture) {
		return new ExtendedCompletableFuture<U>(decomposeToCompletableFuture(pFuture));
	}

	/**
	 * Continues if the result is null
	 *
	 * @param pFunc the function
	 * @return the future
	 */
	@Override
	public ExtendedCompletableFuture<T> continueIfNull(Supplier<T> pFunc) {
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
	public ExtendedCompletableFuture<T> continueComposeIfNull(Supplier<CompletionStage<T>> pFunc) {
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
	public ExtendedCompletableFuture<T> continueAsyncIfNull(Supplier<T> pFunc) {
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
	public static <U> ExtendedCompletableFuture<U> completedFailure(Throwable pEx) {
		CompletableFuture<U> result = new CompletableFuture<U>();
		result.completeExceptionally(pEx);
		return ExtendedCompletableFuture.of(result);
	}

	/**
	 * Continues to compose if
	 *
	 * @param pClass the class
	 * @param pFunc the function
	 * @return the future
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <C, U> ExtendedCompletableFuture<?> continueComposeIf(Class<C> pClass,
		Function<C, @NonNull ? extends @NonNull CompletionStage<U>> pFunc) {
		@SuppressWarnings("null")
		Function<? super T, @NonNull ? extends CompletionStage<Object>> fn = (result) -> {
			if (result != null) {
				if (pClass.isInstance(result) == true) {
					C input = (C) result;
					return (CompletionStage<Object>) pFunc.apply(input);
				}
			}
			return (CompletionStage<Object>) result;
		};
		return ExtendedCompletableFuture.of(mDelegate.thenCompose(new TracerFunction<>(fn)));
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
	public <C, U> ExtendedCompletableFuture<?> continueIf(Class<C> pClass, Function<C, U> pFunc) {
		Function<T, U> fn = result -> {
			if (result != null) {
				if (pClass.isInstance(result) == true) {
					C input = (C) result;
					return pFunc.apply(input);
				}
			}
			return (U) result;
		};
		return ExtendedCompletableFuture.of(mDelegate.thenApply(new TracerFunction<>(fn)));
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
	public <R> ExtendedCompletableFuture<R> splitCompose(Predicate<T> pBoolFunc,
		Function<T, @NonNull ? extends @NonNull CompletionStage<R>> pTrueFunc,
		Function<T, @NonNull ? extends @NonNull CompletionStage<R>> pFalseFunc) {
		Function<? super T, @NonNull ? extends CompletionStage<R>> fn = (input) -> {
			if (pBoolFunc.test(input) == true)
				return pTrueFunc.apply(input);
			else
				return pFalseFunc.apply(input);
		};
		return ExtendedCompletableFuture.of(mDelegate.thenCompose(new TracerFunction<>(fn)));
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
	public <R> ExtendedCompletableFuture<R> splitApply(Predicate<T> pBoolFunc, Function<T, ? extends R> pTrueFunc,
		Function<T, ? extends R> pFalseFunc) {
		Function<? super T, ? extends R> fn = (input) -> {
			if (pBoolFunc.test(input) == true)
				return pTrueFunc.apply(input);
			else
				return pFalseFunc.apply(input);
		};
		return ExtendedCompletableFuture.of(mDelegate.thenApply(new TracerFunction<>(fn)));
	}

}

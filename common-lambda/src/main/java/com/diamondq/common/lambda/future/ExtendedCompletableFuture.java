package com.diamondq.common.lambda.future;

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
public class ExtendedCompletableFuture<T> extends CompletableFuture<T> {

	private CompletableFuture<T> mDelegate;

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
	public T get() throws InterruptedException, ExecutionException {
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
		return ExtendedCompletableFuture.of(mDelegate.thenApply(pFn));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.thenApplyAsync(pFn));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> pFn,
		@Nullable Executor pExecutor) {
		return ExtendedCompletableFuture.of(mDelegate.thenApplyAsync(pFn, pExecutor));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> thenAccept(Consumer<? super T> pAction) {
		return ExtendedCompletableFuture.of(mDelegate.thenAccept(pAction));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> thenAcceptAsync(Consumer<? super T> pAction) {
		return ExtendedCompletableFuture.of(mDelegate.thenAcceptAsync(pAction));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> thenAcceptAsync(Consumer<? super T> pAction,
		@Nullable Executor pExecutor) {
		return ExtendedCompletableFuture.of(mDelegate.thenAcceptAsync(pAction, pExecutor));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> thenRun(Runnable pAction) {
		return ExtendedCompletableFuture.of(mDelegate.thenRun(pAction));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> thenRunAsync(Runnable pAction) {
		return ExtendedCompletableFuture.of(mDelegate.thenRunAsync(pAction));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> thenRunAsync(Runnable pAction, Executor pExecutor) {
		return ExtendedCompletableFuture.of(mDelegate.thenRunAsync(pAction, pExecutor));
	}

	@Override
	public <U, V> ExtendedCompletableFuture<V> thenCombine(CompletionStage<? extends U> pOther,
		@Nullable BiFunction<? super T, ? super U, ? extends V> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.thenCombine(decomposeToCompletionStage(pOther), pFn));
	}

	@Override
	public <U, V> ExtendedCompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> pOther,
		@Nullable BiFunction<? super T, ? super U, ? extends V> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.thenCombineAsync(decomposeToCompletionStage(pOther), pFn));
	}

	@Override
	public <U, V> ExtendedCompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> pOther,
		@Nullable BiFunction<? super T, ? super U, ? extends V> pFn, @Nullable Executor pExecutor) {
		return ExtendedCompletableFuture
			.of(mDelegate.thenCombineAsync(decomposeToCompletionStage(pOther), pFn, pExecutor));
	}

	@Override
	public <U> ExtendedCompletableFuture<@Nullable Void> thenAcceptBoth(CompletionStage<? extends U> pOther,
		@Nullable BiConsumer<? super T, ? super U> pAction) {
		return ExtendedCompletableFuture.of(mDelegate.thenAcceptBoth(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public <U> ExtendedCompletableFuture<@Nullable Void> thenAcceptBothAsync(CompletionStage<? extends U> pOther,
		@Nullable BiConsumer<? super T, ? super U> pAction) {
		return ExtendedCompletableFuture.of(mDelegate.thenAcceptBothAsync(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public <U> ExtendedCompletableFuture<@Nullable Void> thenAcceptBothAsync(CompletionStage<? extends U> pOther,
		@Nullable BiConsumer<? super T, ? super U> pAction, @Nullable Executor pExecutor) {
		return ExtendedCompletableFuture
			.of(mDelegate.thenAcceptBothAsync(decomposeToCompletionStage(pOther), pAction, pExecutor));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> runAfterBoth(CompletionStage<?> pOther,
		@Nullable Runnable pAction) {
		return ExtendedCompletableFuture.of(mDelegate.runAfterBoth(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> runAfterBothAsync(CompletionStage<?> pOther,
		@Nullable Runnable pAction) {
		return ExtendedCompletableFuture.of(mDelegate.runAfterBothAsync(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> runAfterBothAsync(CompletionStage<?> pOther,
		@Nullable Runnable pAction, @Nullable Executor pExecutor) {
		return ExtendedCompletableFuture
			.of(mDelegate.runAfterBothAsync(decomposeToCompletionStage(pOther), pAction, pExecutor));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> applyToEither(CompletionStage<? extends T> pOther,
		@Nullable Function<? super T, U> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.applyToEither(decomposeToCompletionStage(pOther), pFn));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> pOther,
		@Nullable Function<? super T, U> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.applyToEitherAsync(decomposeToCompletionStage(pOther), pFn));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> pOther,
		@Nullable Function<? super T, U> pFn, @Nullable Executor pExecutor) {
		return ExtendedCompletableFuture
			.of(mDelegate.applyToEitherAsync(decomposeToCompletionStage(pOther), pFn, pExecutor));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> acceptEither(CompletionStage<? extends T> pOther,
		@Nullable Consumer<? super T> pAction) {
		return ExtendedCompletableFuture.of(mDelegate.acceptEither(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> acceptEitherAsync(CompletionStage<? extends T> pOther,
		@Nullable Consumer<? super T> pAction) {
		return ExtendedCompletableFuture.of(mDelegate.acceptEitherAsync(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> acceptEitherAsync(CompletionStage<? extends T> pOther,
		@Nullable Consumer<? super T> pAction, @Nullable Executor pExecutor) {
		return ExtendedCompletableFuture
			.of(mDelegate.acceptEitherAsync(decomposeToCompletionStage(pOther), pAction, pExecutor));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> runAfterEither(CompletionStage<?> pOther,
		@Nullable Runnable pAction) {
		return ExtendedCompletableFuture.of(mDelegate.runAfterEither(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> runAfterEitherAsync(CompletionStage<?> pOther,
		@Nullable Runnable pAction) {
		return ExtendedCompletableFuture.of(mDelegate.runAfterEitherAsync(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public ExtendedCompletableFuture<@Nullable Void> runAfterEitherAsync(CompletionStage<?> pOther,
		@Nullable Runnable pAction, @Nullable Executor pExecutor) {
		return ExtendedCompletableFuture
			.of(mDelegate.runAfterEitherAsync(decomposeToCompletionStage(pOther), pAction, pExecutor));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> thenCompose(
		Function<? super T, @NonNull ? extends @NonNull CompletionStage<U>> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.thenCompose(pFn));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> thenComposeAsync(
		Function<? super T, @NonNull ? extends @NonNull CompletionStage<U>> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.thenComposeAsync(pFn));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> thenComposeAsync(
		Function<? super T, @NonNull ? extends @NonNull CompletionStage<U>> pFn, Executor pExecutor) {
		return ExtendedCompletableFuture.of(mDelegate.thenComposeAsync(pFn, pExecutor));
	}

	@Override
	public ExtendedCompletableFuture<T> whenComplete(
		BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction) {
		return ExtendedCompletableFuture.of(mDelegate.whenComplete(pAction));
	}

	@Override
	public ExtendedCompletableFuture<T> whenCompleteAsync(
		BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction) {
		return ExtendedCompletableFuture.of(mDelegate.whenCompleteAsync(pAction));
	}

	@Override
	public ExtendedCompletableFuture<T> whenCompleteAsync(
		BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction, @Nullable Executor pExecutor) {
		return ExtendedCompletableFuture.of(mDelegate.whenCompleteAsync(pAction, pExecutor));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> handle(BiFunction<? super T, @Nullable Throwable, ? extends U> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.handle(pFn));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> handleAsync(BiFunction<? super T, @Nullable Throwable, ? extends U> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.handleAsync(pFn));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> handleAsync(BiFunction<? super T, @Nullable Throwable, ? extends U> pFn,
		@Nullable Executor pExecutor) {
		return ExtendedCompletableFuture.of(mDelegate.handleAsync(pFn, pExecutor));
	}

	@Override
	public CompletableFuture<T> toCompletableFuture() {
		return mDelegate.toCompletableFuture();
	}

	@Override
	public ExtendedCompletableFuture<T> exceptionally(Function<Throwable, ? extends T> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.exceptionally(pFn));
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
		return ExtendedCompletableFuture.of(CompletableFuture.supplyAsync(supplier));
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
		return ExtendedCompletableFuture.of(CompletableFuture.supplyAsync(supplier, executor));
	}

	/**
	 * Returns a new CompletableFuture that is asynchronously completed by a task running in the
	 * {@link ForkJoinPool#commonPool()} after it runs the given action.
	 *
	 * @param runnable the action to run before completing the returned CompletableFuture
	 * @return the new CompletableFuture
	 */
	public static ExtendedCompletableFuture<@Nullable Void> runAsync(Runnable runnable) {
		return ExtendedCompletableFuture.of(CompletableFuture.runAsync(runnable));
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
		return ExtendedCompletableFuture.of(CompletableFuture.runAsync(runnable, executor));
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
	public <U> ExtendedCompletableFuture<?> continueIfNull(Function<@Nullable T, U> pFunc) {
		return ExtendedCompletableFuture.of(mDelegate.thenApply(result -> {
			if (result != null)
				return result;
			return pFunc.apply(null);
		}));
	}

	/**
	 * Continues the compose if null
	 * 
	 * @param pFunc the function
	 * @return the future
	 */
	@SuppressWarnings("unchecked")
	public <U> ExtendedCompletableFuture<?> continueComposeIfNull(
		Function<@Nullable T, @NonNull ? extends @NonNull CompletionStage<U>> pFunc) {
		return ExtendedCompletableFuture.of(mDelegate.thenCompose(result -> {
			if (result != null) {
				CompletableFuture<@Nullable T> future = CompletableFuture.completedFuture(result);
				CompletionStage<Object> finalFuture = (CompletionStage<Object>) future;
				return finalFuture;
			}
			return (CompletionStage<Object>) pFunc.apply(null);
		}));
	}

	/**
	 * Continues async if null
	 * 
	 * @param pFunc the function
	 * @return the future
	 */
	public <U> ExtendedCompletableFuture<?> continueAsyncIfNull(Function<@Nullable T, U> pFunc) {
		return ExtendedCompletableFuture.of(mDelegate.thenApplyAsync(result -> {
			if (result != null)
				return result;
			return pFunc.apply(null);
		}));
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
	@SuppressWarnings("unchecked")
	public <C, U> ExtendedCompletableFuture<?> continueComposeIf(Class<C> pClass,
		Function<C, @NonNull ? extends @NonNull CompletionStage<U>> pFunc) {
		return ExtendedCompletableFuture.of(mDelegate.thenCompose(result -> {
			if (result != null) {
				if (pClass.isInstance(result) == true) {
					C input = (C) result;
					return (CompletionStage<Object>) pFunc.apply(input);
				}
			}
			return (CompletionStage<Object>) result;
		}));
	}

	/**
	 * Continues if
	 * 
	 * @param pClass the class
	 * @param pFunc the function
	 * @return the future
	 */
	@SuppressWarnings("unchecked")
	public <C, U> ExtendedCompletableFuture<?> continueIf(Class<C> pClass, Function<C, U> pFunc) {
		return ExtendedCompletableFuture.of(mDelegate.thenApply(result -> {
			if (result != null) {
				if (pClass.isInstance(result) == true) {
					C input = (C) result;
					return pFunc.apply(input);
				}
			}
			return result;
		}));
	}

	/**
	 * Splits a compose into two tracks
	 * 
	 * @param pBoolFunc the boolean function
	 * @param pTrueFunc the true side
	 * @param pFalseFunc the false side
	 * @return the future
	 */
	public <R> ExtendedCompletableFuture<R> splitCompose(Predicate<T> pBoolFunc,
		Function<T, @NonNull ? extends @NonNull CompletionStage<R>> pTrueFunc,
		Function<T, @NonNull ? extends @NonNull CompletionStage<R>> pFalseFunc) {
		return ExtendedCompletableFuture.of(mDelegate.thenCompose((input) -> {
			if (pBoolFunc.test(input) == true)
				return pTrueFunc.apply(input);
			else
				return pFalseFunc.apply(input);
		}));
	}

	/**
	 * Split based apply
	 * 
	 * @param pBoolFunc the boolean function
	 * @param pTrueFunc the true result
	 * @param pFalseFunc the false result
	 * @return the future
	 */
	public <R> ExtendedCompletableFuture<R> splitApply(Predicate<T> pBoolFunc, Function<T, ? extends R> pTrueFunc,
		Function<T, ? extends R> pFalseFunc) {
		return ExtendedCompletableFuture.of(mDelegate.thenApply((input) -> {
			if (pBoolFunc.test(input) == true)
				return pTrueFunc.apply(input);
			else
				return pFalseFunc.apply(input);
		}));
	}

	/**
	 * The state object for these 'future' based loops
	 * 
	 * @param <INPUT>
	 * @param <STARTPRE>
	 * @param <STARTRESULT>
	 * @param <STARTPOST>
	 * @param <ACTIONPRE>
	 * @param <ACTIONRESULT>
	 * @param <ACTIONPOST>
	 * @param <TESTPRE>
	 * @param <TESTRESULT>
	 * @param <TESTPOST>
	 * @param <ENDPRE>
	 * @param <ENDRESULT>
	 * @param <ENDPOST>
	 */
	public static class LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST> {

		/**
		 * The input
		 */
		public volatile INPUT			input;

		/**
		 * The before start
		 */

		public volatile STARTPRE		startPre;

		/**
		 * The start result
		 */
		public volatile STARTRESULT		startResult;

		/**
		 * The after start
		 */
		public volatile STARTPOST		startPost;

		/**
		 * The before action
		 */
		public volatile ACTIONPRE		actionPre;

		/**
		 * The action
		 */
		public volatile ACTIONRESULT	actionResult;

		/**
		 * The after action
		 */
		public volatile ACTIONPOST		actionPost;

		/**
		 * The before test
		 */
		public volatile TESTPRE			testPre;

		/**
		 * The test
		 */
		public volatile TESTRESULT		testResult;

		/**
		 * The after test
		 */
		public volatile TESTPOST		testPost;

		/**
		 * The before end
		 */
		public volatile ENDPRE			endPre;

		/**
		 * The end
		 */
		public volatile ENDRESULT		endResult;

		/**
		 * The after end
		 */
		public volatile ENDPOST			endPost;

		/**
		 * The default constructor
		 * 
		 * @param pInput the input
		 */
		@SuppressWarnings("null")
		public LoopState(INPUT pInput) {
			input = pInput;
		}
	}

	private static <INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST> ExtendedCompletableFuture<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> startLoop(
		ExtendedCompletableFuture<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current,
		@Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPRE> pStartPreFunction,
		@Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletableFuture<STARTRESULT>> pStartFunction,
		@Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPOST> pStartPostFunction) {

		/* Perform the start pre */

		if (pStartPreFunction != null)
			current = current.thenApply(state -> {
				state.startPre = pStartPreFunction.apply(state);
				return state;
			});

		/* Perform the start */

		if (pStartFunction != null)
			current = current.thenCompose(state -> {
				ExtendedCompletableFuture<STARTRESULT> startFunctionResult = pStartFunction.apply(state);
				return startFunctionResult.thenApply(i -> {
					state.startResult = i;
					return state;
				});
			});

		/* Perform the start post */

		if (pStartPostFunction != null)
			current = current.thenApply(state -> {
				state.startPost = pStartPostFunction.apply(state);
				return state;
			});

		return current;
	}

	private static <INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST> void performDoWhile(
		ExtendedCompletableFuture<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current,
		@Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPRE> pActionPreFunction,
		@Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, @NonNull ExtendedCompletableFuture<ACTIONRESULT>> pActionFunction,
		@Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPOST> pActionPostFunction,
		@Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPRE> pTestPreFunction,
		@Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, @NonNull ExtendedCompletableFuture<TESTRESULT>> pTestFunction,
		@Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPOST> pTestPostFunction,
		@Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPRE> pEndPreFunction,
		@Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, @NonNull ExtendedCompletableFuture<ENDRESULT>> pEndFunction,
		@Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPOST> pEndPostFunction,
		ExtendedCompletableFuture<ENDPOST> pFinalResult) {

		/* Do the work */

		/* Perform the action pre */

		if (pActionPreFunction != null)
			current = current.thenApply(state -> {
				state.actionPre = pActionPreFunction.apply(state);
				return state;
			});

		/* Perform the action */

		if (pActionFunction != null)
			current = current.thenCompose(state -> {
				ExtendedCompletableFuture<ACTIONRESULT> actionFunctionResult = pActionFunction.apply(state);
				return actionFunctionResult.thenApply(i -> {
					state.actionResult = i;
					return state;
				});
			});

		/* Perform the action post */

		if (pActionPostFunction != null)
			current = current.thenApply(state -> {
				state.actionPost = pActionPostFunction.apply(state);
				return state;
			});

		/* Now check to see if we're done */

		/* Perform the test pre */

		if (pTestPreFunction != null)
			current = current.thenApply(state -> {
				state.testPre = pTestPreFunction.apply(state);
				return state;
			});

		/* Perform the test */

		if (pTestFunction != null)
			current = current.thenCompose(state -> {
				ExtendedCompletableFuture<TESTRESULT> testFunctionResult = pTestFunction.apply(state);
				return testFunctionResult.thenApply(i -> {
					state.testResult = i;
					return state;
				});
			});

		/* Perform the test post */

		if (pTestPostFunction != null)
			current = current.thenApply(state -> {
				state.testPost = pTestPostFunction.apply(state);
				return state;
			});

		current = current.whenComplete((state, ex) -> {
			if (ex != null) {
				pFinalResult.completeExceptionally(ex);
				return;
			}

			try {
				if (((state.testPre instanceof Boolean) && (((Boolean) state.testPre) == false))
					|| ((state.testResult instanceof Boolean) && (((Boolean) state.testResult) == false))
					|| ((state.testPost instanceof Boolean) && (((Boolean) state.testPost) == false))) {

					/* We're finished running */

					ExtendedCompletableFuture<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> start =
						ExtendedCompletableFuture.completedFuture(state);

					endLoop(start, pEndPreFunction, pEndFunction, pEndPostFunction, pFinalResult);

				}
				else {

					/* We're not finished, so schedule another run */

					CompletableFuture.runAsync(() -> {
						ExtendedCompletableFuture<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> start =
							ExtendedCompletableFuture.completedFuture(state);
						performDoWhile(start, pActionPreFunction, pActionFunction, pActionPostFunction,
							pTestPreFunction, pTestFunction, pTestPostFunction, pEndPreFunction, pEndFunction,
							pEndPostFunction, pFinalResult);
					}).whenComplete((ignore2, ex2) -> {
						if (ex2 != null)
							pFinalResult.completeExceptionally(ex2);
					});

				}
			}
			catch (RuntimeException ex2) {
				pFinalResult.completeExceptionally(ex2);
			}

		});
	}

	private static <INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST> void endLoop(
		ExtendedCompletableFuture<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current,
		@Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPRE> pEndPreFunction,
		@Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, @NonNull ExtendedCompletableFuture<ENDRESULT>> pEndFunction,
		@Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPOST> pEndPostFunction,
		ExtendedCompletableFuture<ENDPOST> pFinalResult) {
		try {

			/* Perform the end pre */

			if (pEndPreFunction != null)
				current = current.thenApply(state -> {
					state.endPre = pEndPreFunction.apply(state);
					return state;
				});

			/* Perform the end */

			if (pEndFunction != null)
				current = current.thenCompose(state -> {
					ExtendedCompletableFuture<ENDRESULT> endFunctionResult = pEndFunction.apply(state);
					return endFunctionResult.thenApply(i -> {
						state.endResult = i;
						return state;
					});
				});

			/* Perform the end post */

			if (pEndPostFunction != null)
				current = current.thenApply(state -> {
					state.endPost = pEndPostFunction.apply(state);
					return state;
				});

			current.whenComplete((state, error) -> {
				if (error != null) {
					pFinalResult.completeExceptionally(error);
					return;
				}

				pFinalResult.complete(state.endPost);
			});

		}
		catch (RuntimeException ex) {
			pFinalResult.completeExceptionally(ex);
		}
	}

	/**
	 * @param pStartPreFunction this is called first with the result of the calling future. Can be null
	 * @param pStartFunction the result of the calling future, and the start-pre function are passed. The result is a
	 *            future
	 * @param pStartPostFunction this is called third with the previous results.
	 * @param pActionPreFunction
	 * @param pActionFunction
	 * @param pActionPostFunction
	 * @param pTestPreFunction
	 * @param pTestFunction
	 * @param pTestPostFunction
	 * @param pEndPreFunction
	 * @param pEndFunction
	 * @param pEndPostFunction
	 * @return the result
	 */
	public <@Nullable STARTPRE, @Nullable STARTRESULT, @Nullable STARTPOST, @Nullable ACTIONPRE, @Nullable ACTIONRESULT, @Nullable ACTIONPOST, @Nullable TESTPRE, @Nullable TESTRESULT, @Nullable TESTPOST, @Nullable ENDPRE, @Nullable ENDRESULT, @Nullable ENDPOST> ExtendedCompletableFuture<ENDPOST> thenDoWhile(
		@Nullable Function<@NonNull LoopState<@Nullable T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPRE> pStartPreFunction,
		@Nullable Function<@NonNull LoopState<@Nullable T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletableFuture<STARTRESULT>> pStartFunction,
		@Nullable Function<@NonNull LoopState<@Nullable T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPOST> pStartPostFunction,
		@Nullable Function<@NonNull LoopState<@Nullable T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPRE> pActionPreFunction,
		@Nullable Function<@NonNull LoopState<@Nullable T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletableFuture<ACTIONRESULT>> pActionFunction,
		@Nullable Function<@NonNull LoopState<@Nullable T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPOST> pActionPostFunction,
		@Nullable Function<@NonNull LoopState<@Nullable T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPRE> pTestPreFunction,
		@Nullable Function<@NonNull LoopState<@Nullable T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletableFuture<TESTRESULT>> pTestFunction,
		@Nullable Function<@NonNull LoopState<@Nullable T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPOST> pTestPostFunction,
		@Nullable Function<@NonNull LoopState<@Nullable T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPRE> pEndPreFunction,
		@Nullable Function<@NonNull LoopState<@Nullable T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletableFuture<ENDRESULT>> pEndFunction,
		@Nullable Function<@NonNull LoopState<@Nullable T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPOST> pEndPostFunction) {

		ExtendedCompletableFuture<ENDPOST> finalResult = new ExtendedCompletableFuture<ENDPOST>();

		/* Setup the LoopState object */

		ExtendedCompletableFuture<@Nullable LoopState<@Nullable T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> applyResult =
			thenApply(
				input -> new LoopState<@Nullable T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>(
					input));
		@SuppressWarnings("null")
		ExtendedCompletableFuture<@NonNull LoopState<@Nullable T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current =
			(ExtendedCompletableFuture<@NonNull LoopState<@Nullable T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>>) applyResult;

		current = startLoop(current, pStartPreFunction, pStartFunction, pStartPostFunction);

		performDoWhile(current, pActionPreFunction, pActionFunction, pActionPostFunction, pTestPreFunction,
			pTestFunction, pTestPostFunction, pEndPreFunction, pEndFunction, pEndPostFunction, finalResult);

		return finalResult;

	}

}

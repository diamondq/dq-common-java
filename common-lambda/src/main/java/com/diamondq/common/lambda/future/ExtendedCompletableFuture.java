package com.diamondq.common.lambda.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class ExtendedCompletableFuture<T> extends CompletableFuture<T> {

	private CompletableFuture<T> mDelegate;

	private static <U> CompletionStage<U> decomposeToCompletionStage(CompletionStage<U> pStage) {
		if (pStage == null)
			return null;
		if (pStage instanceof ExtendedCompletableFuture)
			return ((ExtendedCompletableFuture<U>) pStage).mDelegate;
		return pStage;
	}

	private static <U> CompletableFuture<U> decomposeToCompletableFuture(CompletableFuture<U> pFuture) {
		if (pFuture == null)
			return null;
		if (pFuture instanceof ExtendedCompletableFuture)
			return ((ExtendedCompletableFuture<U>) pFuture).mDelegate;
		return pFuture;
	}

	@Override
	public int hashCode() {
		return mDelegate.hashCode();
	}

	@Override
	public boolean equals(Object pObj) {
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
	public <U> ExtendedCompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> pFn, Executor pExecutor) {
		return ExtendedCompletableFuture.of(mDelegate.thenApplyAsync(pFn, pExecutor));
	}

	@Override
	public ExtendedCompletableFuture<Void> thenAccept(Consumer<? super T> pAction) {
		return ExtendedCompletableFuture.of(mDelegate.thenAccept(pAction));
	}

	@Override
	public ExtendedCompletableFuture<Void> thenAcceptAsync(Consumer<? super T> pAction) {
		return ExtendedCompletableFuture.of(mDelegate.thenAcceptAsync(pAction));
	}

	@Override
	public ExtendedCompletableFuture<Void> thenAcceptAsync(Consumer<? super T> pAction, Executor pExecutor) {
		return ExtendedCompletableFuture.of(mDelegate.thenAcceptAsync(pAction, pExecutor));
	}

	@Override
	public ExtendedCompletableFuture<Void> thenRun(Runnable pAction) {
		return ExtendedCompletableFuture.of(mDelegate.thenRun(pAction));
	}

	@Override
	public ExtendedCompletableFuture<Void> thenRunAsync(Runnable pAction) {
		return ExtendedCompletableFuture.of(mDelegate.thenRunAsync(pAction));
	}

	@Override
	public ExtendedCompletableFuture<Void> thenRunAsync(Runnable pAction, Executor pExecutor) {
		return ExtendedCompletableFuture.of(mDelegate.thenRunAsync(pAction, pExecutor));
	}

	@Override
	public <U, V> ExtendedCompletableFuture<V> thenCombine(CompletionStage<? extends U> pOther,
		BiFunction<? super T, ? super U, ? extends V> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.thenCombine(decomposeToCompletionStage(pOther), pFn));
	}

	@Override
	public <U, V> ExtendedCompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> pOther,
		BiFunction<? super T, ? super U, ? extends V> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.thenCombineAsync(decomposeToCompletionStage(pOther), pFn));
	}

	@Override
	public <U, V> ExtendedCompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> pOther,
		BiFunction<? super T, ? super U, ? extends V> pFn, Executor pExecutor) {
		return ExtendedCompletableFuture
			.of(mDelegate.thenCombineAsync(decomposeToCompletionStage(pOther), pFn, pExecutor));
	}

	@Override
	public <U> ExtendedCompletableFuture<Void> thenAcceptBoth(CompletionStage<? extends U> pOther,
		BiConsumer<? super T, ? super U> pAction) {
		return ExtendedCompletableFuture.of(mDelegate.thenAcceptBoth(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public <U> ExtendedCompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> pOther,
		BiConsumer<? super T, ? super U> pAction) {
		return ExtendedCompletableFuture.of(mDelegate.thenAcceptBothAsync(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public <U> ExtendedCompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> pOther,
		BiConsumer<? super T, ? super U> pAction, Executor pExecutor) {
		return ExtendedCompletableFuture
			.of(mDelegate.thenAcceptBothAsync(decomposeToCompletionStage(pOther), pAction, pExecutor));
	}

	@Override
	public ExtendedCompletableFuture<Void> runAfterBoth(CompletionStage<?> pOther, Runnable pAction) {
		return ExtendedCompletableFuture.of(mDelegate.runAfterBoth(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public ExtendedCompletableFuture<Void> runAfterBothAsync(CompletionStage<?> pOther, Runnable pAction) {
		return ExtendedCompletableFuture.of(mDelegate.runAfterBothAsync(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public ExtendedCompletableFuture<Void> runAfterBothAsync(CompletionStage<?> pOther, Runnable pAction,
		Executor pExecutor) {
		return ExtendedCompletableFuture
			.of(mDelegate.runAfterBothAsync(decomposeToCompletionStage(pOther), pAction, pExecutor));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> applyToEither(CompletionStage<? extends T> pOther,
		Function<? super T, U> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.applyToEither(decomposeToCompletionStage(pOther), pFn));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> pOther,
		Function<? super T, U> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.applyToEitherAsync(decomposeToCompletionStage(pOther), pFn));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> pOther,
		Function<? super T, U> pFn, Executor pExecutor) {
		return ExtendedCompletableFuture
			.of(mDelegate.applyToEitherAsync(decomposeToCompletionStage(pOther), pFn, pExecutor));
	}

	@Override
	public ExtendedCompletableFuture<Void> acceptEither(CompletionStage<? extends T> pOther,
		Consumer<? super T> pAction) {
		return ExtendedCompletableFuture.of(mDelegate.acceptEither(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public ExtendedCompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> pOther,
		Consumer<? super T> pAction) {
		return ExtendedCompletableFuture.of(mDelegate.acceptEitherAsync(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public ExtendedCompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> pOther,
		Consumer<? super T> pAction, Executor pExecutor) {
		return ExtendedCompletableFuture
			.of(mDelegate.acceptEitherAsync(decomposeToCompletionStage(pOther), pAction, pExecutor));
	}

	@Override
	public ExtendedCompletableFuture<Void> runAfterEither(CompletionStage<?> pOther, Runnable pAction) {
		return ExtendedCompletableFuture.of(mDelegate.runAfterEither(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public ExtendedCompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> pOther, Runnable pAction) {
		return ExtendedCompletableFuture.of(mDelegate.runAfterEitherAsync(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public ExtendedCompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> pOther, Runnable pAction,
		Executor pExecutor) {
		return ExtendedCompletableFuture
			.of(mDelegate.runAfterEitherAsync(decomposeToCompletionStage(pOther), pAction, pExecutor));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.thenCompose(pFn));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.thenComposeAsync(pFn));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> pFn,
		Executor pExecutor) {
		return ExtendedCompletableFuture.of(mDelegate.thenComposeAsync(pFn, pExecutor));
	}

	@Override
	public ExtendedCompletableFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> pAction) {
		return ExtendedCompletableFuture.of(mDelegate.whenComplete(pAction));
	}

	@Override
	public ExtendedCompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> pAction) {
		return ExtendedCompletableFuture.of(mDelegate.whenCompleteAsync(pAction));
	}

	@Override
	public ExtendedCompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> pAction,
		Executor pExecutor) {
		return ExtendedCompletableFuture.of(mDelegate.whenCompleteAsync(pAction, pExecutor));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> handle(BiFunction<? super T, Throwable, ? extends U> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.handle(pFn));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> pFn) {
		return ExtendedCompletableFuture.of(mDelegate.handleAsync(pFn));
	}

	@Override
	public <U> ExtendedCompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> pFn,
		Executor pExecutor) {
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

	public ExtendedCompletableFuture() {
		mDelegate = new CompletableFuture<T>();
	}

	private ExtendedCompletableFuture(CompletableFuture<T> pFuture) {
		mDelegate = pFuture;
	}

	public static ExtendedCompletableFuture<Void> allOf(CompletableFuture<?>... cfs) {
		CompletableFuture<?>[] args = new CompletableFuture<?>[cfs.length];
		for (int i = 0; i < cfs.length; i++)
			args[i] = decomposeToCompletableFuture(cfs[i]);
		return ExtendedCompletableFuture.of(CompletableFuture.allOf(args));
	}

	public static ExtendedCompletableFuture<Object> anyOf(CompletableFuture<?>... cfs) {
		CompletableFuture<?>[] args = new CompletableFuture<?>[cfs.length];
		for (int i = 0; i < cfs.length; i++)
			args[i] = decomposeToCompletableFuture(cfs[i]);
		return ExtendedCompletableFuture.of(CompletableFuture.anyOf(args));
	}

	public static <U> ExtendedCompletableFuture<U> completedFuture(U value) {
		return ExtendedCompletableFuture.of(CompletableFuture.completedFuture(value));
	}

	public static <U> ExtendedCompletableFuture<U> of(CompletableFuture<U> pFuture) {
		return new ExtendedCompletableFuture<U>(decomposeToCompletableFuture(pFuture));
	}

	public <U> ExtendedCompletableFuture<?> continueIfNull(Function<T, U> pFunc) {
		return ExtendedCompletableFuture.of(mDelegate.thenApply(result -> {
			if (result != null)
				return result;
			return pFunc.apply(null);
		}));
	}

	@SuppressWarnings("unchecked")
	public <U> ExtendedCompletableFuture<?> continueComposeIfNull(Function<T, ? extends CompletionStage<U>> pFunc) {
		return ExtendedCompletableFuture.of(mDelegate.thenCompose(result -> {
			if (result != null)
				return CompletableFuture.completedFuture(result);
			return (CompletionStage<Object>) pFunc.apply(null);
		}));
	}

	public <U> ExtendedCompletableFuture<?> continueAsyncIfNull(Function<T, U> pFunc) {
		return ExtendedCompletableFuture.of(mDelegate.thenApplyAsync(result -> {
			if (result != null)
				return result;
			return pFunc.apply(null);
		}));
	}

	public static <U> ExtendedCompletableFuture<U> completedFailure(Throwable pEx) {
		CompletableFuture<U> result = new CompletableFuture<U>();
		result.completeExceptionally(pEx);
		return ExtendedCompletableFuture.of(result);
	}

	@SuppressWarnings("unchecked")
	public <C, U> ExtendedCompletableFuture<?> continueComposeIf(Class<C> pClass,
		Function<C, ? extends CompletionStage<U>> pFunc) {
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

	public static class LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST> {

		public volatile INPUT			input;

		public volatile STARTPRE		startPre;

		public volatile STARTRESULT		startResult;

		public volatile STARTPOST		startPost;

		public volatile ACTIONPRE		actionPre;

		public volatile ACTIONRESULT	actionResult;

		public volatile ACTIONPOST		actionPost;

		public volatile TESTPRE			testPre;

		public volatile TESTRESULT		testResult;

		public volatile TESTPOST		testPost;

		public volatile ENDPRE			endPre;

		public volatile ENDRESULT		endResult;

		public volatile ENDPOST			endPost;

		public LoopState(INPUT pInput) {
			input = pInput;
		}
	}

	private static <INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST> ExtendedCompletableFuture<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> startLoop(
		ExtendedCompletableFuture<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current,
		Function<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPRE> pStartPreFunction,
		Function<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletableFuture<STARTRESULT>> pStartFunction,
		Function<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPOST> pStartPostFunction) {

		/* Perform the start pre */

		if (pStartPreFunction != null)
			current = current.thenApply(state -> {
				state.startPre = pStartPreFunction.apply(state);
				return state;
			});

		/* Perform the start */

		if (pStartFunction != null)
			current = current.thenCompose(state -> {
				return pStartFunction.apply(state).thenApply(i -> {
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
		ExtendedCompletableFuture<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current,
		Function<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPRE> pActionPreFunction,
		Function<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletableFuture<ACTIONRESULT>> pActionFunction,
		Function<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPOST> pActionPostFunction,
		Function<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPRE> pTestPreFunction,
		Function<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletableFuture<TESTRESULT>> pTestFunction,
		Function<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPOST> pTestPostFunction,
		Function<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPRE> pEndPreFunction,
		Function<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletableFuture<ENDRESULT>> pEndFunction,
		Function<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPOST> pEndPostFunction,
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
				return pActionFunction.apply(state).thenApply(i -> {
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
				return pTestFunction.apply(state).thenApply(i -> {
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

					ExtendedCompletableFuture<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> start =
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
		ExtendedCompletableFuture<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current,
		Function<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPRE> pEndPreFunction,
		Function<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletableFuture<ENDRESULT>> pEndFunction,
		Function<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPOST> pEndPostFunction,
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
					return pEndFunction.apply(state).thenApply(i -> {
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
	public <STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST> ExtendedCompletableFuture<ENDPOST> thenDoWhile(
		Function<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPRE> pStartPreFunction,
		Function<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletableFuture<STARTRESULT>> pStartFunction,
		Function<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPOST> pStartPostFunction,
		Function<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPRE> pActionPreFunction,
		Function<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletableFuture<ACTIONRESULT>> pActionFunction,
		Function<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPOST> pActionPostFunction,
		Function<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPRE> pTestPreFunction,
		Function<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletableFuture<TESTRESULT>> pTestFunction,
		Function<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPOST> pTestPostFunction,
		Function<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPRE> pEndPreFunction,
		Function<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletableFuture<ENDRESULT>> pEndFunction,
		Function<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPOST> pEndPostFunction) {

		ExtendedCompletableFuture<ENDPOST> finalResult = new ExtendedCompletableFuture<ENDPOST>();

		/* Setup the LoopState object */

		ExtendedCompletableFuture<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current =
			thenApply(
				input -> new LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>(
					input));

		current = startLoop(current, pStartPreFunction, pStartFunction, pStartPostFunction);

		performDoWhile(current, pActionPreFunction, pActionFunction, pActionPostFunction, pTestPreFunction,
			pTestFunction, pTestPostFunction, pEndPreFunction, pEndFunction, pEndPostFunction, finalResult);

		return finalResult;

	}

}

package com.diamondq.common.lambda.future;

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
		return ExtendedCompletionStage.of(mDelegate.thenApply(pFn));
	}

	@Override
	public <U> ExtendedCompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> pFn) {
		return ExtendedCompletionStage.of(mDelegate.thenApplyAsync(pFn));
	}

	@Override
	public <U> ExtendedCompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> pFn,
		@Nullable Executor pExecutor) {
		return ExtendedCompletionStage.of(mDelegate.thenApplyAsync(pFn, pExecutor));
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> thenAccept(Consumer<? super T> pAction) {
		return ExtendedCompletionStage.of(mDelegate.thenAccept(pAction));
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer<? super T> pAction) {
		return ExtendedCompletionStage.of(mDelegate.thenAcceptAsync(pAction));
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer<? super T> pAction, Executor pExecutor) {
		return ExtendedCompletionStage.of(mDelegate.thenAcceptAsync(pAction, pExecutor));
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> thenRun(Runnable pAction) {
		return ExtendedCompletionStage.of(mDelegate.thenRun(pAction));
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> thenRunAsync(Runnable pAction) {
		return ExtendedCompletionStage.of(mDelegate.thenRunAsync(pAction));
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> thenRunAsync(Runnable pAction, Executor pExecutor) {
		return ExtendedCompletionStage.of(mDelegate.thenRunAsync(pAction, pExecutor));
	}

	@Override
	public <U, V> ExtendedCompletionStage<V> thenCombine(CompletionStage<? extends U> pOther,
		BiFunction<? super T, ? super U, ? extends V> pFn) {
		return ExtendedCompletionStage.of(mDelegate.thenCombine(decomposeToCompletionStage(pOther), pFn));
	}

	@Override
	public <U, V> ExtendedCompletionStage<V> thenCombineAsync(CompletionStage<? extends U> pOther,
		BiFunction<? super T, ? super U, ? extends V> pFn) {
		return ExtendedCompletionStage.of(mDelegate.thenCombineAsync(decomposeToCompletionStage(pOther), pFn));
	}

	@Override
	public <U, V> ExtendedCompletionStage<V> thenCombineAsync(CompletionStage<? extends U> pOther,
		BiFunction<? super T, ? super U, ? extends V> pFn, @Nullable Executor pExecutor) {
		return ExtendedCompletionStage
			.of(mDelegate.thenCombineAsync(decomposeToCompletionStage(pOther), pFn, pExecutor));
	}

	@Override
	public <U> ExtendedCompletionStage<@Nullable Void> thenAcceptBoth(CompletionStage<? extends U> pOther,
		BiConsumer<? super T, ? super U> pAction) {
		return ExtendedCompletionStage.of(mDelegate.thenAcceptBoth(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public <U> ExtendedCompletionStage<@Nullable Void> thenAcceptBothAsync(CompletionStage<? extends U> pOther,
		BiConsumer<? super T, ? super U> pAction) {
		return ExtendedCompletionStage.of(mDelegate.thenAcceptBothAsync(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public <U> ExtendedCompletionStage<@Nullable Void> thenAcceptBothAsync(CompletionStage<? extends U> pOther,
		BiConsumer<? super T, ? super U> pAction, @Nullable Executor pExecutor) {
		return ExtendedCompletionStage
			.of(mDelegate.thenAcceptBothAsync(decomposeToCompletionStage(pOther), pAction, pExecutor));
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> runAfterBoth(CompletionStage<?> pOther, Runnable pAction) {
		return ExtendedCompletionStage.of(mDelegate.runAfterBoth(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> runAfterBothAsync(CompletionStage<?> pOther, Runnable pAction) {
		return ExtendedCompletionStage.of(mDelegate.runAfterBothAsync(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> runAfterBothAsync(CompletionStage<?> pOther, Runnable pAction,
		Executor pExecutor) {
		return ExtendedCompletionStage
			.of(mDelegate.runAfterBothAsync(decomposeToCompletionStage(pOther), pAction, pExecutor));
	}

	@Override
	public <U> ExtendedCompletionStage<U> applyToEither(CompletionStage<? extends T> pOther,
		Function<? super T, U> pFn) {
		return ExtendedCompletionStage.of(mDelegate.applyToEither(decomposeToCompletionStage(pOther), pFn));
	}

	@Override
	public <U> ExtendedCompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> pOther,
		Function<? super T, U> pFn) {
		return ExtendedCompletionStage.of(mDelegate.applyToEitherAsync(decomposeToCompletionStage(pOther), pFn));
	}

	@Override
	public <U> ExtendedCompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> pOther,
		Function<? super T, U> pFn, @Nullable Executor pExecutor) {
		return ExtendedCompletionStage
			.of(mDelegate.applyToEitherAsync(decomposeToCompletionStage(pOther), pFn, pExecutor));
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> acceptEither(CompletionStage<? extends T> pOther,
		Consumer<? super T> pAction) {
		return ExtendedCompletionStage.of(mDelegate.acceptEither(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> acceptEitherAsync(CompletionStage<? extends T> pOther,
		Consumer<? super T> pAction) {
		return ExtendedCompletionStage.of(mDelegate.acceptEitherAsync(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> acceptEitherAsync(CompletionStage<? extends T> pOther,
		Consumer<? super T> pAction, Executor pExecutor) {
		return ExtendedCompletionStage
			.of(mDelegate.acceptEitherAsync(decomposeToCompletionStage(pOther), pAction, pExecutor));
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> runAfterEither(CompletionStage<?> pOther, Runnable pAction) {
		return ExtendedCompletionStage.of(mDelegate.runAfterEither(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> runAfterEitherAsync(CompletionStage<?> pOther, Runnable pAction) {
		return ExtendedCompletionStage.of(mDelegate.runAfterEitherAsync(decomposeToCompletionStage(pOther), pAction));
	}

	@Override
	public ExtendedCompletionStage<@Nullable Void> runAfterEitherAsync(CompletionStage<?> pOther, Runnable pAction,
		Executor pExecutor) {
		return ExtendedCompletionStage
			.of(mDelegate.runAfterEitherAsync(decomposeToCompletionStage(pOther), pAction, pExecutor));
	}

	@Override
	public <U> ExtendedCompletionStage<U> thenCompose(
		Function<? super T, @NonNull ? extends @NonNull CompletionStage<U>> pFn) {
		return ExtendedCompletionStage.of(mDelegate.thenCompose(pFn));
	}

	@Override
	public <U> ExtendedCompletionStage<U> thenComposeAsync(
		Function<? super T, @NonNull ? extends @NonNull CompletionStage<U>> pFn) {
		return ExtendedCompletionStage.of(mDelegate.thenComposeAsync(pFn));
	}

	@Override
	public <U> ExtendedCompletionStage<U> thenComposeAsync(
		Function<? super T, @NonNull ? extends @NonNull CompletionStage<U>> pFn, Executor pExecutor) {
		return ExtendedCompletionStage.of(mDelegate.thenComposeAsync(pFn, pExecutor));
	}

	@Override
	public ExtendedCompletionStage<T> whenComplete(
		BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction) {
		return ExtendedCompletionStage.of(mDelegate.whenComplete(pAction));
	}

	@Override
	public ExtendedCompletionStage<T> whenCompleteAsync(
		BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction) {
		return ExtendedCompletionStage.of(mDelegate.whenCompleteAsync(pAction));
	}

	@Override
	public ExtendedCompletionStage<T> whenCompleteAsync(
		BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction, @Nullable Executor pExecutor) {
		return ExtendedCompletionStage.of(mDelegate.whenCompleteAsync(pAction, pExecutor));
	}

	@Override
	public <U> ExtendedCompletionStage<U> handle(BiFunction<? super T, @Nullable Throwable, ? extends @NonNull U> pFn) {
		return ExtendedCompletionStage.of(mDelegate.handle(pFn));
	}

	@Override
	public <U> ExtendedCompletionStage<U> handleAsync(BiFunction<? super T, @Nullable Throwable, ? extends U> pFn) {
		return ExtendedCompletionStage.of(mDelegate.handleAsync(pFn));
	}

	@Override
	public <U> ExtendedCompletionStage<U> handleAsync(BiFunction<? super T, @Nullable Throwable, ? extends U> pFn,
		@Nullable Executor pExecutor) {
		return ExtendedCompletionStage.of(mDelegate.handleAsync(pFn, pExecutor));
	}

	@Override
	public CompletableFuture<T> toCompletableFuture() {
		return mDelegate.toCompletableFuture();
	}

	@Override
	public ExtendedCompletionStage<T> exceptionally(Function<Throwable, ? extends T> pFn) {
		return ExtendedCompletionStage.of(mDelegate.exceptionally(pFn));
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
	@SuppressWarnings("unchecked")
	public <C, U> ExtendedCompletionStage<?> continueComposeIf(Class<C> pClass,
		Function<C, @NonNull ? extends @NonNull CompletionStage<U>> pFunc) {
		return ExtendedCompletionStage.of(mDelegate.thenCompose(result -> {
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
	@Override
	@SuppressWarnings("unchecked")
	public <C, U> ExtendedCompletionStage<?> continueIf(Class<C> pClass, Function<C, U> pFunc) {
		return ExtendedCompletionStage.of(mDelegate.thenApply(result -> {
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
	@Override
	public <R> ExtendedCompletionStage<R> splitCompose(Predicate<T> pBoolFunc,
		Function<T, @NonNull ? extends @NonNull CompletionStage<R>> pTrueFunc,
		Function<T, @NonNull ? extends @NonNull CompletionStage<R>> pFalseFunc) {
		return ExtendedCompletionStage.of(mDelegate.thenCompose((input) -> {
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
	@Override
	public <R> ExtendedCompletionStage<R> splitApply(Predicate<T> pBoolFunc, Function<T, ? extends R> pTrueFunc,
		Function<T, ? extends R> pFalseFunc) {
		return ExtendedCompletionStage.of(mDelegate.thenApply((input) -> {
			if (pBoolFunc.test(input) == true)
				return pTrueFunc.apply(input);
			else
				return pFalseFunc.apply(input);
		}));
	}
}

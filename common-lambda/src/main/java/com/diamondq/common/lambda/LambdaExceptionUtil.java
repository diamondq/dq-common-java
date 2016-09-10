package com.diamondq.common.lambda;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Helper util class originated by MarcG on Stackoverflow (http://stackoverflow.com/a/27644392)
 */
public final class LambdaExceptionUtil {

	@FunctionalInterface
	public interface Consumer_WithExceptions<T, E extends Exception> {
		void accept(T t) throws E;
	}

	@FunctionalInterface
	public interface Producer_WithExceptions<T, E extends Exception> {
		T call() throws E;
	}

	@FunctionalInterface
	public interface BiConsumer_WithExceptions<T, U, E extends Exception> {
		void accept(T t, U u) throws E;
	}

	@FunctionalInterface
	public interface BiFunction_WithExceptions<T, U, R, E extends Exception> {
		R apply(T t, U u) throws E;
	}

	@FunctionalInterface
	public interface Function_WithExceptions<T, R, E extends Exception> {
		R apply(T t) throws E;
	}

	@FunctionalInterface
	public interface Supplier_WithExceptions<T, E extends Exception> {
		T get() throws E;
	}

	@FunctionalInterface
	public interface Runnable_WithExceptions<E extends Exception> {
		void run() throws E;
	}

	/*
	 * .forEach(rethrowConsumer(name -> System.out.println(Class.forName(name)))); or
	 * .forEach(rethrowConsumer(ClassNameUtil::println));
	 */
	public static <T, E extends Exception> Consumer<T> rethrowConsumer(Consumer_WithExceptions<T, E> consumer) {
		return t -> {
			try {
				consumer.accept(t);
			}
			catch (Exception exception) {
				throwAsUnchecked(exception);
			}
		};
	}

	public static <P, E extends Exception> Comparator<P> rethrowComparator(
		BiFunction_WithExceptions<P, P, Integer, E> comparator) {
		return (a, b) -> {
			try {
				return comparator.apply(a, b);
			}
			catch (Exception exception) {
				throwAsUnchecked(exception);
				return 0;
			}
		};
	}

	public static <T, U, E extends Exception> BiConsumer<T, U> rethrowBiConsumer(
		BiConsumer_WithExceptions<T, U, E> biConsumer) {
		return (t, u) -> {
			try {
				biConsumer.accept(t, u);
			}
			catch (Exception exception) {
				throwAsUnchecked(exception);
			}
		};
	}

	public static <T, U, R, E extends Exception> BiFunction<T, U, R> rethrowBiFunction(
		BiFunction_WithExceptions<T, U, R, E> biFunction) {
		return (t, u) -> {
			try {
				return biFunction.apply(t, u);
			}
			catch (Exception exception) {
				throwAsUnchecked(exception);
				return null;
			}
		};
	}

	/* .map(rethrowFunction(name -> Class.forName(name))) or .map(rethrowFunction(Class::forName)) */
	public static <T, R, E extends Exception> Function<T, R> rethrowFunction(
		Function_WithExceptions<T, R, E> function) {
		return t -> {
			try {
				return function.apply(t);
			}
			catch (Exception exception) {
				throwAsUnchecked(exception);
				return null;
			}
		};
	}

	/* rethrowSupplier(() -> new StringJoiner(new String(new byte[]{77, 97, 114, 107}, "UTF-8"))), */
	public static <T, E extends Exception> Supplier<T> rethrowSupplier(Supplier_WithExceptions<T, E> function) {
		return () -> {
			try {
				return function.get();
			}
			catch (Exception exception) {
				throwAsUnchecked(exception);
				return null;
			}
		};
	}

	public static <T> ExtendedCompletableFuture<T> wrapSyncSupplierResult(Supplier<T> function) {
		try {
			return ExtendedCompletableFuture.completedFuture(function.get());
		}
		catch (RuntimeException ex) {
			return ExtendedCompletableFuture.completedFailure(ex);
		}
	}

	/* uncheck(() -> Class.forName("xxx")); */
	public static void uncheck(@SuppressWarnings("rawtypes") Runnable_WithExceptions t) {
		try {
			t.run();
		}
		catch (Exception exception) {
			throwAsUnchecked(exception);
		}
	}

	/* uncheck(() -> Class.forName("xxx")); */
	public static <R, E extends Exception> R uncheck(Supplier_WithExceptions<R, E> supplier) {
		try {
			return supplier.get();
		}
		catch (Exception exception) {
			throwAsUnchecked(exception);
			return null;
		}
	}

	/* uncheck(Class::forName, "xxx"); */
	public static <T, R, E extends Exception> R uncheck(Function_WithExceptions<T, R, E> function, T t) {
		try {
			return function.apply(t);
		}
		catch (Exception exception) {
			throwAsUnchecked(exception);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private static <E extends Throwable> void throwAsUnchecked(Exception exception) throws E {
		throw (E) exception;
	}

}
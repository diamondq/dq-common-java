package com.diamondq.common.lambda;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Helper util class originated by MarcG on Stackoverflow (http://stackoverflow.com/a/27644392)
 */
public final class LambdaExceptionUtil {

	/**
	 * Consumer with exceptions
	 * 
	 * @param <T> the input type
	 * @param <E> the exception
	 */
	@FunctionalInterface
	public interface Consumer_WithExceptions<T, E extends Exception> {
		/**
		 * @see Consumer#accept(Object)
		 * @param t the input
		 * @throws E the exception
		 */
		void accept(T t) throws E;
	}

	/**
	 * Producer with exceptions
	 * 
	 * @param <T> the result type
	 * @param <E> the exception
	 */
	@FunctionalInterface
	public interface Producer_WithExceptions<T, E extends Exception> {
		/**
		 * producer
		 * 
		 * @return the result
		 * @throws E the exception
		 */
		T call() throws E;
	}

	/**
	 * BiConsumer with Exceptions
	 * 
	 * @param <T> input 1 type
	 * @param <U> input 2 type
	 * @param <E> exception type
	 */
	@FunctionalInterface
	public interface BiConsumer_WithExceptions<T, U, E extends Exception> {
		/**
		 * consumer
		 * 
		 * @param t input 1
		 * @param u input 2
		 * @throws E exception
		 */
		void accept(T t, U u) throws E;
	}

	/**
	 * BiFunction with Exception
	 * 
	 * @param <T> input type 1
	 * @param <U> input type 2
	 * @param <R> result type
	 * @param <E> exception type
	 */
	@FunctionalInterface
	public interface BiFunction_WithExceptions<T, U, R, E extends Exception> {
		/**
		 * function
		 * 
		 * @param t input 1
		 * @param u input 2
		 * @return result
		 * @throws E exception
		 */
		R apply(T t, U u) throws E;
	}

	/**
	 * Function with Exception
	 * 
	 * @param <T> input type
	 * @param <R> result type
	 * @param <E> exception type
	 */
	@FunctionalInterface
	public interface Function_WithExceptions<T, R, E extends Exception> {
		/**
		 * function
		 * 
		 * @param t input
		 * @return result
		 * @throws E exception
		 */
		R apply(T t) throws E;
	}

	/**
	 * Supplier with Exception
	 * 
	 * @param <T> result type
	 * @param <E> exception type
	 */
	@FunctionalInterface
	public interface Supplier_WithExceptions<T, E extends Exception> {
		/**
		 * supplier
		 * 
		 * @return the result
		 * @throws E exception
		 */
		T get() throws E;
	}

	/**
	 * Runnable with Exception
	 * 
	 * @param <E> exception type
	 */
	@FunctionalInterface
	public interface Runnable_WithExceptions<E extends Exception> {
		/**
		 * runnable
		 * 
		 * @throws E exception
		 */
		void run() throws E;
	}

	/*
	 * .forEach(rethrowConsumer(name -> System.out.println(Class.forName(name)))); or
	 * .forEach(rethrowConsumer(ClassNameUtil::println));
	 */
	/**
	 * Takes a consumer with exceptions and returns a Consumer or throws a Runtime Exception
	 * 
	 * @param consumer the consumer with exceptions
	 * @return the consumer
	 */
	public static <T, E extends Exception> Consumer<T> rethrowConsumer(Consumer_WithExceptions<T, E> consumer) {
		return t -> {
			try {
				consumer.accept(t);
			}
			catch (Exception exception) {
				RuntimeException e;
				if (exception instanceof RuntimeException)
					e = (RuntimeException) exception;
				else
					e = new RuntimeException(exception);
				throw e;
			}
		};
	}

	/**
	 * Takes a BiFunction with Exceptions and returns a Comparator or throws a Runtime Exception
	 * 
	 * @param comparator the comparator with exceptions
	 * @return the comparator
	 */
	public static <P, E extends Exception> Comparator<P> rethrowComparator(
		BiFunction_WithExceptions<P, P, @Nullable Integer, E> comparator) {
		return (a, b) -> {
			try {
				Integer result = comparator.apply(a, b);
				if (result == null)
					return 0;
				return result;
			}
			catch (Exception exception) {
				RuntimeException e;
				if (exception instanceof RuntimeException)
					e = (RuntimeException) exception;
				else
					e = new RuntimeException(exception);
				throw e;
			}
		};
	}

	/**
	 * Takes a BiConsumer with Exceptions and returns a BiConsumer or throws a Runtime Exception
	 * 
	 * @param biConsumer the BiConsumer with exceptions
	 * @return the BiConsumer
	 */
	public static <T, U, E extends Exception> BiConsumer<T, U> rethrowBiConsumer(
		BiConsumer_WithExceptions<T, U, E> biConsumer) {
		return (t, u) -> {
			try {
				biConsumer.accept(t, u);
			}
			catch (Exception exception) {
				RuntimeException e;
				if (exception instanceof RuntimeException)
					e = (RuntimeException) exception;
				else
					e = new RuntimeException(exception);
				throw e;
			}
		};
	}

	/**
	 * Takes a BiFunction with Exceptions and returns a BiFunction or throws a Runtime Exception
	 * 
	 * @param biFunction the BiFunction with exceptions
	 * @return the BiFunction
	 */
	public static <T, U, R, E extends Exception> BiFunction<T, U, R> rethrowBiFunction(
		BiFunction_WithExceptions<T, U, R, E> biFunction) {
		return (t, u) -> {
			try {
				return biFunction.apply(t, u);
			}
			catch (Exception exception) {
				RuntimeException e;
				if (exception instanceof RuntimeException)
					e = (RuntimeException) exception;
				else
					e = new RuntimeException(exception);
				throw e;
			}
		};
	}

	/**
	 * Takes a Function with Exceptions and returns a Function or throws a Runtime Exception
	 * 
	 * @param function the Function with exceptions
	 * @return the Function
	 */
	/* .map(rethrowFunction(name -> Class.forName(name))) or .map(rethrowFunction(Class::forName)) */
	public static <T, R, E extends Exception> Function<T, R> rethrowFunction(
		Function_WithExceptions<T, R, E> function) {
		return t -> {
			try {
				return function.apply(t);
			}
			catch (Exception exception) {
				RuntimeException e;
				if (exception instanceof RuntimeException)
					e = (RuntimeException) exception;
				else
					e = new RuntimeException(exception);
				throw e;
			}
		};
	}

	/* rethrowSupplier(() -> new StringJoiner(new String(new byte[]{77, 97, 114, 107}, "UTF-8"))), */
	/**
	 * Takes a Supplier with Exceptions and returns a Supplier or throws a Runtime Exception
	 * 
	 * @param function the Supplier with Exceptions
	 * @return the Supplier
	 */
	public static <T, E extends Exception> Supplier<T> rethrowSupplier(Supplier_WithExceptions<T, E> function) {
		return () -> {
			try {
				return function.get();
			}
			catch (Exception exception) {
				RuntimeException e;
				if (exception instanceof RuntimeException)
					e = (RuntimeException) exception;
				else
					e = new RuntimeException(exception);
				throw e;
			}
		};
	}

	/**
	 * Takes a Supplier and returns a completed future
	 * 
	 * @param function the supplier
	 * @return the future
	 */
	public static <T> ExtendedCompletableFuture<T> wrapSyncSupplierResult(Supplier<T> function) {
		try {
			return ExtendedCompletableFuture.completedFuture(function.get());
		}
		catch (RuntimeException ex) {
			return ExtendedCompletableFuture.completedFailure(ex);
		}
	}

	/**
	 * Takes a Supplier and returns a completed future
	 * 
	 * @param function the supplier
	 * @return the future
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> ExtendedCompletableFuture<T> wrapSyncNonNullSupplierResult(Supplier<T> function) {
		try {
			return ExtendedCompletableFuture.completedFuture(function.get());
		}
		catch (RuntimeException ex) {
			return (ExtendedCompletableFuture<T>) ExtendedCompletableFuture.completedFailure(ex);
		}
	}

	/* uncheck(() -> Class.forName("xxx")); */
	/**
	 * Takes a Runnable with Exceptions and runs it, and either returns or throws a Runtime Exception
	 * 
	 * @param t the Runnable with Exceptions
	 */
	public static void uncheck(@SuppressWarnings("rawtypes") Runnable_WithExceptions t) {
		try {
			t.run();
		}
		catch (Exception exception) {
			RuntimeException e;
			if (exception instanceof RuntimeException)
				e = (RuntimeException) exception;
			else
				e = new RuntimeException(exception);
			throw e;
		}
	}

	/* uncheck(() -> Class.forName("xxx")); */
	/**
	 * Takes a Supplier with Exceptions and returns either the result or throws a Runtime Exception
	 * 
	 * @param supplier the Supplier with Exceptions
	 * @return the result
	 */
	public static <R, E extends Exception> R uncheck(Supplier_WithExceptions<R, E> supplier) {
		try {
			return supplier.get();
		}
		catch (Exception exception) {
			RuntimeException e;
			if (exception instanceof RuntimeException)
				e = (RuntimeException) exception;
			else
				e = new RuntimeException(exception);
			throw e;
		}
	}

	/* uncheck(Class::forName, "xxx"); */
	/**
	 * Takes a Function with Exceptions and returns either the result or throws a Runtime Exception
	 * 
	 * @param function the Function with Exceptions
	 * @param t input parameter
	 * @return the result
	 */
	public static <T, R, E extends Exception> R uncheck(Function_WithExceptions<T, R, E> function, T t) {
		try {
			return function.apply(t);
		}
		catch (Exception exception) {
			RuntimeException e;
			if (exception instanceof RuntimeException)
				e = (RuntimeException) exception;
			else
				e = new RuntimeException(exception);
			throw e;
		}
	}

}
package de.deltage.util;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class LambdaExceptionUtil {
   public LambdaExceptionUtil() {
      super();
   }

   public static <T, E extends Exception> Consumer<T> rethrowConsumer(LambdaExceptionUtil.Consumer_WithExceptions<T, E> consumer) {
      return t -> {
         try {
            consumer.accept((T)t);
         } catch (Exception var3) {
            throwAsUnchecked(var3);
         }
      };
   }

   public static <T, U, E extends Exception> BiConsumer<T, U> rethrowBiConsumer(LambdaExceptionUtil.BiConsumer_WithExceptions<T, U, E> biConsumer) {
      return (t, u) -> {
         try {
            biConsumer.accept((T)t, (U)u);
         } catch (Exception var4) {
            throwAsUnchecked(var4);
         }
      };
   }

   public static <T, R, E extends Exception> Function<T, R> rethrowFunction(LambdaExceptionUtil.Function_WithExceptions<T, R, E> function) {
      return t -> {
         try {
            return function.apply((T)t);
         } catch (Exception var3) {
            throwAsUnchecked(var3);
            return null;
         }
      };
   }

   public static <T, E extends Exception> Supplier<T> rethrowSupplier(LambdaExceptionUtil.Supplier_WithExceptions<T, E> function) {
      return () -> {
         try {
            return function.get();
         } catch (Exception var2) {
            throwAsUnchecked(var2);
            return null;
         }
      };
   }

   public static void uncheck(LambdaExceptionUtil.Runnable_WithExceptions t) {
      try {
         t.run();
      } catch (Exception var2) {
         throwAsUnchecked(var2);
      }
   }

   public static <R, E extends Exception> R uncheck(LambdaExceptionUtil.Supplier_WithExceptions<R, E> supplier) {
      try {
         return supplier.get();
      } catch (Exception var2) {
         throwAsUnchecked(var2);
         return null;
      }
   }

   public static <T, R, E extends Exception> R uncheck(LambdaExceptionUtil.Function_WithExceptions<T, R, E> function, T t) {
      try {
         return function.apply(t);
      } catch (Exception var3) {
         throwAsUnchecked(var3);
         return null;
      }
   }

   private static <E extends Throwable> void throwAsUnchecked(Exception exception) throws E {
      throw exception;
   }

   @FunctionalInterface
   public interface BiConsumer_WithExceptions<T, U, E extends Exception> {
      void accept(T var1, U var2) throws E;
   }

   @FunctionalInterface
   public interface Consumer_WithExceptions<T, E extends Exception> {
      void accept(T var1) throws E;
   }

   @FunctionalInterface
   public interface Function_WithExceptions<T, R, E extends Exception> {
      R apply(T var1) throws E;
   }

   @FunctionalInterface
   public interface Runnable_WithExceptions<E extends Exception> {
      void run() throws E;
   }

   @FunctionalInterface
   public interface Supplier_WithExceptions<T, E extends Exception> {
      T get() throws E;
   }
}

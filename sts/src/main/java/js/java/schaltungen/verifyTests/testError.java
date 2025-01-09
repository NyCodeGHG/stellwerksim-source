package js.java.schaltungen.verifyTests;

import js.java.schaltungen.UserContextMini;

public class testError extends InitTestBase {
   private final String msg;

   public testError(Class<? extends InitTestBase> t) {
      this.msg = "Java Kompatiblitätsfehler mit " + t.getSimpleName();
   }

   public testError(String test) {
      this.msg = "Test Ladefehler mit " + test;
   }

   @Override
   public int test(UserContextMini uc) {
      return -1;
   }

   @Override
   public String name() {
      return this.msg;
   }
}

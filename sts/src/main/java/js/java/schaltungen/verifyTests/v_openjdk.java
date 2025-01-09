package js.java.schaltungen.verifyTests;

import js.java.schaltungen.UserContextMini;
import js.java.tools.JavaKind;

public class v_openjdk extends InitTestBase {
   private String error = "JVM";

   @Override
   public int test(UserContextMini uc) {
      if (JavaKind.isOpenJdk()) {
         this.error = "OpenJDK könnte Probleme haben, bitte Oracle JRE nutzen";
         return 1;
      } else {
         this.error = "Sun JVM";
         return 1;
      }
   }

   @Override
   public String name() {
      return this.error;
   }
}

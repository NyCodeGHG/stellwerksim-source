package js.java.schaltungen.verifyTests;

import js.java.schaltungen.UserContextMini;

public class v_irc extends InitTestBase {
   private String error = "";

   @Override
   public String name() {
      return "IRC & Kontrollserver Verbindungstest" + this.error;
   }

   @Override
   public int test(UserContextMini uc) {
      if (!uc.getChat().isConnected() && uc.getChat().isConnecting()) {
         return 0;
      } else {
         return uc.getChat().isConnected() ? 1 : -1;
      }
   }
}

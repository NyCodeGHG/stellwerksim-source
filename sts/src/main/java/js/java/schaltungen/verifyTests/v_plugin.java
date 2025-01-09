package js.java.schaltungen.verifyTests;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.schaltungen.UserContextMini;

public class v_plugin extends InitTestBase {
   private static final int PLUGINSERVPORT = 3691;
   private boolean failed = false;

   @Override
   public String name() {
      return this.failed ? "Keine Plugin möglich" : "Plugins möglich";
   }

   @Override
   public int test(UserContextMini uc) {
      try {
         ServerSocket tcp = new ServerSocket(3691);
         Throwable var3 = null;

         byte var4;
         try {
            var4 = 1;
         } catch (Throwable var14) {
            var3 = var14;
            throw var14;
         } finally {
            if (tcp != null) {
               if (var3 != null) {
                  try {
                     tcp.close();
                  } catch (Throwable var13) {
                     var3.addSuppressed(var13);
                  }
               } else {
                  tcp.close();
               }
            }
         }

         return var4;
      } catch (IOException var16) {
         Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, var16);
         this.failed = true;
         return 1;
      }
   }
}

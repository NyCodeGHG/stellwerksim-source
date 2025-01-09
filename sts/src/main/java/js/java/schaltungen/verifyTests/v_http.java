package js.java.schaltungen.verifyTests;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.schaltungen.UserContextMini;

public class v_http extends InitTestBase {
   private String error = "";

   @Override
   public int test(UserContextMini uc) {
      int ret = 0;
      String url = uc.getParameter(UserContextMini.DATATYPE.WEBSERVER);

      try {
         URL u = new URL("https://" + url);
         URLConnection ucon = u.openConnection();
         BufferedReader in = new BufferedReader(new InputStreamReader(ucon.getInputStream()));
         Throwable var7 = null;

         try {
            while (in.readLine() != null) {
            }
         } catch (Throwable var17) {
            var7 = var17;
            throw var17;
         } finally {
            if (in != null) {
               if (var7 != null) {
                  try {
                     in.close();
                  } catch (Throwable var16) {
                     var7.addSuppressed(var16);
                  }
               } else {
                  in.close();
               }
            }
         }

         var20 = 1;
      } catch (Exception var19) {
         this.error = " Fehler: " + var19.getMessage();
         var20 = -1;
         Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, var19);
      }

      return var20;
   }

   @Override
   public String name() {
      return "HTTPS Verbindungstest" + this.error;
   }
}

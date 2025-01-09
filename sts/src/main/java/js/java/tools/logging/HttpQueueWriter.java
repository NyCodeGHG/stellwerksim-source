package js.java.tools.logging;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import js.java.tools.streams.UTF8DataOutputStream;

public class HttpQueueWriter extends WriterQueue {
   private final String url;

   public HttpQueueWriter(String url) {
      this.url = url;
   }

   @Override
   protected void write(String m) {
      try {
         URL u = new URL(this.url);
         URLConnection urlConn = u.openConnection();
         urlConn.setDoInput(true);
         urlConn.setDoOutput(true);
         urlConn.setUseCaches(false);
         urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
         UTF8DataOutputStream printout = new UTF8DataOutputStream(urlConn.getOutputStream());
         printout.write(m);
         printout.flush();
         printout.close();
         BufferedReader input = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
         Throwable var7 = null;

         try {
            while (null != input.readLine()) {
            }
         } catch (Throwable var17) {
            var7 = var17;
            throw var17;
         } finally {
            if (input != null) {
               if (var7 != null) {
                  try {
                     input.close();
                  } catch (Throwable var16) {
                     var7.addSuppressed(var16);
                  }
               } else {
                  input.close();
               }
            }
         }
      } catch (Exception var19) {
         System.out.println("Logging exception: " + var19.getMessage());
         var19.printStackTrace();
      }
   }
}

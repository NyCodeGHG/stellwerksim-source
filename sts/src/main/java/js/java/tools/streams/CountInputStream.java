package js.java.tools.streams;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import js.java.tools.analysisWriter;

public class CountInputStream extends FilterInputStream {
   private static analysisWriter debugMode = null;
   private long counter = 0L;
   private StringBuffer bcopy = new StringBuffer();

   public static void setDebug(analysisWriter b) {
      debugMode = b;
   }

   public static boolean isDebug() {
      return debugMode != null;
   }

   public CountInputStream(InputStream in) {
      super(in);
   }

   public int read() throws IOException {
      int ret = this.in.read();
      if (ret >= 0) {
         this.counter++;
         if (isDebug()) {
            debugMode.writeln("Got(" + this.counter + "): " + ret);
         }

         this.bcopy.append(Character.toChars(ret));
      }

      return ret;
   }

   public int read(byte[] b) throws IOException {
      int ret = this.in.read(b);
      if (ret >= 0) {
         this.counter += (long)ret;
         if (isDebug()) {
            debugMode.writeln("Got(" + this.counter + "): " + b);
         }

         this.bcopy.append(new String(b, 0, ret));
      }

      return ret;
   }

   public int read(byte[] b, int off, int len) throws IOException {
      int ret = this.in.read(b, off, len);
      if (ret >= 0) {
         this.counter += (long)ret;
         if (isDebug()) {
            debugMode.writeln("Got(" + this.counter + "): " + b);
         }

         this.bcopy.append(new String(b, off, ret));
      }

      return ret;
   }

   public long getCount() {
      return this.counter;
   }

   public String getLastRead() {
      if (this.bcopy != null) {
         try {
            return this.bcopy.substring(0, (int)Math.min(this.counter, (long)this.bcopy.length()));
         } catch (StringIndexOutOfBoundsException var2) {
            return this.bcopy.toString();
         }
      } else {
         return "";
      }
   }
}

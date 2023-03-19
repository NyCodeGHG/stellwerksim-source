package js.java.tools.streams;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class TeeOutputStream extends FilterOutputStream {
   private boolean enabled = true;
   private OutputStream teeout;

   public TeeOutputStream(OutputStream out, OutputStream teeout) {
      super(out);
      this.teeout = teeout;
      this.enabled = teeout != null;
   }

   public TeeOutputStream(OutputStream out, Writer teeout) {
      super(out);
      this.teeout = null;
   }

   public void setEnable(boolean e) {
      this.enabled = e;
   }

   public void setEnable(boolean e, OutputStream teeout) {
      this.enabled = e;
      this.teeout = teeout;
   }

   public void write(int b) throws IOException {
      this.out.write(b);
      if (this.enabled) {
         this.teeout.write(b);
      }
   }

   public void write(byte[] b) throws IOException {
      this.out.write(b);
      if (this.enabled) {
         this.teeout.write(b);
      }
   }

   public void write(byte[] b, int off, int len) throws IOException {
      this.out.write(b, off, len);
      if (this.enabled) {
         this.teeout.write(b, off, len);
      }
   }

   public void close() throws IOException {
      super.close();
      this.teeout.close();
   }
}

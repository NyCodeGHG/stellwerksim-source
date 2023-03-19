package js.java.tools.streams;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

public class TeeInputStream extends FilterInputStream {
   private boolean enabled = true;
   private final OutputStream teeout;
   private final Writer teewriter;

   public TeeInputStream(InputStream in, OutputStream teeout) {
      super(in);
      this.teeout = teeout;
      this.teewriter = null;
   }

   public TeeInputStream(InputStream in, Writer teeout) {
      super(in);
      this.teewriter = teeout;
      this.teeout = null;
   }

   public void setEnable(boolean e) {
      this.enabled = e;
   }

   public int read() throws IOException {
      int b = this.in.read();
      if (this.enabled) {
         this.teeout.write(b);
      }

      return b;
   }

   public int read(byte[] b) throws IOException {
      int r = this.in.read(b);
      if (this.enabled && r > 0) {
         this.teeout.write(b, 0, r);
      }

      return r;
   }

   public int read(byte[] b, int off, int len) throws IOException {
      int r = this.in.read(b, off, len);
      if (this.enabled && r > 0) {
         this.teeout.write(b, off, r);
      }

      return r;
   }

   public void close() throws IOException {
      super.close();
      this.teeout.close();
   }
}

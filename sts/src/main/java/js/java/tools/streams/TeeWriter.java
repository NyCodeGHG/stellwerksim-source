package js.java.tools.streams;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

public class TeeWriter extends FilterWriter {
   private boolean enabled = true;
   private Writer teeout;

   public TeeWriter(Writer out, Writer teeout) {
      super(out);
      this.teeout = teeout;
      this.enabled = teeout != null;
   }

   public void setEnable(boolean e) {
      this.enabled = e;
   }

   public void setEnable(boolean e, Writer teeout) {
      this.enabled = e;
      this.teeout = teeout;
   }

   public void write(int b) throws IOException {
      this.out.write(b);
      if (this.enabled) {
         this.teeout.write(b);
      }
   }

   public void write(char[] cbuf, int off, int len) throws IOException {
      this.out.write(cbuf, off, len);
      if (this.enabled) {
         this.teeout.write(cbuf, off, len);
      }
   }

   public void write(String str, int off, int len) throws IOException {
      this.out.write(str, off, len);
      if (this.enabled) {
         this.teeout.write(str, off, len);
      }
   }

   public void flush() throws IOException {
      this.out.flush();
      this.teeout.flush();
   }

   public void close() throws IOException {
      super.close();
      this.teeout.close();
   }
}

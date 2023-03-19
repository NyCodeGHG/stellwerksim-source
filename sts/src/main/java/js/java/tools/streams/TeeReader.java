package js.java.tools.streams;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class TeeReader extends FilterReader {
   private boolean enabled = true;
   private final Writer teewriter;

   public TeeReader(Reader in, Writer teeout) {
      super(in);
      this.teewriter = teeout;
   }

   public void setEnable(boolean e) {
      this.enabled = e;
   }

   public int read() throws IOException {
      int b = this.in.read();
      if (this.enabled) {
         this.teewriter.write(b);
      }

      return b;
   }

   public int read(char[] b) throws IOException {
      int r = this.in.read(b);
      if (this.enabled && r > 0) {
         this.teewriter.write(b, 0, r);
      }

      return r;
   }

   public int read(char[] b, int off, int len) throws IOException {
      int r = this.in.read(b, off, len);
      if (this.enabled && r > 0) {
         this.teewriter.write(b, off, r);
      }

      return r;
   }

   public void close() throws IOException {
      super.close();
      this.teewriter.close();
   }
}

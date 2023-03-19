package js.java.tools.streams;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import js.java.tools.gui.dataTransferDisplay.DataTransferDisplayInterface;

public class DataDisplayOutputStream extends FilterOutputStream {
   private final DataTransferDisplayInterface dtdc;

   public DataDisplayOutputStream(OutputStream out, DataTransferDisplayInterface dtdc) {
      super(out);
      this.dtdc = dtdc;
   }

   public void write(int b) throws IOException {
      this.out.write(b);
      this.dtdc.gotData();
   }

   public void write(byte[] b) throws IOException {
      this.out.write(b);
      this.dtdc.gotData();
   }

   public void write(byte[] b, int off, int len) throws IOException {
      this.out.write(b, off, len);
      this.dtdc.gotData();
   }
}

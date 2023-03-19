package js.java.tools.streams;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import js.java.tools.gui.dataTransferDisplay.DataTransferDisplayInterface;

public class DataDisplayInputStream extends FilterInputStream {
   private final DataTransferDisplayInterface dtdc;

   public DataDisplayInputStream(InputStream in, DataTransferDisplayInterface dtdc) {
      super(in);
      this.dtdc = dtdc;
   }

   public int read() throws IOException {
      int ret = this.in.read();
      if (ret >= 0) {
         this.dtdc.gotData();
      }

      return ret;
   }

   public int read(byte[] b) throws IOException {
      int ret = this.in.read(b);
      if (ret >= 0) {
         this.dtdc.gotData();
      }

      return ret;
   }

   public int read(byte[] b, int off, int len) throws IOException {
      int ret = this.in.read(b, off, len);
      if (ret >= 0) {
         this.dtdc.gotData();
      }

      return ret;
   }
}

package js.java.tools.streams;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class LatinDataOutputStream extends OutputStreamWriter {
   public LatinDataOutputStream(OutputStream out) {
      super(out, StandardCharsets.ISO_8859_1);
   }
}

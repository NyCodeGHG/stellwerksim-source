package js.java.tools.streams;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class UTF8DataOutputStream extends OutputStreamWriter {
   public UTF8DataOutputStream(OutputStream out) {
      super(out, StandardCharsets.UTF_8);
   }
}

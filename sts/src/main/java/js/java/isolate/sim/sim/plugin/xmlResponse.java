package js.java.isolate.sim.sim.plugin;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import js.java.tools.HTMLEntities;

public class xmlResponse implements responseSender {
   private final boolean oldMode;
   public static final String EOR = "***EOR***";

   public xmlResponse() {
      super();
      this.oldMode = false;
   }

   public xmlResponse(boolean oldMode) {
      super();
      this.oldMode = oldMode;
   }

   @Override
   public void sendPcData(BufferedWriter output, String tag, Map<String, String> attr, String pcdata) throws IOException {
      StringBuilder b = new StringBuilder();
      b.append("<");
      b.append(tag);
      b.append(" ");

      for(Entry<String, String> e : attr.entrySet()) {
         if (e.getValue() != null) {
            b.append((String)e.getKey());
            b.append("='");
            b.append(HTMLEntities.escape((String)e.getValue()));
            b.append("' ");
         }
      }

      b.append(">");
      b.append(HTMLEntities.escape(pcdata));
      b.append("</").append(tag).append(">\n");
      output.append(b.toString());
      output.flush();
   }

   @Override
   public void sendLine(BufferedWriter output, String cmd, Map<String, String> attr) throws IOException {
      StringBuilder b = new StringBuilder();
      b.append("<");
      b.append(cmd);
      b.append(" ");

      for(Entry<String, String> e : attr.entrySet()) {
         if (e.getValue() != null) {
            b.append((String)e.getKey());
            b.append("='");
            b.append(HTMLEntities.escape((String)e.getValue()));
            b.append("' ");
         }
      }

      b.append("/>\n");
      output.append(b.toString());
      output.flush();
   }

   @Override
   public void sendOpeningLine(BufferedWriter output, String cmd, Map<String, String> attr) throws IOException {
      StringBuilder b = new StringBuilder();
      b.append("<");
      b.append(cmd);
      b.append(" ");

      for(Entry<String, String> e : attr.entrySet()) {
         if (e.getValue() != null) {
            b.append((String)e.getKey());
            b.append("='");
            b.append(HTMLEntities.escape((String)e.getValue()));
            b.append("' ");
         }
      }

      b.append(">\n");
      output.append(b.toString());
      output.flush();
   }

   @Override
   public void sendClosingLine(BufferedWriter output, String cmd) throws IOException {
      StringBuilder b = new StringBuilder();
      b.append("</");
      b.append(cmd);
      b.append(">\n");
      output.append(b.toString());
      output.flush();
   }

   @Override
   public void sendLine(BufferedWriter output, String cmd, String... v) throws IOException {
      if ((v.length & 1) == 0) {
         HashMap<String, String> m = new HashMap();

         for(int i = 0; i < v.length; i += 2) {
            m.put(v[i], v[i + 1]);
         }

         this.sendLine(output, cmd, m);
      } else {
         System.out.println("ungerade: " + cmd + " -> " + v.length);
      }
   }

   @Override
   public void sendOpeningLine(BufferedWriter output, String cmd, String... v) throws IOException {
      if ((v.length & 1) == 0) {
         HashMap<String, String> m = new HashMap();

         for(int i = 0; i < v.length; i += 2) {
            m.put(v[i], v[i + 1]);
         }

         this.sendOpeningLine(output, cmd, m);
      } else {
         System.out.println("ungerade: " + cmd + " -> " + v.length);
      }
   }

   @Override
   public void sendLine(BufferedWriter output, String cmd, long ref, String... v) throws IOException {
      v = (String[])Arrays.copyOf(v, v.length + 2);
      v[v.length - 2] = "ref";
      v[v.length - 1] = Long.toString(ref);
      this.sendLine(output, cmd, v);
   }

   @Override
   public void sendEOR(BufferedWriter output) throws IOException {
      if (!this.oldMode) {
         output.append("***EOR***");
         output.append("\n");
         output.flush();
      }
   }
}

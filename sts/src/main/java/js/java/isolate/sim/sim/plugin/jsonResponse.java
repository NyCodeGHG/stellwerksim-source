package js.java.isolate.sim.sim.plugin;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import js.java.tools.HTMLEntities;

public class jsonResponse implements responseSender {
   private int inSend = 0;
   private boolean firstSend = true;

   @Override
   public void sendPcData(BufferedWriter output, String tag, Map<String, String> attr, String pcdata) throws IOException {
      StringBuilder b = new StringBuilder();
      if (this.inSend > 0) {
         if (!this.firstSend) {
            b.append(",\n");
         }

         this.firstSend = false;
      }

      b.append("{\n\"kind\": \"");
      b.append(tag);
      b.append("\",\n");

      for (Entry<String, String> e : attr.entrySet()) {
         b.append("\"");
         b.append((String)e.getKey());
         b.append("\": \"");
         b.append((String)e.getValue());
         b.append("\",\n");
      }

      b.append("\"pcdata\": \"");
      b.append(HTMLEntities.htmlDoubleQuotes(pcdata));
      b.append("\"\n").append("}\n");
      output.append(b.toString());
      output.flush();
   }

   @Override
   public void sendLine(BufferedWriter output, String cmd, Map<String, String> attr) throws IOException {
      StringBuilder b = new StringBuilder();
      if (this.inSend > 0) {
         if (!this.firstSend) {
            b.append(",\n");
         }

         this.firstSend = false;
      }

      b.append("{\n\"kind\": \"");
      b.append(cmd);
      b.append("\"");

      for (Entry<String, String> e : attr.entrySet()) {
         b.append(",\n");
         b.append("\"");
         b.append((String)e.getKey());
         b.append("\": \"");
         b.append((String)e.getValue());
         b.append("\"");
      }

      b.append("\n}\n");
      output.append(b.toString());
      output.flush();
   }

   @Override
   public void sendOpeningLine(BufferedWriter output, String cmd, Map<String, String> attr) throws IOException {
      StringBuilder b = new StringBuilder();
      if (this.inSend > 0) {
         if (!this.firstSend) {
            b.append(",\n");
         }

         this.firstSend = false;
      }

      b.append("{\n\"kind\": \"");
      b.append(cmd);
      b.append("\"");

      for (Entry<String, String> e : attr.entrySet()) {
         b.append(",\n");
         b.append("\"");
         b.append((String)e.getKey());
         b.append("\": \"");
         b.append((String)e.getValue());
         b.append("\"");
      }

      b.append(",\n\"sub\": [\n");
      this.inSend++;
      this.firstSend = true;
      output.append(b.toString());
      output.flush();
   }

   @Override
   public void sendClosingLine(BufferedWriter output, String cmd) throws IOException {
      StringBuilder b = new StringBuilder();
      b.append("]}\n");
      this.inSend--;
      this.firstSend = false;
      output.append(b.toString());
      output.flush();
   }

   @Override
   public void sendLine(BufferedWriter output, String cmd, String... v) throws IOException {
      if ((v.length & 1) == 0) {
         HashMap<String, String> m = new HashMap();

         for (int i = 0; i < v.length; i += 2) {
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

         for (int i = 0; i < v.length; i += 2) {
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
   }
}

package js.java.isolate.sim.structServ;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import js.java.isolate.sim.sim.stellwerksim_main;

public class structDeliverer {
   protected final stellwerksim_main my_main;
   protected final HashMap<Integer, structinfo> idhash = new HashMap();

   public structDeliverer(stellwerksim_main my_main) {
      super();
      this.my_main = my_main;
   }

   protected void runCommand(String cmd, BufferedWriter output) throws IOException {
      String[] c = cmd.split(" ");
      if (c[0].equals("getlist")) {
         this.getlist(output);
      } else if (c[0].equals("getentry") && c.length > 1) {
         this.getentry(output, Integer.parseInt(c[1]));
      }

      output.flush();
   }

   private void getlist(BufferedWriter output) throws IOException {
      this.idhash.clear();
      Vector v = this.my_main.getStructInfo();

      for(int i = 0; i < v.size(); ++i) {
         Vector vv = (Vector)v.get(i);
         String type = (String)vv.get(0);
         String name = (String)vv.get(1);
         structinfo obj = (structinfo)vv.get(2);
         int id = this.idhash.size() + 1;
         this.idhash.put(id, obj);
         String data = "<entry type='" + type + "' name='" + name + "' id='" + id + "' />\n";
         output.write(data);
      }
   }

   private void getentry(BufferedWriter output, int id) throws IOException {
      structinfo obj = (structinfo)this.idhash.get(id);
      if (obj != null) {
         Vector v = obj.getStructure();
         int l = v.size() / 2;
         int c = 0;

         for(int i = 0; i < v.size(); i += 2) {
            String key = "";
            String value = "";

            try {
               key = (String)v.get(i);
            } catch (Exception var12) {
               key = var12.getMessage();
            }

            try {
               value = v.get(i + 1).toString();
            } catch (Exception var11) {
               value = var11.getMessage();
            }

            String data = "<line line='" + c + "' totallines='" + l + "' key='" + key + "' value='" + value + "' />\n";
            output.write(data);
            ++c;
         }
      }
   }
}

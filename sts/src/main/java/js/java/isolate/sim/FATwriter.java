package js.java.isolate.sim;

import java.util.Iterator;
import js.java.isolate.sim.sim.fat;
import js.java.isolate.sim.structServ.structinfo;
import js.java.tools.analysisWriter;

public class FATwriter extends analysisWriter {
   public FATwriter() {
      super("");
   }

   public FATwriter(String m) {
      super(m);
   }

   public void writeln(String module, String text) {
      fat.FATwriteln(module, text);
   }

   public void writeln(String module, structinfo si) {
      fat.FATwriteln(module, si);
   }

   public void writeln(structinfo si) {
      fat.FATwriteln(this.getModuleName(), si);
   }

   public void writeln(Iterator<? extends structinfo> i) {
      while (i.hasNext()) {
         try {
            structinfo si = (structinfo)i.next();
            fat.FATwriteln(this.getModuleName(), si);
         } catch (Exception var3) {
            System.out.println("Dumper error: " + var3.getMessage());
         }
      }
   }
}

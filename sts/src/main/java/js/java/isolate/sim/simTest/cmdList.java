package js.java.isolate.sim.simTest;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;
import js.java.isolate.sim.sim.stellwerksim_main;

class cmdList implements simCmd {
   @Override
   public String getName() {
      return "list";
   }

   @Override
   public void execute(stellwerksim_main my_main, PrintWriter out, String[] opts) {
      if (opts.length >= 2) {
         Vector v = my_main.getStructInfo();
         Enumeration e = v.elements();

         while (e.hasMoreElements()) {
            Vector vv = (Vector)e.nextElement();
            if (opts[1].equalsIgnoreCase((String)vv.get(0))) {
               out.println(vv.get(1));
            }
         }
      }
   }

   @Override
   public void usage(PrintWriter out) {
      out.println(this.getName() + " <listname>");
      out.println(" <listname>: eventcontainer - definierte Störungen");
      out.println(" <listname>: events - aktive Störungen");
      out.println("bsp: " + this.getName() + " eventcontainer");
   }
}

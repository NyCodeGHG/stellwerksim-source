package js.java.isolate.sim.simTest;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.sim.stellwerksim_main;
import js.java.isolate.sim.structServ.structinfo;

class cmdEventdetails implements simCmd {
   cmdEventdetails() {
      super();
   }

   @Override
   public String getName() {
      return "eventdetails";
   }

   @Override
   public void execute(stellwerksim_main my_main, PrintWriter out, String[] opts) {
      if (opts.length >= 2) {
         Vector v = my_main.getStructInfo();
         Enumeration e = v.elements();

         while(e.hasMoreElements()) {
            Vector vv = (Vector)e.nextElement();
            if ("events".equalsIgnoreCase((String)vv.get(0)) && opts[1].equalsIgnoreCase((String)vv.get(1)) && vv.get(2) instanceof event) {
               out.println("Typ: " + vv.get(2).getClass().getSimpleName());
               this.showStruct(vv, out);
            }

            if ("eventcontainer".equalsIgnoreCase((String)vv.get(0)) && opts[1].equalsIgnoreCase((String)vv.get(1)) && vv.get(2) instanceof eventContainer) {
               eventContainer ev = (eventContainer)vv.get(2);
               out.println("Name: " + ev.getName());
               out.println("Typ: " + ev.getTyp());
               this.showStruct(vv, out);
            }
         }
      }
   }

   private void showStruct(Vector vv, PrintWriter out) {
      Vector sv = ((structinfo)vv.get(2)).getStructure();

      for(int i = 0; i < sv.size(); i += 2) {
         out.println(sv.get(i) + ": " + sv.get(i + 1));
      }
   }

   @Override
   public void usage(PrintWriter out) {
      out.println(this.getName() + " <name>");
      out.println("Bsp: " + this.getName() + " \"Störung 88\"");
   }
}

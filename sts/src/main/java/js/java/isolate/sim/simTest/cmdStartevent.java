package js.java.isolate.sim.simTest;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.sim.stellwerksim_main;

class cmdStartevent implements simCmd {
   cmdStartevent() {
      super();
   }

   @Override
   public String getName() {
      return "startevent";
   }

   @Override
   public void execute(stellwerksim_main my_main, PrintWriter out, String[] opts) {
      if (opts.length >= 2) {
         Vector v = my_main.getStructInfo();
         Enumeration e = v.elements();

         while(e.hasMoreElements()) {
            Vector vv = (Vector)e.nextElement();
            if ("eventcontainer".equalsIgnoreCase((String)vv.get(0)) && opts[1].equalsIgnoreCase((String)vv.get(1)) && vv.get(2) instanceof eventContainer) {
               event.createEvent((eventContainer)vv.get(2), my_main.getGleisbild(), my_main);
               out.println("Gestartet: " + opts[1]);
               break;
            }
         }
      }
   }

   @Override
   public void usage(PrintWriter out) {
      out.println(this.getName() + " <name>");
      out.println("Bsp: " + this.getName() + " \"Störung 88\"");
   }
}

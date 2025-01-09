package js.java.isolate.sim.simTest;

import java.io.PrintWriter;
import java.util.Vector;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.events.displayausfall;
import js.java.isolate.sim.sim.stellwerksim_main;
import js.java.isolate.sim.structServ.structinfo;

class cmdCreateevent implements simCmd {
   @Override
   public String getName() {
      return "createevent";
   }

   @Override
   public void execute(stellwerksim_main my_main, PrintWriter out, String[] opts) {
      if (opts.length >= 2) {
         eventContainer ev = new eventContainer(my_main.getGleisbild(), my_main, opts[1], false);
         out.println("Container:");
         this.showStruct(ev.getStructInfo(), out);
         event running = ev.getRunningEvent();
         if (running != null) {
            out.println();
            out.println("Running event:");
            this.showStruct(running.getStructInfo(), out);
         }
      }
   }

   private void showStruct(Vector vv, PrintWriter out) {
      Vector sv = ((structinfo)vv.get(2)).getStructure();

      for (int i = 0; i < sv.size(); i += 2) {
         out.println(sv.get(i) + ": " + sv.get(i + 1));
      }
   }

   @Override
   public void usage(PrintWriter out) {
      out.println(this.getName() + " <name>");
      out.println("Bsp: " + this.getName() + " \"" + displayausfall.class.getSimpleName() + "\"");
   }
}

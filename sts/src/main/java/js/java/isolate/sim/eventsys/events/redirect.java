package js.java.isolate.sim.eventsys.events;

import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.zug.zug;

public class redirect extends event {
   public redirect(Simulator sim) {
      super(sim);
   }

   @Override
   protected boolean init(eventContainer e) {
      String parameter = e.getValue("parameter");
      String[] ids = parameter.split(",");
      int zid = Integer.parseInt(ids[0]);
      zug z = this.my_main.findZug(zid);
      if (z != null) {
         String aids = "";
         String sep = "";

         for (int i = 1; i < ids.length; i++) {
            aids = aids + sep + ids[i];
            sep = " ";
         }

         this.my_main.requestZugRedirect(z, aids);
         Logger.getLogger("stslogger").log(Level.SEVERE, "externe voll umleitung " + zid + ": " + aids);
      }

      this.eventDone();
      return false;
   }

   @Override
   public String getText() {
      return null;
   }
}

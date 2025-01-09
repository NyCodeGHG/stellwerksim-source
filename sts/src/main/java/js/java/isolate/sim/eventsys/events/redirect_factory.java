package js.java.isolate.sim.eventsys.events;

import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventFactory;
import js.java.isolate.sim.gleisbild.gleisbildModelEventsys;

public class redirect_factory extends eventFactory {
   @Override
   public String getName() {
      return "Umleiten";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return redirect.class;
   }

   @Override
   protected void initGui() {
   }

   @Override
   public String getDescription() {
      return "Umleiten";
   }

   @Override
   public boolean serverEvent(eventContainer ev, gleisbildModelEventsys glb, String parameter) {
      try {
         if (!parameter.isEmpty()) {
            String[] ids = parameter.split(",");
            if (ids.length > 1) {
               ev.setValue("parameter", parameter);
               return true;
            }
         }
      } catch (Exception var5) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "catch ", var5);
      }

      return false;
   }
}

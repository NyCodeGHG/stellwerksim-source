package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventFactory;
import js.java.isolate.sim.gleisbild.gleisbildModelEventsys;

public class allCodes_factory extends eventFactory {
   @Override
   public String getName() {
      return "Alle Hotline Codes";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return null;
   }

   @Override
   protected void initGui() {
   }

   @Override
   public String getDescription() {
      return "Alle Hotline Codes";
   }

   @Override
   public boolean serverEvent(eventContainer ev, gleisbildModelEventsys glb, String parameter) {
      try {
         for (event e : event.events) {
            event.startActivityCall(e, "");
         }
      } catch (Exception var6) {
      }

      return false;
   }
}

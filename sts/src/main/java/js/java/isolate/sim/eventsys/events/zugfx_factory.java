package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventFactory;
import js.java.isolate.sim.gleisbild.gleisbildModelEventsys;

public class zugfx_factory extends eventFactory {
   @Override
   public String getName() {
      return "Zug Spezialmeldungen";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return zugfx.class;
   }

   @Override
   protected void initGui() {
   }

   @Override
   public String getDescription() {
      return "Zug Spezialmeldungen";
   }

   @Override
   public boolean serverEvent(eventContainer ev, gleisbildModelEventsys glb, String parameter) {
      try {
         if (!parameter.isEmpty()) {
            String[] p = parameter.split(",");
            if (p.length >= 2) {
               ev.setValue("cmd", p[0]);
               ev.setValue("zid", p[1]);
               return true;
            }
         }
      } catch (Exception var5) {
      }

      return false;
   }
}

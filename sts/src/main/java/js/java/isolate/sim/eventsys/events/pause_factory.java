package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventFactory;
import js.java.isolate.sim.gleisbild.gleisbildModelEventsys;

public class pause_factory extends eventFactory {
   public pause_factory() {
      super();
   }

   @Override
   public String getName() {
      return "Störungen pausieren";
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
      return "Störungen pausieren";
   }

   @Override
   public boolean serverEvent(eventContainer ev, gleisbildModelEventsys glb, String parameter) {
      try {
         if (!parameter.isEmpty()) {
            String[] p = parameter.split(",");
            int dauer = Integer.parseInt(p[0]);
            glb.getHaeufigkeiten().pauseEvents(dauer);
            return false;
         }
      } catch (Exception var6) {
      }

      return false;
   }
}

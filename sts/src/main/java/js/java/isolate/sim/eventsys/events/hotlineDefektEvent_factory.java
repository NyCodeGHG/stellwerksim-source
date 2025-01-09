package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventFactory;

public class hotlineDefektEvent_factory extends eventFactory {
   @Override
   public String getName() {
      return "Hotline gestört";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return hotlineDefektEvent.class;
   }

   @Override
   protected void initGui() {
   }

   @Override
   public String getDescription() {
      return "";
   }
}

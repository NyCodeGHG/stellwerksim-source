package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventFactory;
import js.java.isolate.sim.gleisbild.gleisbildModelEventsys;

public class oneredirect_factory extends eventFactory {
   @Override
   public String getName() {
      return "Eine Umleitung";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return oneredirect.class;
   }

   @Override
   protected void initGui() {
   }

   @Override
   public String getDescription() {
      return "Eine Umleitung";
   }

   @Override
   public boolean serverEvent(eventContainer ev, gleisbildModelEventsys glb, String parameter) {
      return true;
   }
}

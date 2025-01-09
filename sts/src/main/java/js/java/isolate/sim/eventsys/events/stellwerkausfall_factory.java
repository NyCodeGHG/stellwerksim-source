package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventFactory;
import js.java.isolate.sim.gleisbild.gleisbildModelEventsys;

public class stellwerkausfall_factory extends eventFactory {
   @Override
   public String getName() {
      return "Stellwerksausfall";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return stellwerkausfall.class;
   }

   @Override
   protected void initGui() {
   }

   @Override
   public String getDescription() {
      return "Stromausfall neue Version";
   }

   @Override
   public boolean serverEvent(eventContainer ev, gleisbildModelEventsys glb, String parameter) {
      return true;
   }

   @Override
   public boolean isStopFollowing() {
      return true;
   }
}

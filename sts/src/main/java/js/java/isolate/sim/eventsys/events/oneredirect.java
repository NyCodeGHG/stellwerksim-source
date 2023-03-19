package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;

public class oneredirect extends event {
   public oneredirect(Simulator sim) {
      super(sim);
   }

   @Override
   protected boolean init(eventContainer e) {
      this.my_main.allowOneRedirect();
      this.eventDone();
      return false;
   }

   @Override
   public String getText() {
      return null;
   }
}

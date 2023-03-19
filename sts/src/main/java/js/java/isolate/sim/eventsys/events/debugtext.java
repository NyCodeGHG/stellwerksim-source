package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;

public class debugtext extends event {
   private String text = null;

   public debugtext(Simulator sim) {
      super(sim);
      this.finishIn(2);
   }

   @Override
   public String getText() {
      return this.text;
   }

   @Override
   protected boolean init(eventContainer e) {
      return true;
   }
}

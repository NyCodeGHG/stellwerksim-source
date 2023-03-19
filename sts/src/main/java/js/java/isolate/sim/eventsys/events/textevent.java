package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;

@Deprecated
public class textevent extends event {
   private String text = null;

   public textevent(Simulator sim) {
      super(sim);
   }

   public textevent(Simulator sim, String t) {
      super(sim);
      this.text = this.my_main.getSimutimeString() + ": " + t;
      this.finishIn(10);
   }

   @Override
   public String getText() {
      return this.text;
   }

   @Override
   protected boolean init(eventContainer e) {
      this.finishIn(10);
      return true;
   }
}

package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventGenerator;

public class zugAbfahrtStoerung extends zugStoerungStandard {
   public zugAbfahrtStoerung(Simulator sim) {
      super(sim);
   }

   @Override
   protected boolean init(eventContainer e) {
      return this.init(e, new eventGenerator.TYPES[]{eventGenerator.TYPES.T_ZUG_ABFAHRT});
   }
}

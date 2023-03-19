package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventGenerator;

public class zugFluegelStoerung extends zugStoerungStandard {
   public zugFluegelStoerung(Simulator sim) {
      super(sim);
   }

   @Override
   protected boolean init(eventContainer e) {
      return this.init(e, new eventGenerator.TYPES[]{eventGenerator.TYPES.T_ZUG_FLÜGELN, eventGenerator.TYPES.T_ZUG_LOKFLÜGELN});
   }
}

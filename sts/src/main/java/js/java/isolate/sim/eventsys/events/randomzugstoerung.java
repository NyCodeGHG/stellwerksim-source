package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventGenerator;

public class randomzugstoerung extends zugStoerungStandard {
   public randomzugstoerung(Simulator sim) {
      super(sim);
   }

   @Override
   protected boolean init(eventContainer e) {
      int r = random(0, 30);
      if (r < 10) {
         return this.init(e, new eventGenerator.TYPES[]{eventGenerator.TYPES.T_ZUG_WURDEGRUEN});
      } else {
         return r < 20
            ? this.init(e, new eventGenerator.TYPES[]{eventGenerator.TYPES.T_ZUG_KUPPELN})
            : this.init(e, new eventGenerator.TYPES[]{eventGenerator.TYPES.T_ZUG_ABFAHRT});
      }
   }
}

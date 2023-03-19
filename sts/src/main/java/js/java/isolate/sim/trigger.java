package js.java.isolate.sim;

import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.gleisbild.gleisbildSimControl;

public abstract class trigger extends eventGenerator {
   public static triggerjobmanager tjm = null;

   public trigger() {
      super();
   }

   protected final void tjm_add(trigger t) {
      if (tjm != null) {
         tjm.add(t);
      }
   }

   protected final void tjm_add() {
      if (tjm != null) {
         tjm.add(this);
      }
   }

   protected final gleisbildSimControl getControl() {
      return tjm.getControl();
   }

   public abstract boolean ping();
}

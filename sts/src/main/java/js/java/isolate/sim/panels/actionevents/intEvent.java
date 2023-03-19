package js.java.isolate.sim.panels.actionevents;

import js.java.tools.actions.AbstractStringEvent;

public class intEvent extends AbstractStringEvent {
   private final int i;

   public intEvent(int _i) {
      super("");
      this.i = _i;
   }

   public int getInt() {
      return this.i;
   }
}

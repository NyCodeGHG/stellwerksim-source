package js.java.isolate.sim.panels.actionevents;

import js.java.tools.actions.AbstractStringEvent;

public class boolEvent extends AbstractStringEvent {
   private final boolean b;

   public boolEvent(boolean _b) {
      super();
      this.b = _b;
   }

   public boolean getValue() {
      return this.b;
   }

   public boolean isSet() {
      return this.b;
   }
}

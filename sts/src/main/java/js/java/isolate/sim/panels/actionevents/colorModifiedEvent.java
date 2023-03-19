package js.java.isolate.sim.panels.actionevents;

import js.java.tools.actions.AbstractStringEvent;

public class colorModifiedEvent extends AbstractStringEvent {
   public colorModifiedEvent(String col) {
      super(col);
   }

   public String getColor() {
      return (String)this.getSource();
   }
}

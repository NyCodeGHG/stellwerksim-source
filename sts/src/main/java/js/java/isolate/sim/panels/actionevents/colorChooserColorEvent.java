package js.java.isolate.sim.panels.actionevents;

import js.java.tools.actions.AbstractStringEvent;

public class colorChooserColorEvent extends AbstractStringEvent {
   public colorChooserColorEvent(String col) {
      super(col);
   }

   public String getColor() {
      return (String)this.getSource();
   }
}

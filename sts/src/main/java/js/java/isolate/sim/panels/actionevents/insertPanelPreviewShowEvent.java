package js.java.isolate.sim.panels.actionevents;

import js.java.isolate.sim.inserts.insert;
import js.java.tools.actions.AbstractEvent;

public class insertPanelPreviewShowEvent extends AbstractEvent<insert> {
   public insertPanelPreviewShowEvent(insert in) {
      super(in);
   }

   public insert getInsert() {
      return (insert)this.getSource();
   }
}

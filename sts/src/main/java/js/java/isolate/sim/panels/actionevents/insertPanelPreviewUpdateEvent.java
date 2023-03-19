package js.java.isolate.sim.panels.actionevents;

import js.java.isolate.sim.inserts.insert;
import js.java.tools.actions.AbstractEvent;

public class insertPanelPreviewUpdateEvent extends AbstractEvent<insert> {
   public insertPanelPreviewUpdateEvent(insert in) {
      super(in);
   }

   public insert getInsert() {
      return (insert)this.getSource();
   }
}

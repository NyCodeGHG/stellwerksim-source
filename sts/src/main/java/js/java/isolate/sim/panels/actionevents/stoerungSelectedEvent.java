package js.java.isolate.sim.panels.actionevents;

import js.java.isolate.sim.eventsys.eventContainer;
import js.java.tools.actions.AbstractEvent;

public class stoerungSelectedEvent extends AbstractEvent<eventContainer> {
   private final boolean selected;
   private final boolean update;

   public stoerungSelectedEvent(eventContainer in, boolean sel) {
      super(in);
      this.selected = sel;
      this.update = false;
   }

   public stoerungSelectedEvent(eventContainer in, boolean sel, boolean upd) {
      super(in);
      this.selected = sel;
      this.update = upd;
   }

   public eventContainer getEvent() {
      return (eventContainer)this.getSource();
   }

   public boolean isSelected() {
      return this.selected;
   }

   public boolean isUpdate() {
      return this.update;
   }
}

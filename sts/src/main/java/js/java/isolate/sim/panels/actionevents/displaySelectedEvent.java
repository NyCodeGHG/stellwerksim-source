package js.java.isolate.sim.panels.actionevents;

import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.displayBar.connector;
import js.java.tools.actions.AbstractStringEvent;

public class displaySelectedEvent extends AbstractStringEvent {
   private final LinkedList<connector> selected;
   private final gleis display;

   public displaySelectedEvent() {
      this.display = null;
      this.selected = null;
   }

   public displaySelectedEvent(gleis d, LinkedList<connector> in) {
      super("");
      this.display = d;
      this.selected = in;
   }

   public LinkedList<connector> getSelected() {
      return this.selected;
   }

   public gleis getDisplay() {
      return this.display;
   }
}

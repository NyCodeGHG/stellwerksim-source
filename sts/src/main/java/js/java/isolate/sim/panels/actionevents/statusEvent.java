package js.java.isolate.sim.panels.actionevents;

import js.java.tools.actions.AbstractStringEvent;

public class statusEvent extends AbstractStringEvent {
   private final int typ;

   public statusEvent(String m, int _typ) {
      super(m);
      this.typ = _typ;
   }

   public String getStatus() {
      return (String)this.getSource();
   }

   public int getTyp() {
      return this.typ;
   }
}

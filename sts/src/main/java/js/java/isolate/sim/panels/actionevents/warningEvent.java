package js.java.isolate.sim.panels.actionevents;

import js.java.tools.actions.AbstractStringEvent;

public class warningEvent extends AbstractStringEvent {
   private final int rank;

   public warningEvent(String text, int rank) {
      super(text);
      this.rank = rank;
   }

   public String getWarning() {
      return (String)this.getSource();
   }

   public int getRank() {
      return this.rank;
   }
}

package js.java.isolate.sim.panels.actionevents;

import js.java.isolate.sim.gleis.gleis;
import js.java.tools.actions.AbstractEvent;

public class gleisUIEvent extends AbstractEvent<gleis.gleisUIcom> {
   protected gleisUIEvent(gleis.gleisUIcom _i) {
      super(_i);
   }

   public gleis.gleisUIcom getData() {
      return (gleis.gleisUIcom)this.getSource();
   }
}

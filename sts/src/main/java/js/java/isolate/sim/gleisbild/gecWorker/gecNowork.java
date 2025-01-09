package js.java.isolate.sim.gleisbild.gecWorker;

import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.tools.actions.AbstractEvent;

public class gecNowork extends gecBase<AbstractEvent> {
   @Override
   public void init(gleisbildEditorControl gec, gecBase lastMode) {
      super.init(gec, lastMode);
      gec.getModel().allOff();
   }
}

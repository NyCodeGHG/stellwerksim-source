package js.java.isolate.sim.gleisbild.gecWorker;

import js.java.isolate.sim.gleisbild.gleisbildEditorControl;

public class gecGMassBlockSelect extends gecGBlockSelect {
   @Override
   public void init(gleisbildEditorControl gec, gecBase lastMode) {
      super.init(gec, lastMode);
      gec.setMassVisible(true);
   }

   @Override
   public void deinit(gecBase nextMode) {
      super.deinit(nextMode);
      this.gec.setMassVisible(false);
   }
}

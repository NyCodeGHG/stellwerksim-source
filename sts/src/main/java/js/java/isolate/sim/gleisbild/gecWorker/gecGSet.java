package js.java.isolate.sim.gleisbild.gecWorker;

import java.awt.event.MouseEvent;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;

public class gecGSet extends gecSelect {
   public gecGSet() {
      super();
   }

   @Override
   public void init(gleisbildEditorControl gec, gecBase lastMode) {
      super.init(gec, lastMode);
      gec.getModel().allOff();
   }

   @Override
   public void mousePressed(MouseEvent e) {
      gleis gl = this.gec.gleisUnderMouse(e);
      if (gl != null) {
         this.gec.getModel().setGleisValues(gl);
      }
   }

   @Override
   public void mouseMoved(MouseEvent e) {
      gleis gl = this.gec.gleisUnderMouse(e);
      this.gec.getModel().clearRolloverGleis();
      if (gl != null) {
         this.gec.getModel().addRolloverGleis(gl);
      }
   }

   @Override
   public void mouseDragged(MouseEvent e) {
      this.gec.getModel().clearRolloverGleis();
      gleis gl = this.gec.gleisUnderMouse(e);
      if (gl != null) {
         this.gec.getModel().setGleisValues(gl);
         this.gec.getModel().addRolloverGleis(gl);
      }
   }
}

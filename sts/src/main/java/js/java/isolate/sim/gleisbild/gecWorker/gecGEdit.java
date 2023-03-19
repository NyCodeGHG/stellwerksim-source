package js.java.isolate.sim.gleisbild.gecWorker;

import java.awt.event.MouseEvent;
import js.java.isolate.sim.gleis.gleis;

public class gecGEdit extends gecGSelect {
   public gecGEdit() {
      super();
   }

   @Override
   public void mousePressed(MouseEvent e) {
      gleis gl = this.gec.gleisUnderMouse(e);
      if (gl != null) {
         this.gec.getModel().setSelectedGleis(gl, true);
         this.fireSelectEvent();
      }
   }
}

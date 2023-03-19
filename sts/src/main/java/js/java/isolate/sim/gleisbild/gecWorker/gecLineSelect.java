package js.java.isolate.sim.gleisbild.gecWorker;

import java.awt.event.MouseEvent;
import java.util.List;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;

public abstract class gecLineSelect extends gecSelect {
   protected int line;

   public gecLineSelect() {
      super();
   }

   @Override
   public void init(gleisbildEditorControl gec, gecBase lastMode) {
      super.init(gec, lastMode);
      this.line = -1;
      gec.getModel().allOff();
   }

   @Override
   public void mousePressed(MouseEvent e) {
      gleis gl = this.gec.gleisUnderMouse(e);
      this.gec.getModel().clearHighlightedGleis();
      if (gl != null) {
         this.setLine(gl);
         List<gleis> sel = this.getSel(gl);
         this.gec.getModel().addHighlightedGleis(sel);
         this.fireSelectEvent();
      }
   }

   @Override
   public void mouseMoved(MouseEvent e) {
      gleis gl = this.gec.gleisUnderMouse(e);
      this.gec.getModel().clearRolloverGleis();
      if (gl != null) {
         List<gleis> sel = this.getSel(gl);
         this.gec.getModel().addRolloverGleis(sel);
      }
   }

   protected abstract List<gleis> getSel(gleis var1);

   protected abstract void setLine(gleis var1);

   public abstract void insert();

   public abstract void delete();
}

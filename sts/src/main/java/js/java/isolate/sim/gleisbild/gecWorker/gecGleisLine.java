package js.java.isolate.sim.gleisbild.gecWorker;

import java.awt.event.MouseEvent;
import java.util.List;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;

public class gecGleisLine extends gecSelect {
   private gleis startGleis;
   private boolean drawOver = true;

   public gecGleisLine() {
      super();
   }

   public void setDrawOver(boolean o) {
      this.drawOver = o;
   }

   public boolean isDrawOver() {
      return this.drawOver;
   }

   @Override
   public void init(gleisbildEditorControl gec, gecBase lastMode) {
      super.init(gec, lastMode);
      gec.getModel().allOff();
      this.startGleis = null;
   }

   @Override
   public void mousePressed(MouseEvent e) {
      gleis gl = this.gec.gleisUnderMouse(e);
      this.gec.getModel().allOff();
      if (gl != null) {
         this.startGleis = gl;
         this.gec.getModel().setSelectedGleis(this.startGleis);
      } else {
         this.startGleis = null;
      }
   }

   @Override
   public void mouseReleased(MouseEvent e) {
      if (this.startGleis != null) {
         gleis gl = this.gec.gleisUnderMouse(e);
         if (gl != null) {
            List<gleis> line = this.gec.getModel().makeLine(this.startGleis.getCol(), this.startGleis.getRow(), gl.getCol(), gl.getRow());
            this.gec.getModel().drawline(line, this.gec.getNextColor(), this.drawOver);
            this.fireSelectEvent();
            this.gec.getModel().allOff();
         }

         this.startGleis = null;
      }
   }

   @Override
   public void mouseDragged(MouseEvent e) {
      if (this.startGleis != null) {
         gleis gl = this.gec.gleisUnderMouse(e);
         this.gec.getModel().clearRolloverGleis();
         this.gec.getModel().clearMarkedGleis();
         if (gl != null) {
            this.gec.getModel().addMarkedGleis(gl);
            List<gleis> line = this.gec.getModel().makeLine(this.startGleis.getCol(), this.startGleis.getRow(), gl.getCol(), gl.getRow());
            this.gec.getModel().addRolloverGleis(line);
         }
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
}

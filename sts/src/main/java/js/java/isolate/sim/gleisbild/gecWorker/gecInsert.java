package js.java.isolate.sim.gleisbild.gecWorker;

import java.awt.Point;
import java.awt.event.MouseEvent;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.inserts.insert;

public class gecInsert extends gecGSelect {
   private insert nextInsert;

   public gecInsert() {
      super();
   }

   @Override
   public void init(gleisbildEditorControl gec, gecBase lastMode) {
      super.init(gec, lastMode);
      gec.getModel().allOff();
      this.nextInsert = null;
   }

   @Override
   public void mousePressed(MouseEvent e) {
      if (this.nextInsert != null) {
         gleis gl = this.gec.gleisUnderMouse(e);
         if (gl != null) {
            this.nextInsert.paint(this.gec.getModel(), gl.getCol(), gl.getRow());
            this.fireSelectEvent();
         }
      }
   }

   @Override
   public void mouseMoved(MouseEvent e) {
      if (this.nextInsert != null) {
         gleis gl = this.gec.gleisUnderMouse(e);
         this.gec.getModel().clearRolloverGleis();
         if (gl != null) {
            for(Point p : this.nextInsert.getCoords(this.gec.getModel(), gl.getCol(), gl.getRow())) {
               gleis gls = this.gec.getModel().getXY_null(p.x, p.y);
               if (gls != null) {
                  this.gec.getModel().addRolloverGleis(gls);
               }
            }
         }
      }
   }

   public void setInsert(insert i) {
      this.nextInsert = i;
      this.nextInsert.initInsert();
   }
}

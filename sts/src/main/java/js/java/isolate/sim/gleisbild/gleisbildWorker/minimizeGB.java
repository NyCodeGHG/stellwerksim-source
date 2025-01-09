package js.java.isolate.sim.gleisbild.gleisbildWorker;

import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModel;

public class minimizeGB extends gleisbildWorkerBase<gleisbildModel> {
   public minimizeGB(gleisbildModel glb, GleisAdapter main) {
      super(glb, main);
   }

   private void cutLeft() {
      while (this.glbModel.getGleisWidth() > 1) {
         for (int y = 0; y < this.glbModel.getGleisHeight(); y++) {
            gleis gl = this.glbModel.getXY_null(0, y);
            if (!gl.isEmpty()) {
               return;
            }
         }

         this.glbModel.deleteColumn(0);
      }
   }

   private void cutRight() {
      while (this.glbModel.getGleisWidth() > 1) {
         for (int y = 0; y < this.glbModel.getGleisHeight(); y++) {
            gleis gl = this.glbModel.getXY_null(this.glbModel.getGleisWidth() - 1, y);
            if (!gl.isEmpty()) {
               return;
            }
         }

         this.glbModel.gl_resize(this.glbModel.getGleisWidth() - 1, this.glbModel.getGleisHeight());
      }
   }

   private void cutTop() {
      while (this.glbModel.getGleisWidth() > 1) {
         for (int x = 0; x < this.glbModel.getGleisWidth(); x++) {
            gleis gl = this.glbModel.getXY_null(x, 0);
            if (!gl.isEmpty()) {
               return;
            }
         }

         this.glbModel.deleteRow(0);
      }
   }

   private void cutBottom() {
      while (this.glbModel.getGleisHeight() > 1) {
         for (int x = 0; x < this.glbModel.getGleisWidth(); x++) {
            gleis gl = this.glbModel.getXY_null(x, this.glbModel.getGleisHeight() - 1);
            if (!gl.isEmpty()) {
               return;
            }
         }

         this.glbModel.gl_resize(this.glbModel.getGleisWidth(), this.glbModel.getGleisHeight() - 1);
      }
   }

   private void addBorder() {
      this.glbModel.insertColumn(0);
      this.glbModel.insertRow(0);
      this.glbModel.gl_resize(this.glbModel.getGleisWidth() + 1, this.glbModel.getGleisHeight() + 1);
   }

   public void minimize() {
      this.glbModel.allOff();
      this.cutRight();
      this.cutBottom();
      this.cutLeft();
      this.cutTop();
      this.addBorder();
   }
}

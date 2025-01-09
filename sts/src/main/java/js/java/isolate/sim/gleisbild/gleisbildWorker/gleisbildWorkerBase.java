package js.java.isolate.sim.gleisbild.gleisbildWorker;

import java.awt.Rectangle;
import java.util.LinkedList;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModel;

public class gleisbildWorkerBase<T extends gleisbildModel> {
   protected final T glbModel;
   protected final GleisAdapter my_main;

   public gleisbildWorkerBase(T gl, GleisAdapter main) {
      this.glbModel = gl;
      this.my_main = main;
   }

   protected void repaint() {
      this.glbModel.repaint();
   }

   protected final void showStatus(String s, int type) {
      this.my_main.showStatus(s, type);
   }

   protected final void setProgress(int p) {
      this.my_main.setProgress(p);
   }

   public final void markHLine(gleis gl, int w, LinkedList<gleis> m) {
      for (int i = 0; i <= w; i++) {
         try {
            m.add(this.glbModel.getXY_null(gl.getCol() + i, gl.getRow()));
         } catch (NullPointerException var6) {
         }
      }
   }

   public final void markVLine(gleis gl, int h, LinkedList<gleis> m) {
      for (int i = 0; i <= h; i++) {
         try {
            m.add(this.glbModel.getXY_null(gl.getCol(), gl.getRow() + i));
         } catch (NullPointerException var6) {
         }
      }
   }

   public final void markLines(Rectangle r, LinkedList<gleis> m) {
      gleis g1 = this.glbModel.getXY_null(r.x, r.y);
      gleis g2 = this.glbModel.getXY_null(r.x + r.width, r.y);
      gleis g3 = this.glbModel.getXY_null(r.x, r.y + r.height);
      this.markHLine(g1, r.width, m);
      this.markHLine(g3, r.width, m);
      this.markVLine(g1, r.height, m);
      this.markVLine(g2, r.height, m);
   }

   public final void markLines(LinkedList<Rectangle> areas, LinkedList<gleis> m) {
      for (Rectangle r : areas) {
         this.markLines(r, m);
      }
   }

   public final void markLines(Rectangle r) {
      LinkedList<gleis> m = new LinkedList();
      this.glbModel.clearMarkedGleis();
      this.markLines(r, m);

      for (gleis gl : m) {
         this.glbModel.addMarkedGleis(gl);
      }
   }

   public final void markLines(LinkedList<Rectangle> areas) {
      LinkedList<gleis> m = new LinkedList();
      this.glbModel.clearMarkedGleis();

      for (Rectangle r : areas) {
         this.markLines(r, m);
      }

      for (gleis gl : m) {
         this.glbModel.addMarkedGleis(gl);
      }
   }
}

package js.java.isolate.sim.gleis;

import java.awt.Graphics2D;
import java.util.Iterator;

class paint_kreuzungsbruecke extends paint2Base {
   paint_kreuzungsbruecke(paint2Base p) {
      super(p);
   }

   paint_kreuzungsbruecke() {
      super(null);
   }

   private void paint(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      int x0 = (int)((double)xscal / 2.0);
      int y0 = (int)((double)yscal / 2.0);
      int rc = 0;
      int cc = 0;
      Iterator<gleis> it = gl.getNachbarn();

      while (it.hasNext()) {
         gleis ngl = (gleis)it.next();
         if (ngl.getCol() == gl.getCol()) {
            cc++;
         }

         if (ngl.getRow() == gl.getRow()) {
            rc++;
         }
      }

      g.setColor(gleis.colors.col_stellwerk_geländer);
      if (rc == 2) {
         g.drawLine(x0 - 4, y0 - 2, x0 + 6, y0 - 2);
         g.drawLine(x0 - 4, y0 + 2, x0 + 6, y0 + 2);
      } else if (cc == 2) {
         g.drawLine(x0 - 1, y0 - 2, x0 - 1, y0 + 4);
         g.drawLine(x0 + 2, y0 - 2, x0 + 2, y0 + 4);
      } else {
         g.drawLine(x0 - 4, y0 - 4, x0 + 5, y0 + 5);
         g.drawLine(x0 - 4, y0 - 5, x0 + 5, y0 + 4);
         g.drawLine(x0 - 4, y0, x0 + 3, y0 + 7);
         g.drawLine(x0 - 4, y0 + 1, x0 + 3, y0 + 8);
      }
   }

   @Override
   public void paint3Sim(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.paint(gl, g, xscal, yscal, fscal);
      super.paint3Sim(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint3Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.paint(gl, g, xscal, yscal, fscal);
      super.paint3Editor(gl, g, xscal, yscal, fscal);
   }
}

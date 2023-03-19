package js.java.isolate.sim.gleis;

import java.awt.Graphics2D;

class paint_vorsignaltrenner extends paint2Base {
   paint_vorsignaltrenner(paint2Base p) {
      super(p);
   }

   paint_vorsignaltrenner() {
      super(null);
   }

   private void paint(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      int x0 = xscal;
      int y0 = yscal;
      int x1 = xscal;
      int y1 = yscal;
      switch(gl.richtung) {
         case right:
            x0 = xscal - 1;
            x1 = xscal - 1;
            y0 = yscal - 9;
            y1 = yscal - 4;
            break;
         case left:
            x0 = xscal - 14;
            x1 = xscal - 14;
            y0 = yscal - 14;
            y1 = yscal - 9;
            break;
         case down:
            x0 = xscal - 6;
            x1 = xscal - 1;
            y0 = yscal - 4;
            y1 = yscal - 4;
            break;
         case up:
            x0 = xscal - 14;
            x1 = xscal - 9;
            y0 = yscal - 14;
            y1 = yscal - 14;
      }

      g.setColor(gleis.colors.col_stellwerk_gelbein);
      g.fillOval(x0, y0, 4, 4);
      g.fillOval(x1, y1, 4, 4);
   }

   @Override
   public void paint3Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.paint(gl, g, xscal, yscal, fscal);
      super.paint3Editor(gl, g, xscal, yscal, fscal);
   }
}

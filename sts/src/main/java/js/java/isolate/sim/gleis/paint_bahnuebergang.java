package js.java.isolate.sim.gleis;

import java.awt.Graphics2D;

class paint_bahnuebergang extends paint_wbahnuebergang {
   paint_bahnuebergang(paint2Base p) {
      super(p);
   }

   paint_bahnuebergang() {
      super(null);
   }

   @Override
   protected void paint(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      int x0 = (int)((double)xscal / 2.0);
      int y0 = (int)((double)yscal / 2.0);
      gl.paintSmallKnob(g, x0, y0 + 2, xscal);
      super.paint(gl, g, xscal, yscal, fscal);
   }
}

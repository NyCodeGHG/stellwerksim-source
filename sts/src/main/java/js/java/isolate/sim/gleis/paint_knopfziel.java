package js.java.isolate.sim.gleis;

import java.awt.Graphics2D;

class paint_knopfziel extends paint2Base {
   paint_knopfziel(paint2Base p) {
      super(p);
   }

   paint_knopfziel() {
      super(null);
   }

   private void paint(gleis gl, Graphics2D g, int xscal, int yscal, int fscal, boolean sim) {
      int x0 = (int)((double)xscal / 2.0);
      int y0 = (int)((double)yscal / 2.0);
      gl.paintBigKnob(g, x0, y0, xscal);
   }

   @Override
   public void paint3Sim(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.paint(gl, g, xscal, yscal, fscal, true);
      super.paint3Sim(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint3Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.paint(gl, g, xscal, yscal, fscal, false);
      gl.setSmooth(g, false, 2);
      int x0 = (int)((double)(-1 * xscal) / 14.0 + (double)xscal / 2.0) - 2;
      int y0 = (int)((double)(-1 * yscal) / 14.0 + (double)yscal / 2.0) - 4;
      String t1 = "" + gl.enr;
      gl.printtext(g, t1, gleis.colors.col_text_s, x0 + 1 + 6, y0 + 1 - 12, 12);
      gl.printtext(g, t1, gleis.colors.col_text, x0 + 6, y0 - 12, 12);
      super.paint3Editor(gl, g, xscal, yscal, fscal);
   }
}

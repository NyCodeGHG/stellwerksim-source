package js.java.isolate.sim.gleis;

import java.awt.Graphics2D;
import java.awt.Polygon;

class paint_sprung extends paint2Base {
   paint_sprung(paint2Base p) {
      super(p);
   }

   paint_sprung() {
      super(null);
   }

   @Override
   public void paint3Sim(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      super.paint3Sim(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint3Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      g.setColor(gleis.colors.col_pfeil);
      int x0 = (int)((double)(-1 * xscal) / 14.0 + (double)xscal / 2.0);
      int y0 = (int)((double)(-1 * yscal) / 14.0 + (double)yscal / 2.0) - 4;
      gl.setSmooth(g, true, 2);
      Polygon pgn = new Polygon();
      pgn.addPoint(x0, y0);
      pgn.addPoint(x0 - 8, y0 - 5);
      pgn.addPoint(x0, y0 - 10);
      pgn.addPoint(x0 + 8, y0 - 5);
      g.fillPolygon(pgn);
      gl.setSmooth(g, false, 2);
      String t1 = "" + gl.enr;
      gl.printtext(g, t1, gleis.colors.col_text_s, x0 + 1 + 6, y0 + 1 - 12, 12);
      gl.printtext(g, t1, gleis.colors.col_text, x0 + 6, y0 - 12, 12);
      super.paint3Editor(gl, g, xscal, yscal, fscal);
   }
}

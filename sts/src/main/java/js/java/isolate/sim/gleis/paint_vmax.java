package js.java.isolate.sim.gleis;

import java.awt.Graphics2D;
import java.awt.Polygon;

class paint_vmax extends paint2Base {
   paint_vmax(paint2Base p) {
      super(p);
   }

   paint_vmax() {
      super(null);
   }

   @Override
   public void paint3Sim(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      super.paint3Sim(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint3Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      int x0 = 0;
      int y0 = 0;
      switch(gl.richtung) {
         case right:
            x0 = xscal - 9;
            y0 = yscal - 8;
         case left:
            break;
         case down:
            y0 = yscal - 8;
            break;
         case up:
            x0 = xscal - 9;
            break;
         default:
            String t1 = "Vmax-Fehler";
            gl.printtext(g, t1, gleis.colors.col_text_s, 5, yscal * 3 / 4 + 1, 12);
            gl.printtext(g, t1, gleis.colors.col_text, 4, yscal * 3 / 4, 12);
      }

      if (gl.telement == gleis.ELEMENT_WIEDERVMAX) {
         g.setColor(gleis.colors.col_stellwerk_schwarz);
      } else {
         g.setColor(gleis.colors.col_stellwerk_gelbein);
      }

      g.fillRect(x0, y0, 9, 8);
      if (gl.telement == gleis.ELEMENT_WIEDERVMAX) {
         g.setColor(gleis.colors.col_stellwerk_weiss);
      } else {
         g.setColor(gleis.colors.col_stellwerk_schwarz);
      }

      Polygon pgn = new Polygon();
      pgn.addPoint(x0 + 1, y0 + 1);
      pgn.addPoint(x0 + 3, y0 + 6);
      pgn.addPoint(x0 + 4, y0 + 6);
      pgn.addPoint(x0 + 7, y0 + 1);
      pgn.addPoint(x0 + 6, y0 + 1);
      pgn.addPoint(x0 + 4, y0 + 5);
      pgn.addPoint(x0 + 3, y0 + 5);
      pgn.addPoint(x0 + 2, y0 + 1);
      g.fillPolygon(pgn);
      g.drawPolygon(pgn);
      g.setColor(gleis.colors.col_stellwerk_rot);
      switch(gl.richtung) {
         case right:
            g.drawLine(x0 + 9, y0, x0 + 9, y0 + 8 - 1);
            break;
         case left:
            g.drawLine(x0, y0, x0, y0 + 8 - 1);
            break;
         case down:
            g.drawLine(x0, y0 + 8, x0 + 9 - 1, y0 + 8);
            break;
         case up:
            g.drawLine(x0, y0, x0 + 9 - 1, y0);
      }

      super.paint3Editor(gl, g, xscal, yscal, fscal);
   }
}

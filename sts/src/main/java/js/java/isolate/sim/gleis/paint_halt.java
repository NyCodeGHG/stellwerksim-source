package js.java.isolate.sim.gleis;

import java.awt.Graphics2D;
import java.awt.Polygon;

class paint_halt extends paint2Base {
   paint_halt(paint2Base p) {
      super(p);
   }

   paint_halt() {
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
            String t1 = "HP-Fehler";
            gl.printtext(g, t1, gleis.colors.col_text_s, 5, yscal * 3 / 4 + 1, 12);
            gl.printtext(g, t1, gleis.colors.col_text, 4, yscal * 3 / 4, 12);
      }

      if (gl.telement == gleis.ELEMENT_HALTEPUNKT) {
         g.setColor(gleis.colors.col_stellwerk_geländer);
      } else {
         g.setColor(gleis.colors.col_stellwerk_schwarz);
      }

      g.fillRect(x0, y0, 9, 8);
      g.setColor(gleis.colors.col_stellwerk_weiss);
      Polygon pgn = new Polygon();
      if (gl.telement == gleis.ELEMENT_DISPLAYKONTAKT) {
         pgn.addPoint(x0 + 2, y0 + 1);
         pgn.addPoint(x0 + 2, y0 + 6);
         pgn.addPoint(x0 + 5, y0 + 5);
         pgn.addPoint(x0 + 5, y0 + 2);
      } else {
         pgn.addPoint(x0 + 1, y0 + 1);
         pgn.addPoint(x0 + 1, y0 + 6);
         pgn.addPoint(x0 + 3, y0 + 6);
         pgn.addPoint(x0 + 3, y0 + 4);
         pgn.addPoint(x0 + 5, y0 + 4);
         pgn.addPoint(x0 + 5, y0 + 6);
         pgn.addPoint(x0 + 7, y0 + 6);
         pgn.addPoint(x0 + 7, y0 + 1);
         pgn.addPoint(x0 + 5, y0 + 1);
         pgn.addPoint(x0 + 5, y0 + 3);
         pgn.addPoint(x0 + 3, y0 + 3);
         pgn.addPoint(x0 + 3, y0 + 1);
         g.fillPolygon(pgn);
      }

      g.drawPolygon(pgn);
      g.setColor(gleis.colors.col_stellwerk_rot);
      int xp = 0;
      int yp = 0;
      switch(gl.richtung) {
         case right:
            yp = yscal;
            g.drawLine(x0 + 9, y0, x0 + 9, y0 + 8 - 1);
            break;
         case left:
            yp = yscal;
            g.drawLine(x0, y0, x0, y0 + 8 - 1);
            break;
         case down:
            xp = xscal;
            g.drawLine(x0, y0 + 8, x0 + 9 - 1, y0 + 8);
            break;
         case up:
            xp = xscal;
            g.drawLine(x0, y0, x0 + 9 - 1, y0);
      }

      String t1 = gl.swwert;
      gl.printtext(g, t1, gleis.colors.col_text_s, xp * 3 / 4 + 4 + 1, yp * 3 / 4 + 1, 12);
      gl.printtext(g, t1, gleis.colors.col_text, xp * 3 / 4 + 4, yp * 3 / 4, 12);
      super.paint3Editor(gl, g, xscal, yscal, fscal);
   }
}

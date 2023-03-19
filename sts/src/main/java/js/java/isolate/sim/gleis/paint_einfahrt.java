package js.java.isolate.sim.gleis;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Iterator;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class paint_einfahrt extends paint_ausfahrt {
   private static final int FONTSCALE = 0;

   paint_einfahrt(paint2Base p) {
      super(p);
   }

   paint_einfahrt() {
      super(null);
   }

   @Override
   protected Rectangle getDimensions(String t1, gleis gl, Graphics2D g, int xscal, int yscal) {
      Rectangle ret = super.getDimensions(t1, gl, g, xscal + 0, yscal + 0);
      gleisElements.RICHTUNG r = gl.richtung;
      if (r == gleisElements.RICHTUNG.down) {
         ret.y = -yscal;
      } else {
         ret.y = yscal;
      }

      return ret;
   }

   @Override
   protected void paintText(gleis gl, Graphics2D g, int xscal, int yscal, boolean sim, boolean invers) {
      boolean hasAusfahrt = false;
      Iterator<gleis> it = gl.getNachbarn();

      while(!hasAusfahrt && it.hasNext()) {
         gleis ngl = (gleis)it.next();
         if (ngl.telement == gleis.ELEMENT_AUSFAHRT) {
            hasAusfahrt = true;
         }
      }

      if (!hasAusfahrt || !sim) {
         super.paintText(gl, g, xscal, yscal, sim, true);
      }
   }

   @Override
   public void paint3Sim(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      if (gl.getFluentData().isGesperrt()) {
         g.setColor(gleis.colors.col_stellwerk_rot_locked);
         g.drawLine(0, 0, xscal, yscal);
         g.drawLine(0, yscal, xscal, 0);
      }
   }

   @Override
   public void paint3Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      g.setColor(gleis.colors.col_pfeil);
      int x0 = (int)((double)(-1 * xscal) / 14.0 + (double)xscal / 2.0);
      int y0 = (int)((double)(-1 * yscal) / 14.0 + (double)yscal / 2.0) - 4;
      int x1 = x0 + 5;
      int y1 = y0 - 8;
      int x2 = x0 - 5;
      int y2 = y0 - 8;
      gl.setSmooth(g, true, 2);
      Polygon pgn = new Polygon();
      pgn.addPoint(x0, y0);
      pgn.addPoint(x1, y1);
      pgn.addPoint(x2, y2);
      g.fillPolygon(pgn);
      gl.setSmooth(g, false, 2);
   }
}

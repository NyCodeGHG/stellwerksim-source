package js.java.isolate.sim.gleis;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class paint_ausfahrt extends paint_text_base {
   private static final int FONTSCALE = 1;

   paint_ausfahrt(paint2Base p) {
      super(p);
   }

   paint_ausfahrt() {
      super(null);
   }

   @Override
   protected Rectangle getDimensions(String t1, gleis gl, Graphics2D g, int xscal, int yscal) {
      int w = gl.printwidth(g, t1, xscal - 1) + 3;
      Rectangle ret = new Rectangle(0, 1, w, yscal - 1 + 2);
      gleisElements.RICHTUNG r = gl.richtung;
      if (r == gleisElements.RICHTUNG.up) {
         ret.y = -yscal;
      } else {
         ret.y = yscal;
      }

      return ret;
   }

   @Override
   protected void paintText(String t1, gleis gl, Graphics2D g2, Rectangle textr, int xscal, int yscal, boolean vertical) {
      g2.setColor(gleis.colors.col_stellwerk_signalnummerhgr);
      g2.fillRect(0, 0, textr.width, textr.height);
      gl.printtext(g2, t1, gleis.colors.col_stellwerk_signalnummer, 0, -1, xscal - 1, 1);
   }

   protected void paintText(gleis gl, Graphics2D g, int xscal, int yscal, boolean sim, boolean invers) {
      String t1;
      if (!sim) {
         t1 = gl.swwert;
      } else {
         t1 = gl.getSWWert_special();
      }

      gleisElements.RICHTUNG r = gl.richtung;
      int w = 0;
      int rot = 0;
      if (gl.mycol > gl.glbModel.getGleisWidth() / 2) {
         w = gl.printwidth(g, t1, 12);
         rot = 180;
      }

      if (sim) {
         this.preparePaint2(t1, gl, g, xscal, yscal, rot);
      } else {
         int h = 0;
         if (!invers && r == gleisElements.RICHTUNG.up || invers && r == gleisElements.RICHTUNG.down) {
            h = -yscal * 2;
         }

         gl.printtext(g, t1, gleis.colors.col_stellwerk_schwarz, 5 - w, h + yscal * 3 / 4 + 1, 12);
         gl.printtext(g, t1, gleis.colors.col_stellwerk_schwarz, 3 - w, h + yscal * 3 / 4 + 1, 12);
         gl.printtext(g, t1, gleis.colors.col_stellwerk_schwarz, 3 - w, h + yscal * 3 / 4 - 1, 12);
         gl.printtext(g, t1, gleis.colors.col_stellwerk_schwarz, 5 - w, h + yscal * 3 / 4 - 1, 12);
         gl.printtext(g, t1, gleis.colors.col_stellwerk_weiss, 4 - w, h + yscal * 3 / 4, 12);
      }
   }

   protected void paint(gleis gl, Graphics2D g, int xscal, int yscal, int fscal, boolean sim) {
      int x0 = (int)((double)xscal / 2.0);
      int y0 = (int)((double)yscal / 2.0);
      gl.paintBigKnob(g, x0, y0, xscal);
      gleisElements.RICHTUNG r = gl.richtung;
      switch(r) {
         case right:
            gl.paintAusfahrt(g, 0, yscal * 3 / 4, fscal, Math.PI * 3.0 / 2.0);
            break;
         case left:
            gl.paintAusfahrt(g, xscal, yscal * 1 / 4, fscal, Math.PI / 2);
            break;
         case down:
            gl.paintAusfahrt(g, xscal * 1 / 4, 0, fscal, 0.0);
            break;
         case up:
            gl.paintAusfahrt(g, xscal * 3 / 4, yscal, fscal, Math.PI);
      }
   }

   @Override
   public void paint2Sim(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.paintText(gl, g, xscal, yscal, true, false);
      super.paint2Sim(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint2Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.paintText(gl, g, xscal, yscal, false, false);
      super.paint2Editor(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint3Sim(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.paint(gl, g, xscal, yscal, fscal, true);
      super.paint3Sim(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint3Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.paint(gl, g, xscal, yscal, fscal, false);
      int x0 = 0;
      int y0 = 0;
      int x1 = 0;
      int y1 = 0;
      int x2 = 0;
      int y2 = 0;
      g.setColor(gleis.colors.col_pfeil);
      x0 = (int)((double)(-1 * xscal) / 14.0 + (double)xscal / 2.0);
      y0 = (int)((double)(-1 * yscal) / 14.0 + (double)yscal / 2.0) - 4;
      x1 = x0 + 10;
      y1 = y0 + 0;
      x2 = x0 + 5;
      y2 = y0 - 8;
      gl.setSmooth(g, true, 2);
      Polygon pgn = new Polygon();
      pgn.addPoint(x0, y0);
      pgn.addPoint(x1, y1);
      pgn.addPoint(x2, y2);
      g.fillPolygon(pgn);
      gl.setSmooth(g, false, 2);
      String t1 = "" + gl.enr;
      gl.printtext(g, t1, gleis.colors.col_text_s, x0 + 1 + 6, y0 + 1 - 12, 12);
      gl.printtext(g, t1, gleis.colors.col_text, x0 + 6, y0 - 12, 12);
   }
}

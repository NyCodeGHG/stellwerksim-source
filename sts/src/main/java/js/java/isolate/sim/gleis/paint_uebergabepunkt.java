package js.java.isolate.sim.gleis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class paint_uebergabepunkt extends paint2Base {
   paint_uebergabepunkt(paint2Base p) {
      super(p);
   }

   paint_uebergabepunkt() {
      super(null);
   }

   private void paint(gleis gl, Graphics2D g, int xscal, int yscal, int fscal, boolean alpha) {
      Color col_rot = gl.fdata.stellung == gleisElements.ST_ÜBERGABEPUNKT_ROT ? gleis.colors.col_stellwerk_gelbein : gleis.colors.col_stellwerk_gelbaus;
      Color col_gruen = gl.fdata.stellung == gleisElements.ST_ÜBERGABEPUNKT_GRÜN ? gleis.colors.col_stellwerk_gruenein : gleis.colors.col_stellwerk_gruenaus;
      Color col_draw = gleis.colors.col_stellwerk_schwarz;
      Color col_back = gleis.colors.col_stellwerk_back;
      if (gl.gleisExtend != null) {
         Color colr = (Color)gleis.colors.col_stellwerk_backmulti.get(gl.gleisExtend.getFarbe());
         if (colr != null) {
            col_back = colr;
         }
      }

      if (alpha) {
         col_back = new Color(col_back.getRed(), col_back.getGreen(), col_back.getBlue(), 102);
         col_draw = new Color(col_draw.getRed(), col_draw.getGreen(), col_draw.getBlue(), 68);
         col_rot = new Color(col_rot.getRed(), col_rot.getGreen(), col_rot.getBlue(), 68);
         col_gruen = new Color(col_gruen.getRed(), col_gruen.getGreen(), col_gruen.getBlue(), 68);
      } else {
         col_back = new Color(col_back.getRed(), col_back.getGreen(), col_back.getBlue(), 187);
      }

      switch(gl.richtung) {
         case right:
            gl.paintVSignal(g, 0, yscal * 3 / 4, fscal, col_rot, col_gruen, col_draw, col_back, Math.PI * 3.0 / 2.0, 1, 0);
            break;
         case left:
            gl.paintVSignal(g, xscal, yscal * 1 / 4, fscal, col_rot, col_gruen, col_draw, col_back, Math.PI / 2, -1, 0);
            break;
         case down:
            gl.paintVSignal(g, xscal * 1 / 4, 0, fscal, col_rot, col_gruen, col_draw, col_back, 0.0, 0, 1);
            break;
         case up:
            gl.paintVSignal(g, xscal * 3 / 4, yscal, fscal, col_rot, col_gruen, col_draw, col_back, Math.PI, 0, -1);
      }
   }

   @Override
   public void paint3Sim(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.paint(gl, g, xscal, yscal, fscal, false);
      super.paint3Sim(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint3Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      int x0 = 0;
      int y0 = 0;
      int x1 = 0;
      int y1 = 0;
      int x2 = 0;
      int y2 = 0;
      switch(gl.richtung) {
         case right:
            x0 += xscal - 1;
            x1 = x0;
            y1 = y0 + (yscal - 1);
            break;
         case left:
            x1 = x0;
            y1 = y0 + (yscal - 1);
            break;
         case down:
            y0 += yscal - 1;
            y1 = y0;
            x1 = x0 + (xscal - 1);
            break;
         case up:
            x1 = x0 + (xscal - 1);
            y1 = y0;
      }

      g.setColor(gleis.colors.col_stellwerk_schwarz);
      g.drawLine(x0, y0, x1, y1);
      g.setColor(gleis.colors.col_pfeil);
      x0 = (int)((double)(-1 * xscal) / 14.0 + (double)xscal / 2.0) - 2;
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
      this.paint(gl, g, xscal, yscal, fscal, true);
      gl.setSmooth(g, false, 2);
      String t1 = "" + gl.enr;
      gl.printtext(g, t1, gleis.colors.col_text_s, x0 + 1 + 6, y0 + 1 - 12, 12);
      gl.printtext(g, t1, gleis.colors.col_text, x0 + 6, y0 - 12, 12);
      super.paint3Editor(gl, g, xscal, yscal, fscal);
   }
}

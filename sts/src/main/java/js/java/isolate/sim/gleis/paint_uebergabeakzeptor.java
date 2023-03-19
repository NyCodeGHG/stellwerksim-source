package js.java.isolate.sim.gleis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class paint_uebergabeakzeptor extends paint_text_base {
   private static final int FONTSCALE = 1;

   paint_uebergabeakzeptor(paint2Base p) {
      super(p);
   }

   paint_uebergabeakzeptor() {
      super(null);
   }

   @Override
   protected Rectangle getDimensions(String t1, gleis gl, Graphics2D g, int xscal, int yscal) {
      int w = gl.printwidth(g, t1, xscal - 1) + 3;
      return new Rectangle(0, 1, w, yscal - 1 + 2);
   }

   @Override
   protected void paintText(String t1, gleis gl, Graphics2D g2, Rectangle textr, int xscal, int yscal, boolean vertical) {
      g2.setColor(gleis.colors.col_stellwerk_signalnummerhgr);
      g2.fillRect(0, 0, textr.width, textr.height);
      gl.printtext(g2, t1, gleis.colors.col_stellwerk_signalnummer, 0, -1, xscal - 1, 1);
   }

   @Override
   protected double blockOffset(int rot) {
      return rot == 270 ? 1.5 : 1.0;
   }

   private void paint(gleis gl, Graphics2D g, int xscal, int yscal, int fscal, boolean sim) {
      int x0 = (int)((double)xscal / 2.0);
      int y0 = (int)((double)yscal / 2.0);
      gl.paintBigKnob(g, x0, y0, xscal);
      x0 = (int)((double)xscal / 2.0);
      y0 = (int)((double)yscal / 2.0 - 6.0) + yscal + 3;
      int x1 = (int)((double)xscal / 2.0 + 8.0);
      int y1 = (int)((double)yscal / 2.0 - 6.0) + yscal + 3;
      Color fweiss = gleis.colors.col_stellwerk_frei;
      Color frot = gleis.colors.col_stellwerk_rotaus;
      if (gl.fdata.stellung == gleisElements.ST_ÜBERGABEAKZEPTOR_OK) {
         fweiss = gleis.colors.col_stellwerk_reserviert;
         frot = gleis.colors.col_stellwerk_rotaus;
      } else if (gl.fdata.stellung == gleisElements.ST_ÜBERGABEAKZEPTOR_NOK) {
         fweiss = gleis.colors.col_stellwerk_frei;
         frot = gleis.colors.col_stellwerk_rotein;
      } else if (gl.fdata.stellung == gleisElements.ST_ÜBERGABEAKZEPTOR_ANFRAGE) {
         if (gleis.blinkon) {
            frot = gleis.colors.col_stellwerk_rotaus;
            fweiss = gleis.colors.col_stellwerk_reserviert;
         } else {
            frot = gleis.colors.col_stellwerk_rotein;
            fweiss = gleis.colors.col_stellwerk_frei;
         }
      }

      gl.paintSignalLED(g, x0 - 7, y0 - 3, true, fweiss);
      gl.paintSignalLED(g, x1 - 7, y1 - 3, true, frot);
   }

   @Override
   public void paint2Sim(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      if (!gl.getSWWert_special().isEmpty()) {
         this.preparePaint1(gl.getSWWert_special(), gl, g, xscal, yscal, fscal);
      }

      super.paint2Sim(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint2Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      if (!gl.swwert.isEmpty()) {
         this.preparePaint1(gl.swwert, gl, g, xscal, yscal, fscal);
      }

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
      super.paint3Editor(gl, g, xscal, yscal, fscal);
   }
}

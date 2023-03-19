package js.java.isolate.sim.gleis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class paint_weiche extends paint2Base {
   paint_weiche(paint2Base p) {
      super(p);
   }

   paint_weiche() {
      super(null);
   }

   private void paint(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      int x0 = (int)((double)xscal / 2.0);
      int y0 = (int)((double)yscal / 2.0);
      int rechts = 0;
      int links = 0;
      Iterator<gleis.nachbarGleis> it = gl.p_getNachbarn();

      while(it.hasNext()) {
         gleis.nachbarGleis gl2 = (gleis.nachbarGleis)it.next();
         if (gl2.gl.getCol() > gl.getCol()) {
            ++rechts;
         } else if (gl2.gl.getCol() < gl.getCol()) {
            ++links;
         }
      }

      gl.paintSmallKnob(g, x0 + (links < rechts ? -3 : 2), y0 + 2, xscal);
      x0 = (int)((double)xscal / 2.0) + (links > rechts ? -5 : 1);
      y0 = (int)((double)yscal / 2.0 + (double)(gl.telement == gleis.ELEMENT_WEICHEOBEN ? 9 : -6));
      Color colr = gleis.colors.col_stellwerk_schwarz;
      if (!gl.fdata.power_off) {
         if (gl.fdata.stellung == gleisElements.ST_WEICHE_AUS) {
            if (gleis.blinkon_slow) {
               colr = gleis.colors.col_stellwerk_defekt;
            } else {
               colr = gleis.colors.col_stellwerk_rot;
            }
         } else if (gl.getFluentData().getStatus() != 3 && gl.getFluentData().getStatus() != 4) {
            colr = gleis.colors.col_stellwerk_reserviert;
         }
      }

      if (gl.highlighted > 0 && gleis.blinkon) {
         colr = gleis.colors.col_stellwerk_gruenein;
      }

      gl.setSmooth(g, true, 4);
      gl.paintSignalLED(g, x0, y0 - 3, true, colr);
      gl.setSmooth(g, false, 4);
   }

   @Override
   public void paint3Sim(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.paint(gl, g, xscal, yscal, fscal);
      super.paint3Sim(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint3Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.paint(gl, g, xscal, yscal, fscal);
      super.paint3Editor(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint1Sim(gleis gl, Graphics2D g2, int xscal, int yscal, int fscal) {
      int rechts = 0;
      int links = 0;
      boolean nweiche = false;

      gleis gl2;
      for(Iterator<gleis> it = gl.getNachbarn(); it.hasNext(); nweiche |= gl2.telement == gl.telement) {
         gl2 = (gleis)it.next();
         if (gl2.getCol() > gl.getCol()) {
            ++rechts;
         } else if (gl2.getCol() < gl.getCol()) {
            ++links;
         }
      }

      if (!nweiche || (gl.getCol() & 1) == 0) {
         int ty;
         int by;
         int tx;
         if (gl.telement == gleis.ELEMENT_WEICHEOBEN) {
            tx = xscal / 2;
            ty = yscal + yscal / 3 - 7;
            by = ty + 1;
         } else {
            tx = xscal / 2;
            ty = -yscal / 3 - 2;
            by = ty + 8;
         }

         int w = gl.printwidth(g2, gl.getShortElementName(), 8);
         g2.setColor(gleis.colors.col_stellwerk_gleis);
         if (links < rechts) {
            --tx;
            g2.drawLine(tx - w + 4, by, xscal / 2, by);
            g2.drawLine(xscal / 2, by, xscal / 2, yscal / 2);
         } else {
            tx -= 2;
            g2.drawLine(xscal / 2, by, tx + 2 + w, by);
            g2.drawLine(xscal / 2, by, xscal / 2, yscal / 2);
         }

         if (links < rechts) {
            gl.printtextright(g2, gl.getShortElementName(), gleis.colors.col_stellwerk_nummer, tx, ty, 8);
         } else {
            gl.printtext(g2, gl.getShortElementName(), gleis.colors.col_stellwerk_nummer, tx, ty, 8);
         }
      }

      super.paint1Sim(gl, g2, xscal, yscal, fscal);
   }
}

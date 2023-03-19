package js.java.isolate.sim.gleis;

import java.awt.Color;
import java.awt.Graphics2D;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class paint_wbahnuebergang extends paint2Base {
   paint_wbahnuebergang(paint2Base p) {
      super(p);
   }

   paint_wbahnuebergang() {
      super(null);
   }

   protected void paint(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      int x0 = (int)((double)xscal / 2.0);
      int y0 = (int)((double)yscal / 2.0 - 6.0);
      int x1 = (int)((double)xscal / 2.0 + 8.0);
      int y1 = (int)((double)yscal / 2.0 - 6.0);
      Color colr1 = gleis.colors.col_stellwerk_schwarz;
      Color colr2 = gleis.colors.col_stellwerk_schwarz;
      g.setColor(gleis.colors.col_stellwerk_schwarz);
      if (!gl.fdata.power_off) {
         if (gl.getFluentData().getStatus() != 3 && gl.getFluentData().getStatus() != 4) {
            if (gl.getFluentData().getStatus() == 1 || gl.getFluentData().getStatus() == 2) {
               colr2 = gleis.colors.col_stellwerk_reserviert;
            }
         } else if (!gleis.blinkon) {
            colr2 = gleis.colors.col_stellwerk_reserviert;
         }

         if (gl.fdata.stellung == gleisElements.ST_BAHNÜBERGANG_AUS) {
            colr1 = gleis.colors.col_stellwerk_defekt;
         } else if (gl.fdata.stellung == gleisElements.ST_BAHNÜBERGANG_OFFEN) {
            colr1 = gleis.colors.col_stellwerk_rotaus;
         } else if (gl.fdata.stellung == gleisElements.ST_BAHNÜBERGANG_GESCHLOSSEN) {
            if ((gl.getFluentData().getStatus() == 3 || gl.getFluentData().getStatus() == 4) && !gleis.blinkon) {
               colr1 = gleis.colors.col_stellwerk_rotaus;
            } else {
               colr1 = gleis.colors.col_stellwerk_rotein;
            }
         }
      }

      if (gl.highlighted > 0) {
         if (gleis.blinkon) {
            colr1 = gleis.colors.col_stellwerk_gruenein;
         } else {
            colr2 = gleis.colors.col_stellwerk_gruenein;
         }
      }

      gl.setSmooth(g, true, 4);
      gl.paintSignalLED(g, x0 - 7, y0 - 3, true, colr1);
      gl.paintSignalLED(g, x1 - 7, y1 - 3, true, colr2);
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
      g2.setColor(gleis.colors.col_stellwerk_grau);
      g2.fillRect(4, -yscal / 2, xscal - 7, yscal * 2);
      gl.printtextcentered(g2, gl.getElementName(), gleis.colors.col_stellwerk_nummer, 3, yscal + yscal / 3 - 7, 8);
      super.paint1Sim(gl, g2, xscal, yscal, fscal);
   }

   @Override
   public void paint1Editor(gleis gl, Graphics2D g2, int xscal, int yscal, int fscal) {
      g2.setColor(gleis.colors.col_stellwerk_grau);
      g2.fillRect(4, -yscal / 2, xscal - 7, yscal * 2);
      super.paint1Editor(gl, g2, xscal, yscal, fscal);
   }
}

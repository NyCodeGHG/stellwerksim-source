package js.java.isolate.sim.gleis;

import java.awt.Graphics2D;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class paint_anrufuebergang extends paint_wbahnuebergang {
   paint_anrufuebergang(paint2Base p) {
      super(p);
   }

   paint_anrufuebergang() {
      super(null);
   }

   @Override
   protected void paint(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      int x0 = (int)((double)xscal / 2.0);
      int y0 = (int)((double)yscal / 2.0);
      gl.paintSmallKnob(g, x0, y0 + 2, xscal);
      x0 = (int)((double)xscal / 2.0);
      y0 = (int)((double)yscal / 2.0 - 6.0);
      int x1 = (int)((double)xscal / 2.0 + 8.0);
      int y1 = (int)((double)yscal / 2.0 - 6.0);
      g.setColor(gleis.colors.col_stellwerk_schwarz);
      g.fillRect(x0 - 7, y0 - 3, 5, 5);
      g.fillRect(x1 - 7, y1 - 3, 5, 5);
      if (!gl.fdata.power_off) {
         if (gl.getFluentData().getStatus() != 3 && gl.getFluentData().getStatus() != 4) {
            if (gl.getFluentData().getStatus() == 1) {
               g.setColor(gleis.colors.col_stellwerk_reserviert);
               g.fillRect(x1 - 6, y1 - 2, 3, 3);
            }
         } else if (gleis.blinkon) {
            g.setColor(gleis.colors.col_stellwerk_reserviert);
            g.fillRect(x1 - 6, y1 - 2, 3, 3);
         }

         if (gl.fdata.stellung == gleisElements.ST_ANRUFÜBERGANG_AUS) {
            g.setColor(gleis.colors.col_stellwerk_defekt);
            g.fillRect(x0 - 6, y0 - 2, 3, 3);
         } else if (gl.fdata.stellung == gleisElements.ST_ANRUFÜBERGANG_OFFEN) {
            if (gleis.blinkon) {
               g.setColor(gleis.colors.col_stellwerk_rotaus);
            } else {
               g.setColor(gleis.colors.col_stellwerk_rotein);
            }

            g.fillRect(x0 - 6, y0 - 2, 3, 3);
         } else if (gl.fdata.stellung == gleisElements.ST_ANRUFÜBERGANG_GESCHLOSSEN) {
            g.setColor(gleis.colors.col_stellwerk_rotein);
            g.fillRect(x0 - 6, y0 - 2, 3, 3);
         }
      }
   }
}

package js.java.isolate.sim.gleis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

class paint_text_platte extends paint_text_base {
   paint_text_platte(paint2Base p) {
      super(p);
   }

   paint_text_platte() {
      super(null);
   }

   @Override
   protected Rectangle getDimensions(String t1, gleis gl, Graphics2D g, int xscal, int yscal) {
      int w = gl.printwidth(g, t1, yscal) + xscal * 2;
      return new Rectangle(0, -yscal / 3, w, yscal + 4 + yscal * 2 / 3);
   }

   @Override
   protected void paintText(String t1, gleis gl, Graphics2D g2, Rectangle textr, int xscal, int yscal, boolean vertical) {
      g2.setColor(gleis.colors.col_stellwerk_back);
      if (gl.gleisExtend != null) {
         gleis.colors.col_stellwerk_backms = (Color)gleis.colors.col_stellwerk_backmulti.get(gl.gleisExtend.getFarbe());
         if (gleis.colors.col_stellwerk_backms != null) {
            g2.setColor(gleis.colors.col_stellwerk_backms);
         }
      }

      g2.fill3DRect(0, 0, textr.width, textr.height, false);

      for(int ix = -1; ix <= 1; ++ix) {
         gl.printtext(g2, t1, gleis.colors.col_stellwerk_back, xscal + ix, yscal / 3 + 1, yscal);
      }

      for(int iy = -1; iy <= 1; ++iy) {
         gl.printtext(g2, t1, gleis.colors.col_stellwerk_back, xscal + 1, yscal / 3 + iy, yscal);
      }

      gl.printtext(g2, t1, gleis.colors.col_stellwerk_weiss, xscal - 1, yscal / 3 - 1, yscal);
      gl.printtext(g2, t1, gleis.colors.col_stellwerk_schwarz, xscal, yscal / 3, yscal);
   }

   @Override
   public void paint2Sim(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.preparePaint1(gl.swwert, gl, g, xscal, yscal, fscal);
      super.paint2Sim(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint2Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.preparePaint1(gl.swwert, gl, g, xscal, yscal, fscal);
      super.paint2Editor(gl, g, xscal, yscal, fscal);
   }
}

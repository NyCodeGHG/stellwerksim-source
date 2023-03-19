package js.java.isolate.sim.gleis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

class paint_text_gravur extends paint_text_base {
   paint_text_gravur(paint2Base p) {
      super(p);
   }

   paint_text_gravur() {
      super(null);
   }

   @Override
   protected Rectangle getDimensions(String t1, gleis gl, Graphics2D g, int xscal, int yscal) {
      int w = gl.printwidth(g, t1, yscal - 4) + 4;
      return new Rectangle(0, 0, w, yscal - 4);
   }

   @Override
   protected void paintText(String t1, gleis gl, Graphics2D g2, Rectangle textr, int xscal, int yscal, boolean vertical) {
      Color col1 = gleis.colors.col_stellwerk_schwarz;
      Color col2 = gleis.colors.col_stellwerk_weiss;
      gleis.colors.col_stellwerk_backms = (Color)gleis.colors.col_stellwerk_backmulti.get(gl.gleisExtend.getFarbe());
      if (gleis.colors.col_stellwerk_backms != null) {
         col2 = gleis.colors.col_stellwerk_backms.brighter();
         col1 = gleis.colors.col_stellwerk_backms.darker().darker();
      }

      int xo = vertical ? -1 : 1;
      int yo = 1;
      gl.printtext(g2, t1, col2, xo, yo + 1, yscal - 4);
      gl.printtext(g2, t1, col1, 0, 1, yscal - 4);
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

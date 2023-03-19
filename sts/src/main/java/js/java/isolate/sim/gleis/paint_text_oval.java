package js.java.isolate.sim.gleis;

import java.awt.Graphics2D;
import java.awt.Rectangle;

class paint_text_oval extends paint_text_base {
   paint_text_oval(paint2Base p) {
      super(p);
   }

   paint_text_oval() {
      super(null);
   }

   @Override
   protected Rectangle getDimensions(String t1, gleis gl, Graphics2D g, int xscal, int yscal) {
      int w = gl.printwidth(g, t1, yscal) + 6 + xscal / 2;
      return new Rectangle(0, -2, w, yscal + 4);
   }

   @Override
   protected void paintText(String t1, gleis gl, Graphics2D g2, Rectangle textr, int xscal, int yscal, boolean vertical) {
      gl.setSmooth(g2, true, 3);
      g2.setColor(gleis.colors.col_stellwerk_schwarz);
      g2.fillOval(0, 0, textr.width, yscal + 4);
      gl.printtext(g2, t1, gleis.colors.col_stellwerk_weiss, xscal / 2 - 2, 0, yscal);
      gl.setSmooth(g2, false, 3);
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

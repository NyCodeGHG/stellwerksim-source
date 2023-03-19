package js.java.isolate.sim.gleis;

import java.awt.Graphics2D;
import java.awt.Rectangle;

class paint_gleislabel extends paint_text_base {
   paint_gleislabel(paint2Base p) {
      super(p);
   }

   paint_gleislabel() {
      super(null);
   }

   @Override
   protected Rectangle getDimensions(String t1, gleis gl, Graphics2D g, int xscal, int yscal) {
      int w = gl.printwidth(g, t1, xscal - 3) + 2;
      return new Rectangle(0, 1, w, yscal - 3 + 2);
   }

   @Override
   protected void paintText(String t1, gleis gl, Graphics2D g2, Rectangle textr, int xscal, int yscal, boolean vertical) {
      g2.setColor(gleis.colors.col_stellwerk_signalnummerhgr);
      g2.fillRect(0, 0, textr.width, textr.height);
      gl.printtext(g2, t1, gleis.colors.col_stellwerk_signalnummer, 0, 0, xscal - 3, 0);
   }

   @Override
   public void paint3Sim(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.preparePaint1(gl.swwert, gl, g, xscal, yscal, fscal);
      super.paint3Sim(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint3Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.preparePaint1(gl.swwert, gl, g, xscal, yscal, fscal);
      super.paint3Editor(gl, g, xscal, yscal, fscal);
   }
}

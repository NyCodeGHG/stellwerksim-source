package js.java.isolate.sim.gleis;

import java.awt.Color;
import java.awt.Graphics2D;

class paint_buedisplay extends paint2Base {
   paint_buedisplay(paint2Base p) {
      super(p);
   }

   paint_buedisplay() {
      super(null);
   }

   private void paint(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      g.setColor(gleis.colors.col_stellwerk_schwarz);
      g.fillRect(0, -2, xscal + 1, yscal + 5);
      gl.paintDisplay(g, -3, 0, xscal - xscal / 5);
      gl.paintDisplay(g, 3, 0, xscal - xscal / 5);
   }

   private void paint(gleis gl, Graphics2D g, int xscal, int yscal, int fscal, String büname) {
      büname = "x10";
      gl.printtext(g, büname, gleis.colors.col_stellwerk_weiss, 0, yscal - 4, 6, 1);
   }

   private void paint(gleis gl, Graphics2D g, int xscal, int yscal, Color dcol, String text, boolean editor) {
      char c1 = ' ';
      char c2 = ' ';

      try {
         c1 = text.charAt(0);
      } catch (StringIndexOutOfBoundsException var12) {
      }

      try {
         c2 = text.charAt(1);
      } catch (StringIndexOutOfBoundsException var11) {
         c2 = c1;
         c1 = ' ';
      }

      gl.printDisplay(g, c1, -3, 0, xscal - xscal / 5, dcol, !editor);
      gl.printDisplay(g, c2, 3, 0, xscal - xscal / 5, dcol, !editor);
   }

   @Override
   public void paint3Sim(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      if (!gl.hideDisplay) {
         Color dcol = gleis.colors.col_stellwerk_weiss;
         this.paint(gl, g, xscal, yscal, fscal);
         if (gl.fdata.display_stellung != null) {
            this.paint(gl, g, xscal, yscal, fscal, gl.getShortElementName());
            this.paint(gl, g, xscal, yscal, dcol, gl.fdata.display_stellung, false);
         }
      }

      super.paint3Sim(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint3Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      Color dcol = gleis.colors.col_stellwerk_weiss;
      this.paint(gl, g, xscal, yscal, fscal);
      if (gl.enr > 0) {
         this.paint(gl, g, xscal, yscal, dcol, Integer.toString(gl.enr), true);
         this.paint(gl, g, xscal, yscal, fscal, Integer.toString(gl.enr));
      }

      super.paint3Editor(gl, g, xscal, yscal, fscal);
   }
}

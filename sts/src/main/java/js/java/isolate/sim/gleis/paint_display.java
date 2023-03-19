package js.java.isolate.sim.gleis;

import java.awt.Color;
import java.awt.Graphics2D;
import js.java.isolate.sim.gleis.gleisElements.gleisHelper;

class paint_display extends paint2Base {
   paint_display(paint2Base p) {
      super(p);
   }

   paint_display() {
      super(null);
   }

   private int paint(gleis gl, Graphics2D g, int xscal, int yscal, int fscal, Color dcol) {
      int l = gleisHelper.calcDisplaySize(gl.telement);
      g.setColor(gleis.colors.col_stellwerk_schwarz);
      g.fillRect(0, 0, l * xscal, yscal);

      for(int i = 0; i < l; ++i) {
         gl.paintDisplay(g, i * xscal, 0, xscal);
      }

      if (!gl.hideDisplay) {
         gl.paintSmallKnob(g, 0, yscal / 3 + 2, xscal);
      }

      return l;
   }

   @Override
   public void paint3Sim(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      Color dcol = gl.telement == gleis.ELEMENT_AIDDISPLAY ? gleis.colors.col_stellwerk_aiddisplay : gleis.colors.col_stellwerk_zugdisplay;
      int l = this.paint(gl, g, xscal, yscal, fscal, dcol);
      if (gl.fdata.display_stellung != null) {
         String redu = "";
         boolean bsmode = false;

         for(int i = 0; i < gl.fdata.display_stellung.length(); ++i) {
            char c = gl.fdata.display_stellung.charAt(i);
            if (c == '\\') {
               bsmode = true;
            }

            if (Character.isDigit(c) || Character.isSpaceChar(c) || bsmode) {
               redu = redu + c;
               if (c != '\\') {
                  bsmode = false;
               }
            }
         }

         redu = redu.trim();
         int x = 0;
         bsmode = false;

         for(int i = 0; i < l && i < redu.length(); ++i) {
            if (bsmode) {
               bsmode = false;
               switch(redu.charAt(i)) {
                  case ' ':
                     ++x;
                     break;
                  case '<':
                     if (x > 0) {
                        --x;
                     }
               }
            } else if (redu.charAt(i) == '\\') {
               bsmode = true;
            } else {
               gl.printDisplay(g, redu.charAt(i), x * xscal, 0, xscal, dcol, true);
               ++x;
            }
         }
      }

      super.paint3Sim(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint3Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      Color dcol = gl.telement == gleis.ELEMENT_AIDDISPLAY ? gleis.colors.col_stellwerk_aiddisplay : gleis.colors.col_stellwerk_zugdisplay;
      int l = this.paint(gl, g, xscal, yscal, fscal, dcol);
      if (gl.swwert != null) {
         for(int i = 0; i < l && i < gl.swwert.length(); ++i) {
            gl.printDisplay(g, gl.swwert.charAt(i), i * xscal, 0, xscal, dcol, false);
         }
      }

      super.paint3Editor(gl, g, xscal, yscal, fscal);
   }
}

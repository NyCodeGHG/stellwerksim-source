package js.java.isolate.sim.gleis;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class paint_text_base extends paint2Base {
   public paint_text_base(paint2Base p) {
      super(p);
   }

   public paint_text_base() {
      super(null);
   }

   protected void preparePaint1(String t1, gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      switch(gl.richtung) {
         case left:
            this.preparePaint2(t1, gl, g, xscal, yscal, 180);
            break;
         case right:
            this.preparePaint2(t1, gl, g, xscal, yscal, 0);
            break;
         case up:
            this.preparePaint2(t1, gl, g, xscal, yscal, 90);
            break;
         case down:
            this.preparePaint2(t1, gl, g, xscal, yscal, 270);
      }
   }

   protected void preparePaint2(String t1, gleis gl, Graphics2D g, int xscal, int yscal, int rot) {
      int x0 = xscal / 2;
      int y0 = yscal / 2;
      Rectangle textr = this.getDimensions(t1, gl, g, xscal, yscal);
      double offset = this.blockOffset(rot);
      double theta = 0.0;
      textr.y -= yscal / 2;
      switch(rot) {
         case 0:
            textr.x = textr.x - xscal / 2 + (int)(offset * (double)xscal);
            break;
         case 90:
            textr.x = textr.x - xscal / 2 + (int)(offset * (double)xscal);
            theta = -Math.PI / 2;
            break;
         case 180:
         default:
            textr.x = textr.x - textr.width + xscal / 2 - (int)(offset * (double)xscal);
            break;
         case 270:
            textr.x = textr.x - textr.width + xscal / 2 - (int)(offset * (double)xscal);
            theta = -Math.PI / 2;
      }

      Graphics2D g2 = (Graphics2D)g.create();
      g2.translate(x0, y0);
      g2.rotate(theta);
      g2.translate(textr.x, textr.y);
      this.paintText(t1, gl, g2, textr, xscal, yscal, theta != 0.0);
      g2.dispose();
   }

   protected double blockOffset(int rot) {
      return 0.0;
   }

   protected abstract Rectangle getDimensions(String var1, gleis var2, Graphics2D var3, int var4, int var5);

   protected abstract void paintText(String var1, gleis var2, Graphics2D var3, Rectangle var4, int var5, int var6, boolean var7);
}

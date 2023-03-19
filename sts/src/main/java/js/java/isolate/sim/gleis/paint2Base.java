package js.java.isolate.sim.gleis;

import java.awt.Graphics2D;

public class paint2Base extends gleisDecorBase {
   protected paint2Base parent = null;

   protected Graphics2D shiftGraphicsTo(Graphics2D g2, gleis origin, gleis other, int xscal, int yscal) {
      int dx = other.getCol() - origin.getCol();
      int dy = other.getRow() - origin.getRow();
      Graphics2D ret = (Graphics2D)g2.create();
      ret.translate(dx * xscal, dy * yscal);
      return ret;
   }

   public paint2Base(paint2Base p) {
      super();
      this.parent = p;
   }

   public void paint1Sim(gleis gl, Graphics2D g2, int xscal, int yscal, int fscal) {
      if (this.parent != null) {
         this.parent.paint1Sim(gl, g2, xscal, yscal, fscal);
      }
   }

   public void paint1Editor(gleis gl, Graphics2D g2, int xscal, int yscal, int fscal) {
      if (this.parent != null) {
         this.parent.paint1Editor(gl, g2, xscal, yscal, fscal);
      }
   }

   public void paint2Sim(gleis gl, Graphics2D g2, int xscal, int yscal, int fscal) {
      if (this.parent != null) {
         this.parent.paint2Sim(gl, g2, xscal, yscal, fscal);
      }
   }

   public void paint2Editor(gleis gl, Graphics2D g2, int xscal, int yscal, int fscal) {
      if (this.parent != null) {
         this.parent.paint2Editor(gl, g2, xscal, yscal, fscal);
      }
   }

   public void paint3Sim(gleis gl, Graphics2D g2, int xscal, int yscal, int fscal) {
      if (this.parent != null) {
         this.parent.paint3Sim(gl, g2, xscal, yscal, fscal);
      }
   }

   public void paint3Editor(gleis gl, Graphics2D g2, int xscal, int yscal, int fscal) {
      if (this.parent != null) {
         this.parent.paint3Editor(gl, g2, xscal, yscal, fscal);
      }
   }
}

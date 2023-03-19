package js.java.isolate.sim.gleis;

import java.awt.Color;
import java.awt.Graphics2D;

class paint_bstg_flaeche extends paint2Base {
   private paint_gleislabel label = new paint_gleislabel();

   paint_bstg_flaeche(paint2Base p) {
      super(p);
   }

   paint_bstg_flaeche() {
      super(null);
   }

   private void paintBstg(gleis gl, Graphics2D g, int xscal, int yscal) {
      if (gl.getExtendFarbe().equalsIgnoreCase("normal")) {
         g.setColor(gleis.colors.col_stellwerk_bstgfläche);
      } else {
         g.setColor((Color)gleis.colors.col_stellwerk_backmulti.get(gl.gleisExtend.getFarbe()));
      }

      g.fillRect(0, 0, xscal, yscal);
   }

   @Override
   public void paint1Sim(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.paintBstg(gl, g, xscal, yscal);
      super.paint1Sim(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint1Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.paintBstg(gl, g, xscal, yscal);
      g.setColor(Color.BLUE);
      g.drawLine(0, yscal, xscal, 0);
      super.paint1Editor(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint3Sim(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      if (!gl.swwert.isEmpty()) {
         this.label.preparePaint1(gl.swwert, gl, g, xscal, yscal, fscal);
      }

      super.paint3Sim(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint3Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      if (!gl.swwert.isEmpty()) {
         this.label.preparePaint1(gl.swwert, gl, g, xscal, yscal, fscal);
      }

      super.paint3Editor(gl, g, xscal, yscal, fscal);
   }
}

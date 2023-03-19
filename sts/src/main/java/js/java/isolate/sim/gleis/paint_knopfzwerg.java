package js.java.isolate.sim.gleis;

import java.awt.Graphics2D;

class paint_knopfzwerg extends paint2Base {
   paint_knopfzwerg(paint2Base p) {
      super(p);
   }

   paint_knopfzwerg() {
      super(null);
   }

   private void paint(gleis gl, Graphics2D g, int xscal, int yscal, int fscal, boolean sim, boolean hidden) {
      if (!hidden) {
         int x0 = (int)((double)xscal / 2.0);
         int y0 = (int)((double)yscal / 2.0);
         gl.paintSmallKnob(g, x0, y0 + 2, xscal);
      }
   }

   @Override
   public void paint3Sim(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      boolean kopfgleis = false;
      if (gl.kopfsignaldetect == null) {
         gleis s = gl.glbModel.findFirst(gleis.ELEMENT_SIGNAL, gl.enr);
         if (s != null) {
            kopfgleis = s.isKopfSignal() && s.signalRfOnlyStop;
         }

         gl.kopfsignaldetect = kopfgleis;
      } else {
         kopfgleis = gl.kopfsignaldetect;
      }

      this.paint(gl, g, xscal, yscal, fscal, true, kopfgleis);
      super.paint3Sim(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint3Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.paint(gl, g, xscal, yscal, fscal, false, false);
      super.paint3Editor(gl, g, xscal, yscal, fscal);
   }
}

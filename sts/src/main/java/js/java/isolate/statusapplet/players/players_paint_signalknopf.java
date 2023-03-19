package js.java.isolate.statusapplet.players;

import java.awt.Graphics2D;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.paint2Base;

class players_paint_signalknopf extends paint2Base {
   players_paint_signalknopf(paint2Base p) {
      super(p);
   }

   players_paint_signalknopf() {
      super(null);
   }

   @Override
   public void paint3Sim(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      super.paint3Sim(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint3Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      super.paint3Editor(gl, g, xscal, yscal, fscal);
   }
}

package js.java.isolate.sim.zug;

import js.java.isolate.sim.gleis.gleis;

class c_prerunnerNichtsGesehen extends baseChain {
   @Override
   boolean run(zug z) {
      this.visiting(z);
      z.lasthaltabstand = z.haltabstand;
      if (z.haltabstandcnt > 0 && z.pos_gl.getElement().matches(gleis.ELEMENT_SIGNAL) && z.pos_gl.forUs(z.before_gl)) {
         z.haltabstandcnt--;
      }

      return false;
   }
}

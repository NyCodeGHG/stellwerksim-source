package js.java.isolate.sim.zug;

import js.java.isolate.sim.gleis.gleis;

class c_iswiedervmax extends baseChain {
   @Override
   boolean run(zug z) {
      this.visiting(z);
      if (z.pos_gl.getElement() == gleis.ELEMENT_WIEDERVMAX && z.pos_gl.forUs(z.before_gl)) {
         tl_setvmax.remove(z);
         tl_zs1.remove(z);
      }

      return false;
   }
}

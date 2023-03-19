package js.java.isolate.sim.zug;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class c_prerunner extends baseChain2Chain {
   c_prerunner() {
      super(new c_prerunnerRotGesehen(), new c_prerunnerNichtsGesehen());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      z.haltabstand = 0;
      gleis s_pos_gl = z.pos_gl;
      gleis s_next_gl = null;
      gleis s_before_gl = z.before_gl;

      for(int i = 0; i < 8; ++i) {
         s_next_gl = s_pos_gl.next(s_before_gl);
         if (s_pos_gl.getFluentData().getStellung().getZugStellung() == gleisElements.ZugStellungen.stop && s_pos_gl.forUs(s_before_gl)) {
            z.haltabstand = i;
            break;
         }

         if (s_pos_gl.getElement() == gleis.ELEMENT_AUSFAHRT) {
            z.haltabstand = 0;
            break;
         }

         if (s_next_gl == null || s_next_gl.sameGleis(s_pos_gl)) {
            z.haltabstand = i;
            break;
         }

         if (s_next_gl.getElement() == gleis.ELEMENT_AUSFAHRT) {
            z.haltabstand = 0;
            break;
         }

         s_before_gl = s_pos_gl;
         s_pos_gl = s_next_gl;
      }

      return z.haltabstand > 0 && !z.anyDirection && z.isZugfahrt() ? this.callTrue(z) : this.callFalse(z);
   }
}

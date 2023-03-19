package js.java.isolate.sim.gleis;

import java.util.Iterator;

class next_strecke extends nextGleisBase {
   next_strecke(next_strecke p) {
      super(p);
   }

   next_strecke() {
      super(null);
   }

   @Override
   public gleis nextGleis(gleis gl, gleis before) {
      gleis ret = null;
      Iterator<gleis> it = gl.getNachbarn();

      while(it.hasNext()) {
         gleis nextGl = (gleis)it.next();
         if (!nextGl.sameGleis(before)) {
            ret = nextGl;
            break;
         }
      }

      if (ret == null) {
         ret = super.nextGleis(gl, before);
      }

      return ret;
   }
}

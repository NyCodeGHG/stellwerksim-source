package js.java.isolate.sim.gleis;

class next_einfahrt extends nextGleisBase {
   next_einfahrt(nextGleisBase p) {
      super(p);
   }

   next_einfahrt() {
      super(null);
   }

   @Override
   public gleis nextGleis(gleis gl, gleis before) {
      gleis ret;
      try {
         if (before == null) {
            ret = gl.getFirstNachbar();
            if (ret != null && ret.telement == gleis.ELEMENT_AUSFAHRT) {
               ret = gl.getNachbar(1);
            }
         } else {
            ret = super.nextGleis(gl, before);
         }
      } catch (IndexOutOfBoundsException var5) {
         ret = null;
      }

      return ret;
   }
}

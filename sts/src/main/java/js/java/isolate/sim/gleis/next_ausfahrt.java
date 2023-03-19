package js.java.isolate.sim.gleis;

class next_ausfahrt extends nextGleisBase {
   next_ausfahrt(nextGleisBase p) {
      super(p);
   }

   next_ausfahrt() {
      super(null);
   }

   @Override
   public gleis nextGleis(gleis gl, gleis before) {
      return before == null ? gl.getFirstNachbar() : super.nextGleis(gl, before);
   }
}

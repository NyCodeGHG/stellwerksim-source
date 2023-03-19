package js.java.isolate.sim.gleis;

@Deprecated
class next_signal extends nextGleisBase {
   next_signal(nextGleisBase p) {
      super(p);
   }

   @Override
   public gleis nextGleis(gleis gl, gleis before) {
      gleis ret = null;
      return super.nextGleis(gl, before);
   }
}

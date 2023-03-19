package js.java.isolate.sim.gleis;

@Deprecated
class next_createName extends nextGleisBase {
   next_createName(nextGleisBase p) {
      super(p);
   }

   @Override
   public gleis nextGleis(gleis gl, gleis before) {
      if (gleis.createName && gl.element_enr == 0) {
         gl.createName();
      }

      return super.nextGleis(gl, before);
   }
}

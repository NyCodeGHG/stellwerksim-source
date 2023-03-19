package js.java.isolate.sim.gleis;

class nextGleisBase extends gleisDecorBase {
   protected nextGleisBase parent = null;

   nextGleisBase(nextGleisBase p) {
      super();
      this.parent = p;
   }

   public gleis nextGleis(gleis gl, gleis before) {
      return this.parent != null ? this.parent.nextGleis(gl, before) : null;
   }
}

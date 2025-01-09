package js.java.isolate.sim.gleis;

class pingBase extends gleisDecorBase {
   protected pingBase parent = null;

   pingBase(pingBase p) {
      this.parent = p;
   }

   public boolean ping(gleis gl) {
      return this.parent != null ? this.parent.ping(gl) : false;
   }
}

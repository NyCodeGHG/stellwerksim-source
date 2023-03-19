package js.java.isolate.sim.zug;

abstract class baseChain {
   baseChain() {
      super();
   }

   boolean call(zug z) {
      return false;
   }

   boolean callTrue(zug z) {
      return this.call(z);
   }

   boolean callFalse(zug z) {
      return this.call(z);
   }

   protected final void visiting(zug z) {
      if (zug.debugMode != null) {
         z.chainVisits.add(this.getClass().getSimpleName());
      }
   }

   abstract boolean run(zug var1);
}

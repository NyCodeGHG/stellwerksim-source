package js.java.isolate.sim.zug;

class c_prerunnerWarten extends baseChain1Chain {
   c_prerunnerWarten() {
      super(new c_anruf());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      if (z.haltabstandgesehen < z.mytime - 120000L) {
         z.haltabstandcnt = 0;
      }

      return this.call(z);
   }
}

package js.java.isolate.sim.zug;

class c_prerunnerBahnsteigWarten extends baseChain1Chain {
   c_prerunnerBahnsteigWarten() {
      super(new c_abfahrt());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      if (z.haltabstandgesehen < z.mytime - 120000L) {
         z.haltabstandcnt = 0;
         z.haltabstandanrufcnt = 0;
      }

      return this.call(z);
   }
}

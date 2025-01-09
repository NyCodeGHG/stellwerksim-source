package js.java.isolate.sim.zug;

class tl_notBremsung implements tl_base {
   @Override
   public double calc_tempo(zug z, double omx) {
      return 0.0;
   }

   static void remove(zug z) {
      z.removeLimit(tl_notBremsung.class);
   }

   static void add(zug z) {
      remove(z);
      z.addLimit(new tl_notBremsung());
   }

   static tl_notBremsung get(zug z) {
      return (tl_notBremsung)z.getLimit(tl_notBremsung.class);
   }
}

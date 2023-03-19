package js.java.isolate.sim.zug;

class tl_zs1 implements tl_base {
   tl_zs1() {
      super();
   }

   @Override
   public double calc_tempo(zug z, double omx) {
      return Math.min(2.0, omx);
   }

   static void remove(zug z) {
      z.removeLimit(tl_zs1.class);
   }

   static void add(zug z) {
      if (get(z) == null) {
         z.addLimit(new tl_zs1());
      }
   }

   static tl_zs1 get(zug z) {
      return (tl_zs1)z.getLimit(tl_zs1.class);
   }
}

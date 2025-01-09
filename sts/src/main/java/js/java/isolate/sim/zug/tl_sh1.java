package js.java.isolate.sim.zug;

class tl_sh1 implements tl_base {
   @Override
   public double calc_tempo(zug z, double omx) {
      return Math.min(2.0, omx);
   }

   static void remove(zug z) {
      z.removeLimit(tl_sh1.class);
   }

   static void add(zug z) {
      if (get(z) == null) {
         z.addLimit(new tl_sh1());
      }
   }

   static tl_sh1 get(zug z) {
      return (tl_sh1)z.getLimit(tl_sh1.class);
   }
}

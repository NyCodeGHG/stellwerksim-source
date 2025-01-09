package js.java.isolate.sim.zug;

class tl_vorsignal implements tl_base {
   @Override
   public double calc_tempo(zug z, double omx) {
      return omx < 4.0 ? omx : Math.min(4.0, omx - 1.0);
   }

   static void remove(zug z) {
      z.removeLimit(tl_vorsignal.class);
   }

   static void add(zug z) {
      if (get(z) == null) {
         z.addLimit(new tl_vorsignal());
      }
   }

   static tl_vorsignal get(zug z) {
      return (tl_vorsignal)z.getLimit(tl_vorsignal.class);
   }
}

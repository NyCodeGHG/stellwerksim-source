package js.java.isolate.sim.zug;

class tl_sichtfahrt implements tl_base {
   tl_sichtfahrt() {
      super();
   }

   @Override
   public double calc_tempo(zug z, double omx) {
      return Math.min(1.0, omx);
   }

   static void remove(zug z) {
      z.removeLimit(tl_sichtfahrt.class);
   }

   static void add(zug z) {
      if (get(z) == null) {
         z.addLimit(new tl_sichtfahrt());
      }
   }

   static tl_sichtfahrt get(zug z) {
      return (tl_sichtfahrt)z.getLimit(tl_sichtfahrt.class);
   }
}

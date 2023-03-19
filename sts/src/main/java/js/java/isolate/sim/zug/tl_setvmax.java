package js.java.isolate.sim.zug;

class tl_setvmax implements tl_base {
   private final double vmax;

   tl_setvmax(double v) {
      super();
      this.vmax = v;
   }

   @Override
   public double calc_tempo(zug z, double omx) {
      return Math.min(this.vmax, omx);
   }

   static void remove(zug z) {
      z.removeLimit(tl_setvmax.class);
   }

   static void add(zug z, double m) {
      remove(z);
      z.addLimit(new tl_setvmax(m));
   }

   static tl_setvmax get(zug z) {
      return (tl_setvmax)z.getLimit(tl_setvmax.class);
   }
}

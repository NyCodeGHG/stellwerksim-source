package js.java.isolate.sim.zug;

class tl_langsam implements tl_base {
   private final double max_tempo;

   tl_langsam(double m) {
      this.max_tempo = m;
   }

   @Override
   public double calc_tempo(zug z, double omx) {
      return Math.min(this.max_tempo, omx);
   }

   static void remove(zug z) {
      z.removeLimit(tl_langsam.class);
   }

   static void add(zug z, double m) {
      remove(z);
      z.addLimit(new tl_langsam(m));
   }

   static tl_langsam get(zug z) {
      return (tl_langsam)z.getLimit(tl_langsam.class);
   }
}

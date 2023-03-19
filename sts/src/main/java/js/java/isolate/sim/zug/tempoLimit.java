package js.java.isolate.sim.zug;

class tempoLimit implements tl_base {
   tempoLimit() {
      super();
   }

   @Override
   public double calc_tempo(zug z, double omx) {
      double mx = (double)z.variables.soll_tempo(z.soll_tempo, z.zielgleis);
      if (z.haltabstand > 0) {
         mx -= mx / ((double)z.haltabstand * 1.5);
         if (mx < 0.2) {
            mx = 0.2;
         }
      }

      if (z.vorsignal != null) {
         mx = 0.2;
      }

      return mx;
   }

   static void remove(zug z) {
      z.removeLimit(tempoLimit.class);
   }
}

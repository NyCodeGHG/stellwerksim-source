package js.java.isolate.sim.zug;

import js.java.isolate.sim.gleis.gleis;

class tl_nachrot implements tl_base {
   private gleis pos_gl;
   private final gleis signal_gl;
   private int cnt = 2;

   tl_nachrot(gleis gl) {
      super();
      this.signal_gl = this.pos_gl = gl;
   }

   @Override
   public double calc_tempo(zug z, double omx) {
      if (this.signal_gl.sameGleis(z.pos_gl)) {
         return 1.0;
      } else {
         if (!this.pos_gl.sameGleis(z.pos_gl)) {
            --this.cnt;
         }

         if (this.cnt <= 0) {
            remove(z);
         }

         this.pos_gl = z.pos_gl;
         return 0.2 * (double)(3 - this.cnt);
      }
   }

   static void remove(zug z) {
      z.removeLimit(tl_nachrot.class);
   }

   static void add(zug z) {
      remove(z);
      z.addLimit(new tl_nachrot(z.pos_gl));
   }

   static tl_nachrot get(zug z) {
      return (tl_nachrot)z.getLimit(tl_nachrot.class);
   }
}

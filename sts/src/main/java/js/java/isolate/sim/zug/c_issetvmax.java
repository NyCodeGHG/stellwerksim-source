package js.java.isolate.sim.zug;

import js.java.isolate.sim.gleis.gleis;

class c_issetvmax extends baseChain {
   c_issetvmax() {
      super();
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      if (z.pos_gl.getElement() == gleis.ELEMENT_SETVMAX && z.pos_gl.forUs(z.before_gl)) {
         boolean plus = false;

         String s;
         for(s = z.pos_gl.getSWWert(); s.length() > 0 && s.charAt(0) == '+'; plus = true) {
            s = s.substring(1);
         }

         try {
            int m = Integer.parseInt(s);
            double v = 0.0;
            if (m > 0) {
               v = (double)Math.min(m, z.soll_tempo);
            } else if (m < 0) {
               m = z.soll_tempo + m;
               if (m > 0) {
                  v = (double)m;
               } else {
                  v = 1.0;
               }
            } else if (m == 0) {
               v = 0.0;
            }

            if (v != 0.0 && !plus) {
               tl_setvmax.add(z, v);
            } else {
               tl_setvmax.remove(z);
            }
         } catch (Exception var7) {
            tl_setvmax.remove(z);
         }
      }

      return false;
   }
}

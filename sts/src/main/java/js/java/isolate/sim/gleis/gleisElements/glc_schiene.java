package js.java.isolate.sim.gleis.gleisElements;

import java.util.EnumSet;

class glc_schiene extends element_typElement {
   glc_schiene(int element, EnumSet<gleisElements.RICHTUNG> r) {
      this(element, r, false);
   }

   glc_schiene(int element, EnumSet<gleisElements.RICHTUNG> r, boolean isdefault) {
      super(1, element, r, isdefault);
   }

   public static class glc_schieneNoLight extends glc_schiene {
      glc_schieneNoLight(int element) {
         super(element, gleisElements.R_NONE, false);
      }

      @Override
      public boolean paintLight() {
         return false;
      }
   }
}

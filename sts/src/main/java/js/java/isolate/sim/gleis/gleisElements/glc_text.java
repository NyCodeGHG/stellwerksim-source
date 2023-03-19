package js.java.isolate.sim.gleis.gleisElements;

import java.util.EnumSet;

class glc_text extends element_typElement {
   glc_text(int element, EnumSet<gleisElements.RICHTUNG> r) {
      this(element, r, false);
   }

   glc_text(int element, EnumSet<gleisElements.RICHTUNG> r, boolean isdefault) {
      super(4, element, r, isdefault);
   }
}

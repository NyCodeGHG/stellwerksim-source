package js.java.isolate.sim.gleis.gleisElements;

import java.util.EnumSet;

class glc_knopf extends element_typElement {
   glc_knopf(int element) {
      this(element, false);
   }

   glc_knopf(int element, boolean isdefault) {
      super(6, element, gleisElements.R_NONE, isdefault);
   }

   glc_knopf(int element, EnumSet<gleisElements.RICHTUNG> r, boolean isdefault) {
      super(6, element, r, isdefault);
   }
}

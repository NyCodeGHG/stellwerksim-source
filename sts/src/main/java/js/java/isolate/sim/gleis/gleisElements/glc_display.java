package js.java.isolate.sim.gleis.gleisElements;

import java.util.EnumSet;

class glc_display extends element_typElement {
   glc_display(int element) {
      this(element, false);
   }

   glc_display(int element, boolean isdefault) {
      super(5, element, EnumSet.noneOf(gleisElements.RICHTUNG.class), isdefault);
   }
}

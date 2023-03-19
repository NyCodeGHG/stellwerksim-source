package js.java.isolate.sim.gleis.gleisElements;

class glc_trenner extends element_typElement {
   glc_trenner(int element) {
      this(element, false);
   }

   glc_trenner(int element, boolean isdefault) {
      super(2, element, gleisElements.R_NONE, isdefault);
   }
}

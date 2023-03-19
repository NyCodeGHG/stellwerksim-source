package js.java.isolate.sim.sim.gruppentasten;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.sim.stellwerksim_main;

public class gtSh extends gtBase {
   public gtSh(stellwerksim_main m, gleisbildSimControl glb) {
      super(m, glb);
   }

   @Override
   public String getText() {
      return "Sh";
   }

   @Override
   public char getKey() {
      return 'h';
   }

   @Override
   protected void runCommand(String cmd) {
      if (this.signal1() != null && this.signal1().getElement() == gleisElements.ELEMENT_ZWERGSIGNAL) {
         if (this.signal1().getFluentData().getStellung() == gleisElements.ST_SIGNAL_ROT) {
            if (this.signal1().getFluentData().getStartingFS() == null
               && this.glbControl.getModel().getNumberOfStartingFahrwege(this.signal1(), true) > 0
               && this.signal1().getFluentData().setStellung(gleisElements.ST_SIGNAL_ZS1)) {
               this.my_main.incZählwert();
               this.showGleisChange();
            }
         } else if (this.signal1().getFluentData().getStellung() == gleisElements.ST_SIGNAL_ZS1) {
            if (!this.signal1().getFluentData().hasCurrentFS()) {
               this.signal1().getFluentData().setStellung(gleisElements.ST_SIGNAL_ROT);
               this.showGleisChange();
            }
         } else if (this.signal1().getFluentData().getStellung() == gleisElements.ST_SIGNAL_GRÜN) {
            if (!this.signal1().getFluentData().hasCurrentFS()) {
               this.signal1().getFluentData().setStellung(gleisElements.ST_SIGNAL_ROT);
               this.showGleisChange();
            }
         } else if (this.signal1().getFluentData().getStellung() == gleisElements.ST_SIGNAL_RF && !this.signal1().getFluentData().hasCurrentFS()) {
            this.signal1().getFluentData().setStellung(gleisElements.ST_SIGNAL_ROT);
            this.showGleisChange();
         }
      } else if (this.gl_object1() != null && this.gl_object1().getElement() == gleisElements.ELEMENT_ZDECKUNGSSIGNAL) {
         gleis gl = this.gl_object1().nextByRichtung(true);
         boolean hasNoFS = !this.gl_object1().getFluentData().hasCurrentFS() && gl.getFluentData().getStatus() != 1;
         if (this.gl_object1().getFluentData().getStellung() == gleisElements.ST_ZDSIGNAL_FESTGELEGT && hasNoFS) {
            this.gl_object1().getFluentData().setStellung(gleisElements.ST_ZDSIGNAL_GRÜN);
            this.showGleisChange();
         } else if (this.gl_object1().getFluentData().getStellung() == gleisElements.ST_ZDSIGNAL_GRÜN && hasNoFS) {
            this.gl_object1().getFluentData().setStellung(gleisElements.ST_ZDSIGNAL_FESTGELEGT);
            this.showGleisChange();
         } else if (this.gl_object1().getFluentData().getStellung() == gleisElements.ST_ZDSIGNAL_ROT && hasNoFS) {
            this.gl_object1().getFluentData().setStellung(gleisElements.ST_ZDSIGNAL_GRÜN);
            this.showGleisChange();
         } else if (this.gl_object1().getFluentData().getStellung() == gleisElements.ST_ZDSIGNAL_ROT) {
            this.gl_object1().getFluentData().setStellung(gleisElements.ST_ZDSIGNAL_FESTGELEGT);
            this.showGleisChange();
         }
      }
   }
}

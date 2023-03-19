package js.java.isolate.sim.sim.gruppentasten;

import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.sim.stellwerksim_main;

public class gtZS1 extends gtBase {
   public gtZS1(stellwerksim_main m, gleisbildSimControl glb) {
      super(m, glb);
   }

   @Override
   public String getText() {
      return "ErsGT";
   }

   @Override
   public char getKey() {
      return 'E';
   }

   @Override
   protected void runCommand(String cmd) {
      if (this.signal1().getElement() == gleisElements.ELEMENT_SIGNAL
         && (
            this.signal1().getFluentData().getStellung() == gleisElements.ST_SIGNAL_ROT
               || this.signal1().getFluentData().getStellung() == gleisElements.ST_SIGNAL_AUS
         )) {
         if ((this.signal1().getFluentData().getStartingFS() == null || this.signal1().getFluentData().getStellung() == gleisElements.ST_SIGNAL_AUS)
            && !this.signal1().kopfSignal()
            && this.signal1().getFluentData().setStellung(gleisElements.ST_SIGNAL_ZS1)) {
            this.my_main.incZählwert();
            this.showGleisChange();
         }
      } else if (this.signal1().getElement() == gleisElements.ELEMENT_SIGNAL && this.signal1().getFluentData().getStellung() == gleisElements.ST_SIGNAL_ZS1) {
         this.signal1().getFluentData().setStellung(gleisElements.ST_SIGNAL_ROT);
         this.showGleisChange();
      }
   }
}

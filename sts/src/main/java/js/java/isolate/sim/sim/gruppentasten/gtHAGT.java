package js.java.isolate.sim.sim.gruppentasten;

import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.sim.stellwerksim_main;

public class gtHAGT extends gtBase {
   public gtHAGT(stellwerksim_main m, gleisbildSimControl glb) {
      super(m, glb);
   }

   @Override
   public String getText() {
      return "HAGT";
   }

   @Override
   public char getKey() {
      return ' ';
   }

   @Override
   protected void runCommand(String cmd) {
      if (this.signal1().getFluentData().getStellung() == gleisElements.ST_SIGNAL_GRÜN) {
         this.my_main.incZählwert();
         if (this.signal1().getFluentData().isFrei() && !this.signal1().getFluentData().hasCurrentFS()) {
            this.signal1().getFluentData().setStellung(gleisElements.ST_SIGNAL_ROT);
         }
      }
   }
}

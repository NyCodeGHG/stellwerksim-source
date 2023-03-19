package js.java.isolate.sim.sim.gruppentasten;

import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.sim.stellwerksim_main;

public class gtSPloeschen extends gtBase {
   public gtSPloeschen(stellwerksim_main m, gleisbildSimControl glb) {
      super(m, glb);
   }

   @Override
   public String getText() {
      return "SP löschen";
   }

   @Override
   public char getKey() {
      return 'S';
   }

   @Override
   protected void runCommand(String cmd) {
      if (this.signal1().getFluentData().get_FW_speicher() != null) {
         this.signal1().getFluentData().clear_FW_speicher();
         this.showGleisChange();
      }
   }
}

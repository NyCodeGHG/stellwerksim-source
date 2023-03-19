package js.java.isolate.sim.sim.gruppentasten;

import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.sim.stellwerksim_main;

public class gtAutoFStriggered extends gtBase {
   public gtAutoFStriggered(stellwerksim_main m, gleisbildSimControl glb) {
      super(m, glb);
   }

   @Override
   public String getText() {
      return "Auto FS rot";
   }

   @Override
   public char getKey() {
      return 'g';
   }

   @Override
   protected void runCommand(String cmd) {
      this.signal1().enableAutoFW(true);
      this.showGleisChange();
   }
}

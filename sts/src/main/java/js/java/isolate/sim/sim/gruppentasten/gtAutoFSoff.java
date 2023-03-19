package js.java.isolate.sim.sim.gruppentasten;

import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.sim.stellwerksim_main;

public class gtAutoFSoff extends gtBase {
   public gtAutoFSoff(stellwerksim_main m, gleisbildSimControl glb) {
      super(m, glb);
   }

   @Override
   public String getText() {
      return "Auto FS aus";
   }

   @Override
   public char getKey() {
      return ' ';
   }

   @Override
   protected void runCommand(String cmd) {
      this.signal1().disableAutoFW();
      this.showGleisChange();
   }
}

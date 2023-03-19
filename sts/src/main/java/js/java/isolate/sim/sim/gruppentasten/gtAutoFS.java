package js.java.isolate.sim.sim.gruppentasten;

import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.sim.stellwerksim_main;

public class gtAutoFS extends gtBase {
   public gtAutoFS(stellwerksim_main m, gleisbildSimControl glb) {
      super(m, glb);
   }

   @Override
   public String getText() {
      return "Auto FS grün";
   }

   @Override
   public char getKey() {
      return 'u';
   }

   @Override
   protected void runCommand(String cmd) {
      this.signal1().enableAutoFW(false);
      this.showGleisChange();
   }
}

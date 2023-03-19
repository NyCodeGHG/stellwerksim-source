package js.java.isolate.sim.sim.gruppentasten;

import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.sim.stellwerksim_main;

public class gtAutoFStriggeredUepExplizit extends gtBase {
   public gtAutoFStriggeredUepExplizit(stellwerksim_main m, gleisbildSimControl glb) {
      super(m, glb);
   }

   @Override
   public String getText() {
      return "Ausf FS rot";
   }

   @Override
   public char getKey() {
      return ' ';
   }

   @Override
   protected void runCommand(String cmd) {
      this.signal1().setAutoFWuepExplizid();
      this.signal1().enableAutoFW(true);
      this.showGleisChange();
   }
}

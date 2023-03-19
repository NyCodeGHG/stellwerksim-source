package js.java.isolate.sim.sim.gruppentasten;

import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.sim.stellwerksim_main;

public class gtAutoFStriggeredManual extends gtBase {
   public gtAutoFStriggeredManual(stellwerksim_main m, gleisbildSimControl glb) {
      super(m, glb);
   }

   @Override
   public String getText() {
      return "man. gl. FS";
   }

   @Override
   public char getKey() {
      return ' ';
   }

   @Override
   protected void runCommand(String cmd) {
      this.glbControl.setManualAutoFSon();
      this.setLight(TasterButton.LIGHTMODE.BLINK);
   }

   @Override
   void verifyLight() {
      if (!this.glbControl.isManualAutoFSon()) {
         this.setLight(TasterButton.LIGHTMODE.OFF);
      }
   }
}

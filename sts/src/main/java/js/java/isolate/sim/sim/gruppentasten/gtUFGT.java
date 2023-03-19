package js.java.isolate.sim.sim.gruppentasten;

import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.sim.stellwerksim_main;

public class gtUFGT extends gtBase {
   public gtUFGT(stellwerksim_main m, gleisbildSimControl glb) {
      super(m, glb);
   }

   @Override
   public String getText() {
      return "UFGT";
   }

   @Override
   public char getKey() {
      return 'T';
   }

   @Override
   protected void runCommand(String cmd) {
      this.glbControl.setUFGTon();
      this.setLight(TasterButton.LIGHTMODE.BLINK);
   }

   @Override
   void verifyLight() {
      if (!this.glbControl.isUFGTon()) {
         this.setLight(TasterButton.LIGHTMODE.OFF);
      }
   }
}

package js.java.isolate.sim.sim.gruppentasten;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.sim.stellwerksim_main;

public class gtDisplayClear extends gtBase {
   public gtDisplayClear(stellwerksim_main m, gleisbildSimControl glb) {
      super(m, glb);
   }

   @Override
   public String getText() {
      return "Dsp lösch";
   }

   @Override
   public char getKey() {
      return 'c';
   }

   @Override
   protected void runCommand(String cmd) {
      if (gleis.ALLE_DISPLAYS.matches(this.gl_object1().getElement())) {
         this.gl_object1().getFluentData().displayClear(true);
      }
   }
}

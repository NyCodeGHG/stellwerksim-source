package js.java.isolate.sim.sim.gruppentasten;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.sim.stellwerksim_main;

public class gtDisplayCall extends gtBase {
   public gtDisplayCall(stellwerksim_main m, gleisbildSimControl glb) {
      super(m, glb);
   }

   @Override
   public String getText() {
      return "anrufen";
   }

   @Override
   public char getKey() {
      return 'n';
   }

   @Override
   protected void runCommand(String cmd) {
      if (this.gl_object1().getElement() == gleis.ELEMENT_AIDDISPLAY && this.glbControl.getModel().canCallPhone()) {
         String v = this.gl_object1().getFluentData().displayGetValue();
         if (v != null && !v.isEmpty()) {
            this.glbControl.getModel().callPhone(v);
         }
      }
   }
}

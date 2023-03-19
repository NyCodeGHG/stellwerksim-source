package js.java.isolate.sim.sim.gruppentasten;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.sim.stellwerksim_main;

public class gtUegNOk extends gtBase {
   public gtUegNOk(stellwerksim_main m, gleisbildSimControl glb) {
      super(m, glb);
   }

   @Override
   public String getText() {
      return "löschen";
   }

   @Override
   public char getKey() {
      return 'l';
   }

   @Override
   protected void runCommand(String cmd) {
      if (this.gl_object1().getElement() == gleis.ELEMENT_ÜBERGABEAKZEPTOR) {
         gleisElements.Stellungen os = this.gl_object1().getFluentData().getStellung();
         if (os == gleisElements.ST_ÜBERGABEAKZEPTOR_NOK) {
            this.gl_object1().getFluentData().setStellung(gleisElements.ST_ÜBERGABEAKZEPTOR_UNDEF);
         } else if (os != gleisElements.ST_ÜBERGABEAKZEPTOR_OK) {
            this.gl_object1().getFluentData().setStellung(gleisElements.ST_ÜBERGABEAKZEPTOR_NOK);
         }
      }
   }
}

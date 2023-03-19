package js.java.isolate.sim.zug;

import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.zugmsg;
import js.java.isolate.sim.gleis.gleis;

class c_isuep extends baseChain {
   c_isuep() {
      super();
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      if (z.pos_gl.getElement() == gleis.ELEMENT_ÜBERGABEPUNKT && z.pos_gl.forUs(z.before_gl)) {
         z.exitMode = true;
         z.my_main.getFSallocator().zugMessage(z.pos_gl.getENR(), z);
         z.my_main.syncZug1(z);
         z.storeZugÜP(z.next_gl, true);
         z.üpwaitc = 0;
         if (z.hasHook(eventGenerator.T_ZUG_AUSFAHRT)) {
            z.call(eventGenerator.T_ZUG_AUSFAHRT, new zugmsg(z, z.pos_gl, z.before_gl));
         }

         if (zug.debugMode != null) {
            zug.debugMode.writeln("zug (" + z.getName() + ")", "ÜP Übergabe starten");
         }
      }

      return false;
   }
}

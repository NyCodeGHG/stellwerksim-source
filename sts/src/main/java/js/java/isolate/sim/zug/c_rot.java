package js.java.isolate.sim.zug;

import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.zugmsg;

class c_rot extends baseChain1Chain {
   c_rot() {
      super(new c_rotDrehen());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      z.ist_tempo = 0.0;
      z.namefarbe = 1;
      if (z.vorsignal == null && z.hasHook(eventGenerator.T_ZUG_ROT)) {
         z.call(eventGenerator.T_ZUG_ROT, new zugmsg(z, z.next_gl, z.pos_gl));
      }

      if (z.next_gl.isKopfSignal()) {
         z.wartenOK = true;
      }

      z.vorsignal = z.next_gl.getElementName();
      if (z.positionMelden) {
         z.melde("Stehe vor Halt zeigendem Signal " + z.vorsignal + "!");
      }

      tl_nachrot.add(z);
      return this.call(z);
   }
}

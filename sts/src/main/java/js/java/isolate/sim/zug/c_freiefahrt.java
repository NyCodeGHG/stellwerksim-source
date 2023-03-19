package js.java.isolate.sim.zug;

import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.zugmsg;

class c_freiefahrt extends baseChain1Chain {
   private final listChain cmds = new listChain();

   c_freiefahrt() {
      super(new c_notstop());
      this.cmds.add(new c_issignal());
      this.cmds.add(new c_isvorsignal());
      this.cmds.add(new c_weitergefahren());
      this.cmds.add(new c_wassignal());
      this.cmds.add(new c_isbahnsteig());
      this.cmds.add(new c_iswiedervmax());
      this.cmds.add(new c_issetvmax());
      this.cmds.add(new c_isuep());
      this.cmds.add(new c_isausfahrt());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      z.wartenOK = false;
      z.sichtstopp = false;
      z.schongefahren = true;
      boolean cancontinue = true;
      if (z.rottime > 0L) {
         if (z.hasHook(eventGenerator.T_ZUG_WURDEGRUEN) && !z.call(eventGenerator.T_ZUG_WURDEGRUEN, new zugmsg(z, z.next_gl, z.pos_gl))) {
            cancontinue = false;
         } else {
            z.verspaetung = (int)((long)z.rotVerspaetung + (z.mytime - z.rottime) / 60000L);
            z.outputValueChanged = true;
            z.updateHeat(false, z.verspaetung, z.lastVerspaetung);
         }
      }

      if (!cancontinue) {
         return false;
      } else {
         z.rottime = 0L;
         z.calcAndSet_tempo();
         if (z.hasHook(eventGenerator.T_ZUG_FAHRT) && !z.call(eventGenerator.T_ZUG_FAHRT, new zugmsg(z, z.next_gl, z.pos_gl))
            || !z.next_gl.getFluentData().setStatusByZug(2, z, z.pos_gl) && !z.weiterfahren) {
            return this.callFalse(z);
         } else {
            z.fromRightExtra = false;
            this.cmds.runChain(z);
            z.shortenZug();
            return true;
         }
      }
   }
}

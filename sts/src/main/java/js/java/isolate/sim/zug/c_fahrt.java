package js.java.isolate.sim.zug;

import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.zugmsg;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class c_fahrt extends baseChain {
   private final baseChain endeC = new c_ende();
   private final baseChain rotC = new c_rot();
   private final baseChain zugC = new c_kflag();
   private final baseChain freiefahrtC = new c_verifyFlag();

   c_fahrt() {
      super();
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      z.ready2go = false;
      if (z.tempo_pos < 0.0 && z.tempo_pos + z.ist_tempo >= 0.0) {
         z.my_main.playZug();
         if (zug.debugMode != null) {
            zug.debugMode.writeln("zug (" + z.getName() + ")", "Einfahrt");
         }

         z.reportPosition(z.pos_gl.getENR(), 0);
         if (z.hasHook(eventGenerator.T_ZUG_EINFAHRT)) {
            z.call(eventGenerator.T_ZUG_EINFAHRT, new zugmsg(z, z.pos_gl, z.before_gl));
         }
      }

      z.tempo_pos += z.ist_tempo;
      if (z.ist_tempo < 0.1) {
         z.ist_tempo = 0.1;
      }

      if (z.tempo_pos > (double)z.calcMaxSpeed(z.lastmasstab)) {
         z.vorsignal = null;
         z.tempo_pos %= (double)z.calcMaxSpeed(z.lastmasstab);
         if (z.startCnt > 0) {
            --z.startCnt;
            if (z.startCnt <= 0) {
               z.outputValueChanged |= z.von.setBGColor(0);
            } else {
               z.outputValueChanged |= z.von.setBGColor(2);
            }
         }

         z.namefarbe = 5;
         z.next_gl = z.pos_gl.next(z.before_gl);
         if (z.next_gl == null) {
            return this.endeC.run(z);
         } else {
            gleis next_next_gl = z.next_gl.next(z.pos_gl);
            if (next_next_gl != null && next_next_gl.sameGleis(z.next_gl)) {
               return this.rotC.run(z);
            } else if (z.next_gl.forUs(z.pos_gl)
               && z.next_gl.getFluentData().getStellung().getZugStellung() == gleisElements.ZugStellungen.stop
               && !z.weiterfahren) {
               return this.rotC.run(z);
            } else {
               return z.next_gl.getFluentData().getStatus() == 2 && !z.fromRightExtra ? this.zugC.run(z) : this.freiefahrtC.run(z);
            }
         }
      } else {
         z.calcAndSet_tempo();
         return false;
      }
   }
}

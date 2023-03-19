package js.java.isolate.sim.zug;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class c_rotDrehen extends baseChain1Chain {
   c_rotDrehen() {
      super(new c_warten());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      if (z.anyDirection
         && z.zugbelegt.size() > 1
         && z.glbModel.stellungOfNextSignal((gleis)z.zugbelegt.getFirst(), (gleis)z.zugbelegt.get(1), false).getZugStellung()
            != gleisElements.ZugStellungen.stop
         && z.glbModel.isFreeToNextSignal((gleis)z.zugbelegt.getFirst(), (gleis)z.zugbelegt.get(1), false)) {
         z.richtungUmkehren();
         z.rottime = 0L;
         z.haltabstand = 0;
         if (zug.debugMode != null) {
            zug.debugMode.writeln("zug (" + z.getName() + ")", "jede Richtung Abfahrt");
         }

         return false;
      } else {
         return this.call(z);
      }
   }
}

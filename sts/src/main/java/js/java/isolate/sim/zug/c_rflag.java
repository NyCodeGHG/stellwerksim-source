package js.java.isolate.sim.zug;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.displayBar.displayBar;

class c_rflag extends baseChain1Chain {
   c_rflag() {
      super(new c_eflag());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      if (z.isBahnsteig && z.flags.hasFlag('R')) {
         if (z.mytime - z.warankunft > Math.max((long)z.variables.var_richtungswechselwartezeit(z.zielgleis) * 1000L, 30000L)) {
            z.pos_gl = (gleis)z.zugbelegt.getFirst();
            z.before_gl = (gleis)z.zugbelegt.get(1);
            LinkedList newzugbelegt = new LinkedList();

            try {
               while(z.zugbelegt.size() > 0) {
                  newzugbelegt.addFirst(z.zugbelegt.removeFirst());
               }
            } catch (NoSuchElementException var4) {
            }

            z.zugbelegt = newzugbelegt;
            z.flags.removeFlag('R');
            if (zug.debugMode != null) {
               zug.debugMode.writeln("zug (" + z.getName() + ")", "R-Flag");
            }

            z.haltabstand = 0;
            z.triggerDisplayBar(displayBar.ZUGTRIGGER.RICHTUNG);
         }

         return false;
      } else {
         return this.callFalse(z);
      }
   }
}

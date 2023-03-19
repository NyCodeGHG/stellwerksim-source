package js.java.isolate.sim.zug;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

public class c_signalTempo extends baseChain {
   public c_signalTempo() {
      super();
   }

   @Override
   boolean run(zug z) {
      boolean unterwegsZwerg = false;
      if (gleis.ELEMENT_ZWERGSIGNAL.matches(z.next_gl.getElement()) && z.next_gl.getFluentData().getStartingFS() == null) {
         unterwegsZwerg = true;
      }

      if (!unterwegsZwerg) {
         tl_vorsignal.remove(z);
         tl_langsam.remove(z);
      }

      if (!z.weiterfahren) {
         tl_sichtfahrt.remove(z);
      }

      tl_zs1.remove(z);
      tl_sh1.remove(z);
      if (zug.debugMode != null) {
         zug.debugMode.writeln("zug (" + z.getName() + ")", "Signal " + z.next_gl.getENR());
      }

      if (z.next_gl.getFluentData().getStellung().getZugStellung() == gleisElements.ZugStellungen.langsamfahrt) {
         tl_zs1.add(z);
         if (zug.debugMode != null) {
            zug.debugMode.writeln("zug (" + z.getName() + ")", "Signal " + z.next_gl.getENR() + " (ZS1)");
         }
      } else if (z.next_gl.getFluentData().getStellung().getZugStellung() == gleisElements.ZugStellungen.rangierfahrt) {
         tl_sh1.add(z);
         if (zug.debugMode != null) {
            zug.debugMode.writeln("zug (" + z.getName() + ")", "Signal " + z.next_gl.getENR() + " (Rf/Zwerg)");
         }
      }

      return false;
   }
}

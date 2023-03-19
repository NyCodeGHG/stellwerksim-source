package js.java.isolate.sim.zug;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

class c_issignal extends baseChain1Chain {
   c_issignal() {
      super(new c_signalTempo());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      if (gleis.ALLE_STRECKENSIGNALE.matches(z.next_gl.getElement()) && z.next_gl.forUs(z.pos_gl)) {
         super.callTrue(z);

         try {
            z.my_main.getMsgControl().zugEnterFs(z, z.next_gl);
         } catch (NullPointerException var4) {
         }

         if (z.positionMelden) {
            z.melde("Passiere gerade Signal " + z.next_gl.getElementName() + ".");
         }

         if (gleis.ALLE_STARTSIGNALE.matches(z.next_gl.getElement())) {
            fahrstrasse fs = z.next_gl.getFluentData().getStartingFS();
            fahrstrasse cf = z.next_gl.getFluentData().getCurrentFS();
            if (fs != null || cf == null) {
               z.reportPosition(z.next_gl.getENR(), fs != null ? fs.getStop().getENR() : 0);
            }

            if (fs != null && fs.hasÜP()) {
               z.my_main.getFSallocator().zugBlockMessage(fs.getÜpENR(), z);
            }
         }
      }

      return false;
   }
}

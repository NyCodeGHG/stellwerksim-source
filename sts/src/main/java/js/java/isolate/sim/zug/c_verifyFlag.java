package js.java.isolate.sim.zug;

import java.util.Iterator;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.sim.TEXTTYPE;

class c_verifyFlag extends baseChain2Chain {
   c_verifyFlag() {
      super(new c_signalEntscheider(), new c_notstop());
   }

   private boolean hasMatchingFlag(zug z) {
      return z.flags.hasFlag('E') || z.flags.hasFlag('K') || z.flags.hasFlag('F') || z.flags.hasFlag('W');
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      if (z.next_gl.getElement() == gleis.ELEMENT_AUSFAHRT || z.next_gl.getElement() == gleis.ELEMENT_ÜBERGABEPUNKT && z.next_gl.forUs(z.pos_gl)) {
         boolean match = z.aus_enr == 0 && z.aus_stw == null || this.hasMatchingFlag(z);
         zug zz;
         if (!match && z.unterzuege != null) {
            for(Iterator<zug> it = z.unterzuege.values().iterator(); it.hasNext() && !match; match |= this.hasMatchingFlag(zz)) {
               zz = (zug)it.next();
            }
         }

         if (!match) {
            match |= !z.my_main.findZugPointingMe(z.zid).isEmpty();
         }

         if (match) {
            if (!z.sichtstopp) {
               String text = "Anruf von Triebfahrzeugführer "
                  + z.getSpezialName()
                  + ": \"Achtung, Zug gestoppt! Es wurden wichtige Fahrplanabschnitte nicht erledigt!\"<p>Geben Sie Zugbefehl 'weiterfahren' um dies zu ignorieren.";
               z.my_main.showText(text, TEXTTYPE.ANRUF, z);
               z.my_main.playAnruf();
               z.weiterfahren = false;
            }

            z.sichtstopp = true;
            if (!z.weiterfahren) {
               return this.callFalse(z);
            }
         }
      }

      return this.callTrue(z);
   }
}

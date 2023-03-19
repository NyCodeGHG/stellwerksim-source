package js.java.isolate.sim.zug;

import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.zugmsg;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.sim.TEXTTYPE;

class c_lflag extends baseChain1Chain {
   c_lflag() {
      super(new c_wflag());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      if (z.isBahnsteig && z.flags.hasFlag('L')) {
         if (z.mytime - z.warankunft > 30000L && (!z.hasHook(eventGenerator.T_ZUG_LOKFLÜGELN) || z.call(eventGenerator.T_ZUG_LOKFLÜGELN, new zugmsg(z)))) {
            z.flags.replaceFlag('L', 'l');
            z.waitLWdone = false;
            z.waitLW = true;
            z.laengeBackup = z.laenge;
            z.shortenZug();
            zug nz = new zug(z, true, false, z.gestopptgleis);
            String text = "" + nz.getSpezialName() + " bereit zum Umsetzen.";
            nz.my_main.showText(text, TEXTTYPE.ANRUF, z);
            nz.my_main.playAnruf();
            z.shortenZug();
            z.pos_gl = (gleis)z.zugbelegt.getLast();
            z.before_gl = (gleis)z.zugbelegt.get(z.zugbelegt.size() - 2);
            if (zug.debugMode != null) {
               zug.debugMode.writeln("zug (" + z.getName() + ")", "L-Flag");
            }
         }

         return false;
      } else if (z.isBahnsteig && z.flags.hasFlag('l')) {
         if (z.waitLWdone) {
            z.flags.removeFlag('l');
            z.laenge = z.laengeBackup;
            z.shortenZug();
            z.pos_gl = (gleis)z.zugbelegt.getLast();
            z.before_gl = (gleis)z.zugbelegt.get(z.zugbelegt.size() - 2);
            z.waitLWdone = false;
         }

         return false;
      } else {
         return this.callFalse(z);
      }
   }
}

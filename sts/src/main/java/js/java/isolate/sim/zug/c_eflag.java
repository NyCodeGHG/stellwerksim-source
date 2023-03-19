package js.java.isolate.sim.zug;

import java.util.LinkedList;
import js.java.isolate.sim.gleis.displayBar.displayBar;

class c_eflag extends baseChain1Chain {
   c_eflag() {
      super(new c_fflag());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      if (z.isBahnsteig && z.flags.hasFlag('E')) {
         if (z.mytime - z.warankunft > 60000L) {
            zug neuzug = z.my_main.findZug(z.flags.dataOfFlag('E'));
            if (neuzug != null) {
               z.flags.removeFlag('E');
               zug tmp = new zug();
               tmp.name = z.name;
               tmp.zid = z.zid;
               tmp.verspaetung = z.verspaetung;
               tmp.variables = z.variables;
               z.verspaetung = Math.max(0, z.verspaetung - (int)((neuzug.ab - z.an) / 60000L));
               z.name = neuzug.name;
               z.ab = neuzug.ab;
               z.aus_enr = neuzug.aus_enr;
               z.aus_stw = neuzug.aus_stw;
               z.zid = neuzug.zid;
               z.azid = neuzug.azid;
               z.cur_azid = z.azid;
               z.soll_tempo = neuzug.soll_tempo;
               z.hinweistext = neuzug.hinweistext;
               z.unterzuege = z.transferUnterzuege(neuzug.unterzuege);
               z.flags = neuzug.flags;
               z.variables = neuzug.variables;
               z.ein_stw = null;
               z.wflagList = neuzug.wflagList;
               z.forceSyncWith();
               z.updateData();
               neuzug.zid = tmp.zid;
               neuzug.name = tmp.name;
               neuzug.verspaetung = tmp.verspaetung;
               neuzug.b_richtigeausfahrt = true;
               neuzug.fertig = true;
               neuzug.wflagList = new LinkedList();
               neuzug.variables = tmp.variables;
               neuzug.unterzuege = null;
               if (zug.debugMode != null) {
                  zug.debugMode.writeln("zug (" + z.getName() + ")", "E-Flag zu " + neuzug.getName());
               }

               z.my_main.exchangeZug(z, neuzug, z.zid, neuzug.zid);
               z.my_main.hideZug(neuzug);
               z.my_main.setZugOnBahnsteig(z.gestopptgleis, z, z.pos_gl);
               zug.killEzug.put(neuzug.zid, neuzug);
               z.outputValueChanged = true;
               z.triggerDisplayBar(displayBar.ZUGTRIGGER.NAME);
            } else if (zug.debugMode != null) {
               zug.debugMode.writeln("zug (" + z.getName() + ")", "E-Flag versaut!");
            }
         }

         return false;
      } else {
         return this.callFalse(z);
      }
   }
}

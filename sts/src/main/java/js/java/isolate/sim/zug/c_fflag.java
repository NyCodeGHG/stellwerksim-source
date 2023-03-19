package js.java.isolate.sim.zug;

import java.util.NoSuchElementException;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.zugmsg;
import js.java.isolate.sim.gleis.gleis;

class c_fflag extends baseChain1Chain {
   c_fflag() {
      super(new c_prerunnerBahnsteigWarten());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      if (z.isBahnsteig && z.flags.hasFlag('F')) {
         if (z.mytime - z.warankunft > 30000L) {
            zug neuzug = z.my_main.findZug(z.flags.dataOfFlag('F'));
            if (neuzug != null) {
               if (!z.hasHook(eventGenerator.T_ZUG_FLÜGELN) || z.call(eventGenerator.T_ZUG_FLÜGELN, new zugmsg(z, neuzug))) {
                  z.flags.replaceFlag('F', 'f');
                  neuzug.verspaetung = z.verspaetung;
                  neuzug.warankunft = z.warankunft;
                  neuzug.ambahnsteig = z.ambahnsteig;
                  neuzug.schongefahren = true;
                  neuzug.gestopptgleis = z.gestopptgleis;
                  neuzug.mytrain = true;
                  neuzug.gleiswarok = true;
                  neuzug.namect.setBGColor(3);
                  neuzug.updateData();
                  z.updateData();

                  try {
                     z.laenge -= neuzug.laenge;
                     if (z.laenge < 2) {
                        z.laenge = 2;
                        System.out.println("Warnung: Flügel länger als Urzug!");
                     }

                     if (neuzug.laenge > z.laenge) {
                        while(neuzug.zugbelegt.size() < neuzug.calcLaenge(z.lastmasstab) && z.zugbelegt.size() > 2 || neuzug.zugbelegt.size() < 2) {
                           neuzug.zugbelegt.addLast(z.zugbelegt.removeFirst());
                        }
                     } else {
                        while(z.zugbelegt.size() > z.calcLaenge(z.lastmasstab) || neuzug.zugbelegt.size() < 2) {
                           neuzug.zugbelegt.addLast(z.zugbelegt.removeFirst());
                        }
                     }
                  } catch (IndexOutOfBoundsException var4) {
                  } catch (NoSuchElementException var5) {
                     System.out.println("Warnung: Flügel länger als Urzug!");
                  }

                  neuzug.pos_gl = (gleis)neuzug.zugbelegt.getLast();
                  neuzug.before_gl = (gleis)neuzug.zugbelegt.get(neuzug.zugbelegt.size() - 2);
                  neuzug.visible = true;
                  neuzug.refreshZugAmGleis();
                  z.my_main.updateZug(neuzug);
                  neuzug.forceSyncWith();
                  neuzug.tjmAdd();
                  z.outputValueChanged = true;
                  if (zug.debugMode != null) {
                     zug.debugMode.writeln("zug (" + z.getName() + ")", "F-Flag zu " + neuzug.getName());
                  }
               }
            } else {
               if (zug.debugMode != null) {
                  zug.debugMode.writeln("zug (" + z.getName() + ")", "F-Flag versaut!");
               }

               z.flags.removeFlag('F');
            }
         }

         return false;
      } else {
         return this.callFalse(z);
      }
   }
}

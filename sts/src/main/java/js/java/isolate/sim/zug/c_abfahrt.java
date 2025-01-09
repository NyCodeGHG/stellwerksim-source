package js.java.isolate.sim.zug;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.zugmsg;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class c_abfahrt extends baseChain1Chain {
   c_abfahrt() {
      super(new c_warten());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      z.bahnsteigcnt++;
      if (z.bahnsteigcnt > 1000) {
         z.bahnsteigcnt = 0;
         z.my_main.reportZugPosition(z.zid, z.gestopptgleis, z.pos_gl);
      }

      if (z.mytime > z.ab || z.abfahrtbefehl && z.flags.hasFlag('A')) {
         boolean needsK = false;
         String k = z.zielgleis + "/" + z.zid + "/" + z.ab;
         if (zug.flagZug.containsKey(k)) {
            int kzug = (Integer)zug.flagZug.get(k);
            if (kzug > 0) {
               zug neuzug = z.my_main.findZug(kzug);
               if (neuzug != null) {
                  needsK = neuzug.flags.hasFlag('K');
                  if (neuzug.unterzuege != null) {
                     Iterator<zug> it = neuzug.unterzuege.values().iterator();

                     while (!needsK && it.hasNext()) {
                        zug zz = (zug)it.next();
                        needsK = zz.flags.hasFlag('K');
                        if (needsK && z.positionMelden) {
                           z.melde("Ich steh am Bahnsteig " + z.gestopptgleis + " und warte auf Zug " + zz.getSpezialName());
                        }
                     }
                  }
               }
            }
         }

         if (!needsK && (z.ready2go || z.flags.hasFlag('A') || z.variables.calcAbfahrt(z.mytime, z.warankunft, z.an, z.ab, z.zielgleis, z.gleiswarok))) {
            z.ready2go = true;
            z.outputValueChanged = z.abfahrt.setBGColor(4) | z.outputValueChanged;
            int overspaetung = z.verspaetung;
            z.verspaetung = (int)((z.mytime - z.ab) / 60000L);
            z.lastAbfahrt = z.mytime;
            z.updateHeat(false, z.verspaetung, z.lastVerspaetung);
            z.outputValueChanged = z.outputValueChanged | z.verspaetung != overspaetung;
            if (zug.debugMode != null) {
               zug.debugMode.writeln("zug (" + z.getName() + ")", "will abfahren");
            }

            z.updateData();
            if (z.hasHook(eventGenerator.T_ZUG_ABFAHRT) && !z.call(eventGenerator.T_ZUG_ABFAHRT, new zugmsg(z, z.pos_gl, z.before_gl))) {
               z.outputValueChanged = z.abfahrt.setBGColor(7) | z.outputValueChanged;
               if (z.positionMelden) {
                  z.melde("Stehe am Bahnsteig " + z.gestopptgleis + ".");
               }
            } else if (z.isBahnsteig
               && (
                  !z.glbModel.isFreeToNextSignal(z.pos_gl, z.before_gl, true)
                     || z.glbModel.stellungOfNextSignal(z.pos_gl, z.before_gl, true).getZugStellung() == gleisElements.ZugStellungen.stop && !z.weiterfahren
               )) {
               if (z.zugbelegt.size() > 1
                  && !z.schongefahren
                  && (
                     z.glbModel.stellungOfNextSignal((gleis)z.zugbelegt.getFirst(), (gleis)z.zugbelegt.get(1), true).getZugStellung()
                           != gleisElements.ZugStellungen.stop
                        || z.weiterfahren
                  )) {
                  z.pos_gl = (gleis)z.zugbelegt.getFirst();
                  z.before_gl = (gleis)z.zugbelegt.get(1);
                  z.rottime = 0L;
                  z.haltabstand = 0;
                  z.updateHeat(true, z.verspaetung, z.lastVerspaetung);
                  z.lastVerspaetung = z.verspaetung;
                  z.my_main.reportFahrplanAb(z.zid, z.cur_azid, z.verspaetung);
                  LinkedList<gleis> newzugbelegt = new LinkedList();

                  try {
                     while (z.zugbelegt.size() > 0) {
                        newzugbelegt.addFirst(z.zugbelegt.removeFirst());
                     }
                  } catch (NoSuchElementException var8) {
                  }

                  z.zugbelegt = newzugbelegt;
                  if (zug.debugMode != null) {
                     zug.debugMode.writeln("zug (" + z.getName() + ")", "parken Abfahrt");
                  }
               } else {
                  if (z.positionMelden) {
                     z.melde("Stehe abfahrbereit an Bahnsteig " + z.gestopptgleis + ", Fahrtsignal wäre gut.");
                  }

                  this.call(z);
               }
            } else {
               if (z.haltabstand > 0) {
                  tl_nachrot.add(z);
               }

               if (z.positionMelden) {
                  z.melde("Fahre gerade von Bahnsteig " + z.gestopptgleis + " ab.");
               }

               z.updateHeat(true, z.verspaetung, z.lastVerspaetung);
               z.lastVerspaetung = z.verspaetung;
               z.rottime = 0L;
               z.haltabstand = 0;
               z.ambahnsteig = false;
               z.ist_tempo = 0.2;
               z.my_main.setZugOnBahnsteig(z.gestopptgleis, null, null);
               z.my_main.reportFahrplanAb(z.zid, z.cur_azid, z.verspaetung);
               z.outputValueChanged = z.abfahrt.setBGColor(0) | z.outputValueChanged;
               if (z.isBahnsteig && z.flags.hasFlag('f')) {
                  zug neuzug = z.my_main.findZug(z.flags.dataOfFlag('f'));
                  if (neuzug != null && neuzug.ambahnsteig) {
                     z.my_main.setZugOnBahnsteig(neuzug.gestopptgleis, neuzug, z.pos_gl);
                  }

                  z.flags.removeFlag('f');
               }

               z.nextUnterzug();
               if (zug.debugMode != null) {
                  zug.debugMode.writeln("zug (" + z.getName() + ")", "Abfahrt");
               }

               z.gestopptgleis = null;
            }
         } else {
            if (!needsK) {
               z.outputValueChanged = z.abfahrt.setBGColor(3) | z.outputValueChanged;
            }

            if (z.positionMelden) {
               z.melde("Stehe an Bahnsteig " + z.gestopptgleis + ".");
            }
         }

         return false;
      } else {
         if (z.positionMelden) {
            z.melde("Stehe an Bahnsteig " + z.gestopptgleis + ".");
         }

         if (z.mytime > z.ab - 30000L) {
            z.outputValueChanged = z.abfahrt.setBGColor(13) | z.outputValueChanged;
         } else if (z.mytime > z.ab - 60000L) {
            z.outputValueChanged = z.abfahrt.setBGColor(12) | z.outputValueChanged;
         } else if (z.mytime > z.ab - 60000L * 2L) {
            z.outputValueChanged = z.abfahrt.setBGColor(11) | z.outputValueChanged;
         }

         return false;
      }
   }
}

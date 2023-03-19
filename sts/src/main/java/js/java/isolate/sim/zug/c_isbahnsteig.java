package js.java.isolate.sim.zug;

import java.util.Iterator;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.zugmsg;
import js.java.isolate.sim.gleis.gleis;

class c_isbahnsteig extends baseChain {
   c_isbahnsteig() {
      super();
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      if ((z.pos_gl.getElement() == gleis.ELEMENT_BAHNSTEIG || z.pos_gl.getElement() == gleis.ELEMENT_HALTEPUNKT)
         && z.pos_gl.forUs(z.before_gl)
         && !z.pos_gl.equals(z.lastBahnsteig)) {
         if (z.positionMelden) {
            z.melde("Erreiche gerade Bahnsteig " + z.pos_gl.getSWWert() + ".");
         }

         if (!z.ignoriereBahnsteig) {
            if (z.bstgRedirectSpecial) {
               Iterator<zug> it = z.getAllUnseenFahrplanzeilen();

               while(it.hasNext()) {
                  zug zz = (zug)it.next();
                  if (zz.zielgleis.equalsIgnoreCase(z.pos_gl.getSWWert()) || z.my_main.getBahnsteige().isNeighborBahnsteigOf(zz.zielgleis, z.pos_gl)) {
                     for(;
                        !zz.zielgleis.equalsIgnoreCase(z.zielgleis) && !z.unterzuege.isEmpty() && !z.flags.hasFlag('F') && !z.flags.hasFlag('W');
                        z.nextUnterzug()
                     ) {
                        if (zug.debugMode != null) {
                           zug.debugMode.writeln("zug (" + z.getName() + ")", "vorspulen: " + z.zielgleis + " -> " + zz.zielgleis);
                        }
                     }

                     z.bstgRedirectSpecial = false;
                     break;
                  }

                  if (zz.flags.hasFlag('F') || zz.flags.hasFlag('W')) {
                     break;
                  }
               }
            }

            String _gleis = z.zielgleis;
            z.lastBahnsteig = z.pos_gl;
            z.isBahnsteig = z.pos_gl.getElement() == gleis.ELEMENT_BAHNSTEIG;
            if (zug.debugMode != null) {
               zug.debugMode.writeln("zug (" + z.getName() + ")", "(A) Bahnsteig (" + z.isBahnsteig + ") Ziel: " + _gleis + " Ist: " + z.pos_gl.getSWWert());
            }

            if (z.befehl_zielgleis != null) {
               _gleis = z.befehl_zielgleis;
               ++z.c_geaendertbahnsteig;
            }

            if (zug.debugMode != null) {
               zug.debugMode.writeln("zug (" + z.getName() + ")", "(B) Bahnsteig (" + z.isBahnsteig + ") Ziel: " + _gleis + " Ist: " + z.pos_gl.getSWWert());
            }

            if (z.flags.hasFlag('D') && (_gleis.equalsIgnoreCase(z.pos_gl.getSWWert()) || z.my_main.getBahnsteige().isNeighborBahnsteigOf(_gleis, z.pos_gl))) {
               z.flags.removeFlag('D');
               z.warankunft = z.mytime;
               z.gestopptgleis = _gleis;
               int overspaetung = z.verspaetung;
               z.verspaetung = (int)((z.warankunft - z.ab) / 60000L);
               z.lastAbfahrt = z.warankunft;
               z.updateHeat(true, z.verspaetung, z.lastVerspaetung);
               z.lastVerspaetung = z.verspaetung;
               z.outputValueChanged |= z.verspaetung != overspaetung;
               z.gleiswarok = true;
               ++z.c_richtigbahnsteig;
               z.my_main.reportFahrplanAn(z.zid, z.cur_azid, z.gestopptgleis, z.gleiswarok, z.verspaetung, z.lastVerspaetung);
               z.my_main.reportFahrplanAb(z.zid, z.cur_azid, z.verspaetung);
               if (z.hasHook(eventGenerator.T_ZUG_ANKUNFT)) {
                  z.call(eventGenerator.T_ZUG_ANKUNFT, new zugmsg(z, z.pos_gl, z.before_gl));
               }

               z.nextUnterzug();
               if (zug.debugMode != null) {
                  zug.debugMode.writeln("zug (" + z.getName() + ")", "D-Flag");
               }
            } else if (_gleis.equalsIgnoreCase(z.pos_gl.getSWWert())) {
               z.warankunft = z.mytime;
               z.gestopptgleis = z.pos_gl.getSWWert();
               z.gleiswarok = z.isGleisänderungTimeout();
               z.ambahnsteig = true;
               z.ist_tempo = 0.0;
               if (z.verspaetung < 0) {
                  int overspaetung = z.verspaetung;
                  z.verspaetung = 0;
                  z.lastVerspaetung = z.verspaetung;
                  z.outputValueChanged |= z.verspaetung != overspaetung;
               } else {
                  int overspaetung = (int)((z.mytime - z.an) / 60000L);
                  if (overspaetung > 0) {
                     z.updateHeat(false, overspaetung, z.lastVerspaetung);
                  }
               }

               z.my_main.setZugOnBahnsteig(z.gestopptgleis, z, z.pos_gl);
               z.my_main.reportFahrplanAn(z.zid, z.cur_azid, z.gestopptgleis, true, z.verspaetung, z.lastVerspaetung);
               if (z.isBahnsteig) {
                  z.glbModel.befreieBisSignal(z.pos_gl, z.before_gl);
               }

               z.outputValueChanged = true;
               ++z.c_richtigbahnsteig;
               tl_vorsignal.remove(z);
               tl_langsam.remove(z);
               tl_sichtfahrt.remove(z);
               tl_zs1.remove(z);
               tl_sh1.remove(z);
               if (z.hasHook(eventGenerator.T_ZUG_ANKUNFT)) {
                  z.call(eventGenerator.T_ZUG_ANKUNFT, new zugmsg(z, z.pos_gl, z.before_gl));
               }

               if (zug.debugMode != null) {
                  zug.debugMode.writeln("zug (" + z.getName() + ")", "OK Halt");
               }
            } else if (z.my_main.getBahnsteige().isNeighborBahnsteigOf(_gleis, z.pos_gl)) {
               z.warankunft = z.mytime;
               z.gestopptgleis = z.pos_gl.getSWWert();
               z.gleiswarok = false;
               z.ambahnsteig = true;
               z.ist_tempo = 0.0;
               if (z.verspaetung < 0) {
                  int overspaetung = z.verspaetung;
                  z.verspaetung = 0;
                  z.lastVerspaetung = z.verspaetung;
                  z.outputValueChanged |= z.verspaetung != overspaetung;
               } else {
                  int overspaetung = (int)((z.mytime - z.an) / 60000L);
                  if (overspaetung > 0) {
                     z.updateHeat(false, overspaetung, z.lastVerspaetung);
                  }
               }

               z.my_main.setZugOnBahnsteig(z.gestopptgleis, z, z.pos_gl);
               z.my_main.reportFahrplanAn(z.zid, z.cur_azid, z.gestopptgleis, false, z.verspaetung, z.lastVerspaetung);
               if (z.isBahnsteig) {
                  z.glbModel.befreieBisSignal(z.pos_gl, z.before_gl);
               }

               ++z.c_falschbahnsteig;
               tl_vorsignal.remove(z);
               tl_langsam.remove(z);
               tl_sichtfahrt.remove(z);
               tl_zs1.remove(z);
               tl_sh1.remove(z);
               if (z.hasHook(eventGenerator.T_ZUG_ANKUNFT)) {
                  z.call(eventGenerator.T_ZUG_ANKUNFT, new zugmsg(z, z.pos_gl, z.before_gl));
               }

               if (zug.debugMode != null) {
                  zug.debugMode.writeln("zug (" + z.getName() + ")", "Nachbar Halt");
               }
            }
         }
      }

      return false;
   }
}

package js.java.isolate.sim.zug;

import java.util.NoSuchElementException;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.zugmsg;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.displayBar.displayBar;
import js.java.isolate.sim.sim.TEXTTYPE;

class c_kflag extends baseChain1Chain {
   c_kflag() {
      super(new c_notstop());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      z.sichtstopp = true;
      if (z.flags.hasFlag('K')) {
         zug dazug = z.next_gl.getFluentData().getZugAmGleis();
         zug neuzug = z.my_main.findZug(z.flags.dataOfFlag('K'));
         if (dazug != null && neuzug != null && dazug.zid == neuzug.zid) {
            if ((z.callme == null || !z.callme.waitLWdone && z.callme.waitLW)
               && (!z.hasHook(eventGenerator.T_ZUG_KUPPELN) || z.call(eventGenerator.T_ZUG_KUPPELN, new zugmsg(z, neuzug)))) {
               String text = "Züge " + neuzug.getSpezialName() + " und " + z.getSpezialName() + " gekuppelt!";
               z.my_main.showText(text, TEXTTYPE.ANRUF, z);
               z.my_main.playAnruf();
               z.flags.removeFlag('K');
               z.pos_gl.getFluentData().setStatusByZug(2, z, z.before_gl);
               z.before_gl = z.pos_gl;
               z.pos_gl = z.next_gl;
               z.lastmasstab = z.pos_gl.getMasstab();
               z.triggerDisplayBar(displayBar.ZUGTRIGGER.KUPPELN);
               z.shortenZug();
               neuzug.laenge = neuzug.laenge + z.laenge;
               boolean drehen = neuzug.pos_gl.sameGleis(z.next_gl);
               if (drehen) {
                  neuzug.richtungUmkehren();
                  if (zug.debugMode != null) {
                     zug.debugMode.writeln("zug (" + z.getName() + ")", "K-Flag drehen");
                  }
               }

               try {
                  while (z.zugbelegt.size() > 0) {
                     neuzug.zugbelegt.addFirst(z.zugbelegt.removeLast());
                  }
               } catch (NoSuchElementException var7) {
               }

               z.visible = false;
               z.namefarbe = 0;
               z.fertig = true;
               z.my_main.hideZug(z);
               if (drehen) {
                  neuzug.richtungUmkehren();
                  tl_sichtfahrt.remove(neuzug);
               }

               neuzug.refreshZugAmGleis();
               z.outputValueChanged = true;
               if (z.callme != null) {
                  z.callme.kuppeln(z);
                  z.callme = null;
               }

               if (zug.debugMode != null) {
                  zug.debugMode.writeln("zug (" + z.getName() + ")", "K-Flag zu " + neuzug.getName());
               }
            }

            return true;
         } else {
            return this.callFalse(z);
         }
      } else {
         zug dazug = z.next_gl.getFluentData().getZugAmGleis();
         if (dazug != null && dazug.flags.hasFlag('K') && dazug.callme == null) {
            zug neuzug = z.my_main.findZug(dazug.flags.dataOfFlag('K'));
            if (neuzug != null && z.zid == neuzug.zid) {
               if (!dazug.hasHook(eventGenerator.T_ZUG_KUPPELN) || dazug.call(eventGenerator.T_ZUG_KUPPELN, new zugmsg(dazug, neuzug))) {
                  String textx = "Züge " + dazug.getSpezialName() + " und " + z.getSpezialName() + " gekuppelt!";
                  z.my_main.showText(textx, TEXTTYPE.ANRUF, z);
                  z.my_main.playAnruf();
                  dazug.flags.removeFlag('K');
                  z.pos_gl.getFluentData().setStatusByZug(2, z, z.before_gl);
                  dazug.triggerDisplayBar(displayBar.ZUGTRIGGER.KUPPELN);
                  z.before_gl = z.pos_gl;
                  z.pos_gl = z.next_gl;
                  z.lastmasstab = z.pos_gl.getMasstab();
                  z.zugbelegt.addLast(z.pos_gl);
                  z.shortenZug();
                  z.laenge = z.laenge + dazug.laenge;
                  boolean drehenx = dazug.pos_gl.sameGleis(z.next_gl);
                  if (drehenx) {
                     dazug.richtungUmkehren();
                     if (zug.debugMode != null) {
                        zug.debugMode.writeln("zug (" + z.getName() + ")", "K-Flag drehen");
                     }
                  }

                  try {
                     while (dazug.zugbelegt.size() > 0) {
                        z.zugbelegt.addLast(dazug.zugbelegt.removeFirst());
                     }
                  } catch (NoSuchElementException var8) {
                  }

                  tl_sichtfahrt.remove(z);
                  z.pos_gl = (gleis)z.zugbelegt.getLast();
                  z.before_gl = (gleis)z.zugbelegt.get(z.zugbelegt.size() - 2);
                  z.lastmasstab = z.pos_gl.getMasstab();
                  if (dazug.ambahnsteig) {
                     z.warankunft = dazug.warankunft;
                     z.gestopptgleis = dazug.gestopptgleis;
                     z.gleiswarok = dazug.gleiswarok;
                     z.ambahnsteig = dazug.ambahnsteig;
                     z.isBahnsteig = dazug.isBahnsteig;
                     z.lastBahnsteig = dazug.lastBahnsteig;
                     z.ist_tempo = 0.0;
                     z.my_main.setZugOnBahnsteig(z.gestopptgleis, z, z.pos_gl);
                     z.c_richtigbahnsteig++;
                     tl_langsam.remove(z);
                     tl_vorsignal.remove(z);
                     tl_sichtfahrt.remove(z);
                     tl_zs1.remove(z);
                     tl_sh1.remove(z);
                  }

                  z.namefarbe = 0;
                  z.outputValueChanged = true;
                  dazug.visible = false;
                  dazug.fertig = true;
                  z.my_main.hideZug(dazug);
                  z.refreshZugAmGleis();
                  if (zug.debugMode != null) {
                     zug.debugMode.writeln("zug (" + z.getName() + ")", "K-Flag mit " + dazug.getName() + " (FR!)");
                  }
               }

               return false;
            } else {
               return this.callFalse(z);
            }
         } else {
            return this.callFalse(z);
         }
      }
   }
}

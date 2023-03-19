package js.java.isolate.sim.zug;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.sim.TEXTTYPE;

public class c_richtungDrehen extends baseChain1Chain {
   c_richtungDrehen() {
      super(new c_mainloop());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      if (z.umdrehen == zug.RICHTUNGWECHELN.DAUERHAFT) {
         if (this.zurueckFertig(z, false)) {
            String text = z.getSpezialName() + " ändert Richtung.";
            z.my_main.showText(text, TEXTTYPE.ANRUF, z);
            z.my_main.playAnruf();
         } else {
            String text = z.getSpezialName() + " kann Richtung hier nicht ändern!";
            z.my_main.showText(text, TEXTTYPE.ANRUF, z);
            z.my_main.playAnruf();
         }

         return false;
      } else if (z.umdrehen == zug.RICHTUNGWECHELN.LOK_UMSETZEN) {
         if (z.runningUmdrehen) {
            if (z.waitLWdone) {
               z.laenge = z.laengeBackup;
               z.waitLWdone = false;
               z.shortenZug();
               z.pos_gl = (gleis)z.zugbelegt.getLast();
               z.before_gl = (gleis)z.zugbelegt.get(z.zugbelegt.size() - 2);
               String text = z.getSpezialName() + " Lok umgesetzt und Fahrtrichtung gewechselt.";
               z.my_main.showText(text, TEXTTYPE.ANRUF, z);
               z.my_main.playAnruf();
               this.zurueckFertig(z, false);
            } else {
               for(gleis gl : z.zugbelegt) {
                  gl.getFluentData().setStatusByZug(2, z);
               }
            }
         } else if (z.ambahnsteig) {
            z.runningUmdrehen = true;
            z.waitLW = true;
            z.waitLWdone = false;
            z.laengeBackup = z.laenge;
            z.shortenZug();
            zug nz = new zug(z, true, false, z.gestopptgleis);
            String text = nz.getSpezialName() + " bereit zum Umsetzen.";
            nz.my_main.showText(text, TEXTTYPE.ANRUF, z);
            nz.my_main.playAnruf();
            z.shortenZug();
            z.pos_gl = (gleis)z.zugbelegt.getLast();
            z.before_gl = (gleis)z.zugbelegt.get(z.zugbelegt.size() - 2);
         } else {
            z.umdrehen = zug.RICHTUNGWECHELN.NONE;
            String text = z.getSpezialName() + " setzt hier keine Lok um!";
            z.my_main.showText(text, TEXTTYPE.ANRUF, z);
            z.my_main.playAnruf();
         }

         return false;
      } else {
         if (z.umdrehen == zug.RICHTUNGWECHELN.ZURUECKSETZEN) {
            if (!z.runningUmdrehen) {
               z.runningUmdrehen = true;
               z.richtungUmkehren();
               z.weiterfahren = true;
               z.notBremsung = false;
               tl_sichtfahrt.add(z);
               return false;
            }

            gleis nextgl = z.pos_gl.next(z.before_gl);
            if (nextgl == null) {
               this.zurueckFertig(z, true);
               return false;
            }

            if (gleis.ALLE_STARTSIGNALE.matches(nextgl.getElement()) && nextgl.forUs(z.pos_gl)) {
               this.zurueckFertig(z, true);
               return false;
            }

            if (z.ambahnsteig) {
               this.zurueckFertig(z, false);
               return false;
            }

            if (gleis.ELEMENT_AUSFAHRT.matches(nextgl.getElement()) || gleis.ELEMENT_EINFAHRT.matches(nextgl.getElement())) {
               this.zurueckFertig(z, true);
               return false;
            }

            if (nextgl.getFluentData().getStatus() == 2) {
               this.zurueckFertig(z, true);
               return false;
            }
         }

         return this.call(z);
      }
   }

   private boolean zurueckFertig(zug z, boolean stop) {
      z.runningUmdrehen = false;
      z.umdrehen = zug.RICHTUNGWECHELN.NONE;
      if (z.richtungUmkehren()) {
         z.notBremsung = stop;
         return true;
      } else {
         return false;
      }
   }
}

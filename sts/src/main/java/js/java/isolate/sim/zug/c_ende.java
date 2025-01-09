package js.java.isolate.sim.zug;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.sim.TEXTTYPE;

class c_ende extends baseChain {
   @Override
   boolean run(zug z) {
      this.visiting(z);
      if (z.pos_gl.getElement() == gleis.ELEMENT_AUSFAHRT) {
         z.visible = false;
      } else if (z.visible && z.zugbelegt.size() > 0) {
         if (z.rottime == 0L) {
            z.rottime = z.mytime;
            z.anruftime = z.rottime;
            z.anrufzaehler = 0;
         }

         z.ist_tempo = 1.0;
         z.namefarbe = 1;
         z.sichtstopp = true;
         z.anrufzaehler++;
         if (z.anrufzaehler == 1) {
            String text = "Anruf von Triebfahrzeugführer "
               + z.getSpezialName()
               + ": <i>\"Zug steht am Gleisende! Was soll das?\"</i><br><br>Geben Sie Befehl 'Richtung ändern'!";
            z.my_main.showText(text, TEXTTYPE.ANRUF, z);
            z.my_main.playAnruf();
            z.anruftime = z.mytime;
         }
      }

      return false;
   }
}

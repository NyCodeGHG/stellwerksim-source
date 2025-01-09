package js.java.isolate.sim.zug;

import js.java.isolate.sim.sim.TEXTTYPE;

class c_anruf extends baseChain {
   @Override
   boolean run(zug z) {
      this.visiting(z);
      if ((z.mytime - z.anruftime) / 60000L > (long)z.variables.var_anrufwartezeit(z.zielgleis) && !z.flags.hasFlag('K')) {
         if (!z.wartenOK) {
            z.anrufzaehler++;
            String text;
            if (z.anrufzaehler > 1) {
               text = z.anrufzaehler
                  + ". Anruf von Triebfahrzeugführer "
                  + z.getSpezialName()
                  + ": <i>\"Zug steht jetzt schon über "
                  + z.anrufzaehler * z.variables.var_anrufwartezeit(z.zielgleis)
                  + " Minuten vor rotem Signal! Pennt ihr?\"</i><br><br>Geben Sie Befehl 'warten' um Anrufe (auch weitere zu diesem Signal) zu verhindern.";
            } else {
               text = "Anruf von Triebfahrzeugführer "
                  + z.getSpezialName()
                  + ": <i>\"Zug steht schon über "
                  + z.variables.var_anrufwartezeit(z.zielgleis)
                  + " Minuten vor rotem Signal! Was ist denn los?\"</i><br><br>Geben Sie Befehl 'warten' um Anrufe (auch weitere zu diesem Signal) zu verhindern.";
            }

            z.my_main.showText(text, TEXTTYPE.ANRUF, z);
            z.my_main.playAnruf();
         }

         z.anruftime = z.mytime;
      }

      return false;
   }
}

package js.java.isolate.sim.zug;

import js.java.isolate.sim.sim.TEXTTYPE;

class c_prerunnerRotGesehen extends baseChain {
   c_prerunnerRotGesehen() {
      super();
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      if (z.haltabstand == 4 && z.lasthaltabstand != z.haltabstand) {
         if (z.haltabstandgesehen < z.mytime - 600000L) {
            z.haltabstandcnt = 0;
            z.haltabstandanrufcnt = 0;
         }

         z.haltabstandcnt += 2;
         z.haltabstandgesehen = z.mytime;
         if (z.haltabstandcnt > 7 && z.haltabstandanrufcnt < 2) {
            String text = "Anruf von Triebfahrzeugführer " + z.getSpezialName() + ": <i>\"Zug fährt jetzt zum wiederholten mal auf ein rotes Signal zu.";
            z.my_main.showText(text, TEXTTYPE.ANRUF, z);
            z.my_main.playAnruf();
            ++z.haltabstandanrufcnt;
         }
      }

      z.lasthaltabstand = z.haltabstand;
      return false;
   }
}

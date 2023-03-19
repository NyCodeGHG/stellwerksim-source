package js.java.isolate.sim.zug;

import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.zugmsg;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.sim.TEXTTYPE;

class c_isausfahrt extends baseChain {
   c_isausfahrt() {
      super();
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      if (z.pos_gl.getElement() == gleis.ELEMENT_AUSFAHRT) {
         z.visible = false;
         z.genommeneausfahrt = z.pos_gl.getENR();
         z.b_richtigeausfahrt = z.genommeneausfahrt == z.aus_enr;
         long et = z.exitTime();
         if (et > 0L) {
            long vp = (et + z.lastAbfahrt) / 60000L;
            long mytime = z.mytime / 60000L;
            z.verspaetung = (int)((long)z.lastVerspaetung + (mytime - vp));
            z.outputValueChanged = true;
         }

         if (z.aus_enr != z.pos_gl.getENR()) {
            String text = "Anruf von Triebfahrzeugführer "
               + z.getSpezialName()
               + ": \"Achtung, Zug fährt in die falsche Richtung!\"\n\nDies führt zu schlechter Bewertung durch Fahrgäste.";
            z.my_main.showText(text, TEXTTYPE.ANRUF, z);
            z.my_main.playAnruf();
            z.verspaetung += 30;
            z.outputValueChanged = true;
         }

         if (z.hasHook(eventGenerator.T_ZUG_AUSFAHRT)) {
            z.call(eventGenerator.T_ZUG_AUSFAHRT, new zugmsg(z, z.pos_gl, z.before_gl));
         }

         z.updateHeat(true, z.verspaetung, z.lastVerspaetung);
         z.decHeat();
         if (zug.debugMode != null) {
            zug.debugMode.writeln("zug (" + z.getName() + ")", "Ausfahrt");
         }
      }

      return false;
   }
}

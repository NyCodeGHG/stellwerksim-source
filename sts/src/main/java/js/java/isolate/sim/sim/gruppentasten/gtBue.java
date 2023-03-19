package js.java.isolate.sim.sim.gruppentasten;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.sim.stellwerksim_main;

public class gtBue extends gtBase {
   private gleis lastBü = null;
   private gleisElements.Stellungen gotoStellung = gleisElements.Stellungen.undef;

   public gtBue(stellwerksim_main m, gleisbildSimControl glb) {
      super(m, glb);
   }

   @Override
   public String getText() {
      return "Bü";
   }

   @Override
   public char getKey() {
      return 'B';
   }

   @Override
   protected void runCommand(String cmd) {
      if (this.gl_object1().getElement() == gleis.ELEMENT_BAHNÜBERGANG || this.gl_object1().getElement() == gleis.ELEMENT_ANRUFÜBERGANG) {
         boolean ret = false;
         gleisElements.Stellungen os = this.gl_object1().getFluentData().getStellung();
         if (os != gleisElements.ST_BAHNÜBERGANG_AUS) {
            if (os == gleisElements.ST_BAHNÜBERGANG_OFFEN) {
               ret = this.gl_object1().getFluentData().setStellung(gleisElements.ST_BAHNÜBERGANG_GESCHLOSSEN);
               if (ret && this.gl_object1().getFluentData().isFrei()) {
                  this.gl_object1().getFluentData().setStatus(4);
                  this.gl_object1().tjmAdd();
                  this.lastBü = this.gl_object1();
                  this.gotoStellung = gleisElements.ST_BAHNÜBERGANG_GESCHLOSSEN;
                  this.setLight(TasterButton.LIGHTMODE.ON);
                  this.showGleisChange();
               }
            } else if (os == gleisElements.ST_BAHNÜBERGANG_GESCHLOSSEN) {
               ret = this.gl_object1().getFluentData().setStellung(gleisElements.ST_BAHNÜBERGANG_OFFEN);
               if (ret && this.gl_object1().getFluentData().isFrei()) {
                  this.gl_object1().getFluentData().setStatus(4);
                  this.gl_object1().tjmAdd();
                  this.lastBü = this.gl_object1();
                  this.gotoStellung = gleisElements.ST_BAHNÜBERGANG_OFFEN;
                  this.setLight(TasterButton.LIGHTMODE.ON);
                  this.showGleisChange();
               }
            }
         }
      }
   }

   @Override
   void verifyLight() {
      if (this.lastBü == null || this.lastBü.getFluentData().getStellung() == this.gotoStellung) {
         this.setLight(TasterButton.LIGHTMODE.OFF);
         this.lastBü = null;
      }
   }
}

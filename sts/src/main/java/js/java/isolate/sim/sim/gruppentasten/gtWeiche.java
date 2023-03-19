package js.java.isolate.sim.sim.gruppentasten;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.sim.stellwerksim_main;

public class gtWeiche extends gtBase {
   private ConcurrentLinkedQueue<gleis> weichen = new ConcurrentLinkedQueue();

   public gtWeiche(stellwerksim_main m, gleisbildSimControl glb) {
      super(m, glb);
   }

   @Override
   public String getText() {
      return "Weiche";
   }

   @Override
   public char getKey() {
      return 'W';
   }

   @Override
   protected void runCommand(String cmd) {
      if ((this.gl_object1().getElement() == gleis.ELEMENT_WEICHEOBEN || this.gl_object1().getElement() == gleis.ELEMENT_WEICHEUNTEN)
         && this.gl_object1().getFluentData().isFrei()) {
         boolean ret = false;
         gleisElements.Stellungen os = this.gl_object1().getFluentData().getStellung();
         if (os != gleisElements.ST_WEICHE_AUS) {
            if (os == gleisElements.ST_WEICHE_GERADE) {
               ret = this.gl_object1().getFluentData().setStellung(gleisElements.ST_WEICHE_ABZWEIG);
               if (ret) {
                  this.gl_object1().getFluentData().setStatus(4);
                  this.gl_object1().tjmAdd();
                  this.showGleisChange();
                  this.weichen.add(this.gl_object1());
                  this.setLight(TasterButton.LIGHTMODE.ON);
               }
            } else if (os == gleisElements.ST_WEICHE_ABZWEIG) {
               ret = this.gl_object1().getFluentData().setStellung(gleisElements.ST_WEICHE_GERADE);
               if (ret) {
                  this.gl_object1().getFluentData().setStatus(4);
                  this.gl_object1().tjmAdd();
                  this.showGleisChange();
                  this.weichen.add(this.gl_object1());
                  this.setLight(TasterButton.LIGHTMODE.ON);
               }
            }
         }
      }
   }

   @Override
   void verifyLight() {
      TasterButton.LIGHTMODE l = TasterButton.LIGHTMODE.OFF;
      Iterator<gleis> it = this.weichen.iterator();

      while(it.hasNext()) {
         gleis g = (gleis)it.next();
         if (g.getFluentData().getStatus() == 4) {
            l = TasterButton.LIGHTMODE.ON;
            break;
         }

         it.remove();
      }

      this.setLight(l);
   }
}

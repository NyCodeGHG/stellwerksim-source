package js.java.isolate.sim.eventsys.events;

import java.util.HashSet;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.gleis.gleis;
import js.java.schaltungen.audio.AudioController;

public class sicherungausfall extends event {
   private static boolean oneIsRunning = false;
   private eventContainer ec;
   private String text = null;
   protected int dauer;
   private HashSet<gleis> poweroffgleise;
   private static final int AREAWIDTH = 30;
   private static final int AREAHEIGHT = 20;

   public sicherungausfall(Simulator sim) {
      super(sim);
   }

   @Override
   public String getText() {
      return this.text;
   }

   @Override
   protected boolean init(eventContainer e) {
      if (oneIsRunning) {
         this.eventDone();
         return false;
      } else {
         oneIsRunning = true;
         this.ec = e;
         this.poweroffgleise = new HashSet();
         int hareas = this.glbModel.getGleisWidth() / 30;
         int vareas = this.glbModel.getGleisHeight() / 20;
         int xa = random(0, hareas + 1);
         int ya = random(0, vareas + 1);
         String bereich = "" + (char)(65 + xa) + (ya + 1);
         this.dauer = e.getIntValue("dauer");

         for (int y = 20 * ya; y < Math.min(this.glbModel.getGleisHeight(), 20 * (ya + 1)); y++) {
            for (int x = 30 * xa; x < Math.min(this.glbModel.getGleisWidth(), 30 * (xa + 1)); x++) {
               gleis gl = this.glbModel.getXY_null(x, y);
               if (gl != null && (gleis.ALLE_GLEISE.matches(gl.getElement()) || gleis.ALLE_DISPLAYS.matches(gl.getElement()))) {
                  gl.getFluentData().setPowerOff(true);
                  this.poweroffgleise.add(gl);
               }
            }
         }

         if (this.poweroffgleise.isEmpty()) {
            oneIsRunning = false;
            this.eventDone();
            return false;
         } else {
            this.text = "Sicherung im Bereich " + bereich + " des Stelltisches ausgefallen, wird getauscht.";
            this.showMessageNow(this.text);
            this.callMeIn(this.dauer);
            this.my_main.playFX(AudioController.FXSOUND.FUSE);
            this.my_main.repaintGleisbild();
            return true;
         }
      }
   }

   @Override
   public boolean pong() {
      for (gleis gl : this.poweroffgleise) {
         gl.getFluentData().setPowerOff(false);
      }

      this.showMessageNow("Sicherung getauscht.");
      oneIsRunning = false;
      this.eventDone();
      return true;
   }

   @Override
   public String funkName() {
      return "Sicherung geflogen!";
   }

   @Override
   public String funkAntwort() {
      return "Noch ca. " + this.restTime() + " Minuten.";
   }
}

package js.java.schaltungen.verifyTests;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.schaltungen.UserContextMini;
import js.java.schaltungen.audio.AudioPlayer;

public class v_sound extends InitTestBase {
   private AudioPlayer play = null;
   private String error = "";

   public v_sound() {
      super();
   }

   @Override
   public String name() {
      return "Ton-Test" + this.error;
   }

   @Override
   public int test(UserContextMini uc) {
      int r = 0;
      if (this.play == null) {
         this.play = new AudioPlayer(AudioPlayer.SAMPLES.WELCOME);
         Optional<Exception> e = this.play.play();
         if (e.isPresent()) {
            r = -1;
            this.error = " Fehler: " + ((Exception)e.get()).getMessage();
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, (Throwable)e.get());
         }
      } else if (!this.play.isRunning()) {
         r = 1;
      }

      return r;
   }
}

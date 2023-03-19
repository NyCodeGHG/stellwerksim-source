package js.java.isolate.sim;

import java.awt.Frame;
import js.java.isolate.sim.zug.zug;
import js.java.isolate.sim.zug.zugModelInterface;
import js.java.schaltungen.audio.AudioController;
import js.java.schaltungen.timesystem.timedelivery;

public interface Simulator extends GleisAdapter, zugModelInterface, timedelivery {
   boolean isBotMode();

   boolean wasBotMode();

   boolean isCaller();

   @Override
   AudioController getAudio();

   void playAlarm(int var1);

   void playCounter();

   void playFX(AudioController.FXSOUND var1);

   void playDingdong(int var1);

   void allowOneRedirect();

   void requestZugRedirect(zug var1, String var2);

   Frame getFrame();
}

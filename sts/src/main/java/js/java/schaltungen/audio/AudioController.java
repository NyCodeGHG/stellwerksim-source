package js.java.schaltungen.audio;

public interface AudioController {
   void playZug();

   void playÜG(int var1);

   void playBÜ(int var1);

   void playMessage();

   void playCounter();

   void playAlarm(int var1);

   void playFX(AudioController.FXSOUND var1);

   void playChatAnruf();

   void playAnruf();

   void playChat();

   void playDingdong(int var1);

   void playSimStart();

   public static enum FXSOUND {
      FUSE(AudioPlayer.SAMPLES.FUSE),
      BEEP(AudioPlayer.SAMPLES.BEEP);

      public final AudioPlayer.SAMPLES sample;

      private FXSOUND(AudioPlayer.SAMPLES s) {
         this.sample = s;
      }
   }
}

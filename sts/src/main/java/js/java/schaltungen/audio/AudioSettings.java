package js.java.schaltungen.audio;

public interface AudioSettings {
   void open();

   void close();

   AudioSettings.SoundSettings playZugSettings();

   AudioSettings.SoundSettings playÜGSettings();

   AudioSettings.SoundSettings playBÜSettings();

   AudioSettings.SoundSettings playMessageSettings();

   AudioSettings.SoundSettings playCounterSettings();

   AudioSettings.SoundSettings playAlarmSettings();

   AudioSettings.SoundSettings playChatSettings();

   AudioSettings.SoundSettings playDingdongSettings();

   public interface SoundSettings {
      void setEnabled(boolean var1);

      boolean isEnabled();

      void setGain(float var1);

      float getGain();

      float getDefaultGain();
   }
}

package js.java.schaltungen.audio;

import de.deltaga.eb.EventBusService;
import js.java.schaltungen.UserContextMini;
import js.java.schaltungen.adapter.simPrefs;

public class AudioSettingsImpl implements AudioSettings {
   private static final simPrefs prefs = new simPrefs("audio");
   private final UserContextMini uc;
   private boolean notSync = false;

   public AudioSettingsImpl(UserContextMini uc) {
      super();
      this.uc = uc;
   }

   @Override
   public void open() {
      this.notSync = true;
   }

   @Override
   public void close() {
      boolean o = this.notSync;
      this.notSync = false;
      if (o) {
         EventBusService.getInstance().publish(new AudioSettingsChangedEvent());
      }
   }

   @Override
   public AudioSettings.SoundSettings playZugSettings() {
      return new AudioSettingsImpl.OneSoundSetting("zug");
   }

   @Override
   public AudioSettings.SoundSettings playÜGSettings() {
      return new AudioSettingsImpl.OneSoundSetting("üg");
   }

   @Override
   public AudioSettings.SoundSettings playBÜSettings() {
      return new AudioSettingsImpl.OneSoundSetting("bü");
   }

   @Override
   public AudioSettings.SoundSettings playMessageSettings() {
      return new AudioSettingsImpl.OneSoundSetting("funksound");
   }

   @Override
   public AudioSettings.SoundSettings playCounterSettings() {
      return new AudioSettingsImpl.OneSoundSetting("counter", -10.0F);
   }

   @Override
   public AudioSettings.SoundSettings playAlarmSettings() {
      return new AudioSettingsImpl.OneSoundSetting("alarm");
   }

   @Override
   public AudioSettings.SoundSettings playChatSettings() {
      return new AudioSettingsImpl.OneSoundSetting("chat", -10.0F);
   }

   @Override
   public AudioSettings.SoundSettings playDingdongSettings() {
      return new AudioSettingsImpl.OneSoundSetting("dingdong");
   }

   private class OneSoundSetting implements AudioSettings.SoundSettings {
      private final String nameOn;
      private final String nameGain;
      private final float defaultGain;

      OneSoundSetting(String name) {
         this(name, 0.0F);
      }

      OneSoundSetting(String name, float defaultGain) {
         super();
         this.nameOn = name + "_on";
         this.nameGain = name + "_gain";
         this.defaultGain = defaultGain;
      }

      @Override
      public void setEnabled(boolean b) {
         AudioSettingsImpl.prefs.putBoolean(this.nameOn, b);
         AudioSettingsImpl.prefs.flush();
         if (!AudioSettingsImpl.this.notSync) {
            EventBusService.getInstance().publish(new AudioSettingsChangedEvent());
         }
      }

      @Override
      public boolean isEnabled() {
         return AudioSettingsImpl.prefs.getBoolean(this.nameOn, true);
      }

      @Override
      public void setGain(float g) {
         AudioSettingsImpl.prefs.putFloat(this.nameGain, g);
         AudioSettingsImpl.prefs.flush();
         if (!AudioSettingsImpl.this.notSync) {
            EventBusService.getInstance().publish(new AudioSettingsChangedEvent());
         }
      }

      @Override
      public float getGain() {
         return AudioSettingsImpl.prefs.getFloat(this.nameGain, this.defaultGain);
      }

      @Override
      public float getDefaultGain() {
         return this.defaultGain;
      }
   }
}

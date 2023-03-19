package js.java.schaltungen.audio;

import js.java.schaltungen.UserContext;
import js.java.schaltungen.moduleapi.SessionClose;

public class AudioControllerImpl implements AudioController, SessionClose, AudioPlayer.EndListener {
   private final UserContext uc;
   private AudioPlayer tablePlayer = null;
   private long tablePlayerFinished = 0L;
   private int zugQueueCount = 0;
   private final AudioSettings settings;

   public AudioControllerImpl(UserContext uc) {
      super();
      this.uc = uc;
      uc.addCloseObject(this);
      this.settings = new AudioSettingsImpl(uc);
   }

   @Override
   public synchronized void playZug() {
      if (this.settings.playZugSettings().isEnabled()) {
         if (this.tablePlayer == null) {
            this.tablePlayer = new AudioPlayer(AudioPlayer.SAMPLES.TRAIN);
            this.tablePlayer.setEndListener(this);
            this.tablePlayer.setGain(this.settings.playZugSettings().getGain());
            if (this.tablePlayer.play().isPresent()) {
               this.tablePlayer = null;
            }
         } else {
            ++this.zugQueueCount;
         }
      }
   }

   @Override
   public void playSimStart() {
      this.play(AudioPlayer.SAMPLES.SIMSTART, null);
   }

   @Override
   public void playÜG(int hash) {
      this.playTable(hash, this.settings.playÜGSettings());
   }

   @Override
   public void playBÜ(int hash) {
      this.playTable(hash, this.settings.playBÜSettings());
   }

   private synchronized void playTable(int hash, AudioSettings.SoundSettings set) {
      if (set.isEnabled() && this.tablePlayer == null && this.tablePlayerFinished < System.currentTimeMillis() - 500L) {
         this.tablePlayer = new AudioPlayer(AudioPlayer.SAMPLES.TABLE);
         this.tablePlayer.setEndListener(this);
         this.tablePlayer.setGain(set.getGain());
         if (this.tablePlayer.play().isPresent()) {
            this.tablePlayer = null;
         }
      }
   }

   private void play(AudioPlayer.SAMPLES p, AudioSettings.SoundSettings pref) {
      if (pref == null || pref.isEnabled()) {
         AudioPlayer ap = new AudioPlayer(p);
         if (pref != null) {
            ap.setGain(pref.getGain());
         }

         ap.play();
      }
   }

   @Override
   public void playMessage() {
      this.play(AudioPlayer.SAMPLES.MELDUNG, this.settings.playMessageSettings());
   }

   @Override
   public void playCounter() {
      this.play(AudioPlayer.SAMPLES.COUNTER, this.settings.playCounterSettings());
   }

   @Override
   public void playAlarm(int cnt) {
      this.play(AudioPlayer.SAMPLES.ALARM, this.settings.playAlarmSettings());
   }

   @Override
   public void playFX(AudioController.FXSOUND f) {
      this.play(f.sample, null);
   }

   @Override
   public void playChatAnruf() {
      this.play(AudioPlayer.SAMPLES.PHONE1, this.settings.playChatSettings());
   }

   @Override
   public void playAnruf() {
      this.play(AudioPlayer.SAMPLES.PHONE2, this.settings.playMessageSettings());
   }

   @Override
   public void playChat() {
      this.play(AudioPlayer.SAMPLES.CHAT, this.settings.playChatSettings());
   }

   @Override
   public void playDingdong(int d) {
      if (d == 1) {
         this.play(AudioPlayer.SAMPLES.DINGDONG1, this.settings.playDingdongSettings());
      }

      if (d == 2) {
         this.play(AudioPlayer.SAMPLES.DINGDONG2, this.settings.playDingdongSettings());
      }

      if (d == 3) {
         this.play(AudioPlayer.SAMPLES.DINGDONG3, this.settings.playDingdongSettings());
      }

      if (d == 4) {
         this.play(AudioPlayer.SAMPLES.DINGDONG4, this.settings.playDingdongSettings());
      }
   }

   @Override
   public void close() {
      this.zugQueueCount = 0;
      this.tablePlayer = null;
   }

   @Override
   public synchronized void endReached(AudioPlayer p) {
      if (this.tablePlayer == p) {
         this.tablePlayer = null;
         this.tablePlayerFinished = System.currentTimeMillis();
         if (this.zugQueueCount > 0) {
            --this.zugQueueCount;
            this.tablePlayer = new AudioPlayer(AudioPlayer.SAMPLES.TRAIN);
            this.tablePlayer.setEndListener(this);
            if (this.tablePlayer.play().isPresent()) {
               this.tablePlayer = null;
            }
         }
      }
   }
}

package js.java.schaltungen.audio;

import java.io.BufferedInputStream;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.FloatControl.Type;

public class AudioPlayer implements LineListener {
   private final AudioPlayer.SAMPLES sample;
   private AudioPlayer.EndListener listener = null;
   private Clip clip = null;
   private final int loop;
   private float gain = 0.0F;

   public AudioPlayer(AudioPlayer.SAMPLES s, int loop) {
      this.sample = s;
      this.loop = loop;
   }

   public AudioPlayer(AudioPlayer.SAMPLES s) {
      this.sample = s;
      this.loop = s.plannedLoop;
   }

   public void setEndListener(AudioPlayer.EndListener l) {
      this.listener = l;
   }

   public void setGain(float gain) {
      this.gain = gain;
   }

   public Optional<Exception> play() {
      try {
         if (this.clip != null) {
            return Optional.empty();
         } else {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(this.getClass().getResourceAsStream(this.sample.filename)));
            AudioFormat format = ais.getFormat();
            if (format.getEncoding() == Encoding.ULAW || format.getEncoding() == Encoding.ALAW) {
               AudioFormat tmp = new AudioFormat(
                  Encoding.PCM_SIGNED,
                  format.getSampleRate(),
                  format.getSampleSizeInBits() * 2,
                  format.getChannels(),
                  format.getFrameSize() * 2,
                  format.getFrameRate(),
                  true
               );
               ais = AudioSystem.getAudioInputStream(tmp, ais);
               format = tmp;
            }

            Info info = new Info(Clip.class, format, (int)ais.getFrameLength() * format.getFrameSize());
            this.clip = (Clip)AudioSystem.getLine(info);
            if (this.clip == null) {
               ais.close();
               return Optional.empty();
            } else {
               this.clip.open(ais);
               if (this.gain < 0.0F) {
                  FloatControl gainControl = (FloatControl)this.clip.getControl(Type.MASTER_GAIN);
                  gainControl.setValue(this.gain);
               }

               this.clip.addLineListener(this);
               this.clip.loop(this.loop - 1);
               this.clip.setLoopPoints(0, -1);
               this.clip.start();
               return Optional.empty();
            }
         }
      } catch (Exception var5) {
         Logger.getLogger(AudioPlayer.class.getName()).log(Level.SEVERE, null, var5);
         return Optional.of(var5);
      }
   }

   public boolean isRunning() {
      return this.clip != null && this.clip.isRunning();
   }

   public void stop() {
      if (this.clip != null) {
         this.clip.stop();
         this.clip.close();
         this.clip = null;
      }
   }

   public void update(LineEvent event) {
      if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
         if (this.listener != null) {
            this.listener.endReached(this);
         }

         if (this.clip != null) {
            this.clip.close();
            this.clip = null;
         }
      }
   }

   public interface EndListener {
      void endReached(AudioPlayer var1);
   }

   public static enum SAMPLES {
      WELCOME("warten.wav"),
      ALARM("alarm.wav"),
      SIMSTART("simstart.wav"),
      COUNTER("counter.wav"),
      TRAIN("ZugEin.wav", 3),
      TABLE("ZugEin.wav"),
      FUSE("fuse.wav"),
      BEEP("beep.wav"),
      PHONE1("phone1.wav"),
      PHONE2("phone2.wav"),
      MELDUNG("Meldung.wav"),
      DINGDONG1("dingdong1.wav"),
      DINGDONG2("dingdong2.wav"),
      DINGDONG3("dingdong3.wav"),
      DINGDONG4("dingdong4.wav"),
      CHAT("chat.wav");

      private final String filename;
      private final int plannedLoop;

      private SAMPLES(String name) {
         this.filename = name;
         this.plannedLoop = 1;
      }

      private SAMPLES(String name, int plannedLoop) {
         this.filename = name;
         this.plannedLoop = plannedLoop;
      }
   }
}

package js.java.tools;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineEvent.Type;

public class extendedClip {
   private Clip clip;
   private boolean wasopen = false;
   private boolean wasstarted = false;
   private boolean wasstopped = false;
   private boolean wasclosed = false;

   private void addListener() {
      this.clip.addLineListener(new LineListener() {
         public void update(LineEvent e) {
            if (e.getType().equals(Type.OPEN)) {
               extendedClip.this.wasopen = true;
            } else if (e.getType().equals(Type.CLOSE)) {
               extendedClip.this.wasclosed = true;
            } else if (e.getType().equals(Type.START)) {
               extendedClip.this.wasstarted = true;
            } else if (e.getType().equals(Type.STOP)) {
               extendedClip.this.wasstopped = true;
               extendedClip.this.clip.close();
            }
         }
      });
   }

   public extendedClip(Clip c) {
      this.clip = c;
      this.addListener();
   }

   public void start() {
      this.clip.start();
   }

   public void stop() {
      this.clip.stop();
   }

   public void loop(int l) {
      this.clip.loop(l);
   }

   public boolean isRunning() {
      return this.clip.isRunning();
   }

   public boolean isOpen() {
      return this.clip.isOpen();
   }

   public boolean isActive() {
      return this.clip.isActive();
   }

   public void drain() {
      this.clip.drain();
   }

   public boolean isFinished() {
      return this.wasstopped || this.wasclosed;
   }
}

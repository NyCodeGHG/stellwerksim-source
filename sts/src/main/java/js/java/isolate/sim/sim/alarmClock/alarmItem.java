package js.java.isolate.sim.sim.alarmClock;

import js.java.isolate.sim.trigger;
import js.java.schaltungen.timesystem.TimeFormat;

public class alarmItem extends trigger {
   private final alarmClock my_main;
   private long timestamp;
   private String text;
   private final TimeFormat tf;
   private int changeDownCount = 0;
   private boolean removeMe = false;

   alarmItem(alarmClock my_main, int h, int m, String text) {
      super();
      this.my_main = my_main;
      this.text = text;
      this.tf = TimeFormat.getInstance(TimeFormat.STYLE.HM);
      this.setTime(h, m);
   }

   alarmItem(alarmClock my_main) {
      this(my_main, 5, 0, "neu");
      this.changeDownCount = 300;
   }

   void setTime(int h, int m) {
      this.timestamp = (long)h * 3600000L + (long)m * 60000L;
      this.changeDownCount = 40;
      this.tjm_add();
   }

   int getH() {
      return (int)(this.timestamp / 3600000L);
   }

   int getM() {
      return (int)(this.timestamp / 60000L % 60L);
   }

   void update(int h, int m, String t) {
      this.text = t;
      this.setTime(h, m);
   }

   String getText() {
      return this.text;
   }

   public String toString() {
      return this.tf.format(this.timestamp) + ": " + this.text;
   }

   @Override
   public boolean ping() {
      if (this.removeMe) {
         return false;
      } else {
         if (this.changeDownCount > 0) {
            --this.changeDownCount;
            this.tjm_add();
         } else {
            long t = this.my_main.getSimutime();
            if (t < this.timestamp) {
               this.tjm_add();
            } else {
               this.my_main.alarm(this);
               this.my_main.removeAlarm(this);
            }
         }

         return false;
      }
   }

   public void remove() {
      this.removeMe = true;
   }
}

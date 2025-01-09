package js.java.tools.gui.clock;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.Date;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import js.java.schaltungen.timesystem.timeSync;

public class uhrComponent extends JPanel implements bahnhofsUhr.timeDeliverer, timerecipient {
   private long timeOffset = 0L;
   private timeSync tsync = null;
   private bahnhofsUhr.timeDeliverer utimer;
   String hostname = "sts.stellwerksim.de";
   private bahnhofsUhr uhr;
   int instanz;
   private ButtonGroup modeGroup;

   public uhrComponent(String titel, String hostname, int instanz) {
      this.hostname = hostname;
      this.instanz = instanz;
      this.init(titel);
      this.setStstime();
   }

   public uhrComponent(String titel, int instanz) {
      this.instanz = instanz;
      this.init(titel);
      this.setStstime();
   }

   public uhrComponent(String titel) {
      this.init(titel);
      this.setRealtime();
   }

   private void init(String titel) {
      this.initComponents();
      this.uhr = new bahnhofsUhr(this, titel);
      this.add(this.uhr, "Center");
   }

   public void finish() {
      this.uhr.finish();
   }

   private void setRealtime() {
      this.utimer = new uhrComponent.realtimer();
   }

   private void setStstime() {
      try {
         this.tsync = new timeSync(this.hostname, this.instanz, this);
         this.tsync.sync();
         this.utimer = new uhrComponent.syncedtimer();
         this.uhr.set24Mode(true);
      } catch (IOException var2) {
         var2.printStackTrace();
      }
   }

   private void initComponents() {
      this.modeGroup = new ButtonGroup();
      this.setMinimumSize(new Dimension(100, 100));
      this.setPreferredSize(new Dimension(200, 200));
      this.setLayout(new BorderLayout());
   }

   @Override
   public void timeQuery(bahnhofsUhr u) {
      this.utimer.timeQuery(u);
   }

   @Override
   public void timeChange(long offsetToLocal, short tagescode, int latency) {
      this.timeOffset = offsetToLocal;
   }

   private class realtimer extends uhrComponent.syncedtimer {
      private realtimer() {
      }

      @Override
      public void timeQuery(bahnhofsUhr u) {
         Date d = new Date();
         int s = d.getSeconds();
         int m = d.getMinutes();
         int h = d.getHours();
         u.setTime(h, m, s);
      }
   }

   private class syncedtimer implements bahnhofsUhr.timeDeliverer {
      private syncedtimer() {
      }

      protected void setTime(long t, bahnhofsUhr u) {
         int s = (int)(t / 1000L % 60L);
         int m = (int)(t / 60000L % 60L);
         int h = (int)(t / 3600000L);
         u.setTime(h, m, s);
         if (h == 21 && uhrComponent.this.tsync != null) {
            uhrComponent.this.tsync.sync();
         }
      }

      @Override
      public void timeQuery(bahnhofsUhr u) {
         long t = System.currentTimeMillis() - uhrComponent.this.timeOffset;
         this.setTime(t, u);
      }
   }
}

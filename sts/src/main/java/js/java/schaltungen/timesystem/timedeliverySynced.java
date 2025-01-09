package js.java.schaltungen.timesystem;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.UnresolvedAddressException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.gui.clock.timerecipient;

public class timedeliverySynced extends timedeliveryBase implements timedelivery, timerecipient, timedeliveryAndSet, SessionClose {
   private long timeOffset = 0L;
   private timeSync tsync = null;
   private Timer repeatTimer = new Timer();
   private boolean synced = false;

   protected void finalize() throws Throwable {
      try {
         this.stop();
      } finally {
         super.finalize();
      }
   }

   @Override
   public long getSimutime() {
      return System.currentTimeMillis() - this.timeOffset;
   }

   public void timeChange(long offsetToLocal, short tagescode, int latency) {
      this.synced = true;
      this.timeOffset = offsetToLocal;
   }

   @Override
   public void stop() {
      this.repeatTimer.cancel();
      this.tsync.stop();
   }

   @Override
   public void close() {
      this.stop();
   }

   private void init() {
      this.timeOffset = System.currentTimeMillis();
      this.tsync.sync();
      this.repeatTimer.scheduleAtFixedRate(new timedeliverySynced.syncTimer(), 3600000L, 3600000L);
   }

   public timedeliverySynced(String url) {
      try {
         this.tsync = new timeSync(new URL(url), this);
      } catch (UnresolvedAddressException | MalformedURLException var5) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "caught, fallback", var5);

         try {
            this.tsync = new timeSync("www.stellwerksim.de", 0, this);
         } catch (IOException var4) {
            Logger.getLogger("stslogger").log(Level.SEVERE, null, var4);
         }
      } catch (IOException var6) {
         Logger.getLogger("stslogger").log(Level.SEVERE, null, var6);
      }

      this.init();
   }

   public timedeliverySynced(String host, int instance) {
      try {
         this.tsync = new timeSync(host, instance, this);
      } catch (IOException var4) {
         Logger.getLogger("stslogger").log(Level.SEVERE, null, var4);
      }

      this.init();
   }

   @Override
   public void setTime(long t) {
      if (!this.synced) {
         this.timeOffset = System.currentTimeMillis() - t;
      }
   }

   private class syncTimer extends TimerTask {
      private syncTimer() {
      }

      public void run() {
         try {
            timedeliverySynced.this.tsync.sync();
         } catch (Exception var2) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "sync", var2);
         }
      }
   }
}

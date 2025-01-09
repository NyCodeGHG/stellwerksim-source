package js.java.schaltungen.timesystem;

import js.java.schaltungen.moduleapi.SessionClose;

public class simTimeHolder implements timedelivery, SessionClose {
   private timedeliveryBase simTime = new timedeliveryLoaded();

   protected void finalize() throws Throwable {
      try {
         if (this.simTime != null) {
            this.simTime.stop();
         }
      } finally {
         super.finalize();
      }
   }

   public final void setTimeDeliverer(String url) {
      this.simTime = new timedeliverySynced(url);
   }

   public final void setTimeDeliverer(String host, int instance) {
      this.simTime = new timedeliverySynced(host, instance);
   }

   public final void setTimeDeliverer(timedeliveryBase t) {
      if (this.simTime != null) {
         this.simTime.stop();
      }

      this.simTime = t;
   }

   public void setPause(boolean on) {
      try {
         this.simTime.setPause(on);
      } catch (NullPointerException var3) {
      }
   }

   @Override
   public boolean isPause() {
      return this.simTime != null ? this.simTime.isPause() : false;
   }

   @Override
   public final long getSimutime() {
      try {
         return this.simTime.getSimutime();
      } catch (NullPointerException var2) {
         return 0L;
      }
   }

   @Override
   public final String getSimutimeString() {
      try {
         return this.simTime.getSimutimeString();
      } catch (NullPointerException var2) {
         return "";
      }
   }

   @Override
   public final String getSimutimeString(long t) {
      return this.simTime.getSimutimeString(t);
   }

   public final void setTime(long t) {
      if (this.simTime instanceof timedeliveryAndSet) {
         ((timedeliveryAndSet)this.simTime).setTime(t);
      }
   }

   public void endOfTime() {
      if (this.simTime != null) {
         this.simTime.stop();
      }
   }

   @Override
   public void close() {
      if (this.simTime != null) {
         this.simTime.stop();
         this.simTime = null;
      }
   }
}

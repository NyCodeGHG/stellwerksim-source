package js.java.schaltungen.timesystem;

public class timedeliveryLoaded extends timedeliveryBase implements timedeliveryAndSet {
   private long simusynctime = 0L;
   private long simutime = 0L;
   private long pausetime = 0L;
   private boolean pauseon = false;
   private Object pauseWaiter = new Object();

   @Override
   public void setPause(boolean on) {
      if (on) {
         this.pausetime = this.getSimutime();
         synchronized (this.pauseWaiter) {
            this.pauseon = on;
         }
      } else {
         this.setTime(this.pausetime);
         synchronized (this.pauseWaiter) {
            this.pauseon = on;
            this.pauseWaiter.notifyAll();
         }
      }
   }

   @Override
   public boolean isPause() {
      return this.pauseon;
   }

   @Override
   public long getSimutime() {
      return this.pauseon ? this.pausetime : this.simutime + (System.currentTimeMillis() - this.simusynctime);
   }

   @Override
   public void setTime(long t) {
      this.simusynctime = System.currentTimeMillis();
      this.simutime = t;
   }

   public timedeliveryLoaded() {
      this.simusynctime = System.currentTimeMillis();
   }
}

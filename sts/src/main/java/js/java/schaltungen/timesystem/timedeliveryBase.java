package js.java.schaltungen.timesystem;

public abstract class timedeliveryBase implements timedelivery {
   private TimeFormat df = TimeFormat.getInstance(TimeFormat.STYLE.HMS);

   public void setPause(boolean on) {
   }

   @Override
   public boolean isPause() {
      return false;
   }

   @Override
   public String getSimutimeString() {
      return this.df.format(this.getSimutime());
   }

   @Override
   public String getSimutimeString(long t) {
      return this.df.format(t);
   }

   public void stop() {
   }
}

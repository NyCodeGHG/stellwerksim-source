package js.java.schaltungen.timesystem;

public class timedeliveryRealtime extends timedeliveryBase implements timedelivery {
   @Override
   public long getSimutime() {
      return System.currentTimeMillis() % 86400000L;
   }
}

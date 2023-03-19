package js.java.tools;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class RunLater implements Runnable {
   private static final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(2);

   public static void runlater(Runnable r, long millisec) {
      scheduler.schedule(r, millisec, TimeUnit.MILLISECONDS);
   }

   protected RunLater(long millisec) {
      super();
      runlater(this, millisec);
   }
}

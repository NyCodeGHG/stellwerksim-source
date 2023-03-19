package js.java.tools;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class TimeoutTimer {
   private final ScheduledExecutorService scheduler;
   private final long timeout;
   private final TimeUnit timeUnit;
   private final Runnable task;
   private final AtomicReference<ScheduledFuture<?>> ticket = new AtomicReference();

   public TimeoutTimer(ScheduledExecutorService scheduler, long timeout, TimeUnit timeUnit, Runnable task) {
      super();
      this.scheduler = scheduler;
      this.timeout = timeout;
      this.timeUnit = timeUnit;
      this.task = task;
   }

   public TimeoutTimer(long timeout, Runnable task) {
      this(new ScheduledThreadPoolExecutor(1), timeout, TimeUnit.MILLISECONDS, task);
   }

   public TimeoutTimer reset(boolean mayInterruptIfRunning) {
      ScheduledFuture<?> newTicket = this.scheduler.schedule(this.task, this.timeout, this.timeUnit);
      ScheduledFuture<?> oldTicket = (ScheduledFuture)this.ticket.getAndSet(newTicket);
      if (oldTicket != null) {
         oldTicket.cancel(mayInterruptIfRunning);
      }

      return this;
   }
}

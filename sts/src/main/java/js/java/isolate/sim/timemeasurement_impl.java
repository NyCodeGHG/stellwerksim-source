package js.java.isolate.sim;

import js.java.isolate.sim.sim.stellwerksim_main;
import js.java.isolate.sim.zug.zug;
import js.java.schaltungen.timesystem.TimeFormat;

@Deprecated
class timemeasurement_impl extends timemeasurement {
   private final int[] mintime = new int[7];
   private final int[] maxtime = new int[7];
   private final int[] avgtime = new int[7];
   private final long[] cnttime = new long[7];
   private long prgstart;
   private long eventcounter = 0L;
   private stellwerksim_main mainprg = null;

   protected timemeasurement_impl(stellwerksim_main m) {
      myself = this;
      this.mainprg = m;

      for (int i = 0; i < 7; i++) {
         this.mintime[i] = Integer.MAX_VALUE;
         this.maxtime[i] = 0;
         this.avgtime[i] = 0;
         this.cnttime[i] = 0L;
      }
   }

   @Override
   protected void C_init() {
      System.out.println("timemeasurement_impl");
      this.prgstart = System.currentTimeMillis();
      this.eventcounter = 0L;
   }

   @Override
   protected int C_stop(int T) {
      int t = super.C_stop(T);
      if (t > 0) {
         this.mintime[T] = Math.min(this.mintime[T], t);
      }

      this.maxtime[T] = Math.max(this.maxtime[T], t);
      this.avgtime[T] = (Math.max(this.avgtime[T], t) - Math.min(this.avgtime[T], t)) / 2;
      this.cnttime[T]++;
      return t;
   }

   @Override
   protected void C_incEvent() {
      this.eventcounter++;
   }

   @Override
   protected String C_mkUrl() {
      long d = (System.currentTimeMillis() - this.prgstart) / 60000L;
      int processorsNum = Runtime.getRuntime().availableProcessors();
      String r = "&d=" + d + "&cn=" + processorsNum;

      for (int i = 0; i < 7; i++) {
         r = r + "&min[" + i + "]=" + this.mintime[i];
         r = r + "&max[" + i + "]=" + this.maxtime[i];
         r = r + "&avg[" + i + "]=" + this.avgtime[i];
         r = r + "&cnt[" + i + "]=" + this.cnttime[i];
      }

      r = r + "&evntcnt=" + this.eventcounter;
      r = r + "&heat=" + zug.getHeat();
      if (this.mainprg != null) {
         TimeFormat df = TimeFormat.getInstance(TimeFormat.STYLE.HMS);
         r = r + "&stoptime=" + df.format(this.mainprg.getSimutime());
      }

      return r;
   }
}

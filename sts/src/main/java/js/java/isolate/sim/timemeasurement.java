package js.java.isolate.sim;

@Deprecated
class timemeasurement {
   protected static final int CNT = 7;
   public static final int T_REPAINT = 0;
   public static final int T_GPAINT0 = 1;
   public static final int T_GPAINT1 = 2;
   public static final int T_GPAINT1B = 3;
   public static final int T_GPAINT2 = 4;
   public static final int T_GPAINT3 = 5;
   public static final int T_TJM = 6;
   protected static timemeasurement myself = null;
   protected long[] starttime = new long[7];

   protected timemeasurement() {
      super();
   }

   protected void C_init() {
   }

   public static void init() {
      myself.C_init();
   }

   protected void C_start(int T) {
      this.starttime[T] = System.currentTimeMillis();
   }

   public static void start(int T) {
      myself.C_start(T);
   }

   protected int C_stop(int T) {
      return (int)(System.currentTimeMillis() - this.starttime[T]);
   }

   public static int stop(int T) {
      return myself.C_stop(T);
   }

   protected String C_mkUrl() {
      return "";
   }

   public static void incEvent() {
      myself.C_incEvent();
   }

   protected void C_incEvent() {
   }

   public static String mkUrl() {
      return myself.C_mkUrl();
   }
}

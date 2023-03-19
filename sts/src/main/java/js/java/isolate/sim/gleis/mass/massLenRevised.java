package js.java.isolate.sim.gleis.mass;

public class massLenRevised extends massLenClassic {
   private static final String[] masstabLabels = new String[]{
      "1:1",
      "1:1,3 (T1,5/L1,3)",
      "1:2 (T2,0/L2,0)",
      "1:4 (T2,5/L4,5)",
      "1:8 (T3,0/L9,0)",
      "1:16 (T3,5/L&infin;)",
      "1:32 (T4,0/L&infin;)",
      "1:64 (T4,5/L&infin;)"
   };
   private static final double[] masstabLength = new double[]{1.0, 1.3, 2.0, 4.0, 8.0, 16.0, 32.0, 64.0};

   public massLenRevised() {
      super();
   }

   @Override
   protected int calcLaengeImpl(int masstab, int laenge) {
      try {
         return (int)((double)(laenge * 2) / masstabLength[masstab]);
      } catch (ArrayIndexOutOfBoundsException var4) {
         return laenge;
      }
   }

   @Override
   protected String[] getMasstabLabels() {
      return masstabLabels;
   }
}

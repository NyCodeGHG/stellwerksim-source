package js.java.isolate.sim.gleis.mass;

public class massLenClassic extends massSpeed05 {
   private static final String[] masstabLabels = new String[]{
      "1:1",
      "1:1,3 (T1,5/L1,3)",
      "1:2 (T2,0/L2,0)",
      "1:4 (T2,5/L4,0)",
      "1:8 (T3,0/L&infin;)",
      "1:16 (T3,5/L&infin;)",
      "1:32 (T4,0/L&infin;)",
      "1:64 (T4,5/L&infin;)"
   };
   private static final int[] masstabValues = new int[]{0, 1, 2, 3, 4, 5, 6, 7};

   @Override
   protected int calcLaengeImpl(int masstab, int laenge) {
      return laenge * 2 - laenge * masstab / 2;
   }

   @Override
   protected String[] getMasstabLabels() {
      return masstabLabels;
   }

   @Override
   protected int[] getMasstabValues() {
      return masstabValues;
   }

   @Override
   public boolean isCompatible(massBase c) {
      return c instanceof massLenClassic;
   }
}

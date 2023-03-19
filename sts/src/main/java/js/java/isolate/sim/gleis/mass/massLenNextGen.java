package js.java.isolate.sim.gleis.mass;

public class massLenNextGen extends massSpeed05 {
   private static final int SPEEDSHIFT = 0;
   private static final int LENGTHSHIFT = 16;
   private static final String[] masstabLabels = new String[]{
      "TL 1:1",
      "TL 1:1,5",
      "TL 1:2",
      "TL 1:2,5",
      "TL 1:3",
      "TL 1:3,5",
      "TL 1:4",
      "TL 1:4,5",
      "1:T1,5+L2,0",
      "T 1:2,0",
      "L 1:2,0",
      "L 1:3,0",
      "L 1:&infin; (Absteller)"
   };
   private static final int[] masstabValues = new int[]{0, 65537, 131074, 196611, 262148, 327685, 393222, 458759, 131073, 2, 131072, 262144, 524288};
   private static final double[] masstabLength = new double[]{1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 20.0};

   public massLenNextGen() {
      super();
   }

   @Override
   protected int calcLaengeImpl(int masstab, int laenge) {
      masstab >>= 16;

      try {
         return (int)((double)(laenge * 2) / masstabLength[masstab]);
      } catch (ArrayIndexOutOfBoundsException var4) {
         return laenge * 2;
      }
   }

   @Override
   protected int calcMaxSpeedImpl(int masstab) {
      masstab &= 65535;
      return super.calcMaxSpeedImpl(masstab);
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
      return c instanceof massLenNextGen;
   }

   public boolean isLimitedMass(int m) {
      return m == 524288;
   }
}

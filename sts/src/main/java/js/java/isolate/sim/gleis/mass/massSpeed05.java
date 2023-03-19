package js.java.isolate.sim.gleis.mass;

public abstract class massSpeed05 extends massBase {
   protected static final int SPEED = 6;

   public massSpeed05() {
      super();
   }

   @Override
   protected int calcMaxSpeedImpl(int masstab) {
      return 6 + 6 * masstab / 2;
   }
}

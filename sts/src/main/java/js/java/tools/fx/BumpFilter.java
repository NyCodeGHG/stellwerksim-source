package js.java.tools.fx;

public class BumpFilter extends ConvolveFilter {
   private static final float[] embossMatrix = new float[]{-1.0F, -1.0F, 0.0F, -1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F};

   public BumpFilter() {
      super(embossMatrix);
   }

   @Override
   public String toString() {
      return "Blur/Emboss Edges";
   }
}

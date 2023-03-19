package js.java.tools.fx;

public class BlurFilter extends ConvolveFilter {
   protected static float[] blurMatrix = new float[]{
      0.071428575F, 0.14285715F, 0.071428575F, 0.14285715F, 0.14285715F, 0.14285715F, 0.071428575F, 0.14285715F, 0.071428575F
   };

   public BlurFilter() {
      super(blurMatrix);
   }

   @Override
   public String toString() {
      return "Blur/Simple Blur";
   }
}

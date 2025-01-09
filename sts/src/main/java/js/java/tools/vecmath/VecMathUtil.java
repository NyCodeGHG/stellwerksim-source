package js.java.tools.vecmath;

class VecMathUtil {
   static int floatToIntBits(float f) {
      return f == 0.0F ? 0 : Float.floatToIntBits(f);
   }

   static long doubleToLongBits(double d) {
      return d == 0.0 ? 0L : Double.doubleToLongBits(d);
   }

   private VecMathUtil() {
   }
}

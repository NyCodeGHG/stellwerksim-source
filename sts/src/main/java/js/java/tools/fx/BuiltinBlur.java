package js.java.tools.fx;

import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class BuiltinBlur {
   public BuiltinBlur() {
      super();
   }

   public static ConvolveOp create() {
      int lockOutPos = 10;
      float[] brightKernel = new float[100];
      float v = 0.01F;

      for(int i = 0; i < brightKernel.length; ++i) {
         brightKernel[i] = 0.01F;
      }

      return new ConvolveOp(new Kernel(10, 10, brightKernel));
   }
}

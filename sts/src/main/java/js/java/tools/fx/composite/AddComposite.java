package js.java.tools.fx.composite;

import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;

public final class AddComposite extends RGBComposite {
   public AddComposite(float alpha) {
      super(alpha);
   }

   public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
      return new AddComposite.Context(this.extraAlpha, srcColorModel, dstColorModel);
   }

   static class Context extends RGBComposite.RGBCompositeContext {
      Context(float alpha, ColorModel srcColorModel, ColorModel dstColorModel) {
         super(alpha, srcColorModel, dstColorModel);
      }

      @Override
      public void composeRGB(int[] src, int[] dst, float alpha) {
         int w = src.length;

         for (int i = 0; i < w; i += 4) {
            int sr = src[i];
            int dir = dst[i];
            int sg = src[i + 1];
            int dig = dst[i + 1];
            int sb = src[i + 2];
            int dib = dst[i + 2];
            int sa = src[i + 3];
            int dia = dst[i + 3];
            int dor = dir + sr;
            if (dor > 255) {
               dor = 255;
            }

            int dog = dig + sg;
            if (dog > 255) {
               dog = 255;
            }

            int dob = dib + sb;
            if (dob > 255) {
               dob = 255;
            }

            float a = alpha * (float)sa / 255.0F;
            float ac = 1.0F - a;
            dst[i] = (int)(a * (float)dor + ac * (float)dir);
            dst[i + 1] = (int)(a * (float)dog + ac * (float)dig);
            dst[i + 2] = (int)(a * (float)dob + ac * (float)dib);
            dst[i + 3] = (int)((float)sa * alpha + (float)dia * ac);
         }
      }
   }
}

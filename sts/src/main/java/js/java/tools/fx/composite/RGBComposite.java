package js.java.tools.fx.composite;

import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public abstract class RGBComposite implements Composite {
   protected float extraAlpha;

   public RGBComposite() {
      this(1.0F);
   }

   public RGBComposite(float alpha) {
      super();
      if (!(alpha < 0.0F) && !(alpha > 1.0F)) {
         this.extraAlpha = alpha;
      } else {
         throw new IllegalArgumentException("RGBComposite: alpha must be between 0 and 1");
      }
   }

   public float getAlpha() {
      return this.extraAlpha;
   }

   public int hashCode() {
      return Float.floatToIntBits(this.extraAlpha);
   }

   public boolean equals(Object o) {
      if (!(o instanceof RGBComposite)) {
         return false;
      } else {
         RGBComposite c = (RGBComposite)o;
         return this.extraAlpha == c.extraAlpha;
      }
   }

   public abstract static class RGBCompositeContext implements CompositeContext {
      private float alpha;
      private ColorModel srcColorModel;
      private ColorModel dstColorModel;

      public RGBCompositeContext(float alpha, ColorModel srcColorModel, ColorModel dstColorModel) {
         super();
         this.alpha = alpha;
         this.srcColorModel = srcColorModel;
         this.dstColorModel = dstColorModel;
      }

      public void dispose() {
      }

      static int multiply255(int a, int b) {
         int t = a * b + 128;
         return (t >> 8) + t >> 8;
      }

      static int clamp(int a) {
         return a < 0 ? 0 : (a > 255 ? 255 : a);
      }

      public abstract void composeRGB(int[] var1, int[] var2, float var3);

      public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
         float alpha = this.alpha;
         int[] srcPix = null;
         int[] dstPix = null;
         int x = dstOut.getMinX();
         int w = dstOut.getWidth();
         int y0 = dstOut.getMinY();
         int y1 = y0 + dstOut.getHeight();

         for(int y = y0; y < y1; ++y) {
            srcPix = src.getPixels(x, y, w, 1, srcPix);
            dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
            this.composeRGB(srcPix, dstPix, alpha);
            dstOut.setPixels(x, y, w, 1, dstPix);
         }
      }
   }
}

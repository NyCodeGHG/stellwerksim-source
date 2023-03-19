package js.java.tools.fx;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public abstract class PointFilter extends AbstractBufferedImageOp {
   protected boolean canFilterIndexColorModel = false;

   public PointFilter() {
      super();
   }

   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
      int width = src.getWidth();
      int height = src.getHeight();
      int type = src.getType();
      WritableRaster srcRaster = src.getRaster();
      if (dst == null) {
         dst = this.createCompatibleDestImage(src, null);
      }

      WritableRaster dstRaster = dst.getRaster();
      this.setDimensions(width, height);
      int[] inPixels = new int[width];

      for(int y = 0; y < height; ++y) {
         if (type == 2) {
            srcRaster.getDataElements(0, y, width, 1, inPixels);

            for(int x = 0; x < width; ++x) {
               inPixels[x] = this.filterRGB(x, y, inPixels[x]);
            }

            dstRaster.setDataElements(0, y, width, 1, inPixels);
         } else {
            src.getRGB(0, y, width, 1, inPixels, 0, width);

            for(int x = 0; x < width; ++x) {
               inPixels[x] = this.filterRGB(x, y, inPixels[x]);
            }

            dst.setRGB(0, y, width, 1, inPixels, 0, width);
         }
      }

      return dst;
   }

   public void setDimensions(int width, int height) {
   }

   public abstract int filterRGB(int var1, int var2, int var3);
}

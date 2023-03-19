package js.java.tools.fx;

import java.awt.image.BufferedImage;

public class DisplaceFilter extends TransformFilter {
   private float amount = 1.0F;
   private BufferedImage displacementMap = null;
   private int[] xmap;
   private int[] ymap;
   private int dw;
   private int dh;

   public DisplaceFilter() {
      super();
   }

   public void setDisplacementMap(BufferedImage displacementMap) {
      this.displacementMap = displacementMap;
   }

   public BufferedImage getDisplacementMap() {
      return this.displacementMap;
   }

   public void setAmount(float amount) {
      this.amount = amount;
   }

   public float getAmount() {
      return this.amount;
   }

   @Override
   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
      int w = src.getWidth();
      int h = src.getHeight();
      BufferedImage dm = this.displacementMap != null ? this.displacementMap : src;
      this.dw = dm.getWidth();
      this.dh = dm.getHeight();
      int[] mapPixels = new int[this.dw * this.dh];
      this.getRGB(dm, 0, 0, this.dw, this.dh, mapPixels);
      this.xmap = new int[this.dw * this.dh];
      this.ymap = new int[this.dw * this.dh];
      int i = 0;

      for(int y = 0; y < this.dh; ++y) {
         for(int x = 0; x < this.dw; ++x) {
            int rgb = mapPixels[i];
            int r = rgb >> 16 & 0xFF;
            int g = rgb >> 8 & 0xFF;
            int b = rgb & 0xFF;
            mapPixels[i] = (r + g + b) / 8;
            ++i;
         }
      }

      i = 0;

      for(int y = 0; y < this.dh; ++y) {
         int j1 = (y + this.dh - 1) % this.dh * this.dw;
         int j2 = y * this.dw;
         int j3 = (y + 1) % this.dh * this.dw;

         for(int x = 0; x < this.dw; ++x) {
            int k1 = (x + this.dw - 1) % this.dw;
            int k3 = (x + 1) % this.dw;
            this.xmap[i] = mapPixels[k1 + j1] + mapPixels[k1 + j2] + mapPixels[k1 + j3] - mapPixels[k3 + j1] - mapPixels[k3 + j2] - mapPixels[k3 + j3];
            this.ymap[i] = mapPixels[k1 + j3] + mapPixels[x + j3] + mapPixels[k3 + j3] - mapPixels[k1 + j1] - mapPixels[x + j1] - mapPixels[k3 + j1];
            ++i;
         }
      }

      int[] var17 = null;
      dst = super.filter(src, dst);
      this.xmap = this.ymap = null;
      return dst;
   }

   @Override
   protected void transformInverse(int x, int y, float[] out) {
      float nx = (float)x;
      float ny = (float)y;
      int i = y % this.dh * this.dw + x % this.dw;
      out[0] = (float)x + this.amount * (float)this.xmap[i];
      out[1] = (float)y + this.amount * (float)this.ymap[i];
   }

   public String toString() {
      return "Distort/Displace...";
   }
}

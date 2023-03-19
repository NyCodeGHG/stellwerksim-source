package js.java.tools.fx;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.image.BufferedImage;

public class MotionBlurOp extends AbstractBufferedImageOp {
   private float centreX = 0.5F;
   private float centreY = 0.5F;
   private float distance;
   private float angle;
   private float rotation;
   private float zoom;

   public MotionBlurOp() {
      super();
   }

   public MotionBlurOp(float distance, float angle, float rotation, float zoom) {
      super();
      this.distance = distance;
      this.angle = angle;
      this.rotation = rotation;
      this.zoom = zoom;
   }

   public void setAngle(float angle) {
      this.angle = angle;
   }

   public float getAngle() {
      return this.angle;
   }

   public void setDistance(float distance) {
      this.distance = distance;
   }

   public float getDistance() {
      return this.distance;
   }

   public void setRotation(float rotation) {
      this.rotation = rotation;
   }

   public float getRotation() {
      return this.rotation;
   }

   public void setZoom(float zoom) {
      this.zoom = zoom;
   }

   public float getZoom() {
      return this.zoom;
   }

   public void setCentreX(float centreX) {
      this.centreX = centreX;
   }

   public float getCentreX() {
      return this.centreX;
   }

   public void setCentreY(float centreY) {
      this.centreY = centreY;
   }

   public float getCentreY() {
      return this.centreY;
   }

   public void setCentre(Point2D centre) {
      this.centreX = (float)centre.getX();
      this.centreY = (float)centre.getY();
   }

   public Point2D getCentre() {
      return new Float(this.centreX, this.centreY);
   }

   private int log2(int n) {
      int m = 1;

      int log2n;
      for(log2n = 0; m < n; ++log2n) {
         m *= 2;
      }

      return log2n;
   }

   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
      if (dst == null) {
         dst = this.createCompatibleDestImage(src, null);
      }

      BufferedImage tsrc = src;
      float cx = (float)src.getWidth() * this.centreX;
      float cy = (float)src.getHeight() * this.centreY;
      float imageRadius = (float)Math.sqrt((double)(cx * cx + cy * cy));
      float translateX = (float)((double)this.distance * Math.cos((double)this.angle));
      float translateY = (float)((double)this.distance * -Math.sin((double)this.angle));
      float scale = this.zoom;
      float rotate = this.rotation;
      float maxDistance = this.distance + Math.abs(this.rotation * imageRadius) + this.zoom * imageRadius;
      int steps = this.log2((int)maxDistance);
      translateX /= maxDistance;
      translateY /= maxDistance;
      scale /= maxDistance;
      rotate /= maxDistance;
      if (steps == 0) {
         Graphics2D g = dst.createGraphics();
         g.drawRenderedImage(src, null);
         g.dispose();
         return dst;
      } else {
         BufferedImage tmp = this.createCompatibleDestImage(src, null);

         for(int i = 0; i < steps; ++i) {
            Graphics2D g = tmp.createGraphics();
            g.drawImage(tsrc, null, null);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setComposite(AlphaComposite.getInstance(3, 0.5F));
            g.translate((double)(cx + translateX), (double)(cy + translateY));
            g.scale(1.0001 + (double)scale, 1.0001 + (double)scale);
            if (this.rotation != 0.0F) {
               g.rotate((double)rotate);
            }

            g.translate((double)(-cx), (double)(-cy));
            g.drawImage(dst, null, null);
            g.dispose();
            BufferedImage ti = dst;
            dst = tmp;
            tmp = ti;
            tsrc = dst;
            translateX *= 2.0F;
            translateY *= 2.0F;
            scale *= 2.0F;
            rotate *= 2.0F;
         }

         return dst;
      }
   }

   public String toString() {
      return "Blur/Faster Motion Blur...";
   }
}

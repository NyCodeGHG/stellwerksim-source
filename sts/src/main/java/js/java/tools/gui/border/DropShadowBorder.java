package js.java.tools.gui.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.RoundRectangle2D.Double;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.HashMap;
import java.util.Map;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class DropShadowBorder implements Border {
   private static final Map<Integer, Map<DropShadowBorder.Position, BufferedImage>> CACHE = new HashMap();
   private Color lineColor;
   private int lineWidth;
   private int shadowSize;
   private float shadowOpacity;
   private int cornerSize;
   private boolean showTopShadow;
   private boolean showLeftShadow;
   private boolean showBottomShadow;
   private boolean showRightShadow;

   public void setShowTopShadow(boolean showTopShadow) {
      this.showTopShadow = showTopShadow;
   }

   public void setShowLeftShadow(boolean showLeftShadow) {
      this.showLeftShadow = showLeftShadow;
   }

   public void setShowBottomShadow(boolean showBottomShadow) {
      this.showBottomShadow = showBottomShadow;
   }

   public void setShowRightShadow(boolean showRightShadow) {
      this.showRightShadow = showRightShadow;
   }

   public DropShadowBorder() {
      this(UIManager.getColor("Control"), 1, 5);
   }

   public DropShadowBorder(boolean showTopShadow, boolean showLeftShadow, boolean showBottomShadow, boolean showRightShadow) {
      this(UIManager.getColor("Control"), 1, 5, 0.5F, 12, showTopShadow, showLeftShadow, showBottomShadow, showRightShadow);
   }

   public DropShadowBorder(Color lineColor, int lineWidth, int shadowSize) {
      this(lineColor, lineWidth, shadowSize, 0.5F, 12, false, false, true, true);
   }

   public DropShadowBorder(Color lineColor, int lineWidth, boolean showLeftShadow) {
      this(lineColor, lineWidth, 5, 0.5F, 12, false, showLeftShadow, true, true);
   }

   public DropShadowBorder(
      Color lineColor,
      int lineWidth,
      int shadowSize,
      float shadowOpacity,
      int cornerSize,
      boolean showTopShadow,
      boolean showLeftShadow,
      boolean showBottomShadow,
      boolean showRightShadow
   ) {
      super();
      this.lineColor = lineColor;
      this.lineWidth = lineWidth;
      this.shadowSize = shadowSize;
      this.shadowOpacity = shadowOpacity;
      this.cornerSize = cornerSize;
      this.showTopShadow = showTopShadow;
      this.showLeftShadow = showLeftShadow;
      this.showBottomShadow = showBottomShadow;
      this.showRightShadow = showRightShadow;
   }

   public void paintBorder(Component c, Graphics graphics, int x, int y, int width, int height) {
      Map<DropShadowBorder.Position, BufferedImage> images = this.getImages(null);
      Graphics2D g2 = (Graphics2D)graphics;
      g2.setColor(this.lineColor);
      Point topLeftShadowPoint = null;
      if (this.showLeftShadow || this.showTopShadow) {
         topLeftShadowPoint = new Point();
         if (this.showLeftShadow && !this.showTopShadow) {
            topLeftShadowPoint.setLocation(x, y + this.shadowSize);
         } else if (this.showLeftShadow && this.showTopShadow) {
            topLeftShadowPoint.setLocation(x, y);
         } else if (!this.showLeftShadow && this.showTopShadow) {
            topLeftShadowPoint.setLocation(x + this.shadowSize, y);
         }
      }

      Point bottomLeftShadowPoint = null;
      if (this.showLeftShadow || this.showBottomShadow) {
         bottomLeftShadowPoint = new Point();
         if (this.showLeftShadow && !this.showBottomShadow) {
            bottomLeftShadowPoint.setLocation(x, y + height - this.shadowSize - this.shadowSize);
         } else if (this.showLeftShadow && this.showBottomShadow) {
            bottomLeftShadowPoint.setLocation(x, y + height - this.shadowSize);
         } else if (!this.showLeftShadow && this.showBottomShadow) {
            bottomLeftShadowPoint.setLocation(x + this.shadowSize, y + height - this.shadowSize);
         }
      }

      Point bottomRightShadowPoint = null;
      if (this.showRightShadow || this.showBottomShadow) {
         bottomRightShadowPoint = new Point();
         if (this.showRightShadow && !this.showBottomShadow) {
            bottomRightShadowPoint.setLocation(x + width - this.shadowSize, y + height - this.shadowSize - this.shadowSize);
         } else if (this.showRightShadow && this.showBottomShadow) {
            bottomRightShadowPoint.setLocation(x + width - this.shadowSize, y + height - this.shadowSize);
         } else if (!this.showRightShadow && this.showBottomShadow) {
            bottomRightShadowPoint.setLocation(x + width - this.shadowSize - this.shadowSize, y + height - this.shadowSize);
         }
      }

      Point topRightShadowPoint = null;
      if (this.showRightShadow || this.showTopShadow) {
         topRightShadowPoint = new Point();
         if (this.showRightShadow && !this.showTopShadow) {
            topRightShadowPoint.setLocation(x + width - this.shadowSize, y + this.shadowSize);
         } else if (this.showRightShadow && this.showTopShadow) {
            topRightShadowPoint.setLocation(x + width - this.shadowSize, y);
         } else if (!this.showRightShadow && this.showTopShadow) {
            topRightShadowPoint.setLocation(x + width - this.shadowSize - this.shadowSize, y);
         }
      }

      if (this.showLeftShadow) {
         Rectangle leftShadowRect = new Rectangle(
            x,
            (int)(topLeftShadowPoint.getY() + (double)this.shadowSize),
            this.shadowSize,
            (int)(bottomLeftShadowPoint.getY() - topLeftShadowPoint.getY() - (double)this.shadowSize)
         );

         try {
            g2.drawImage(
               ((BufferedImage)images.get(DropShadowBorder.Position.LEFT)).getScaledInstance(leftShadowRect.width, leftShadowRect.height, 2),
               leftShadowRect.x,
               leftShadowRect.y,
               null
            );
         } catch (IllegalArgumentException var18) {
         }
      }

      if (this.showBottomShadow) {
         Rectangle bottomShadowRect = new Rectangle(
            (int)(bottomLeftShadowPoint.getX() + (double)this.shadowSize),
            y + height - this.shadowSize,
            (int)(bottomRightShadowPoint.getX() - bottomLeftShadowPoint.getX() - (double)this.shadowSize),
            this.shadowSize
         );

         try {
            g2.drawImage(
               ((BufferedImage)images.get(DropShadowBorder.Position.BOTTOM)).getScaledInstance(bottomShadowRect.width, bottomShadowRect.height, 2),
               bottomShadowRect.x,
               bottomShadowRect.y,
               null
            );
         } catch (IllegalArgumentException var17) {
         }
      }

      if (this.showRightShadow) {
         Rectangle rightShadowRect = new Rectangle(
            x + width - this.shadowSize,
            (int)(topRightShadowPoint.getY() + (double)this.shadowSize),
            this.shadowSize,
            (int)(bottomRightShadowPoint.getY() - topRightShadowPoint.getY() - (double)this.shadowSize)
         );

         try {
            g2.drawImage(
               ((BufferedImage)images.get(DropShadowBorder.Position.RIGHT)).getScaledInstance(rightShadowRect.width, rightShadowRect.height, 2),
               rightShadowRect.x,
               rightShadowRect.y,
               null
            );
         } catch (IllegalArgumentException var16) {
         }
      }

      if (this.showTopShadow) {
         Rectangle topShadowRect = new Rectangle(
            (int)topLeftShadowPoint.getX() + this.shadowSize,
            y,
            (int)(topRightShadowPoint.getX() - topLeftShadowPoint.getX() - (double)this.shadowSize),
            this.shadowSize
         );

         try {
            g2.drawImage(
               ((BufferedImage)images.get(DropShadowBorder.Position.TOP)).getScaledInstance(topShadowRect.width, topShadowRect.height, 2),
               topShadowRect.x,
               topShadowRect.y,
               null
            );
         } catch (IllegalArgumentException var15) {
         }
      }

      if (this.showLeftShadow || this.showTopShadow) {
         g2.drawImage((BufferedImage)images.get(DropShadowBorder.Position.TOP_LEFT), null, (int)topLeftShadowPoint.getX(), (int)topLeftShadowPoint.getY());
      }

      if (this.showLeftShadow || this.showBottomShadow) {
         g2.drawImage(
            (BufferedImage)images.get(DropShadowBorder.Position.BOTTOM_LEFT), null, (int)bottomLeftShadowPoint.getX(), (int)bottomLeftShadowPoint.getY()
         );
      }

      if (this.showRightShadow || this.showBottomShadow) {
         g2.drawImage(
            (BufferedImage)images.get(DropShadowBorder.Position.BOTTOM_RIGHT), null, (int)bottomRightShadowPoint.getX(), (int)bottomRightShadowPoint.getY()
         );
      }

      if (this.showRightShadow || this.showTopShadow) {
         g2.drawImage((BufferedImage)images.get(DropShadowBorder.Position.TOP_RIGHT), null, (int)topRightShadowPoint.getX(), (int)topRightShadowPoint.getY());
      }
   }

   private Map<DropShadowBorder.Position, BufferedImage> getImages(Graphics2D g2) {
      Map<DropShadowBorder.Position, BufferedImage> images = (Map)CACHE.get(this.shadowSize);
      if (images == null) {
         images = new HashMap();
         int rectWidth = this.cornerSize + 1;
         RoundRectangle2D rect = new Double(0.0, 0.0, (double)rectWidth, (double)rectWidth, (double)this.cornerSize, (double)this.cornerSize);
         int imageWidth = rectWidth + this.shadowSize * 2;
         BufferedImage image = new BufferedImage(imageWidth, imageWidth, 2);
         Graphics2D buffer = (Graphics2D)image.getGraphics();
         buffer.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
         buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         buffer.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
         buffer.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
         buffer.setColor(new Color(0.0F, 0.0F, 0.0F, this.shadowOpacity));
         buffer.translate(this.shadowSize, this.shadowSize);
         buffer.fill(rect);
         float blurry = 1.0F / (float)(this.shadowSize * this.shadowSize);
         float[] blurKernel = new float[this.shadowSize * this.shadowSize];

         for(int i = 0; i < blurKernel.length; ++i) {
            blurKernel[i] = blurry;
         }

         ConvolveOp blur = new ConvolveOp(new Kernel(this.shadowSize, this.shadowSize, blurKernel));
         BufferedImage targetImage = new BufferedImage(imageWidth, imageWidth, 2);
         ((Graphics2D)targetImage.getGraphics()).drawImage(image, blur, -(this.shadowSize / 2), -(this.shadowSize / 2));
         int x = 1;
         int y = 1;
         int w = this.shadowSize;
         int h = this.shadowSize;
         images.put(DropShadowBorder.Position.TOP_LEFT, targetImage.getSubimage(x, y, w, h));
         int var17 = 1;
         w = this.shadowSize;
         int var32 = 1;
         images.put(DropShadowBorder.Position.LEFT, targetImage.getSubimage(var17, h, w, var32));
         var17 = (byte)1;
         w = this.shadowSize;
         h = this.shadowSize;
         images.put(DropShadowBorder.Position.BOTTOM_LEFT, targetImage.getSubimage(var17, rectWidth, w, h));
         var17 = this.cornerSize + 1;
         int var27 = 1;
         h = this.shadowSize;
         images.put(DropShadowBorder.Position.BOTTOM, targetImage.getSubimage(var17, rectWidth, var27, h));
         var27 = this.shadowSize;
         h = this.shadowSize;
         images.put(DropShadowBorder.Position.BOTTOM_RIGHT, targetImage.getSubimage(rectWidth, rectWidth, var27, h));
         y = this.cornerSize + 1;
         var27 = this.shadowSize;
         int var36 = 1;
         images.put(DropShadowBorder.Position.RIGHT, targetImage.getSubimage(rectWidth, y, var27, var36));
         int var23 = 1;
         var27 = this.shadowSize;
         var36 = this.shadowSize;
         images.put(DropShadowBorder.Position.TOP_RIGHT, targetImage.getSubimage(rectWidth, var23, var27, var36));
         var17 = this.shadowSize;
         var23 = 1;
         int var31 = 1;
         var36 = this.shadowSize;
         images.put(DropShadowBorder.Position.TOP, targetImage.getSubimage(var17, var23, var31, var36));
         buffer.dispose();
         image.flush();
      }

      return images;
   }

   public Insets getBorderInsets(Component c) {
      int top = this.showTopShadow ? this.lineWidth + this.shadowSize : this.lineWidth;
      int left = this.showLeftShadow ? this.lineWidth + this.shadowSize : this.lineWidth;
      int bottom = this.showBottomShadow ? this.lineWidth + this.shadowSize : this.lineWidth;
      int right = this.showRightShadow ? this.lineWidth + this.shadowSize : this.lineWidth;
      return new Insets(top, left, bottom, right);
   }

   public boolean isBorderOpaque() {
      return true;
   }

   public boolean isShowTopShadow() {
      return this.showTopShadow;
   }

   public boolean isShowLeftShadow() {
      return this.showLeftShadow;
   }

   public boolean isShowRightShadow() {
      return this.showRightShadow;
   }

   public boolean isShowBottomShadow() {
      return this.showBottomShadow;
   }

   public int getLineWidth() {
      return this.lineWidth;
   }

   public Color getLineColor() {
      return this.lineColor;
   }

   public int getShadowSize() {
      return this.shadowSize;
   }

   public float getShadowOpacity() {
      return this.shadowOpacity;
   }

   public int getCornerSize() {
      return this.cornerSize;
   }

   private static enum Position {
      TOP,
      TOP_LEFT,
      LEFT,
      BOTTOM_LEFT,
      BOTTOM,
      BOTTOM_RIGHT,
      RIGHT,
      TOP_RIGHT;
   }
}

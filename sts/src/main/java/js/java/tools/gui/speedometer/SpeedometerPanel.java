package js.java.tools.gui.speedometer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Arc2D.Double;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SpeedometerPanel extends JPanel {
   public static final int MODE_LEFT0 = 0;
   public static final int MODE_CENTER0 = 1;
   private final Font font;
   private final Font textfont;
   private final int PAD;
   private double[] phi;
   private double[] gotophi;
   private double maxvalue = 1.0;
   private int setmaxvalue = 1;
   private final int mode;
   private double[] currentvalue;
   private double[] speed;
   private Color shadowColor = new Color(0, 0, 0, 51);
   private Color shadowColorTransparent = new Color(0, 0, 0, 17);
   private Color[] needleColor;
   private Color[] needleColorTransparent;
   private String[] text = null;
   private NeedlePainterBase[] needlePainter = null;
   private boolean paintLabels = true;
   private boolean autoMax = false;
   private boolean forceNewBackground = false;
   private static final int DELAY = 50;
   private final Timer tm = new Timer(50, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         SpeedometerPanel.this.move();
      }
   });
   private Dimension mindim = new Dimension(90, 60);
   private BufferedImage image = null;

   public SpeedometerPanel(int mode, int needlecount) {
      this.font = new Font("Sans-Serif", 0, 10);
      this.textfont = new Font("Sans-Serif", 1, 12);
      this.PAD = 5;
      this.tm.setCoalesce(false);
      this.phi = new double[needlecount];
      this.gotophi = new double[needlecount];
      this.speed = new double[needlecount];
      this.needleColor = new Color[needlecount];
      this.needleColorTransparent = new Color[needlecount];
      this.currentvalue = new double[needlecount];
      this.needlePainter = new NeedlePainterBase[needlecount];

      for (int i = 0; i < this.phi.length; i++) {
         this.phi[i] = 0.0;
         this.gotophi[i] = 0.0;
         this.speed[i] = 1.0;
         this.currentvalue[i] = 0.0;
         this.needlePainter[i] = new ClassicNeedlePainter();
         switch (i) {
            case 0:
            default:
               this.needleColor[i] = new Color(0, 0, 221);
               break;
            case 1:
               this.needleColor[i] = new Color(221, 0, 0);
               break;
            case 2:
               this.needleColor[i] = new Color(0, 221, 0);
               break;
            case 3:
               this.needleColor[i] = new Color(221, 221, 0);
               break;
            case 4:
               this.needleColor[i] = new Color(221, 0, 221);
               break;
            case 5:
               this.needleColor[i] = new Color(0, 221, 255);
         }

         this.needleColorTransparent[i] = new Color(this.needleColor[i].getRed(), this.needleColor[i].getGreen(), this.needleColor[i].getBlue(), 68);
      }

      if (mode == 1) {
         this.mode = 1;
      } else {
         this.mode = 0;
      }

      for (int i = 0; i < this.phi.length; i++) {
         this.setValue(i, 0.0);
         this.phi[i] = this.gotophi[i];
      }

      this.setMaxValue(1);
      this.setBackground(Color.GRAY);
   }

   public SpeedometerPanel(int mode) {
      this(mode, 1);
   }

   public void setMaxValue(int m) {
      if (m < 10) {
         m = 10;
      }

      this.maxvalue = (double)m;
      this.setmaxvalue = m;

      for (int i = 0; i < this.phi.length; i++) {
         this.setValue(i, this.currentvalue[i]);
      }

      this.repaint();
   }

   public int getMaxValue() {
      return (int)this.maxvalue;
   }

   int getMode() {
      return this.mode;
   }

   public void setText(String t) {
      this.text = t.split("\n");
      this.repaint();
   }

   public void setPaintLabels(boolean p) {
      this.paintLabels = p;
      this.repaint();
   }

   boolean isPaintLabels() {
      return this.paintLabels;
   }

   public void setAutoMax(boolean p) {
      this.autoMax = p;
   }

   private void paintImage() {
      Graphics2D g2 = this.image.createGraphics();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      int w = this.image.getWidth();
      int h = this.image.getHeight();
      g2.setColor(this.getBackground());
      g2.fillRect(0, 0, w, h);
      double dia = (double)w;
      if (dia / 2.0 + 20.0 > (double)h) {
         dia = (double)(h * 2 - h / 4);
      }

      double x = ((double)w - dia) / 2.0;
      double y = ((double)h - dia) / 2.0 + dia / 4.0;
      double start = 0.0;
      double extent = 180.0;
      Shape arc = new Double(x, y, dia, dia, start, extent, 0);
      g2.setColor(Color.WHITE);
      g2.fillRect((int)x, (int)(y + dia / 2.0) - 2, (int)dia, 20);
      g2.setColor(Color.BLACK);
      g2.drawRect((int)x, (int)(y + dia / 2.0) - 2, (int)dia, 20);
      g2.setColor(Color.WHITE);
      g2.fill(arc);
      g2.setColor(Color.BLACK);
      g2.draw(arc);
      double theta = 0.0;
      double x0 = x + dia / 2.0;
      double y0 = y + dia / 2.0;
      double r = dia / 2.0;
      g2.setColor(Color.BLACK);

      for (int i = 0; i < 11 * (this.mode + 1) - this.mode; i++) {
         theta = (double)i * (extent / (10.0 * (double)(this.mode + 1))) + 180.0;
         double x1 = x0 + (r - 4.0) * Math.cos(Math.toRadians(theta));
         double y1 = y0 + (r - 4.0) * Math.sin(Math.toRadians(theta));
         double x2 = x0 + r * Math.cos(Math.toRadians(theta));
         double y2 = y0 + r * Math.sin(Math.toRadians(theta));
         Shape tick = new java.awt.geom.Line2D.Double(x1, y1, x2, y2);
         g2.draw(tick);
      }

      if (this.paintLabels) {
         g2.setFont(this.font);
         FontRenderContext frc = g2.getFontRenderContext();

         for (int i = 0; i < 11 * (this.mode + 1) - this.mode; i++) {
            int v;
            if (this.mode == 1) {
               v = i - 10;
               v = (int)Math.round(this.maxvalue * (double)v / 10.0);
            } else {
               v = (int)Math.round(this.maxvalue * (double)i / 10.0);
            }

            String s = String.valueOf(v);
            theta = (double)i * (extent / (10.0 * (double)(this.mode + 1))) + 180.0;
            float width = (float)this.font.getStringBounds(s, frc).getWidth();
            LineMetrics lm = this.font.getLineMetrics(s, frc);
            float height = lm.getAscent();
            double cos = Math.cos(Math.toRadians(theta));
            double sin = Math.sin(Math.toRadians(theta));
            double diag = Math.sqrt((double)(width * width + height * height)) / 2.0;
            double cx = x0 + (r - (double)this.PAD) * cos - (double)width * cos / 2.0;
            double cy = y0 + (r - (double)this.PAD) * sin - (double)height * sin / 2.0;
            float sx = (float)cx - width / 2.0F;
            float sy = (float)cy + height / 2.0F;
            Graphics2D g3 = (Graphics2D)g2.create();
            g3.translate(cx, cy);
            g3.rotate(Math.toRadians(theta + 90.0));
            if (v >= 0) {
               g3.setColor(Color.BLACK);
            } else {
               g3.setColor(Color.RED);
            }

            g3.drawString(s, -width / 2.0F, height);
            g3.dispose();
         }
      }

      if (this.text != null && this.text.length > 0) {
         g2.setFont(this.textfont);
         FontRenderContext frc = g2.getFontRenderContext();
         int ty = 0;

         for (String t : this.text) {
            float width = (float)this.textfont.getStringBounds(t, frc).getWidth();
            float sx = (float)x0 - width / 2.0F;
            float sy = (float)(h - h / 3 + ty * 14);
            g2.setPaint(this.needleColor[ty].darker());
            g2.drawString(t, sx, sy);
            ty++;
         }
      }

      g2.dispose();
   }

   protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (this.forceNewBackground || this.image != null && (this.image.getWidth() != this.getWidth() || this.image.getHeight() != this.getHeight())) {
         this.image = null;
      }

      if (this.image == null) {
         this.image = new BufferedImage(this.getWidth(), this.getHeight(), 1);
         this.paintImage();
      }

      Graphics2D g2 = (Graphics2D)g;
      g2.drawImage(this.image, 0, 0, null);
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      int w = this.getWidth();
      int h = this.getHeight();
      double dia = (double)w;
      if (dia / 2.0 + 20.0 > (double)h) {
         dia = (double)(h * 2 - h / 4);
      }

      double x = ((double)w - dia) / 2.0;
      double y = ((double)h - dia) / 2.0 + dia / 4.0;
      double x0 = x + dia / 2.0;
      double y0 = y + dia / 2.0;
      double r = dia / 2.0;
      Shape[] needle = new Shape[this.phi.length];
      Graphics2D g3 = (Graphics2D)g2.create();
      g3.setColor(this.shadowColor);
      g3.translate(3, 3);

      for (int i = 0; i < this.phi.length; i++) {
         if (this.phi[i] > 0.0 && this.phi[i] < 180.0) {
            this.phi[i] = 360.0 - this.phi[i];
         }

         try {
            needle[i] = this.needlePainter[i].paint(this, x0, y0, r, this.phi[i]);
            if (this.needlePainter[i].shouldPaintTransparent()) {
               g3.setColor(this.shadowColorTransparent);
            } else {
               g3.setColor(this.shadowColor);
            }

            g3.fill(needle[i]);
         } catch (Exception var22) {
            var22.printStackTrace();
         }
      }

      g3.dispose();

      for (int i = this.phi.length - 1; i >= 0; i--) {
         try {
            if (this.needlePainter[i].shouldPaintTransparent()) {
               g2.setPaint(this.needleColorTransparent[i]);
            } else {
               g2.setPaint(this.needleColor[i]);
            }

            g2.fill(needle[i]);
         } catch (Exception var21) {
            var21.printStackTrace();
         }
      }
   }

   private void move() {
      boolean shouldRepaint = false;
      boolean anyRunning = false;

      for (int i = 0; i < this.phi.length; i++) {
         if (this.gotophi[i] > this.phi[i]) {
            this.phi[i] = this.phi[i] + this.speed[i];
            if (this.speed[i] < 0.8) {
               this.speed[i] = this.speed[i] + 0.1;
            }

            shouldRepaint = true;
         } else if (this.gotophi[i] < this.phi[i]) {
            this.phi[i] = this.phi[i] - this.speed[i];
            if (this.speed[i] < 0.8) {
               this.speed[i] = this.speed[i] + 0.1;
            }

            shouldRepaint = true;
         }

         if (Math.abs(this.gotophi[i] - this.phi[i]) <= 0.1) {
            this.phi[i] = this.gotophi[i];
         } else {
            anyRunning = true;
         }

         if (Math.abs(this.gotophi[i] - this.phi[i]) < 6.0) {
            this.speed[i] = this.speed[i] - 0.2;
            if (this.speed[i] <= 0.0) {
               this.speed[i] = 0.1;
            }
         }
      }

      if (shouldRepaint) {
         this.repaint();
      }

      if (!anyRunning) {
         this.tm.stop();
         if (this.mode == 1 && this.proveMax()) {
            this.doRecalc();
         }
      }
   }

   public Dimension getMinimumSize() {
      return this.mindim;
   }

   public Dimension getPreferredSize() {
      return this.mindim;
   }

   public void setPreferredSize(Dimension d) {
      this.mindim = d;
   }

   public void setNeedlePainter(int needlenumber, NeedlePainterBase p) {
      this.needlePainter[needlenumber] = p;
   }

   public void setValue(double v) {
      this.setValue(0, v);
   }

   public double getValue() {
      return this.getValue(0);
   }

   public double getValue(int needlenumber) {
      return this.currentvalue[needlenumber];
   }

   public void setValue(int needlenumber, double v) {
      boolean needAllRecalc = false;
      if (v > this.maxvalue) {
         if (this.autoMax) {
            this.maxvalue = v + 2.0;
            needAllRecalc = true;
            this.forceNewBackground = true;
         } else {
            v = this.maxvalue;
         }
      }

      if (v < -this.maxvalue && this.mode == 1) {
         if (this.autoMax) {
            this.maxvalue = -v + 2.0;
            needAllRecalc = true;
            this.forceNewBackground = true;
         } else {
            v = -this.maxvalue;
         }
      } else if (v < 0.0 && this.mode == 0) {
         v = 0.0;
      }

      this.currentvalue[needlenumber] = v;
      if (this.mode == 0) {
         this.gotophi[needlenumber] = 180.0 * v / this.maxvalue + 180.0;
      } else {
         this.gotophi[needlenumber] = 90.0 * v / this.maxvalue - 90.0;
      }

      if (this.gotophi[needlenumber] != this.phi[needlenumber]) {
         this.tm.start();
      }

      if (this.mode == 0) {
         needAllRecalc |= this.proveMax();
      }

      if (needAllRecalc) {
         this.doRecalc();
      }
   }

   private boolean proveMax() {
      boolean needAllRecalc = false;
      if (this.autoMax && this.maxvalue > (double)this.setmaxvalue) {
         boolean oneMatch = false;

         for (int i = 0; i < this.phi.length; i++) {
            if (this.maxvalue / 2.0 < Math.abs(this.currentvalue[i])) {
               oneMatch = true;
            }
         }

         if (!oneMatch) {
            this.maxvalue = this.maxvalue - this.maxvalue / 4.0;
            needAllRecalc = true;
         }

         if (this.maxvalue < (double)this.setmaxvalue) {
            this.maxvalue = (double)this.setmaxvalue;
         }
      }

      return needAllRecalc;
   }

   private void doRecalc() {
      for (int i = 0; i < this.phi.length; i++) {
         if (this.mode == 0) {
            this.gotophi[i] = 180.0 * this.currentvalue[i] / this.maxvalue + 180.0;
         } else {
            this.gotophi[i] = 90.0 * this.currentvalue[i] / this.maxvalue - 90.0;
         }

         if (this.gotophi[i] != this.phi[i]) {
            this.tm.start();
         }
      }
   }
}

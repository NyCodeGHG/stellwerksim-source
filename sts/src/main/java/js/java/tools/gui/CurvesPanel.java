package js.java.tools.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.CubicCurve2D.Double;
import javax.swing.JPanel;
import javax.swing.Timer;

public class CurvesPanel extends JPanel {
   private RenderingHints hints;
   private int counter = 0;
   private Timer rtimer = new Timer(50, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         CurvesPanel.this.repaint();
      }
   });

   public CurvesPanel() {
      this.hints = new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
      this.hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      this.hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
      this.hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      this.hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      this.startAnim();
   }

   public void startAnim() {
      this.rtimer.start();
   }

   public void stopAnim() {
      this.rtimer.stop();
   }

   public void paintComponent(Graphics g) {
      this.counter++;
      Graphics2D g2 = (Graphics2D)g;
      g2.setRenderingHints(this.hints);
      super.paintComponent(g2);
      float width = (float)this.getWidth();
      float height = (float)this.getHeight();
      g2.translate(0, -30);
      this.drawCurve(g2, 20.0F, -10.0F, 20.0F, -10.0F, width / 2.0F - 40.0F, 10.0F, 0.0F, -5.0F, width / 2.0F + 40.0F, 1.0F, 0.0F, 5.0F, 50.0F, 5.0F, false);
      g2.translate(0, 30);
      g2.translate(0.0, (double)(height - 60.0F));
      this.drawCurve(g2, 30.0F, -15.0F, 50.0F, 15.0F, width / 2.0F - 40.0F, 1.0F, 15.0F, -25.0F, width / 2.0F, 0.5F, 0.0F, 25.0F, 15.0F, 6.0F, false);
      g2.translate(0.0, (double)(-height + 60.0F));
      this.drawCurve(
         g2,
         height - 35.0F,
         -5.0F,
         height - 50.0F,
         10.0F,
         width / 2.0F - 40.0F,
         1.0F,
         height - 35.0F,
         -25.0F,
         width / 2.0F,
         0.5F,
         height - 20.0F,
         25.0F,
         25.0F,
         4.0F,
         true
      );
   }

   private void drawCurve(
      Graphics2D g2,
      float y1,
      float y1_offset,
      float y2,
      float y2_offset,
      float cx1,
      float cx1_offset,
      float cy1,
      float cy1_offset,
      float cx2,
      float cx2_offset,
      float cy2,
      float cy2_offset,
      float thickness,
      float speed,
      boolean invert
   ) {
      float width = (float)this.getWidth();
      float height = (float)this.getHeight();
      double offset = Math.sin((double)this.counter / ((double)speed * Math.PI));
      float start_x = 0.0F;
      float start_y = y1 + (float)(offset * (double)y1_offset);
      float end_y = y2 + (float)(offset * (double)y2_offset);
      float ctrl1_x = (float)offset * cx1_offset + cx1;
      float ctrl1_y = cy1 + (float)(offset * (double)cy1_offset);
      float ctrl2_x = (float)(offset * (double)cx2_offset) + cx2;
      float ctrl2_y = (float)(offset * (double)cy2_offset) + cy2;
      CubicCurve2D curve = new Double(
         (double)start_x, (double)start_y, (double)ctrl1_x, (double)ctrl1_y, (double)ctrl2_x, (double)ctrl2_y, (double)width, (double)end_y
      );
      GeneralPath path = new GeneralPath(curve);
      path.lineTo(width, height);
      path.lineTo(0.0F, height);
      path.closePath();
      Area thickCurve = new Area((Shape)path.clone());
      AffineTransform translation = AffineTransform.getTranslateInstance(0.0, (double)thickness);
      path.transform(translation);
      thickCurve.subtract(new Area(path));
      Color start = new Color(255, 255, 255, 200);
      Color end = new Color(255, 255, 255, 0);
      Rectangle bounds = thickCurve.getBounds();
      GradientPaint painter = new GradientPaint(
         0.0F, (float)curve.getBounds().y, invert ? end : start, 0.0F, (float)(bounds.y + bounds.height), invert ? start : end
      );
      Paint oldPainter = g2.getPaint();
      g2.setPaint(painter);
      g2.fill(thickCurve);
      g2.setPaint(oldPainter);
   }
}

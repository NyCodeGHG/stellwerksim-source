package js.java.isolate.landkarteneditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D.Double;
import java.util.Iterator;
import javax.swing.JComponent;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.gui.GraphicTools;

public class landkarte extends JComponent implements SessionClose {
   public static final int BORDER = 50;
   private knotenList klist = null;
   private Dimension size = new Dimension(1, 1);
   private Point offset = new Point(0, 0);
   private final Color liningModeColor = new Color(255, 102, 102);
   private final Color liningDestinationColor = new Color(255, 255, 102);
   private knoten startk = null;
   private knoten stopk = null;
   private Point calcPosition = null;

   @Override
   public void close() {
      this.startk = null;
      this.stopk = null;
      this.klist = null;
   }

   void setKnotenList(knotenList klist) {
      this.klist = klist;
      this.updateSize();
   }

   public void updateSize() {
      Rectangle r = new Rectangle(0, 0, 10, 10);
      Iterator<knoten> kit = this.klist.knotenIterator();

      while (kit.hasNext()) {
         knoten k = (knoten)kit.next();
         r = r.union(k);
      }

      this.size = new Dimension((int)r.getWidth() + 100, (int)r.getHeight() + 100);
      this.offset = r.getLocation();
      this.offset.translate(-50, -50);
      this.setSize(this.size);
      this.repaint();
   }

   public Dimension getMinimumSize() {
      return this.size;
   }

   public Dimension getPreferredSize() {
      return this.size;
   }

   public Dimension getMaximumSize() {
      return this.size;
   }

   Point getOffset() {
      return this.offset;
   }

   public void paintComponent(Graphics g) {
      g.clearRect(0, 0, this.getWidth(), this.getHeight());
      g.setColor(Color.LIGHT_GRAY);
      g.drawRect(0, 0, this.size.width - this.offset.x, this.size.height - this.offset.y);
      Graphics2D g2 = (Graphics2D)g.create(0, 0, this.size.width - this.offset.x, this.size.height - this.offset.y);
      g2.translate(-this.offset.x, -this.offset.y);
      GraphicTools.enableGfxAA(g2);
      GraphicTools.enableTextAA(g2);
      g2.setColor(Color.LIGHT_GRAY);
      g2.drawLine(-2, 0, 2, 0);
      g2.drawLine(0, -2, 0, 2);
      if (this.klist != null) {
         if (this.startk != null && this.calcPosition != null) {
            int B = 12;
            g2.setColor(this.liningModeColor);
            Double r1 = new Double(
               (double)(this.startk.x - 12), (double)(this.startk.y - 12), (double)(this.startk.width + 24), (double)(this.startk.height + 24)
            );
            g2.fill(r1);
            if (this.stopk != null) {
               g2.setColor(this.liningDestinationColor);
               Double r2 = new Double(
                  (double)(this.stopk.x - 12), (double)(this.stopk.y - 12), (double)(this.stopk.width + 24), (double)(this.stopk.height + 24)
               );
               g2.fill(r2);
               g2.setStroke(new BasicStroke(4.0F));
               g2.setColor(this.liningDestinationColor);
               g2.drawLine((int)this.startk.getCenterX(), (int)this.startk.getCenterY(), (int)this.stopk.getCenterX(), (int)this.stopk.getCenterY());
               g2.setColor(this.liningModeColor);
            }

            float[] dash = new float[]{3.0F, 2.0F};
            g2.setStroke(new BasicStroke(2.0F, 0, 2, 1.0F, dash, 0.0F));
            g2.drawLine((int)this.startk.getCenterX(), (int)this.startk.getCenterY(), this.calcPosition.x, this.calcPosition.y);
            Shape arr = GraphicTools.createArrow(new Point((int)this.startk.getCenterX(), (int)this.startk.getCenterY()), this.calcPosition);
            g2.fill(arr);
            g2.setStroke(new BasicStroke(1.0F));
         }

         Iterator<verbindung> vit = this.klist.verbindungIterator();

         while (vit.hasNext()) {
            verbindung v = (verbindung)vit.next();
            v.paint(g2);
         }

         Iterator<knoten> kit = this.klist.knotenIterator();

         while (kit.hasNext()) {
            knoten k = (knoten)kit.next();
            k.paint(g2);
         }
      }
   }

   void verbinder(knoten startk, Point calcPosition, knoten stopk) {
      this.startk = startk;
      this.stopk = stopk;
      this.calcPosition = calcPosition;
      this.repaint();
   }
}

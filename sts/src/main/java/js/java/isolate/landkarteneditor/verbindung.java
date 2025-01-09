package js.java.isolate.landkarteneditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D.Double;
import js.java.tools.vecmath.Vector2d;

class verbindung extends Double implements Comparable {
   private final control my_main;
   private final knotenList parent;
   private final int kid1;
   private final int kid2;
   private final Color backcolor = new Color(0, 0, 0);
   private final Color selectcolor = new Color(238, 68, 68);
   private int direction = 0;

   verbindung(control main, knotenList p, int kid1, int kid2) {
      this.my_main = main;
      this.parent = p;
      this.kid1 = kid1;
      this.kid2 = kid2;
   }

   void calc() {
      knoten k1 = this.my_main.getKnoten(this.kid1);
      knoten k2 = this.my_main.getKnoten(this.kid2);
      if (k1 != null && k2 != null) {
         this.x1 = k1.getCenterX();
         this.y1 = k1.getCenterY();
         this.x2 = k2.getCenterX();
         this.y2 = k2.getCenterY();
      }
   }

   public int getKid1() {
      return this.kid1;
   }

   public int getKid2() {
      return this.kid2;
   }

   public boolean hasKid(int k) {
      return k == this.kid1 || k == this.kid2;
   }

   public int getDirection() {
      return this.direction;
   }

   public void setDirection(int d) {
      this.direction = d;
   }

   public String toString() {
      knoten k1 = this.my_main.getKnoten(this.kid1);
      knoten k2 = this.my_main.getKnoten(this.kid2);
      return k1.getName() + " - " + k2.getName();
   }

   public String extraString() {
      return "";
   }

   void paint(Graphics2D g2) {
      knoten k1 = this.my_main.getKnoten(this.kid1);
      knoten k2 = this.my_main.getKnoten(this.kid2);
      this.calc();
      Shape drawable = this.getShape();
      if (this.my_main.isSelectedVerbindung(this)) {
         g2.setColor(this.selectcolor);
         g2.setStroke(new BasicStroke(6.0F));
         g2.draw(drawable);
      }

      g2.setColor(this.backcolor);
      if ((k1 == null || k1.getRid() == this.my_main.getMainRid()) && (k2 == null || k2.getRid() == this.my_main.getMainRid())) {
         g2.setStroke(new BasicStroke(2.0F));
      } else {
         float[] dash = new float[]{5.0F, 5.0F};
         g2.setStroke(new BasicStroke(2.0F, 0, 2, 1.0F, dash, 0.0F));
      }

      g2.draw(drawable);
      g2.setStroke(new BasicStroke(1.0F));
   }

   public Shape getShape() {
      Shape drawable = this;
      if (this.direction > 0) {
         Vector2d v = new Vector2d(this.x2 - this.x1, this.y2 - this.y1);
         v.normalize();
         v.mirror();
         v.scale(120.0);
         Vector2d v2 = new Vector2d(v);
         v2.ortogonal();
         v2.normalize();
         v2.scale(130.0);
         if (this.direction > 1) {
            v2.mirror();
         }

         v2.add(v);
         java.awt.geom.CubicCurve2D.Double curve = new java.awt.geom.CubicCurve2D.Double(
            this.x1, this.y1, this.x1 + v.x, this.y1 + v.y, this.x1 + v2.x, this.y1 + v2.y, this.x2, this.y2
         );
         drawable = curve;
      }

      return drawable;
   }

   public int compareTo(Object o) {
      return this.toString().compareToIgnoreCase(o.toString());
   }

   void generateSaveString(StringBuffer data) {
      int id = this.kid1 * 10000 + this.kid2;
      data.append("vkid1[" + id + "]=");
      data.append(this.kid1);
      data.append("&vkid2[" + id + "]=");
      data.append(this.kid2);
      data.append('&');
   }
}

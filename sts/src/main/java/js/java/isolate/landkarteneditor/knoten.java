package js.java.isolate.landkarteneditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

class knoten extends Rectangle implements Comparable {
   private final control my_main;
   private final knotenList parent;
   private int rx;
   private int ry;
   private bahnhofList.bahnhofListData data;
   private final int kid;
   private int mx;
   private int my;
   private final Color backcolor = new Color(255, 255, 238);
   private final Color frontcolor = Color.BLACK;
   private final Color bordercolor = Color.BLACK;
   private final Color regioncolor = new Color(238, 238, 255);
   private final Color selectcolor = new Color(238, 68, 68);
   private final Color overcolor = new Color(0, 0, 0);
   private final Color previewcolor = new Color(187, 187, 187);
   private final Font font = new Font("Dialog", 0, 10);

   knoten(control main, knotenList p, bahnhofList.bahnhofListData data, int kid, int x, int y) {
      super(x * 3 * 25, y * 2 * 25, 4 * 25, 1 * 25);
      this.parent = p;
      this.my_main = main;
      this.data = data;
      this.kid = kid;
      this.rx = x;
      this.ry = y;
   }

   public void setData(bahnhofList.bahnhofListData data) {
      this.data = data;
   }

   public void setSelected() {
      this.my_main.setSelectedKnoten(this);
   }

   public void moveLocationBy(int dx, int dy) {
      this.setLocation(this.rx + dx, this.ry + dy);
   }

   public void setLocation(int x, int y) {
      if (x != this.rx || y != this.ry) {
         this.rx = x;
         this.ry = y;
         super.setLocation(x * 3 * 25, y * 2 * 25);
         this.parent.refresh(this);
      }
   }

   public void setLocationFromScreen(Point p) {
      this.setLocationFromScreen(p.x, p.y);
   }

   public void setLocationFromScreen(int x, int y) {
      this.mx = x;
      this.my = y;
      if (x >= 0) {
         x -= 25 / 2;
      } else {
         x -= 25 * 3;
      }

      if (y >= 0) {
         y += 25 / 2;
      } else {
         y -= 25;
      }

      x = x / 3 / 25;
      y = y / 2 / 25;
      this.setLocation(x, y);
   }

   int getKid() {
      return this.kid;
   }

   int getRid() {
      return this.data instanceof bahnhofList.bahnhofData
         ? this.my_main.getRegion(((bahnhofList.bahnhofData)this.data).netzname).rid
         : ((bahnhofList.regionData)this.data).rid;
   }

   bahnhofList.bahnhofListData getData() {
      return this.data;
   }

   void paint(Graphics2D g) {
      if (this.my_main.isSelectedKnoten(this)) {
         float[] dash = new float[]{2.0F, 1.0F};
         g.setStroke(new BasicStroke(1.0F, 0, 2, 1.0F, dash, 0.0F));
         Rectangle r1 = new Rectangle(this);
         r1.grow(1, 1);
         Rectangle r2 = new Rectangle(this);
         r2.grow(2, 2);
         g.setColor(this.selectcolor);
         g.draw(r1);
         g.setColor(this.bordercolor);
         g.draw(r2);
         g.setStroke(new BasicStroke(1.0F));
      }

      Graphics2D g2 = (Graphics2D)g.create(this.x, this.y, this.width + 1, this.height + 1);
      g2.setBackground(this.backcolor);
      g2.clearRect(0, 0, this.width, this.height);
      if (this.data instanceof bahnhofList.regionData) {
         g2.setColor(this.regioncolor);
         g2.fillOval(0, 0, this.width, this.height);
         if (this.getRid() != this.my_main.getMainRid()) {
            float[] dash = new float[]{5.0F, 5.0F};
            g2.setStroke(new BasicStroke(1.0F, 0, 2, 1.0F, dash, 0.0F));
         }
      } else if (this.getRid() != this.my_main.getMainRid()) {
         float[] dash = new float[]{5.0F, 5.0F};
         g2.setStroke(new BasicStroke(1.0F, 0, 2, 1.0F, dash, 0.0F));
      }

      this.paintObject(g2, 0, 0, this.bordercolor, this.frontcolor);
      if (this.my_main.isMoveKnoten(this)) {
         g.setColor(this.previewcolor);
         g.drawLine(this.x, this.y, this.mx - this.width / 2, this.my - this.height / 2);
         g.drawLine(this.x + this.width, this.y, this.mx - this.width / 2 + this.width, this.my - this.height / 2);
         g.drawLine(this.x, this.y + this.height, this.mx - this.width / 2, this.my - this.height / 2 + this.height);
         g.drawLine(this.x + this.width, this.y + this.height, this.mx - this.width / 2 + this.width, this.my - this.height / 2 + this.height);
         FontMetrics mfont = g2.getFontMetrics();
         String koord = this.rx + "/" + this.ry;
         int w = mfont.stringWidth(koord) + 4;
         int mvx = this.mx - this.width / 2;
         int mvy = this.my - this.height / 2;
         g.fillRect(mvx + this.width - w, mvy + this.height - mfont.getHeight(), w, mfont.getHeight());
         g.setColor(this.frontcolor);
         g.drawString(koord, mvx + this.width - w + 2, mvy + this.height - mfont.getDescent());
         this.paintObject(g, mvx, mvy, this.previewcolor, this.previewcolor);
      }
   }

   private void paintObject(Graphics2D g2, int x, int y, Color bordercolor, Color frontcolor) {
      g2.setColor(bordercolor);
      g2.drawRect(x, y, this.width, this.height);
      g2.setStroke(new BasicStroke(1.0F));
      g2.setColor(frontcolor);
      g2.setFont(this.font);
      FontMetrics mfont = g2.getFontMetrics();
      g2.drawString(this.data.name, x + 4, y + mfont.getAscent());
   }

   String getName() {
      return this.data.name;
   }

   public int getKX() {
      return this.rx;
   }

   public int getKY() {
      return this.ry;
   }

   public String toString() {
      return this.getName();
   }

   public String extraString() {
      return String.format("[%2d/%2d]", this.rx, this.ry);
   }

   public int compareTo(Object o) {
      return this.data.name.compareToIgnoreCase(((knoten)o).data.name);
   }

   void generateSaveString(StringBuffer data) {
      data.append("kid[" + this.kid + "]=");
      data.append(this.kid);
      data.append("&x[" + this.kid + "]=");
      data.append(this.rx);
      data.append("&y[" + this.kid + "]=");
      data.append(this.ry);
      if (this.data instanceof bahnhofList.regionData) {
         data.append("&erid[" + this.kid + "]=");
      } else {
         data.append("&aid[" + this.kid + "]=");
      }

      data.append(this.data.getId());
      data.append('&');
   }
}

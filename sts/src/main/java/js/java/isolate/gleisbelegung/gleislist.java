package js.java.isolate.gleisbelegung;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D.Float;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

class gleislist extends JComponent implements Comparable, Iterable {
   private String gleis = "";
   private final TreeSet<zuggleis> zuege = new TreeSet();
   private int height = 1;
   private boolean odd = false;
   static Color bgcol1 = new Color(187, 187, 187);
   static Color bgcol2 = new Color(221, 221, 221);

   gleislist(String g) {
      super();
      this.gleis = g;
      this.setOpaque(true);
      this.setComponentPopupMenu(this.buildMenu());
   }

   private JPopupMenu buildMenu() {
      JPopupMenu p = new JPopupMenu();
      JMenuItem m = new JMenuItem("alle Markierungen aufheben");
      m.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            try {
               zuggleis zg = (zuggleis)gleislist.this.zuege.first();
               zg.mark(0);
            } catch (Exception var3) {
            }
         }
      });
      p.add(m);
      return p;
   }

   public Iterator<zuggleis> iterator() {
      return this.zuege.iterator();
   }

   public gleisnamepane getNamePane() {
      return new gleisnamepane(this);
   }

   public void addZug(zuggleis z) {
      this.zuege.add(z);
   }

   public String getGleis() {
      return this.gleis;
   }

   public Color getBGcol() {
      return this.odd ? bgcol1 : bgcol2;
   }

   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D)g;
      int w = this.getWidth();
      int h = this.getHeight();
      if (this.odd) {
         g2.setBackground(bgcol1);
      } else {
         g2.setBackground(bgcol2);
      }

      g2.clearRect(0, 0, this.getWidth(), this.getHeight());
      g2.setColor(timeline.bgcol.darker());
      g2.drawLine(0, h - 1, w, h - 1);
      g2.setColor(timeline.bgcol.brighter());
      g2.drawLine(0, 0, w, 0);

      for(int s = timeline.VON; s < timeline.BIS; ++s) {
         g2.drawLine((s - timeline.VON) * timeline.MINUTEWIDTH * 60, 0, (s - timeline.VON) * timeline.MINUTEWIDTH * 60, this.getHeight());
      }
   }

   public int compareTo(Object o) {
      return this.gleis.compareToIgnoreCase(((gleislist)o).gleis);
   }

   public Dimension getMinimumSize() {
      return this.getMinsize();
   }

   public Dimension getMaximumSize() {
      return this.getMinsize();
   }

   public Dimension getPreferredSize() {
      return this.getMinsize();
   }

   public zuggleis getZug(int zid) {
      zuggleis ret = null;

      for(zuggleis zg : this.zuege) {
         if (zid == zg.getZid()) {
            ret = zg;
            break;
         }
      }

      return ret;
   }

   public zuggleis findParentZug(int zid) {
      zuggleis ret = null;

      for(zuggleis zg : this.zuege) {
         if (zg.isParentOf(zid)) {
            ret = zg;
            break;
         }
      }

      return ret;
   }

   public void searchZug(String text) {
      this.zuege.stream().forEach(zg -> zg.searchZug(text));
   }

   void setBGcolnum(boolean _odd) {
      this.odd = _odd;
   }

   private Dimension getMinsize() {
      return new Dimension(timeline.MINUTEWIDTH * (timeline.BIS - timeline.VON) * 60, this.height);
   }

   public void render() {
      int maxy = timeline.fontheight + 10;

      for(zuggleis zg : this.zuege) {
         zg.recalc();
      }

      for(zuggleis zg : this.zuege) {
         boolean repeat = false;
         zg.flagrender();

         do {
            repeat = false;
            Rectangle r = zg.getObject();

            for(zuggleis zg2 : this.zuege) {
               if (zg2 == zg) {
                  break;
               }

               if (r.intersects(zg2.getObject())) {
                  zg.moveY(r.y + zg2.getObject().height);
                  repeat = true;
                  if (!zg.hasEKF() && (zg.getRZid() != zg2.getRZid() || zg.hasThemamarker(zg2.getThemamarker()))) {
                     zg.setCollision(true);
                  }
               }
            }

            maxy = (int)Math.max(r.getY() + r.getHeight(), (double)maxy);
         } while(repeat);

         this.add(zg);
         zg.render();
         if (zg.hasP()) {
            pflagmark pfm = new pflagmark(zg);
            this.add(pfm);
            pfm.render();
         }
      }

      this.height = maxy + 2;
      this.setSize(this.getMinsize());
   }

   void clear() {
      this.removeAll();
   }

   public ArrayList<Shape> getCollsions() {
      return (ArrayList<Shape>)this.zuege.stream().filter(zg -> zg.hasCollision()).map(zg -> {
         Rectangle r = zg.getObject();
         return new Float((float)(r.x - 20), (float)this.getY(), (float)(r.width + 40), (float)this.height);
      }).collect(Collectors.toCollection(ArrayList::new));
   }
}

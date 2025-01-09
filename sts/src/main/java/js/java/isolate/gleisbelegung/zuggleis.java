package js.java.isolate.gleisbelegung;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import js.java.isolate.sim.flagdata;
import js.java.tools.gui.GraphicTools;
import org.xml.sax.Attributes;

class zuggleis extends JComponent implements MouseListener, Comparable {
   public final DateFormat sdf;
   private final int zid;
   private final String name;
   private final String thematag;
   private final String gleis;
   private final gleislist parent;
   private Date an;
   private Date ab;
   private final flagdata flags;
   private int stoptime;
   private int x = 0;
   private int y = 0;
   private Dimension myd;
   private Rectangle myr;
   private GradientPaint gp;
   private final belegung master;
   private final String infotext;
   private Color txtcol;
   private boolean error = false;
   private boolean over = false;
   private boolean over2 = false;
   private boolean eziel = false;
   private zuggleis flagziel = null;
   private HashSet<zuggleis> flagquelle = new HashSet();
   private boolean highlight = false;
   private int ein_enr = 0;
   private int aus_enr = 0;
   private int rzid = 0;
   private final String templatename;
   private boolean collision = false;
   private int displayMode = 0;
   private final zuglist otherstops;

   zuggleis(int _zid, gleislist gl, Attributes attrs, belegung _p, zuglist others) {
      this.parent = gl;
      this.master = _p;
      this.zid = _zid;
      this.otherstops = others;
      this.sdf = DateFormat.getTimeInstance(3, Locale.GERMAN);
      this.name = attrs.getValue("name");
      this.gleis = attrs.getValue("gleis");
      this.thematag = attrs.getValue("thematag");
      this.flags = new flagdata(attrs.getValue("flags"), attrs.getValue("flagdata"), attrs.getValue("flagparam"));

      try {
         this.an = this.sdf.parse(attrs.getValue("an"));
      } catch (Exception var11) {
      }

      try {
         this.ab = this.sdf.parse(attrs.getValue("ab"));
      } catch (Exception var10) {
      }

      try {
         this.ein_enr = Integer.parseInt(attrs.getValue("ein_enr"));
      } catch (Exception var9) {
      }

      try {
         this.aus_enr = Integer.parseInt(attrs.getValue("aus_enr"));
      } catch (Exception var8) {
      }

      try {
         this.rzid = Integer.parseInt(attrs.getValue("rzid"));
      } catch (Exception var7) {
      }

      this.templatename = attrs.getValue("rzid_name");
      this.setOpaque(true);
      this.calcDim();
      this.setupCols();
      this.infotext = this.name + "(" + this.zid + ") " + this.sdf.format(this.an) + " - " + this.sdf.format(this.ab);
      this.setToolTipText(
         "<html>"
            + this.name
            + " ("
            + this.zid
            + ")<br>"
            + this.sdf.format(this.an)
            + " - "
            + this.sdf.format(this.ab)
            + "<br>Flags: "
            + this.flags.toString()
            + "<br>Themamarker: "
            + this.thematag
            + "<br>"
            + this.getEinAus()
            + "<br>"
            + this.getTemplate()
            + "</html>"
      );
      this.addMouseListener(this);
      this.setComponentPopupMenu(this.buildMenu());
   }

   private JPopupMenu buildMenu() {
      JPopupMenu p = new JPopupMenu();
      JLabel l = new JLabel(this.name + " (" + this.zid + ")");
      l.setHorizontalAlignment(0);
      l.setHorizontalTextPosition(0);
      p.add(l);
      p.add(new Separator());
      JMenuItem m = new JMenuItem("Züges des Zug-Templates zeigen");
      m.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            zuggleis.this.mark(1);
         }
      });
      p.add(m);
      m = new JMenuItem("Züges des Zug-Templates ausblenden");
      m.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            zuggleis.this.mark(-1);
         }
      });
      p.add(m);
      p.add(new Separator());
      m = new JMenuItem("Züges des Zug-Templates und dieser Themenmarker zeigen");
      m.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            zuggleis.this.mark(2);
         }
      });
      p.add(m);
      m = new JMenuItem("Züges des Zug-Templates und dieser Themenmarker ausblenden");
      m.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            zuggleis.this.mark(-2);
         }
      });
      p.add(m);
      p.add(new Separator());
      m = new JMenuItem("alle Markierungen aufheben");
      m.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            zuggleis.this.mark(0);
         }
      });
      p.add(m);
      return p;
   }

   public void mark(int m) {
      int am = Math.abs(m);
      Iterator<gleislist> it = this.master.getGleisList();

      while (it.hasNext()) {
         gleislist gl = (gleislist)it.next();

         for (zuggleis zg : gl) {
            if (m == 0) {
               zg.setDisplayMode(0);
            } else if (zg.rzid == this.rzid && (am == 1 || zg.hasThemamarker(this.thematag))) {
               zg.setDisplayMode(m / am);
            }
         }
      }
   }

   public void setCollision(boolean b) {
      this.collision = b;
   }

   public boolean hasCollision() {
      return this.collision;
   }

   private void calcDim() {
      this.stoptime = (int)((this.ab.getTime() - this.an.getTime()) / 1000L / 60L);
      this.y = 2;
      this.x = timeline.MINUTEWIDTH * ((int)(this.an.getTime() / 1000L / 60L) - timeline.VON * 60 + 60);
      this.myd = new Dimension(timeline.MINUTEWIDTH * Math.max(1, this.stoptime), timeline.fontheight);
      Point p = new Point(this.x, this.y);
      this.myr = new Rectangle(p, this.myd);
   }

   public void recalc() {
      this.calcDim();
   }

   private void setupCols() {
      Color c = Color.BLUE;
      Color start = this.parent.getBGcol();
      this.txtcol = Color.WHITE;
      if (this.flags.hasFlag('E') || this.eziel) {
         c = Color.YELLOW;
         this.txtcol = Color.BLACK;
      }

      if (this.flags.hasFlag('K')) {
         c = Color.GREEN;
         this.txtcol = Color.BLACK;
      }

      if (this.flags.hasFlag('F')) {
         c = Color.CYAN;
         this.txtcol = Color.BLACK;
      }

      if (this.displayMode < 0) {
         for (int i = 0; i < 3; i++) {
            c = c.darker();
         }

         this.txtcol = Color.BLACK;
      }

      if (this.displayMode > 0) {
         start = c;
         c = Color.WHITE;
         this.txtcol = Color.BLACK;
      }

      this.gp = new GradientPaint(0.0F, 0.0F, start, 0.0F, (float)(this.myd.height / 2), c, true);
   }

   public String getGleis() {
      return this.gleis;
   }

   public int getZid() {
      return this.zid;
   }

   public String getName() {
      return this.name + " (" + this.zid + ")";
   }

   public String getAnAb() {
      return this.sdf.format(this.an) + " - " + this.sdf.format(this.ab);
   }

   public String getFlags() {
      return this.flags.toString();
   }

   public boolean hasEKF() {
      return this.flags.hasFlag('E') || this.flags.hasFlag('K') || this.flags.hasFlag('F');
   }

   public boolean hasP() {
      return this.flags.hasFlag('P');
   }

   public boolean isParentOf(int zid) {
      if (this.flags.hadFlag('E')) {
         return this.flags.dataOfFlag('E') == zid;
      } else if (this.flags.hadFlag('K')) {
         return this.flags.dataOfFlag('K') == zid;
      } else {
         return this.flags.hadFlag('F') ? this.flags.dataOfFlag('F') == zid : false;
      }
   }

   public String getTemplate() {
      return this.templatename + " (" + this.rzid + ")";
   }

   public int getRZid() {
      return this.rzid;
   }

   public String getThemamarker() {
      return this.thematag;
   }

   boolean hasThemamarker(String othematag) {
      for (int i = 0; i < this.thematag.length(); i++) {
         char c = this.thematag.charAt(i);
         if (othematag.indexOf(c) >= 0) {
            return true;
         }
      }

      return false;
   }

   public String getEinAus() {
      return this.master.findEinfahrt(this.ein_enr) + " (" + this.ein_enr + ") -> " + this.master.findAusfahrt(this.aus_enr) + " (" + this.aus_enr + ")";
   }

   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D)g;
      GraphicTools.enableTextAA(g2);
      int w = this.getWidth();
      int h = this.getHeight();
      Color _txtcol = this.txtcol;
      if (this.highlight) {
         g2.setColor(Color.WHITE);
         _txtcol = Color.BLACK;
      } else if (this.error) {
         g2.setColor(Color.RED);
      } else {
         g2.setPaint(this.gp);
      }

      g2.fillRect(0, 0, w, h);
      g2.setPaint(null);
      g2.setColor(_txtcol);
      g2.drawLine(0, 0, 0, h);
      g2.setFont(timeline.font_mini);
      FontMetrics mfont = g2.getFontMetrics();
      g2.drawString(this.infotext, 1 + (this.over ? 1 : 0), mfont.getHeight() - 4 + (this.over ? 1 : 0));
      if (this.over) {
         g2.setColor(Color.RED);
         g2.drawRect(0, 0, w - 1, h - 1);
      } else if (this.over2) {
         g2.setColor(Color.MAGENTA);
         g2.drawRect(0, 0, w - 1, h - 1);
      }
   }

   public int calcPFlagLength() {
      zuggleis prev = (zuggleis)this.otherstops.first();
      Date t = prev.an;

      for (zuggleis zg : this.otherstops) {
         if (zg == this) {
            break;
         }

         if (zg.hasP()) {
            t = zg.ab;
         }
      }

      return (int)((this.an.getTime() - t.getTime()) / 1000L / 60L);
   }

   public void moveY(int ny) {
      this.y = ny;
      this.myr.y = this.y;
   }

   public void render() {
      this.setSize(this.myd);
      this.setLocation(this.x, this.y);
   }

   public void flagrender() {
      if (this.flags.hasFlag('E')) {
         zuggleis z = this.parent.getZug(this.flags.dataOfFlag('E'));
         if (z != null) {
            z.eziel = true;
            this.flagziel = z;
            z.flagquelle.add(this);
            Date d = new Date(this.an.getTime() + 60000L);
            if (d.before(z.ab)) {
               z.an = d;
               z.calcDim();
            } else {
               this.ab = z.an;
               this.calcDim();
            }

            z.setupCols();
         } else {
            this.error = true;
         }
      } else if (this.flags.hasFlag('F')) {
         zuggleis z = this.parent.getZug(this.flags.dataOfFlag('F'));
         if (z != null) {
            this.flagziel = z;
            z.flagquelle.add(this);
            Date d = new Date(this.an.getTime() + 60000L);
            if (d.before(z.ab)) {
               z.an = d;
               z.calcDim();
            }
         } else {
            this.error = true;
         }
      } else if (this.flags.hasFlag('K')) {
         zuggleis z = this.parent.getZug(this.flags.dataOfFlag('K'));
         if (z != null) {
            this.flagziel = z;
            z.flagquelle.add(this);
            this.ab = z.ab;
            this.calcDim();
         } else {
            this.error = true;
         }
      }

      if (this.ein_enr == 0 && this.otherstops.first() == this) {
         zuggleis z = this.parent.findParentZug(this.zid);
         if (z == null) {
            this.error = true;
         }
      }
   }

   public Dimension getMaximumSize() {
      return this.getMinsize();
   }

   public Dimension getMinimumSize() {
      return this.getMinsize();
   }

   public Dimension getPreferredSize() {
      return this.getMinsize();
   }

   private Dimension getMinsize() {
      return this.myd;
   }

   public Rectangle getObject() {
      return this.myr;
   }

   public void mouseClicked(MouseEvent e) {
      this.master.show(this);
   }

   public void mousePressed(MouseEvent e) {
   }

   public void mouseReleased(MouseEvent e) {
   }

   public void mouseEntered(MouseEvent e) {
      this.over = true;
      this.repaint();
      if (this.flagziel != null) {
         this.flagziel.over2 = true;
         this.flagziel.repaint();
      }

      for (zuggleis z : this.flagquelle) {
         z.over2 = true;
         z.repaint();
      }
   }

   public void mouseExited(MouseEvent e) {
      this.over = false;
      this.repaint();
      if (this.flagziel != null) {
         this.flagziel.over2 = false;
         this.flagziel.repaint();
      }

      for (zuggleis z : this.flagquelle) {
         z.over2 = false;
         z.repaint();
      }
   }

   public int compareTo(Object o) {
      zuggleis z = (zuggleis)o;
      int c = this.an.compareTo(z.an);
      if (c == 0) {
         c = this.ab.compareTo(z.ab);
      }

      if (c == 0) {
         c = this.zid < z.zid ? -1 : 1;
      }

      return c;
   }

   public boolean equals(Object o) {
      if (!(o instanceof zuggleis)) {
         return false;
      } else {
         zuggleis z = (zuggleis)o;
         return z.zid == this.zid && z.an.equals(this.an) && z.ab.equals(this.ab) && z.gleis.equals(this.gleis);
      }
   }

   public int hashCode() {
      int hash = 5;
      hash = 83 * hash + this.zid;
      hash = 83 * hash + Objects.hashCode(this.gleis);
      hash = 83 * hash + Objects.hashCode(this.an);
      return 83 * hash + Objects.hashCode(this.ab);
   }

   public void searchZug(String t) {
      this.setHighlight(!t.isEmpty() && this.name.toUpperCase().indexOf(t.toUpperCase()) >= 0);
   }

   private void setHighlight(boolean h) {
      this.highlight = h;
      this.repaint();
   }

   public int getDisplayMode() {
      return this.displayMode;
   }

   public void setDisplayMode(int displayMode) {
      this.displayMode = displayMode;
      this.setupCols();
      this.repaint();
   }
}

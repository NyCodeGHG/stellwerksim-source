package js.java.isolate.sim.sim.fahrplanRenderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.GrayFilter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.zug.fahrplanCollection.zugPlanLine;
import js.java.tools.gui.GraphicTools;

public class lineRenderer extends clickableRenderer {
   private final Dimension dim;
   private final zugPlanLine zpl;
   private static final Color col1 = new Color(255, 255, 238);
   private static final Color selectedColor = new Color(187, 187, 255);
   private static final Color gleisColor = new Color(238, 238, 238);
   private static boolean needLayout = true;
   private static int ANKUNFTSTART = 0;
   private static int ANABSEPSTART = 28;
   private static int ABFAHRTSTART = 32;
   private static int GLEISSTART = 65;
   private boolean mouseIn = false;
   private final int lineNumber;
   private static final int XOFFSET = 10;
   private hinweisRenderer hrLine = null;
   private final boolean selectable;
   private static final int NOPAINT = 1;
   private static final int GRAY = 2;

   public lineRenderer(zugRenderer zr, zugPlanLine zpl, boolean col0, boolean selectable, int lineNumber) {
      super(zr);
      this.zpl = zpl;
      this.lineNumber = lineNumber;
      this.selectable = selectable;
      if (col0) {
         this.setBackground(Color.WHITE);
      } else {
         this.setBackground(col1);
      }

      int l = 1;
      if (zpl.kErwartet != null || zpl.flagK || zpl.flagF) {
         l++;
      }

      if (zpl.hinweistext != null && !zpl.hinweistext.trim().isEmpty()) {
         this.hrLine = new hinweisRenderer(zr, zpl.hinweistext, l, zpl.azid);
         this.add(this.hrLine);
         l++;
      }

      this.dim = new Dimension(100, this.LINEHEIGHT * l + 2);
      this.buildToolTip();
   }

   public void layout() {
      if (needLayout) {
         needLayout = false;
         FontMetrics fm = this.getFontMetrics(this.getFont());
         Rectangle2D anbounds = this.getFont().getStringBounds("00:00", fm.getFontRenderContext());
         Rectangle2D sepbounds = this.getFont().getStringBounds("-", fm.getFontRenderContext());
         Rectangle2D abbounds = this.getFont().getStringBounds("00:00", fm.getFontRenderContext());
         ANABSEPSTART = (int)((double)ANKUNFTSTART + anbounds.getWidth() + 1.0);
         ABFAHRTSTART = (int)((double)ANABSEPSTART + sepbounds.getWidth() + 1.0);
         GLEISSTART = (int)((double)ABFAHRTSTART + abbounds.getWidth() + 9.0);
      }

      if (this.hrLine != null) {
         this.hrLine.setBounds(GLEISSTART + 10, this.LINEHEIGHT * this.hrLine.getLine() + 1, this.getWidth() - GLEISSTART - 10, this.LINEHEIGHT);
      }
   }

   public Dimension getPreferredSize() {
      return this.dim;
   }

   public Dimension getMinimumSize() {
      return this.dim;
   }

   public Dimension getMaximumSize() {
      return this.dim;
   }

   private void buildToolTip() {
      StringBuilder toolTipText = new StringBuilder();
      if (this.zpl.kErwartet != null) {
         if (this.zpl.kErwartet != null) {
            toolTipText.append("kuppelt mit ").append(this.zpl.kErwartet).append("<br>");
         } else {
            toolTipText.append("kuppelt").append("<br>");
         }
      } else if (this.zpl.flagK) {
         if (this.zpl.flagZiel != null) {
            toolTipText.append("kuppelt mit ").append(this.zpl.flagZiel).append("<br>");
         } else {
            toolTipText.append("kuppelt").append("<br>");
         }
      } else if (this.zpl.flagF) {
         if (this.zpl.flagZiel != null) {
            toolTipText.append("flügelt ").append(this.zpl.flagZiel).append("<br>");
         } else {
            toolTipText.append("flügelt").append("<br>");
         }
      }

      if (this.zpl.flagL || this.zpl.flagL_running) {
         toolTipText.append(this.flagUrl("L")).append("Lok setzt um").append("<br>");
      }

      if (this.zpl.flagW || this.zpl.flagW_running) {
         toolTipText.append(this.flagUrl("W")).append("tauscht Lok").append("<br>");
      }

      if (this.zpl.flagD) {
         toolTipText.append(this.flagUrl("D")).append("Durchfahrt, kein Halt").append("<br>");
      }

      if (this.zpl.flagA) {
         toolTipText.append(this.flagUrl("A")).append("vorzeitige Abfahrt per Befehl").append("<br>");
      }

      if (this.zpl.flagR) {
         toolTipText.append(this.flagUrl("R")).append("ändert Richtung").append("<br>");
      }

      if (this.zpl.flagE) {
         if (this.zpl.flagZiel != null) {
            toolTipText.append(this.flagUrl("E")).append("ändert Name in ").append(this.zpl.flagZiel).append("<br>");
         } else {
            toolTipText.append(this.flagUrl("E")).append("ändert Name").append("<br>");
         }
      }

      if (this.zpl.flagG) {
         if (this.zpl.gMode) {
            toolTipText.append("weiter als Rangierfahrt").append("<br>");
         } else {
            toolTipText.append("ab hier als Rangierfahrt").append("<br>");
         }
      } else if (this.zpl.gMode) {
         toolTipText.append("bis hier als Rangierfahrt").append("<br>");
      }

      if (toolTipText.length() > 0) {
         this.setToolTipText("<html>" + toolTipText.toString() + "</html>");
      }
   }

   @Override
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g3 = (Graphics2D)g;
      GraphicTools.enableTextAA(g3);
      GraphicTools.enableGfxAA(g3);
      Color bg;
      if (this.lineNumber >= 0 && this.zpl.azid == this.zr.getAZid()) {
         bg = selectedColor;
      } else {
         bg = this.getBackground();
      }

      if (this.mouseIn) {
         bg = GraphicTools.darker(bg, 0.9);
      }

      g3.setBackground(bg);
      g3.clearRect(0, 0, this.getWidth(), this.getHeight());
      if (this.hrLine != null) {
         this.hrLine.setBackground(bg);
      }

      g3.setColor(Color.BLACK);
      if (this.zpl.flagG) {
         g3.fillRect(2, this.LINEHEIGHT / 2, 5, this.getHeight());
      }

      if (this.zpl.gMode) {
         g3.fillRect(2, 0, 5, this.LINEHEIGHT / 2);
      }

      Graphics2D g2 = (Graphics2D)g3.create();
      g2.translate(10, 0);
      int x = 0;
      int y = 0;
      if (!this.zpl.an.isEmpty()) {
         this.drawString(g2, this.plainFont, this.zpl.an, ANKUNFTSTART, 0);
      }

      this.drawString(g2, this.plainFont, "-", ANABSEPSTART, 0);
      if (!this.zpl.ab.isEmpty()) {
         this.drawString(g2, this.plainFont, this.zpl.ab, ABFAHRTSTART, 0);
      }

      x = this.drawString(g2, this.plainFont, this.zpl.zielGleis, GLEISSTART, 0);
      if (this.zpl.befehlGleis != null) {
         g2.setColor(Color.RED);
         g2.drawLine(GLEISSTART, this.plainFont.getSize() / 2 + 4, x, this.plainFont.getSize() / 2 + 1);
         g2.drawLine(GLEISSTART, this.plainFont.getSize() / 2 + 4 + 1, x, this.plainFont.getSize() / 2 + 1 + 1);
         if (!this.zpl.befehlGleisAccepted && !gleis.blinkon_slow) {
            g2.setColor(Color.WHITE);
         } else {
            g2.setColor(Color.BLACK);
         }

         x = this.drawString(g2, this.plainFont, this.zpl.befehlGleis, x + 5, 0);
         if (!this.zpl.befehlGleisAccepted) {
            this.triggerRepaint();
         }
      }

      g2.setColor(gleisColor);
      g2.drawRect(GLEISSTART - 5, 1, x - GLEISSTART + 10, this.LINEHEIGHT - 1);
      g2.setColor(Color.BLACK);
      x = Math.max(x + 10, 120);
      int l = 1;
      if (this.zpl.kErwartet != null) {
         if (this.zpl.kErwartet != null) {
            this.drawString(g2, this.plainFont, "kuppelt mit |" + this.zpl.kErwartet + "|", x, this.LINEHEIGHT * l, this.zpl.kErwartetZid);
         } else {
            this.drawString(g2, this.plainFont, "kuppelt", x, this.LINEHEIGHT * l, this.zpl.kErwartetZid);
         }

         this.clickArea.x += 10;
         l++;
      } else if (this.zpl.flagK) {
         if (this.zpl.flagZiel != null) {
            this.drawString(g2, this.plainFont, "kuppelt mit |" + this.zpl.flagZiel + "|", x, this.LINEHEIGHT * l, this.zpl.flagZielZid);
         } else {
            this.drawString(g2, this.plainFont, "kuppelt", x, this.LINEHEIGHT * l, this.zpl.flagZielZid);
         }

         this.clickArea.x += 10;
         l++;
      } else if (this.zpl.flagF) {
         if (this.zpl.flagZiel != null) {
            this.drawString(g2, this.plainFont, "flügelt |" + this.zpl.flagZiel + "|", x, this.LINEHEIGHT * l, this.zpl.flagZielZid);
         } else {
            this.drawString(g2, this.plainFont, "flügelt", x, this.LINEHEIGHT * l, this.zpl.flagZielZid);
         }

         this.clickArea.x += 10;
         l++;
      }

      if (this.zpl.flagK) {
         x = this.img(g2, "K", x);
      }

      if (this.zpl.flagF) {
         x = this.img(g2, "F", x);
      }

      if (this.zpl.flagL || this.zpl.flagL_running) {
         x = this.img(g2, "L", x, this.zpl.flagL_running && gleis.blinkon ? 2 : 0);
         if (this.zpl.flagL_running) {
            this.triggerRepaint();
         }
      }

      if (this.zpl.flagW || this.zpl.flagW_running) {
         x = this.img(g2, "W", x, this.zpl.flagW_running && gleis.blinkon ? 2 : 0);
         if (this.zpl.flagW_running) {
            this.triggerRepaint();
         }
      }

      if (this.zpl.flagD) {
         x = this.img(g2, "D", x);
      }

      if (this.zpl.flagA) {
         x = this.img(g2, "A", x);
      }

      if (this.zpl.flagR) {
         x = this.img(g2, "R", x);
      }

      if (this.zpl.flagE) {
         x = this.img(g2, "E", x);
         if (this.zpl.flagZiel != null) {
            x = this.drawString(g2, this.plainFont, "(weiter als |" + this.zpl.flagZiel + "|)", x, 0, this.zpl.flagZielZid);
         } else {
            x = this.drawString(g2, this.plainFont, "(weiter mit neuem Namen)", x, 0, this.zpl.flagZielZid);
         }

         this.clickArea.x += 10;
      }

      g2.dispose();
   }

   private String flagUrl(String flag) {
      return "<img src='" + this.getClass().getResource(flag + "-Flag.png").toString() + "'>&nbsp;";
   }

   private int img(Graphics2D g2, String flag, int x) {
      return this.img(g2, flag, x, 0);
   }

   private int img(Graphics2D g2, String flag, int x, int mode) {
      try {
         InputStream imgStream = this.getClass().getResourceAsStream(flag + "-Flag.png");
         Image img = ImageIO.read(imgStream);
         if ((mode & 1) == 0) {
            if ((mode & 2) != 0) {
               img = GrayFilter.createDisabledImage(img);
            }

            g2.drawImage(img, x, (this.LINEHEIGHT - img.getHeight(null)) / 2, null);
         }

         return x + img.getWidth(null) + 3;
      } catch (IOException var7) {
         return x;
      }
   }

   @Override
   public void mousePressed(MouseEvent e) {
      super.mousePressed(e);
      if (this.selectable) {
         this.zr.setUnterzug(this.lineNumber, this.zpl.azid);
      }
   }

   @Override
   public void mouseEntered(MouseEvent e) {
      super.mouseEntered(e);
      this.mouseIn = this.selectable;
      this.repaint();
   }

   @Override
   public void mouseExited(MouseEvent e) {
      super.mouseExited(e);
      this.mouseIn = false;
      this.repaint();
   }
}

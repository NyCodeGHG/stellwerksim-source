package js.java.isolate.sim.gleisbild;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Path2D.Double;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import js.java.isolate.sim.gleis.gleis;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.gui.GraphicTools;

public class gleisbildPainter implements SessionClose {
   private int overtimecnt = 0;
   private boolean paintLocked = false;
   private final Timer ftimer = new Timer();
   private gleisbildPainter.cntTask ctimer = null;
   private int fcnt = 0;
   private double rotcnt = 0.0;
   private int sizecnt = 0;
   private int hcnt = 0;
   private static final int MAXFNT = 30;
   private volatile boolean inPaintComponent = false;
   private static final Color focus_col1 = new Color(255, 255, 0, 170);
   private static final Color focus_col2 = new Color(0, 0, 255, 170);
   private static final Color shadowColor = new Color(0, 0, 0, 51);
   private gleis lastFocus = null;

   @Override
   public void close() {
      if (this.ctimer != null) {
         this.ctimer.cancel();
         this.ctimer = null;
      }

      this.ftimer.cancel();
   }

   public void lockPaint(boolean l) {
      this.paintLocked = l;
   }

   private Graphics2D createXYGfx(Graphics2D g, int x, int y) {
      Graphics2D ret = (Graphics2D)g.create();
      ret.translate(x, y);
      return ret;
   }

   private Graphics2D createXYGfx(Graphics2D g, gleis gl, scaleHolder scaler) {
      return this.createXYGfx(g, gl.getCol() * scaler.getPaintingScale(), gl.getRow() * scaler.getPaintingScale());
   }

   public void paintOverlay(PaintSaveInterface panel, Graphics _g, scaleHolder scaler) {
      boolean needTimer = false;
      gleisbildModel model = panel.getModel();
      Graphics2D g = scaler.createScaledGraphics(_g);
      if (panel.isEditorView()) {
         for (gleis gl : model.getMarkedGleis()) {
            Graphics2D g2 = this.createXYGfx(g, gl, scaler);
            gl.aktiv3(panel, g2, scaler.getPaintingScale(), scaler.getPaintingScale(), this.hcnt);
            needTimer = true;
            g2.dispose();
         }

         if (model.getSelectedGleis() != null) {
            Graphics2D g2 = this.createXYGfx(g, model.getSelectedGleis(), scaler);
            model.getSelectedGleis().aktiv2(panel, g2, scaler.getPaintingScale(), scaler.getPaintingScale(), this.hcnt);
            needTimer = true;
            g2.dispose();
         } else {
            for (gleis gl : model.getHighlightedGleis()) {
               Graphics2D g2 = this.createXYGfx(g, gl, scaler);
               gl.aktiv2(panel, g2, scaler.getPaintingScale(), scaler.getPaintingScale(), this.hcnt);
               needTimer = true;
               g2.dispose();
            }
         }
      }

      for (gleis gl : model.getRolloverGleis()) {
         Graphics2D g2 = this.createXYGfx(g, gl, scaler);
         gl.highlight(panel, g2, scaler.getPaintingScale(), scaler.getPaintingScale(), this.hcnt);
         needTimer = true;
         g2.dispose();
      }

      if (model.getFocus() != null) {
         needTimer = true;
         gleis t = model.getFocus();
         if (this.lastFocus != t) {
            this.sizecnt = 9;
         }

         Graphics2D g2 = this.createXYGfx(g, model.getFocus(), scaler);
         this.paintFocus(panel, g2, (double)scaler.getPaintingScale(), (double)scaler.getPaintingScale(), t);
         this.lastFocus = model.getFocus();
         g2.dispose();
      } else {
         this.lastFocus = null;
      }

      this.updateTimer(panel, needTimer);
   }

   public void paintComponent(PaintSaveInterface panel, Graphics _g, scaleHolder scaler) {
      if (this.paintLocked) {
         System.out.println("Paint during lock");
      } else {
         boolean masstabMode = panel.isMasstabView();
         gleisbildModel model = panel.getModel();
         long starttime = System.currentTimeMillis();
         Graphics2D g = scaler.createScaledGraphics(_g);
         g.setColor(gleis.colors.col_stellwerk_back);
         g.fillRect(0, 0, model.getGleisWidth() * scaler.getPaintingScale(), model.getGleisHeight() * scaler.getPaintingScale());
         HashMap<gleis, Graphics2D> gfxCache = new HashMap();
         synchronized (this) {
            if (this.inPaintComponent) {
               System.out.println("paintComponent race condition!");
            }

            this.inPaintComponent = true;

            for (gleis gl : model) {
               Graphics2D g2 = this.createXYGfx(g, gl, scaler);
               gfxCache.put(gl, g2);
               gl.setMasstabModus(masstabMode);
               gl.reset();
               gl.resetN();
               gl.paint0(panel, g2, scaler.getPaintingScale(), scaler.getPaintingScale(), scaler.getFontScale());
            }

            if (panel.isEditorView()) {
               if (model.getSelectedGleis() != null) {
                  model.getSelectedGleis()
                     .aktiv(panel, (Graphics2D)gfxCache.get(model.getSelectedGleis()), scaler.getPaintingScale(), scaler.getPaintingScale());
               } else {
                  for (gleis gl : model.getHighlightedGleis()) {
                     gl.aktiv(panel, (Graphics2D)gfxCache.get(gl), scaler.getPaintingScale(), scaler.getPaintingScale());
                  }
               }
            }

            for (gleis gl : model) {
               gl.paint1(panel, (Graphics2D)gfxCache.get(gl), scaler.getPaintingScale(), scaler.getPaintingScale(), scaler.getFontScale());
            }

            gleis.switchNachbar();
         }

         for (gleis gl : model) {
            gl.paint1b(panel, (Graphics2D)gfxCache.get(gl), scaler.getPaintingScale(), scaler.getPaintingScale(), scaler.getFontScale());
         }

         for (gleis gl : model) {
            Graphics2D g2 = (Graphics2D)gfxCache.get(gl);
            gl.paint2(panel, g2, scaler.getPaintingScale(), scaler.getPaintingScale(), scaler.getFontScale());
         }

         for (gleis gl : model) {
            Graphics2D g2 = (Graphics2D)gfxCache.get(gl);
            gl.paint3(panel, g2, scaler.getPaintingScale(), scaler.getPaintingScale(), scaler.getFontScale());
            g2.dispose();
         }

         long runtime = System.currentTimeMillis() - starttime;
         if (runtime > 450L) {
            this.overtimecnt++;
            if (this.overtimecnt > 6) {
               System.out
                  .println(
                     "Die Kantenglättung wurde um 1 Stufe verringert, da die Darstellungszeit "
                        + runtime
                        + " Millisekunden war. Das System bringt nicht die erforderliche Darstellungsleistung. Näheres zu dieser Meldung in der FAQ."
                  );
               gleis.setAllowSmooth(false);
               this.overtimecnt = 0;
            }
         }

         this.inPaintComponent = false;
      }
   }

   private void updateTimer(PaintSaveInterface panel, boolean needTimer) {
      if (needTimer && this.ctimer == null) {
         this.fcnt = 0;
         this.hcnt = 0;
         this.ctimer = new gleisbildPainter.cntTask(panel);
         this.ftimer.scheduleAtFixedRate(this.ctimer, 120L, 120L);
      } else if (!needTimer && this.ctimer != null) {
         this.ctimer.cancel();
         this.ctimer = null;
         panel.repaint();
      }
   }

   private void paintFocusLine(Graphics2D g2, BasicStroke s1, BasicStroke s2, int x0, int y0, int x1, int y1) {
      g2.setColor(focus_col2);
      g2.setStroke(s1);
      g2.drawLine(x0, y0, x1, y1);
      g2.setColor(focus_col1);
      g2.setStroke(s2);
      g2.drawLine(x0, y0, x1, y1);
   }

   private void paint(Graphics2D g2, Polygon p1, int arc) {
      Path2D p2 = new Double(p1);
      p2.transform(AffineTransform.getRotateInstance((double)arc * Math.PI / 180.0));
      g2.fill(p2);
   }

   private void paint(Graphics2D g2, Polygon p1, Shape circle, Color pcolor, Color ccolor) {
      g2.setColor(pcolor);
      this.paint(g2, p1, 0);
      this.paint(g2, p1, 90);
      this.paint(g2, p1, 180);
      this.paint(g2, p1, 270);
      g2.setColor(ccolor);
      g2.fill(circle);
   }

   private void paintFocus(PaintSaveInterface cmp, Graphics2D g, double xscale, double yscale, gleis focusGleis) {
      Graphics2D g2 = (Graphics2D)g.create();
      GraphicTools.enableGfxAA(g2);
      g2.translate(xscale / 2.0, yscale / 2.0);
      double size = xscale * 2.0 * (1.0 - (double)this.sizecnt / 10.0);
      int border = 3;
      int arrowSize = Math.min((int)size, 5);
      Rectangle2D rect1 = new java.awt.geom.Rectangle2D.Double(-size, -size, size * 2.0, size * 2.0);
      Rectangle2D rect2 = new java.awt.geom.Rectangle2D.Double(-size + 3.0, -size + 3.0, size * 2.0 - 6.0, size * 2.0 - 6.0);
      Shape s1 = new java.awt.geom.Arc2D.Double(rect1, 0.0, 360.0, 0);
      Shape s2 = new java.awt.geom.Arc2D.Double(rect2, 0.0, 360.0, 0);
      Polygon p1 = new Polygon();
      p1.addPoint(-arrowSize, (int)(-size + 3.0 - 1.0));
      p1.addPoint(arrowSize, (int)(-size + 3.0 - 1.0));
      p1.addPoint(0, (int)(-size + 3.0) + arrowSize * 2);
      GeneralPath circle = new GeneralPath(0);
      circle.append(s1, false);
      circle.append(s2, false);
      Graphics2D g3 = (Graphics2D)g2.create();
      g3.translate(3, 3);
      g3.rotate(-this.rotcnt);
      this.paint(g3, p1, circle, shadowColor, shadowColor);
      g3.dispose();
      g2.rotate(-this.rotcnt);
      this.paint(g2, p1, circle, focus_col1, focus_col2);
      g2.dispose();
   }

   private class cntTask extends TimerTask {
      private final PaintSaveInterface panel;

      cntTask(PaintSaveInterface panel) {
         this.panel = panel;
      }

      public void run() {
         gleisbildPainter.this.fcnt++;
         if (gleisbildPainter.this.fcnt >= 28) {
            gleisbildPainter.this.fcnt = 0;
         }

         gleisbildPainter.this.hcnt++;
         if (gleisbildPainter.this.hcnt >= 10) {
            gleisbildPainter.this.hcnt = 0;
         }

         gleisbildPainter.this.rotcnt = gleisbildPainter.this.rotcnt + 0.02;
         if (gleisbildPainter.this.rotcnt >= Math.PI / 2) {
            gleisbildPainter.this.rotcnt = 0.0;
         }

         if (gleisbildPainter.this.sizecnt > 0) {
            gleisbildPainter.this.sizecnt--;
         } else if (gleisbildPainter.this.sizecnt < 0) {
            gleisbildPainter.this.sizecnt++;
         }

         this.panel.repaint();
      }
   }
}

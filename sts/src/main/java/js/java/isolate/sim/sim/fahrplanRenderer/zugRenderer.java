package js.java.isolate.sim.sim.fahrplanRenderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import js.java.isolate.sim.sim.zugUndPlanPanel;
import js.java.isolate.sim.zug.zug;
import js.java.isolate.sim.zug.fahrplanCollection.zugPlan;
import js.java.isolate.sim.zug.fahrplanCollection.zugPlanLine;
import js.java.tools.gui.layout.SimpleOneColumnLayout;

public class zugRenderer extends JPanel implements Scrollable {
   private zug lastZug = null;
   private int zugLine = -1;
   private int zugAZid = -1;
   private final HashMap<Integer, Integer> xCache = new HashMap();
   private final zugUndPlanPanel my_main;

   public zugRenderer(zugUndPlanPanel my_main) {
      this.my_main = my_main;
      this.setDoubleBuffered(true);
      this.setLayout(new SimpleOneColumnLayout());
      this.setBackground(Color.WHITE);
   }

   public void showFahrplan(zug z) {
      if (this.lastZug != null && this.lastZug.getZID_num() != z.getZID_num()) {
         this.zugLine = -1;
         this.zugAZid = -1;
         this.clearXcache();
      }

      this.lastZug = z;
      this.removeAll();
      zugPlan zp = z.getZugDetails();
      this.add(new headerRenderer(this, zp, false));
      if (!zp.userText.isEmpty()) {
         this.add(new hinweisRenderer(this, "Fdl Mitteilung: " + zp.userText, 0, -1));
      }

      boolean col0 = false;
      int i = 0;
      boolean gMode = false;

      for (zugPlanLine zpl : zp.plan) {
         zpl.gMode = gMode;
         this.add(new lineRenderer(this, zpl, col0, true, i));
         gMode = zpl.flagG;
         if (this.zugAZid == zpl.azid) {
            this.zugLine = i;
         }

         col0 = !col0;
         i++;
      }

      if (zp.umleitungText != null) {
         this.add(new umleitungsRenderer(this, zp));
      }

      while (zp.follower != null) {
         zp = zp.follower;
         this.add(new headerRenderer(this, zp, true));
         col0 = false;

         for (zugPlanLine zpl : zp.plan) {
            this.add(new lineRenderer(this, zpl, col0, false, -1));
            col0 = !col0;
         }

         if (zp.umleitungText != null) {
            this.add(new umleitungsRenderer(this, zp));
         }
      }

      this.revalidate();
      this.repaint();
   }

   public void refresh() {
      if (this.lastZug != null) {
         if (this.my_main.containsZug(this.lastZug.getZID_num())) {
            this.showFahrplan(this.lastZug);
         } else {
            this.lastZug = null;
         }
      }
   }

   void setUnterzug(int u, int azid) {
      this.zugLine = u;
      this.zugAZid = azid;

      for (int i = 0; i < this.getComponentCount(); i++) {
         this.getComponent(i).repaint();
      }
   }

   void gotoZid(int zid) {
      this.my_main.selectZug(zid);
   }

   int getAZid() {
      return this.zugAZid;
   }

   public int getUnterzug() {
      return this.zugLine > 0 ? this.zugLine : 0;
   }

   public int getUnterzugAZid() {
      return this.zugLine > 0 ? this.zugAZid : this.lastZug.getCurAzid();
   }

   public Dimension getPreferredScrollableViewportSize() {
      return this.getPreferredSize();
   }

   public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
      return 10;
   }

   public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
      return visibleRect.height / 2;
   }

   public boolean getScrollableTracksViewportWidth() {
      return true;
   }

   public boolean getScrollableTracksViewportHeight() {
      return false;
   }

   private void clearXcache() {
      this.xCache.clear();
   }

   void setXvalue(int key, int x) {
      this.xCache.put(key, x);
   }

   int getXvalue(int key) {
      return this.xCache.containsKey(key) ? (Integer)this.xCache.get(key) : 0;
   }
}

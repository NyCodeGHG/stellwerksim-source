package js.java.isolate.sim.sim.gruppentasten;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;
import js.java.isolate.sim.gleis.gleis;
import js.java.tools.gui.GraphicTools;

public class TasterImage implements Icon {
   private static final int SIZE = 12;
   private final boolean pressed;
   private final boolean rollover;
   private boolean lightOn = false;

   public TasterImage(boolean pressed, boolean rollover) {
      super();
      this.pressed = pressed;
      this.rollover = rollover;
   }

   public void paintIcon(Component c, Graphics g, int x, int y) {
      Graphics2D g2 = (Graphics2D)g;
      GraphicTools.enableGfxAA(g2);
      int o = 11 - (this.pressed ? 2 : 0);
      int o2 = o - 4;
      if (!this.pressed) {
         g.setColor(gleis.colors.col_stellwerk_knopfseite);
         g.fillOval(x + 1, y + 1, o, o);
      }

      g.setColor(gleis.colors.col_stellwerk_schwarz);
      g.fillOval(x + (this.pressed ? 2 : 0), y + (this.pressed ? 2 : 0), o, o);
      if (this.lightOn) {
         g.setColor(gleis.colors.col_stellwerk_rot_umleuchtung);
         g.fillOval(x + (this.pressed ? 2 : 0) - 2, y + (this.pressed ? 2 : 0) - 2, o + 4, o + 4);
         g.setColor(gleis.colors.col_stellwerk_rot_beleuchtet);
      } else if (this.rollover) {
         g.setColor(gleis.colors.col_stellwerk_rot.brighter());
      } else {
         g.setColor(gleis.colors.col_stellwerk_rot);
      }

      g.fillOval(x + 2 + (this.pressed ? 2 : 0), y + 2 + (this.pressed ? 2 : 0), o2, o2);
   }

   public int getIconWidth() {
      return 12;
   }

   public int getIconHeight() {
      return 12;
   }

   void setLight(boolean on) {
      this.lightOn = on;
   }
}

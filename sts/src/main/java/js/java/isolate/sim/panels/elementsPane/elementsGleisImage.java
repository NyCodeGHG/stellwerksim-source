package js.java.isolate.sim.panels.elementsPane;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;
import js.java.tools.gui.GraphicTools;

public class elementsGleisImage implements Icon {
   private static final int HSIZE = 12;
   private static final int WSIZE = 12;
   private static final int WSIZE4 = 4;
   private static final Color schiene = new Color(85, 85, 85);
   private static final Color schwelle = new Color(68, 0, 0);

   public elementsGleisImage() {
      super();
   }

   public void paintIcon(Component c, Graphics g, int x, int y) {
      Graphics2D g2 = (Graphics2D)g;
      GraphicTools.enableGfxAA(g2);
      g2.draw3DRect(x, y, 12, 12, false);
      g2.setColor(schwelle);

      for(int i = 2; i < 10; i += 3) {
         g2.fillRect(2, i, 10, 1);
      }

      g2.setColor(schiene);
      g2.fillRect(4, 1, 2, 10);
      g2.fillRect(8, 1, 2, 10);
   }

   public int getIconWidth() {
      return 12;
   }

   public int getIconHeight() {
      return 12;
   }
}

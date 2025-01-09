package js.java.tools.gui;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class fadeablePanel extends JPanel {
   private int alpha = 100;

   public void fadeTo(int f) {
      if (f != this.alpha) {
         this.alpha = f;
         this.repaint();
      }
   }

   public void paint(Graphics g) {
      Graphics2D g2d = (Graphics2D)g;
      Composite oldComp = g2d.getComposite();
      Composite alphaComp = AlphaComposite.getInstance(3, (float)this.alpha / 100.0F);
      g2d.setComposite(alphaComp);
      super.paint(g2d);
      g2d.setComposite(oldComp);
   }
}

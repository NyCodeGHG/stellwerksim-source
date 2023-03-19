package js.java.tools.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.Timer;

public class SmoothViewport extends JViewport implements ActionListener {
   private Timer scrollTimer = new Timer(70, this);
   private Point gotoPoint = null;
   private int ownPosition = 0;
   private boolean inAction = false;
   private static boolean enableScrollAnimation = true;

   public SmoothViewport() {
      super();
   }

   public SmoothViewport(JComponent p) {
      super();
      this.add(p);
   }

   public static void setScrollAnimMode(boolean enable) {
      enableScrollAnimation = enable;
   }

   public void setViewPosition(Point p) {
      if (enableScrollAnimation) {
         if (this.ownPosition > 0) {
            this.gotoPoint = new Point(p);
            this.scrollTimer.start();
         } else {
            if (!this.inAction && !p.equals(this.getViewPosition())) {
               this.scrollTimer.stop();
               this.gotoPoint = null;
            }

            super.setViewPosition(p);
         }
      } else {
         super.setViewPosition(p);
      }
   }

   public void scrollRectToVisible(Rectangle contentRect) {
      ++this.ownPosition;
      super.scrollRectToVisible(contentRect);
      --this.ownPosition;
   }

   public void actionPerformed(ActionEvent e) {
      this.inAction = true;

      try {
         Point c = super.getViewPosition();
         if (this.gotoPoint == null || Math.abs(c.x - this.gotoPoint.x) < 2 && Math.abs(c.y - this.gotoPoint.y) < 2) {
            if (this.gotoPoint != null) {
               super.setViewPosition(this.gotoPoint);
            }

            this.gotoPoint = null;
            this.scrollTimer.stop();
         } else {
            c.x += (this.gotoPoint.x - c.x) / 2;
            c.y += (this.gotoPoint.y - c.y) / 2;
            super.setViewPosition(c);
         }
      } finally {
         this.inAction = false;
      }
   }
}

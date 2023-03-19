package js.java.isolate.landkarteneditor;

import java.awt.Point;
import java.awt.event.MouseEvent;

class editKnoten extends editBase {
   editKnoten(control main, knotenList klist, bahnhofList bhflist, landkarte lk) {
      super(main, klist, bhflist, lk);
   }

   public void mouseClicked(MouseEvent e) {
   }

   public void mousePressed(MouseEvent e) {
      if (this.lk.isEnabled()) {
         knoten k = this.findKnoten(e.getX(), e.getY());
         this.my_main.setSelectedKnoten(k);
      }
   }

   public void mouseReleased(MouseEvent e) {
      this.my_main.setMoveKnoten(null);
   }

   public void mouseEntered(MouseEvent e) {
   }

   public void mouseExited(MouseEvent e) {
   }

   public void mouseDragged(MouseEvent e) {
      if (this.my_main.getSelectedKnoten() != null) {
         knoten k = this.my_main.getSelectedKnoten();
         this.my_main.setMoveKnoten(k);
         Point p = this.calcPosition(e.getX(), e.getY());
         k.setLocationFromScreen(p);
         this.my_main.repaint();
      }
   }

   public void mouseMoved(MouseEvent e) {
   }
}

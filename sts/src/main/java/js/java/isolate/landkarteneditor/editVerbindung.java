package js.java.isolate.landkarteneditor;

import java.awt.event.MouseEvent;

class editVerbindung extends editBase {
   private knoten startk = null;

   editVerbindung(control main, knotenList klist, bahnhofList bhflist, landkarte lk) {
      super(main, klist, bhflist, lk);
   }

   public void mouseClicked(MouseEvent e) {
   }

   public void mousePressed(MouseEvent e) {
      if (this.lk.isEnabled()) {
         knoten k = this.findKnoten(e.getX(), e.getY());
         this.startk = k;
         if (k == null) {
            verbindung v = this.findVerbindung(e.getX(), e.getY());
            this.my_main.setSelectedVerbindung(v);
         } else {
            this.my_main.setSelectedVerbindung(null);
            this.lk.verbinder(this.startk, this.calcPosition(e.getX(), e.getY()), null);
         }
      }
   }

   public void mouseReleased(MouseEvent e) {
      if (this.startk != null) {
         knoten k = this.findKnoten(e.getX(), e.getY());
         if (k != null && k != this.startk) {
            this.my_main.setSelectedVerbindung(this.klist.addVerbindung(this.startk.getKid(), k.getKid()));
         }
      }

      this.lk.verbinder(null, null, null);
      this.startk = null;
   }

   public void mouseEntered(MouseEvent e) {
   }

   public void mouseExited(MouseEvent e) {
   }

   public void mouseDragged(MouseEvent e) {
      if (this.startk != null) {
         knoten k = this.findKnoten(e.getX(), e.getY());
         if (k == this.startk) {
            k = null;
         }

         this.lk.verbinder(this.startk, this.calcPosition(e.getX(), e.getY()), k);
      }
   }

   public void mouseMoved(MouseEvent e) {
   }
}

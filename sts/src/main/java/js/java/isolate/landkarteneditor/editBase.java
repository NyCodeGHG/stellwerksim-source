package js.java.isolate.landkarteneditor;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Iterator;

abstract class editBase implements MouseListener, MouseMotionListener {
   protected control my_main;
   protected knotenList klist;
   protected bahnhofList bhflist;
   protected landkarte lk;
   private Rectangle lineIntersec = new Rectangle(0, 0, 5, 5);

   editBase(control main, knotenList klist, bahnhofList bhflist, landkarte lk) {
      this.my_main = main;
      this.klist = klist;
      this.bhflist = bhflist;
      this.lk = lk;
   }

   protected int translateX(int x) {
      return x + this.lk.getOffset().x;
   }

   protected int translateY(int y) {
      return y + this.lk.getOffset().y;
   }

   protected knoten findKnoten(int x, int y) {
      x = this.translateX(x);
      y = this.translateY(y);
      Iterator<knoten> kit = this.klist.knotenIterator();

      while (kit.hasNext()) {
         knoten k = (knoten)kit.next();
         if (k.contains(x, y)) {
            return k;
         }
      }

      return null;
   }

   protected verbindung findVerbindung(int x, int y) {
      x = this.translateX(x);
      y = this.translateY(y);
      this.lineIntersec.setLocation(x, y);
      Iterator<verbindung> kit = this.klist.verbindungIterator();

      while (kit.hasNext()) {
         verbindung k = (verbindung)kit.next();
         if (k.getShape().intersects(this.lineIntersec)) {
            return k;
         }
      }

      return null;
   }

   protected Point calcPosition(int x, int y) {
      x = this.translateX(x);
      y = this.translateY(y);
      return new Point(x, y);
   }
}

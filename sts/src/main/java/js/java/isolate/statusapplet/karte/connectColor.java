package js.java.isolate.statusapplet.karte;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Map.Entry;

public class connectColor {
   final Color col;
   final LinkedList<Integer> markVkids = new LinkedList();
   private final kartePanel zp;

   connectColor(kartePanel _zp, Color c) {
      this.zp = _zp;
      this.col = c;
   }

   void resetMark() {
      this.markVkids.clear();
   }

   void addMarkAid(int aid1, int aid2) {
      for (Entry<Integer, karten_container> e : this.zp.kids.entrySet()) {
         int vkid = (Integer)e.getKey();
         karten_container k = (karten_container)e.getValue();
         karten_container k1 = (karten_container)this.zp.namen.get(k.kid1);
         karten_container k2 = (karten_container)this.zp.namen.get(k.kid2);
         if (k1 != null && k2 != null && (k1.aaid == aid1 && k2.aaid == aid2 || k1.aaid == aid2 && k2.aaid == aid1)) {
            this.markVkids.add(vkid);
         }
      }
   }
}

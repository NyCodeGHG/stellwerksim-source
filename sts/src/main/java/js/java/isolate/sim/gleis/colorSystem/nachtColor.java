package js.java.isolate.sim.gleis.colorSystem;

import java.awt.Color;
import java.util.Map.Entry;

class nachtColor extends colorStruct {
   private final colorStruct baseColor;

   nachtColor(colorStruct base) {
      this.baseColor = base;
      base.cloneTo(this);

      for (Entry<String, Color> e : this.col_stellwerk_backmulti.entrySet()) {
         String s = (String)e.getKey();
         Color c = (Color)e.getValue();
         if (s.compareTo("normal") == 0) {
            this.col_stellwerk_back = c.darker().darker();
            e.setValue(this.col_stellwerk_back);
         } else {
            e.setValue(c.darker().darker());
         }
      }

      this.col_stellwerk_frei = this.col_stellwerk_frei.darker().darker();
      this.col_stellwerk_bstgfläche = this.col_stellwerk_bstgfläche.darker().darker();
   }

   public colorStruct getOldColor() {
      return this.baseColor;
   }
}

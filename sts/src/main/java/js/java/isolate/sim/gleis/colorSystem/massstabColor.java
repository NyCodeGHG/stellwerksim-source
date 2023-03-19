package js.java.isolate.sim.gleis.colorSystem;

import java.awt.Color;
import java.util.Map.Entry;

class massstabColor extends colorStruct {
   massstabColor(colorStruct base) {
      super();
      base.cloneTo(this);

      for(Entry<String, Color> e : this.col_stellwerk_backmulti.entrySet()) {
         String s = (String)e.getKey();
         Color c = (Color)e.getValue();
         if (s.compareTo("normal") == 0) {
            e.setValue(c.darker());
         } else {
            e.setValue(c.darker().darker().darker().darker());
         }
      }

      this.col_stellwerk_back = this.col_stellwerk_back.darker();
      this.col_stellwerk_raster = this.col_stellwerk_raster.darker();
      this.col_stellwerk_gleis = this.col_stellwerk_gleis.darker();
      this.col_stellwerk_frei = this.col_stellwerk_frei.darker();
      this.col_stellwerk_displayoff = this.col_stellwerk_displayoff.darker();
      this.col_stellwerk_reserviert = this.col_stellwerk_reserviert.darker();
      this.col_stellwerk_belegt = this.col_stellwerk_belegt.darker();
      this.col_stellwerk_schwarz = this.col_stellwerk_schwarz.darker();
      this.col_stellwerk_rotein = this.col_stellwerk_rotein.darker();
      this.col_stellwerk_rotaus = this.col_stellwerk_rotaus.darker();
      this.col_stellwerk_rot = this.col_stellwerk_rot.darker();
      this.col_stellwerk_gruenein = this.col_stellwerk_gruenein.darker();
      this.col_stellwerk_gruenaus = this.col_stellwerk_gruenaus.darker();
      this.col_stellwerk_weiss = this.col_stellwerk_weiss.darker();
      this.col_stellwerk_defekt = this.col_stellwerk_defekt.darker();
      this.col_stellwerk_grau = this.col_stellwerk_grau.darker();
      this.col_stellwerk_geländer = this.col_stellwerk_geländer.darker();
      this.col_stellwerk_zs1 = this.col_stellwerk_zs1.darker();
      this.col_stellwerk_nummer = this.col_stellwerk_nummer.darker();
      this.col_stellwerk_bstgfläche = this.col_stellwerk_bstgfläche.darker();
   }
}

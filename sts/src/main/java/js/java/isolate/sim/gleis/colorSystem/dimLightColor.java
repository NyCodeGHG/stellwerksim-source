package js.java.isolate.sim.gleis.colorSystem;

import java.awt.Color;

class dimLightColor extends colorStruct {
   dimLightColor(colorStruct base, int level) {
      super();
      base.cloneTo(this);
      this.col_stellwerk_belegt = this.dimColor(this.col_stellwerk_belegt_backup, this.col_stellwerk_frei, level);
      this.col_stellwerk_reserviert = this.dimColor(this.col_stellwerk_reserviert_backup, this.col_stellwerk_frei, level);
      this.col_stellwerk_defekt = this.dimColor(this.col_stellwerk_defekt_backup, this.col_stellwerk_frei, level);
      this.col_stellwerk_zs1 = this.dimColor(this.col_stellwerk_zs1_backup, this.col_stellwerk_frei, level);
      this.col_stellwerk_gelbein = this.dimColor(this.col_stellwerk_gelbein_backup, this.col_stellwerk_gelbaus, level);
      this.col_stellwerk_gruenein = this.dimColor(this.col_stellwerk_gruenein_backup, this.col_stellwerk_gruenaus, level);
      this.col_stellwerk_rotein = this.dimColor(this.col_stellwerk_rotein_backup, this.col_stellwerk_rotaus, level);
      this.col_stellwerk_zugdisplay = this.dimColor(this.col_stellwerk_zugdisplay_backup, this.col_stellwerk_rotaus, level);
      this.col_stellwerk_aiddisplay = this.dimColor(this.col_stellwerk_aiddisplay_backup, this.col_stellwerk_gelbaus, level);
   }

   private int dimVal(int src, int dest, int level) {
      return src - (src - dest) * level / 20;
   }

   private Color dimColor(Color src, Color dest, int level) {
      return new Color(
         this.dimVal(src.getRed(), dest.getRed(), level),
         this.dimVal(src.getGreen(), dest.getGreen(), level),
         this.dimVal(src.getBlue(), dest.getBlue(), level)
      );
   }
}

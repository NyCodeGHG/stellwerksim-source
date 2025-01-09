package js.java.isolate.sim.gleis.colorSystem;

import java.awt.Color;

class alternativeColor extends colorStruct {
   alternativeColor(colorStruct base) {
      base.cloneTo(this);
      this.col_stellwerk_rotein = new Color(102, 102, 255);
      this.col_stellwerk_rotaus = new Color(17, 17, 102);
      this.col_stellwerk_rot = new Color(68, 68, 221);
      this.col_stellwerk_rot_locked = new Color(68, 68, 221, 187);
      this.col_stellwerk_zugdisplay = new Color(102, 102, 255);
      this.col_stellwerk_belegt = new Color(34, 153, 255);
      this.backupColors();
   }
}

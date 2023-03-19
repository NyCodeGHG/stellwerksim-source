package js.java.isolate.statusapplet.players;

import js.java.isolate.sim.gleis.colorSystem.gleisColor;

class players_colors extends gleisColor {
   players_colors() {
      super(gleisColor.COLORTYPE.SIMULATOR_TAG);
      instance = this;
      this.mycolorbase.col_stellwerk_signalnummerhgr = this.mycolorbase.col_stellwerk_signalnummerhgr.darker();
      this.mycolorbase.col_stellwerk_back = this.mycolorbase.col_stellwerk_grau.darker();
   }
}

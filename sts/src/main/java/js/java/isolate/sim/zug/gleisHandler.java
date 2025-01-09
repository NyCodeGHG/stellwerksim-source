package js.java.isolate.sim.zug;

import js.java.tools.ColorText;

class gleisHandler extends zugHandler {
   @Override
   protected int compareImpl(ZugColorText ct, frozenZug z, ZugColorText other, frozenZug otherz) {
      return z.getGleis().compareToIgnoreCase(otherz.getGleis());
   }

   @Override
   void update(ColorText ct, zug z) {
      if (z.flags.hadFlag('D')) {
         ct.setText("<html><i>" + z.getGleis() + "</i></html>");
      } else if (z.flags.hadFlag('R')) {
         ct.setText("<html><b>" + z.getGleis() + "</b></html>");
      } else {
         ct.setText(z.getGleis());
      }
   }
}

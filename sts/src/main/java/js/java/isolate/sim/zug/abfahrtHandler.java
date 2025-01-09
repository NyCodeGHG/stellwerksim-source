package js.java.isolate.sim.zug;

import js.java.tools.ColorText;

class abfahrtHandler extends zugHandler {
   @Override
   protected int compareImpl(ZugColorText ct, frozenZug z, ZugColorText other, frozenZug otherz) {
      return z.getAbfahrt().compareTo(otherz.getAbfahrt());
   }

   @Override
   void update(ColorText ct, zug z) {
      if (z.flags.hadFlag('A')) {
         ct.setText("<html><i>" + z.getAbfahrt() + "</i></html>");
      } else {
         ct.setText(z.getAbfahrt());
      }
   }
}

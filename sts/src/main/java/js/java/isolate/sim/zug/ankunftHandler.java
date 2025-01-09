package js.java.isolate.sim.zug;

import js.java.tools.ColorText;

class ankunftHandler extends zugHandler {
   @Override
   protected int compareImpl(ZugColorText ct, frozenZug z, ZugColorText other, frozenZug otherz) {
      return z.compareTo(otherz);
   }

   @Override
   void update(ColorText ct, zug z) {
      String t = z.getAnkunft();
      ct.setText(t);
   }
}

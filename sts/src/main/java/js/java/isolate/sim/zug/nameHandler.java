package js.java.isolate.sim.zug;

import js.java.tools.ColorText;

class nameHandler extends zugHandler {
   @Override
   protected int compareImpl(ZugColorText ct, frozenZug z, ZugColorText other, frozenZug otherz) {
      return z.getName().compareToIgnoreCase(otherz.getName());
   }

   @Override
   void update(ColorText ct, zug z) {
      ct.setText(z.getName(), z.getSpezialName());
   }
}

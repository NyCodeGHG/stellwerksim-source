package js.java.isolate.sim.zug;

import js.java.tools.ColorText;

class nachHandler extends zugHandler {
   nachHandler() {
      super();
   }

   @Override
   protected int compareImpl(ZugColorText ct, frozenZug z, ZugColorText other, frozenZug otherz) {
      if (z.getNach().isEmpty() && !otherz.getNach().isEmpty()) {
         return 1;
      } else {
         return otherz.getNach().isEmpty() && !z.getNach().isEmpty() ? -1 : z.getNach().compareToIgnoreCase(otherz.getNach());
      }
   }

   @Override
   void update(ColorText ct, zug z) {
      String t = z.getNach();
      if (z.aus_stw != null) {
         t = "<html><i>" + t + "</i></html>";
         ct.setBGColor(8);
      }

      ct.setText(t);
   }
}

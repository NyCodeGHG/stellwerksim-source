package js.java.isolate.sim.zug;

import js.java.tools.ColorText;

class vonHandler extends zugHandler {
   vonHandler() {
      super();
   }

   @Override
   protected int compareImpl(ZugColorText ct, frozenZug z, ZugColorText other, frozenZug otherz) {
      if (z.getVon().isEmpty() && !otherz.getVon().isEmpty()) {
         return 1;
      } else {
         return otherz.getVon().isEmpty() && !z.getVon().isEmpty() ? -1 : z.getVon().compareToIgnoreCase(otherz.getVon());
      }
   }

   @Override
   void update(ColorText ct, zug z) {
      String add = "";
      if (z.ein_enr_changed) {
         add = " *";
      }

      String t = z.getVon() + add;
      if (z.ein_stw != null) {
         t = "<html><i>" + t + "</i></html>";
         ct.setBGColor(8);
      }

      ct.setText(t);
   }
}

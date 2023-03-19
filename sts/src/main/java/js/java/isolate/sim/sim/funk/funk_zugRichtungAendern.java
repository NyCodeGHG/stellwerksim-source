package js.java.isolate.sim.sim.funk;

import js.java.isolate.sim.sim.zugUndPlanPanel;
import js.java.isolate.sim.zug.zug;

public class funk_zugRichtungAendern extends funk_zugBase {
   public funk_zugRichtungAendern(zugUndPlanPanel.funkAdapter a, zug z, int unterzug) {
      super("Zug " + z.getSpezialName() + ": Richtung ändern", a, z, unterzug);
      if (z.isVisible() && z.firstSignalPassed()) {
         this.addValueItem(new funkAuftragBase.funkValueItem("dauerhaft ändern", 1, "R-Flag"));
      }

      if (z.isVisible() && !z.isAnyDirection() && !z.getFlags().hadFlag('L') && !z.getFlags().hadFlag('W') && z.firstSignalPassed()) {
         this.addValueItem(new funkAuftragBase.funkValueItem("Lok umsetzen", 2, "L-Flag"));
      }
   }

   @Override
   public void selected(funkAuftragBase.funkValueItem sel) {
      zug.RICHTUNGWECHELN v;
      switch(sel.id) {
         case 1:
         default:
            v = zug.RICHTUNGWECHELN.DAUERHAFT;
            break;
         case 2:
            v = zug.RICHTUNGWECHELN.LOK_UMSETZEN;
      }

      this.z.richtungWechseln(v);
      this.my_main.showFahrplan(this.z);
   }
}

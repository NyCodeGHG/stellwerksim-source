package js.java.isolate.sim.sim.funk;

import js.java.isolate.sim.sim.zugUndPlanPanel;
import js.java.isolate.sim.zug.zug;

public class funk_zugWeiterfahren extends funk_zugBase {
   public funk_zugWeiterfahren(zugUndPlanPanel.funkAdapter a, zug z, int unterzug) {
      super("Zug " + z.getSpezialName() + ": fahren", a, z, unterzug);
      this.addValueItem(new funkAuftragBase.funkValueItem("weiterfahren", 1));
      this.addValueItem(new funkAuftragBase.funkValueItem("vorzeitige Abfahrt", 2, "A-Flag"));
   }

   @Override
   public void selected(funkAuftragBase.funkValueItem sel) {
      if (sel.id == 1) {
         this.z.setWeiterfahren();
      } else {
         this.z.setAbfahrtbefehl();
      }

      this.my_main.showFahrplan(this.z);
   }
}

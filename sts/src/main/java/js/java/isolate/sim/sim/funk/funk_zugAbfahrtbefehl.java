package js.java.isolate.sim.sim.funk;

import js.java.isolate.sim.sim.zugUndPlanPanel;
import js.java.isolate.sim.zug.zug;

@Deprecated
public class funk_zugAbfahrtbefehl extends funk_zugBase {
   public funk_zugAbfahrtbefehl(zugUndPlanPanel.funkAdapter a, zug z, int unterzug) {
      super("Zug " + z.getSpezialName() + ": vorzeitige Abfahrt", a, z, unterzug);
      this.addValueItem(new funkAuftragBase.funkValueItem("jetzt", -1, "A-Flag"));
   }

   @Override
   public void selected(funkAuftragBase.funkValueItem sel) {
      this.z.setAbfahrtbefehl();
      this.my_main.showFahrplan(this.z);
   }
}

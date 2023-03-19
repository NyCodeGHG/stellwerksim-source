package js.java.isolate.sim.sim.funk;

import js.java.isolate.sim.sim.zugUndPlanPanel;
import js.java.isolate.sim.zug.zug;

public class funk_zugGeschwindigkeit extends funk_zugBase {
   public funk_zugGeschwindigkeit(zugUndPlanPanel.funkAdapter a, zug z, int unterzug) {
      super("Zug " + z.getSpezialName() + ": Geschwindigkeit", a, z, unterzug);
      this.addValueItem(new funkAuftragBase.funkValueItem("langsam", 1));
      this.addValueItem(new funkAuftragBase.funkValueItem("lt. Fahrplan", 0));
   }

   @Override
   public void selected(funkAuftragBase.funkValueItem sel) {
      this.z.setMaxTempo(sel.id);
      this.my_main.showFahrplan(this.z);
   }
}

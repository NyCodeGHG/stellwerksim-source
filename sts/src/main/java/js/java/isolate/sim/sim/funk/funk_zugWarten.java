package js.java.isolate.sim.sim.funk;

import java.util.Iterator;
import js.java.isolate.sim.sim.zugUndPlanPanel;
import js.java.isolate.sim.zug.zug;

public class funk_zugWarten extends funk_zugBase {
   public funk_zugWarten(zugUndPlanPanel.funkAdapter a, zug z, int unterzug) {
      super("Zug: warten", a, z, unterzug);
      if (z != null) {
         this.addValueItem(new funkAuftragBase.funkValueItem(z.getSpezialName() + " warten", 1));
      }

      this.addValueItem(new funkAuftragBase.funkValueItem("alle warten", -1));
   }

   @Override
   public void selected(funkAuftragBase.funkValueItem sel) {
      if (sel.id == -1) {
         Iterator<zug> it = this.my_main.zugIterator();

         while (it.hasNext()) {
            zug zz = (zug)it.next();
            zz.warten();
         }
      } else {
         this.z.warten();
      }

      if (this.z != null) {
         this.my_main.showFahrplan(this.z);
      }
   }
}

package js.java.isolate.sim.sim.funk;

import java.util.Iterator;
import java.util.TreeSet;
import js.java.isolate.sim.sim.zugUndPlanPanel;
import js.java.isolate.sim.zug.zug;

public class funk_zugGleisaenderung extends funk_zugBase {
   public funk_zugGleisaenderung(zugUndPlanPanel.funkAdapter a, zug z, int unterzugAZid) {
      super("Zug " + z.getSpezialName() + ": Gleisänderung nach", a, z, unterzugAZid);
      if (z.allowesÄnderung()) {
         TreeSet<String> bstgs = this.my_main.alleBahnsteige().getAlternativebahnsteigeOf(z.getZielgleisByAZid(unterzugAZid));
         Iterator<String> it = bstgs.iterator();

         for (int i = 0; it.hasNext(); i++) {
            String b = (String)it.next();
            this.addValueItem(new funkAuftragBase.funkValueItem(b, i));
         }

         this.addValueItem(new funkAuftragBase.funkValueItem("lt. Fahrplan", -1));
      } else {
         this.addValueItem(new funkAuftragBase.funkValueItem("lt. Fahrplan, keine Gleisänderung erlaubt", -1));
      }
   }

   @Override
   public void selected(funkAuftragBase.funkValueItem sel) {
      if (sel.id == -1) {
         this.z.setGleisByAZid(null, this.unterzugAZid);
      } else {
         this.z.setGleisByAZid(sel.text, this.unterzugAZid);
      }

      this.my_main.showFahrplan(this.z);
   }
}

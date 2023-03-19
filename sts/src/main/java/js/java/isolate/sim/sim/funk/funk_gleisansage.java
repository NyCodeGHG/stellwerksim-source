package js.java.isolate.sim.sim.funk;

import js.java.isolate.sim.sim.TEXTTYPE;
import js.java.isolate.sim.sim.zugUndPlanPanel;

public class funk_gleisansage extends funkAuftragBase {
   private final int d;

   public funk_gleisansage(zugUndPlanPanel.funkAdapter a) {
      super("Gleisansage", a);
      this.d = a.my_gleisbild().getAid() % 4 + 1;
      int i = 0;

      for(String b : this.my_main.alleBahnsteige().getAlleBahnsteig()) {
         this.addValueItem(new funkAuftragBase.funkValueItem(b, i));
         ++i;
      }
   }

   @Override
   public void selected(funkAuftragBase.funkValueItem sel) {
      this.my_main.playDingdong(this.d);
      this.my_main.showText("Bitte nach dem Signalton Ansage für Gleis <i>" + sel.text + "</i> sprechen.", TEXTTYPE.REPLY, this);
   }
}

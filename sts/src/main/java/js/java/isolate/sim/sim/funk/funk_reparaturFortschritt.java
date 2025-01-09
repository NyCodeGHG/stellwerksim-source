package js.java.isolate.sim.sim.funk;

import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.sim.zugUndPlanPanel;

public class funk_reparaturFortschritt extends funkAuftragBase {
   public funk_reparaturFortschritt(zugUndPlanPanel.funkAdapter a) {
      super("Reparaturteam: Fortschritt erfragen", a);

      for (event e : event.events) {
         String s = e.funkName();
         if (s != null) {
            this.addValueItem(new funkAuftragBase.dataFunkValueItem<>(s, 0, e));
         }
      }
   }

   @Override
   public void selected(funkAuftragBase.funkValueItem sel) {
      event e = (event)((funkAuftragBase.dataFunkValueItem)sel).e;
      if (e != null) {
         String s = e.funkAntwort();
         if (s != null) {
            this.my_main.showText_replay(s, e);
         }
      }
   }
}

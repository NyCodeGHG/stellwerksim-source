package js.java.isolate.sim.sim.funk;

import js.java.isolate.sim.sim.zugUndPlanPanel;
import js.java.isolate.sim.zug.zug;

public abstract class funk_zugBase extends funkAuftragBase {
   protected final zug z;
   protected final int unterzugAZid;

   protected funk_zugBase(String t, zugUndPlanPanel.funkAdapter a, zug z, int unterzugAZid) {
      super(t, a);
      this.z = z;
      this.unterzugAZid = unterzugAZid;
   }
}

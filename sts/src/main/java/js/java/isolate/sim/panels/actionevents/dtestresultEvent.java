package js.java.isolate.sim.panels.actionevents;

import java.util.TreeSet;
import js.java.isolate.sim.dtest.dtestresult;
import js.java.tools.actions.AbstractEvent;

public class dtestresultEvent extends AbstractEvent<TreeSet<dtestresult>> {
   public dtestresultEvent(TreeSet<dtestresult> m) {
      super(m);
   }

   public TreeSet<dtestresult> getResults() {
      return (TreeSet<dtestresult>)this.getSource();
   }
}

package js.java.isolate.sim.zug;

import js.java.tools.NumColorText;

public class gleisNameComparator implements ZugTableComparator {
   public int compare(Object o1, Object o2) {
      return ((NumColorText)o1).compareTo(o2);
   }
}

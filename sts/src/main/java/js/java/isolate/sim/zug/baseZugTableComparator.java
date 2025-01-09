package js.java.isolate.sim.zug;

public class baseZugTableComparator implements ZugTableComparator {
   public int compare(Object o1, Object o2) {
      if (o1 == null && o2 == null) {
         return 0;
      } else if (o1 == null) {
         return 1;
      } else {
         return o2 == null ? -1 : 0;
      }
   }
}

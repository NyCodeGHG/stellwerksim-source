package js.java.isolate.sim.zug;

public class zugDefaultComparator extends baseZugTableComparator implements ZugTableComparator {
   public zugDefaultComparator() {
      super();
   }

   @Override
   public int compare(Object o1, Object o2) {
      int r = super.compare(o1, o2);
      if (r != 0) {
         return r;
      } else {
         ZugColorText oo1 = (ZugColorText)o1;
         ZugColorText oo2 = (ZugColorText)o2;
         return oo1.getHandler().compare(oo1, oo1.getFZug(), oo2, oo2.getFZug());
      }
   }
}

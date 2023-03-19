package js.java.isolate.sim.zug;

public class gleisDefaultComparator extends baseZugTableComparator implements ZugTableComparator {
   private final ZugTableComparator ztc;

   gleisDefaultComparator(ZugTableComparator ztc) {
      super();
      this.ztc = ztc;
   }

   @Override
   public int compare(Object o1, Object o2) {
      int r = super.compare(o1, o2);
      if (r != 0) {
         return r;
      } else if (o1 instanceof gleisModel.emptyColorText && !(o2 instanceof gleisModel.emptyColorText)) {
         return 1;
      } else if (!(o1 instanceof gleisModel.emptyColorText) && o2 instanceof gleisModel.emptyColorText) {
         return -1;
      } else {
         return o1 instanceof gleisModel.emptyColorText && o2 instanceof gleisModel.emptyColorText
            ? ((gleisModel.emptyColorText)o1).getGleis().compareTo(((gleisModel.emptyColorText)o2).getGleis())
            : this.ztc.compare(o1, o2);
      }
   }
}

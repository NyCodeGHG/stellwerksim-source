package js.java.isolate.gleisbelegung;

import java.util.TreeSet;

class zuglist extends TreeSet<zuggleis> {
   public final int zid;

   zuglist(int _zid) {
      this.zid = _zid;
   }
}

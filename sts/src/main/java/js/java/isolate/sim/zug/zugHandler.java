package js.java.isolate.sim.zug;

import js.java.tools.ColorText;

abstract class zugHandler {
   int compare(ZugColorText ct, frozenZug z, ZugColorText other, frozenZug otherz) {
      int r = this.compareImpl(ct, z, other, otherz);
      return r != 0 ? r : z.zid - otherz.zid;
   }

   protected abstract int compareImpl(ZugColorText var1, frozenZug var2, ZugColorText var3, frozenZug var4);

   abstract void update(ColorText var1, zug var2);
}

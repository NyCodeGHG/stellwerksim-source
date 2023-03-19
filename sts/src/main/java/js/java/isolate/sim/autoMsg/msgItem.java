package js.java.isolate.sim.autoMsg;

import js.java.isolate.sim.gleis.gleis;

class msgItem {
   gleis signal = null;
   String ziel = null;
   String zielnachbar = "";

   msgItem() {
      super();
   }

   msgItem(msgItem mi) {
      super();
      this.signal = mi.signal;
      this.ziel = mi.ziel;
      this.zielnachbar = mi.zielnachbar;
   }

   public String toString() {
      String r = "";
      if (this.signal != null) {
         r = this.signal.getElementName();
      }

      return r + ";" + (this.ziel != null ? this.ziel : "") + ";" + this.zielnachbar;
   }
}

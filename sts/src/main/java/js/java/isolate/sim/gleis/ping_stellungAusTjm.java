package js.java.isolate.sim.gleis;

import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class ping_stellungAusTjm extends pingBase {
   ping_stellungAusTjm(pingBase parent) {
      super(parent);
   }

   ping_stellungAusTjm() {
      super(null);
   }

   @Override
   public boolean ping(gleis gl) {
      boolean ret = false;
      if (gl.fdata.stellung == gleisElements.Stellungen.aus) {
         gl.tjmAdd();
         ret = true;
      }

      return super.ping(gl) || ret;
   }
}

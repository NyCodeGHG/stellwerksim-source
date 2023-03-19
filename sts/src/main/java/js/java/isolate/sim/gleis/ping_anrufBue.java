package js.java.isolate.sim.gleis;

import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class ping_anrufBue extends pingBase {
   ping_anrufBue(pingBase parent) {
      super(parent);
   }

   ping_anrufBue() {
      super(null);
   }

   @Override
   public boolean ping(gleis gl) {
      boolean ret = false;
      if (gl.fdata.stellung == gleisElements.ST_ANRUFÜBERGANG_OFFEN) {
         gl.tjmAdd();
         ret = true;
      }

      return super.ping(gl) | ret;
   }
}

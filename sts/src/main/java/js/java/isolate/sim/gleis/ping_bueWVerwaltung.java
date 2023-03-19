package js.java.isolate.sim.gleis;

import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class ping_bueWVerwaltung extends ping_bueVerwaltung {
   ping_bueWVerwaltung(pingBase parent) {
      super(parent);
   }

   ping_bueWVerwaltung() {
      super(null);
   }

   @Override
   public boolean ping(gleis gl) {
      boolean ret = false;
      if (gl.fdata.stellung == gleisElements.ST_BAHNÜBERGANG_OFFEN && gl.fdata.status == 3) {
         gl.tjmAdd();
         ret = true;
      }

      return super.ping(gl) | ret;
   }
}

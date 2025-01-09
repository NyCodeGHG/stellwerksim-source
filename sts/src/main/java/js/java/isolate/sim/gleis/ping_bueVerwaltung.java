package js.java.isolate.sim.gleis;

import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class ping_bueVerwaltung extends pingBase {
   ping_bueVerwaltung(pingBase parent) {
      super(parent);
   }

   ping_bueVerwaltung() {
      super(null);
   }

   @Override
   public boolean ping(gleis gl) {
      boolean ret = false;
      if (gl.fdata.stellung == gleisElements.ST_BAHNÜBERGANG_GESCHLOSSEN) {
         gl.blinkcc++;
         if ((double)gl.blinkcc > 8.0 + 30.0 * Math.random()) {
            if (gl.fdata.status == 4) {
               gl.getFluentData().setStatus(0);
            } else if (gl.fdata.status == 3) {
               gl.getFluentData().setStatus(1);
            }
         } else {
            gl.tjmAdd();
            ret = true;
         }
      }

      return super.ping(gl) | ret;
   }
}

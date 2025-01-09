package js.java.isolate.sim.gleis;

import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class ping_bueAutoVerwaltung extends ping_bueVerwaltung {
   ping_bueAutoVerwaltung(pingBase parent) {
      super(parent);
   }

   ping_bueAutoVerwaltung() {
      super(null);
   }

   @Override
   public boolean ping(gleis gl) {
      boolean ret = false;
      if (gl.fdata.stellung == gleisElements.ST_BAHNÜBERGANG_OFFEN && gl.fdata.status == 3) {
         gl.blinkcc++;
         if ((double)gl.blinkcc > 4.0 + 20.0 * Math.random()) {
            if (gl.fdata.status == 4) {
               gl.getFluentData().setStellung(gleisElements.ST_BAHNÜBERGANG_GESCHLOSSEN);
            } else if (gl.fdata.status == 3) {
               gl.getFluentData().setStellung(gleisElements.ST_BAHNÜBERGANG_GESCHLOSSEN);
            }

            gl.blinkcc = 0;
         } else {
            gl.tjmAdd();
            ret = true;
         }
      }

      return super.ping(gl) | ret;
   }
}

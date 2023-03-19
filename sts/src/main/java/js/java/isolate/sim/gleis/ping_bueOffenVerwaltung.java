package js.java.isolate.sim.gleis;

import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class ping_bueOffenVerwaltung extends ping_bueVerwaltung {
   ping_bueOffenVerwaltung(pingBase parent) {
      super(parent);
   }

   ping_bueOffenVerwaltung() {
      super(null);
   }

   @Override
   public boolean ping(gleis gl) {
      boolean ret = false;
      if (!gl.fdata.gesperrt && gl.fdata.stellung == gleisElements.ST_BAHNÜBERGANG_OFFEN && gl.fdata.status == 3) {
         ++gl.blinkcc;
         if (gl.blinkcc > 4) {
            gl.blinkcc = 0;
            gl.theapplet.getAudio().playBÜ(gl.getENR());
         }

         gl.tjmAdd();
         ret = true;
      }

      return super.ping(gl) | ret;
   }
}

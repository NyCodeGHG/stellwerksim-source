package js.java.isolate.sim.gleis;

import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fsAllocs;

class ping_signalFs extends pingBase {
   ping_signalFs(pingBase parent) {
      super(parent);
   }

   ping_signalFs() {
      super(null);
   }

   @Override
   public boolean ping(gleis gl) {
      boolean ret = false;
      if (gl.fdata.fsspeicher != null) {
         if (gl.fdata.stellung == gleisElements.ST_SIGNAL_ROT
            && gl.call(gl.fdata.fsspeicher, true)
            && gl.fdata.fsspeicher.getStop().call(gl.fdata.fsspeicher, false)) {
            gl.theapplet.getFSallocator().getFS(gl.fdata.fsspeicher, fsAllocs.ALLOCM_GET);
         }

         gl.tjmAdd();
         ret = true;
      } else {
         ret = gl.autoFW.pingGetAutoFS();
      }

      if (gl.fdata.stellung == gleisElements.ST_SIGNAL_ZS1) {
         if (gl.fdata.stellungChangeTime + 90000L <= System.currentTimeMillis()) {
            gl.fdata.setStellung(gleisElements.ST_SIGNAL_ROT);
         }

         gl.tjmAdd();
         ret = true;
      }

      return super.ping(gl) | ret;
   }
}

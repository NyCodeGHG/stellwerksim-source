package js.java.isolate.sim.gleis;

class ping_reserving extends pingBase {
   ping_reserving(pingBase parent) {
      super(parent);
   }

   ping_reserving() {
      super(null);
   }

   @Override
   public boolean ping(gleis gl) {
      boolean ret = false;
      if (gl.getFluentData().getStatus() == 3 || gl.getFluentData().getStatus() == 4) {
         ++gl.blinkcc;
         if ((double)gl.blinkcc > 8.0 + 20.0 * Math.random()) {
            if (gl.getFluentData().getStatus() == 4) {
               gl.getFluentData().setStatus(0);
            } else {
               gl.getFluentData().setStatus(1);
            }
         } else {
            gl.tjmAdd();
         }

         ret = true;
      }

      return super.ping(gl) || ret;
   }
}

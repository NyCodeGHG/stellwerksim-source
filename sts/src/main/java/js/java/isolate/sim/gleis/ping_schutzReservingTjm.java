package js.java.isolate.sim.gleis;

class ping_schutzReservingTjm extends pingBase {
   ping_schutzReservingTjm(pingBase parent) {
      super(parent);
   }

   ping_schutzReservingTjm() {
      super(null);
   }

   @Override
   public boolean ping(gleis gl) {
      boolean ret = false;
      if (gl.getFluentData().getStatus() == 3 || gl.getFluentData().getStatus() == 4) {
         gl.tjmAdd();
         ret = true;
      }

      return super.ping(gl) || ret;
   }
}

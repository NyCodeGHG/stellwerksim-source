package js.java.isolate.sim.gleis;

class ping_ausfahrt extends pingBase {
   ping_ausfahrt(pingBase parent) {
      super(parent);
   }

   ping_ausfahrt() {
      super(null);
   }

   @Override
   public boolean ping(gleis gl) {
      boolean ret = false;
      ret = gl.autoFW.pingGetAutoFS();
      return super.ping(gl) | ret;
   }
}

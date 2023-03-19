package js.java.isolate.sim.gleis;

class ping_uep extends pingBase {
   ping_uep(pingBase parent) {
      super(parent);
   }

   ping_uep() {
      super(null);
   }

   @Override
   public boolean ping(gleis gl) {
      boolean ret = false;
      if (gl.getFluentData().getStatus() == 3 || gl.getFluentData().getStatus() == 4) {
         ++gl.blinkcc;
         if (gl.blinkcc > 20) {
            gl.blinkcc = 0;
            gl.theapplet.getFSallocator().reserveAusfahrt(gl.enr);
         }

         gl.tjmAdd();
         ret = true;
      }

      return super.ping(gl) | ret;
   }
}

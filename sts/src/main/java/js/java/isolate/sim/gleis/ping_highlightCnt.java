package js.java.isolate.sim.gleis;

class ping_highlightCnt extends pingBase {
   ping_highlightCnt(pingBase parent) {
      super(parent);
   }

   ping_highlightCnt() {
      super(null);
   }

   @Override
   public boolean ping(gleis gl) {
      boolean ret = false;
      if (gl.highlighted > 0) {
         gl.highlighted--;
         gl.tjmAdd();
         ret = true;
      }

      return super.ping(gl) | ret;
   }
}

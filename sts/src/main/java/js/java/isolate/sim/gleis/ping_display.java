package js.java.isolate.sim.gleis;

class ping_display extends pingBase {
   ping_display(pingBase parent) {
      super(parent);
   }

   ping_display() {
      super(null);
   }

   @Override
   public boolean ping(gleis gl) {
      boolean ret = false;
      if (gl.fdata.display_blink) {
         if (gleis.blinkon_3er) {
            if (gl.fdata.display_blink_count == 0) {
               gl.fdata.display_blink = false;
            }

            gl.fdata.display_stellung = gl.fdata.display_new_stellung;
         } else {
            if (gl.fdata.display_blink_count > 0) {
               gl.fdata.display_blink_count--;
            }

            gl.fdata.display_stellung = "";
         }

         gl.tjmAdd();
         ret = true;
      } else {
         gl.blinkcc++;
         if (gl.blinkcc < 20) {
            gl.tjmAdd();
         } else {
            gl.fdata.display_stellung = gl.fdata.display_new_stellung;
            ret = true;
         }
      }

      return super.ping(gl) | ret;
   }
}

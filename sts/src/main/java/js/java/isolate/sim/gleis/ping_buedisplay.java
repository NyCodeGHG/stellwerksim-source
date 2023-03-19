package js.java.isolate.sim.gleis;

class ping_buedisplay extends pingBase {
   ping_buedisplay(pingBase parent) {
      super(parent);
   }

   ping_buedisplay() {
      super(null);
   }

   @Override
   public boolean ping(gleis gl) {
      boolean ret = false;
      gleis bü = gl.glbModel.findFirst(gl.getENR(), gleis.ALLE_BAHNÜBERGÄNGE);
      if (bü != null) {
         if (!bü.fdata.power_off) {
            int c = (int)((System.currentTimeMillis() - bü.fdata.stellungChangeTime) / 1000L);
            String v = "";
            if (bü.fdata.stellung == gleis.ST_BAHNÜBERGANG_OFFEN) {
               c = 60 - c;
               if (c > 0) {
                  v = Integer.toString(c / 10);
                  gl.tjmAdd();
               }

               ret = true;
            } else if (bü.fdata.stellung == gleis.ST_BAHNÜBERGANG_GESCHLOSSEN) {
               c = 180 - c;
               if (c > 0) {
                  v = Integer.toString(c / 10);
                  gl.tjmAdd();
               } else {
                  v = "##";
               }

               ret = true;
            }

            gl.fdata.displaySet(v);
         }
      } else {
         System.out.println("nix bü!");
      }

      return super.ping(gl) | ret;
   }
}

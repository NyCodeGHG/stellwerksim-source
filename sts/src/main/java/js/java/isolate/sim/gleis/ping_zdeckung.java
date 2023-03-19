package js.java.isolate.sim.gleis;

import js.java.isolate.sim.zug.zug;

class ping_zdeckung extends pingBase {
   ping_zdeckung(pingBase parent) {
      super(parent);
   }

   ping_zdeckung() {
      super(null);
   }

   @Override
   public boolean ping(gleis gl) {
      boolean ret = false;
      if (gl.getFluentData().getStellung() == gleis.ST_ZDSIGNAL_ROT) {
         try {
            gleis bgl = gl.nextByRichtung(true);

            try {
               bgl.getFluentData().getCurrentFS().extendWeg();
            } catch (NullPointerException var5) {
            }

            if (gl.getFluentData().getStellung() == gleis.ST_ZDSIGNAL_ROT) {
               zug z = bgl.getFluentData().getZugAmGleis();
               if (z.getIST() < 0.1) {
                  gl.getFluentData().setStellung(gleis.ST_ZDSIGNAL_GRÜN);
               }
            }
         } catch (NullPointerException var6) {
         }
      }

      gl.tjmAdd();
      return super.ping(gl) || ret;
   }
}

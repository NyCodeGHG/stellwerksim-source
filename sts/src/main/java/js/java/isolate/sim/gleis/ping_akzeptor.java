package js.java.isolate.sim.gleis;

import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class ping_akzeptor extends pingBase {
   ping_akzeptor(pingBase parent) {
      super(parent);
   }

   ping_akzeptor() {
      super(null);
   }

   @Override
   public boolean ping(gleis gl) {
      boolean ret = false;
      if (gl.fdata.stellung == gleisElements.ST_ÜBERGABEAKZEPTOR_ANFRAGE) {
         gl.blinkcc++;
         if (gl.blinkcc > 6) {
            if (gl.blinkcc > 9) {
               gl.blinkcc = 0;
               gl.theapplet.getAudio().playÜG(gl.getCol() * gl.getRow());
            } else if (gl.blinkcc == 7) {
               gl.theapplet.getAudio().playÜG(gl.getCol() * gl.getRow());
            }
         }

         gl.tjmAdd();
         ret = true;
      } else if (gl.fdata.stellung == gleisElements.ST_ÜBERGABEAKZEPTOR_NOK) {
         gl.blinkcc++;
         if (gl.blinkcc > 60) {
            gl.blinkcc = 0;
            gl.getFluentData().setStellung(gleisElements.ST_ÜBERGABEAKZEPTOR_UNDEF);
            ret = true;
         } else {
            gl.tjmAdd();
         }
      }

      return super.ping(gl) | ret;
   }
}

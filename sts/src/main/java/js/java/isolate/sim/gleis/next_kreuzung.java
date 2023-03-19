package js.java.isolate.sim.gleis;

import java.util.Iterator;

class next_kreuzung extends nextGleisBase {
   next_kreuzung(nextGleisBase p) {
      super(p);
   }

   next_kreuzung() {
      super(null);
   }

   @Override
   public gleis nextGleis(gleis gl, gleis before) {
      gleis ret = null;
      if (gl.getNachbarCount() != 4) {
         ret = super.nextGleis(gl, before);
      } else {
         Iterator<gleis> it = gl.getNachbarn();

         while(it.hasNext()) {
            gleis nextGl = (gleis)it.next();
            if (before.myrow == gl.myrow && gl.myrow == nextGl.myrow && !nextGl.sameGleis(before)) {
               ret = nextGl;
               break;
            }

            if (before.mycol == gl.mycol && gl.mycol == nextGl.mycol && !nextGl.sameGleis(before)) {
               ret = nextGl;
               break;
            }

            if (before.myrow != gl.myrow
               && before.mycol != gl.mycol
               && gl.mycol != nextGl.mycol
               && gl.myrow != nextGl.myrow
               && before.myrow != nextGl.myrow
               && before.mycol != nextGl.mycol
               && !nextGl.sameGleis(before)) {
               ret = nextGl;
               break;
            }
         }
      }

      return ret;
   }
}

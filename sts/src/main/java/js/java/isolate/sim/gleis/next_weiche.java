package js.java.isolate.sim.gleis;

import java.util.Iterator;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class next_weiche extends nextGleisBase {
   next_weiche(nextGleisBase p) {
      super(p);
   }

   next_weiche() {
      super(null);
   }

   @Override
   public gleis nextGleis(gleis gl, gleis before) {
      gleis ret = null;
      Iterator<gleis> it = gl.getNachbarn();
      gleis g1a = null;
      gleis g2a = null;
      gleis g1b = null;
      gleis g2b = null;
      int c = 0;

      while (it.hasNext()) {
         gleis nextGl = (gleis)it.next();
         if (nextGl.mycol == before.mycol) {
            c++;
            if (g1a == null) {
               g1a = nextGl;
            } else if (g2a == null) {
               g2a = nextGl;
            }
         } else if (g1b == null) {
            g1b = nextGl;
         } else if (g2b == null) {
            g2b = nextGl;
         }
      }

      if (c == 1) {
         if (gl.fdata.stellung == gleisElements.ST_WEICHE_GERADE) {
            if (g1b != null && g1b.myrow == gl.myrow) {
               ret = g1b;
            } else if (g2b != null && g2b.myrow == gl.myrow) {
               ret = g2b;
            }
         } else if (g1b != null && g1b.myrow != gl.myrow) {
            ret = g1b;
         } else if (g2b != null && g2b.myrow != gl.myrow) {
            ret = g2b;
         }
      } else if (c == 2) {
         if (gl.fdata.stellung == gleisElements.ST_WEICHE_GERADE) {
            if (gl.myrow == before.myrow) {
               ret = g1b;
            }
         } else if (gl.myrow != before.myrow) {
            ret = g1b;
         }
      }

      if (ret == null) {
         ret = super.nextGleis(gl, before);
      }

      if (ret == null) {
         ret = gl;
      }

      return ret;
   }
}

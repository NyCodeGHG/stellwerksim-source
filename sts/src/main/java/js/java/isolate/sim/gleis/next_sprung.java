package js.java.isolate.sim.gleis;

import java.util.Iterator;

class next_sprung extends nextGleisBase {
   next_sprung(nextGleisBase p) {
      super(p);
   }

   next_sprung() {
      super(null);
   }

   @Override
   public gleis nextGleis(gleis gl, gleis before) {
      gleis ret = null;
      Iterator<gleis> it = gl.getNachbarn();

      label37:
      while(it.hasNext()) {
         gleis nextGl = (gleis)it.next();
         if (nextGl.sameGleis(before)) {
            Iterator<gleis> git = gl.glbModel.findIterator(gl.enr, gleis.ELEMENT_SPRUNG);

            while(true) {
               if (git.hasNext()) {
                  gleis gl2 = (gleis)git.next();
                  if (gl2 == gl) {
                     continue;
                  }

                  ret = gl2;
               }

               if (ret != null) {
                  break label37;
               }
               break;
            }
         }
      }

      if (ret == null) {
         ret = super.nextGleis(gl, before);
      }

      if (ret == null) {
         ret = gl.getFirstNachbar();
      }

      return ret;
   }
}

package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class akzeptortest1 implements dtest {
   public akzeptortest1() {
      super();
   }

   @Override
   public String getName() {
      return "Akzeptor mit Einfahrt";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it = glb.findIterator(new Object[]{gleis.ELEMENT_ÜBERGABEAKZEPTOR});

      while(it.hasNext()) {
         gleis gl = (gleis)it.next();
         LinkedList<gleis> m = new LinkedList();
         Iterator<gleis> it2 = glb.findIterator(new Object[]{gleis.ELEMENT_EINFAHRT, gl.getENR()});

         while(it2.hasNext()) {
            gleis gl2 = (gleis)it2.next();
            if (gl2 != gl) {
               m.add(gl2);
            }
         }

         if (m.size() > 1) {
            dtestresult d = new dtestresult(2, "Mehrere Einfahrten haben die selbe ENR wie ein Akzeptor!", gl, m);
            r.add(d);
         } else if (m.size() == 0) {
            dtestresult d = new dtestresult(2, "Ein Akzeptor hat keine Einfahrt!", gl);
            r.add(d);
         }
      }

      return r;
   }
}

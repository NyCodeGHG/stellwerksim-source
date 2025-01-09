package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class texttest2 implements dtest {
   @Override
   public String getName() {
      return "Gleisschild";
   }

   @Override
   public String getVersion() {
      return "$Revision: 3774 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_GLEISLABEL});

      while (it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         int ncnt = 0;

         for (Iterator<gleis> git = gl.getNachbarn(); git.hasNext(); ncnt++) {
            git.next();
         }

         if (ncnt == 0) {
            dtestresult d = new dtestresult(2, "Gleisschild ohne Anschlussgleise, Textschildchen nutzen!", gl);
            r.add(d);
         }
      }

      return r;
   }
}

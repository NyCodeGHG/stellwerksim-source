package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class displaytest3 extends displaytest2 {
   public displaytest3() {
      super();
   }

   @Override
   public String getName() {
      return "Displaykontakt verbunden";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it = glb.findIterator(new Object[]{gleis.ELEMENT_DISPLAYKONTAKT});
      this.iterate(glb, it, "Ein Displaykontakt hat kein Display.", r);
      return r;
   }

   @Override
   protected boolean legacyTest(gleis gl, gleisbildModelSts glb, LinkedList<dtestresult> r) {
      String sw = gl.getSWWert();
      return glb.findFirst(new Object[]{gleis.ALLE_ZUGDISPLAYS, sw}) == null;
   }
}

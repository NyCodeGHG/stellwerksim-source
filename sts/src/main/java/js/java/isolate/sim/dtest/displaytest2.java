package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.displayBar.displayBar;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class displaytest2 implements dtest {
   public displaytest2() {
      super();
   }

   @Override
   public String getName() {
      return "Display verbunden";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   protected void iterate(gleisbildModelSts glb, Iterator<gleis> it, String message, LinkedList<dtestresult> r) {
      while(it.hasNext()) {
         gleis gl = (gleis)it.next();
         boolean error;
         if (glb.getDisplayBar().isLegacy()) {
            error = this.legacyTest(gl, glb, r);
         } else {
            error = this.displayBarTest(gl, glb, r);
         }

         if (error) {
            dtestresult d = new dtestresult(2, message, gl);
            r.add(d);
         }
      }
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it = glb.findIterator(new Object[]{gleis.ALLE_ZUGDISPLAYS});
      this.iterate(glb, it, "Ein Display hat keinen Auslöser (Kontakt/Einfahrt/Bahnsteig).", r);
      return r;
   }

   protected boolean legacyTest(gleis gl, gleisbildModelSts glb, LinkedList<dtestresult> r) {
      String sw = gl.getSWWert();
      return glb.findFirst(new Object[]{gleis.ALLE_GLEISE, sw}) == null;
   }

   protected boolean displayBarTest(gleis gl, gleisbildModelSts glb, LinkedList<dtestresult> r) {
      displayBar db = glb.getDisplayBar();
      LinkedList<gleis> conlist = db.getConnectedItems(gl);
      Iterator<gleis> it = conlist.iterator();

      while(it.hasNext()) {
         gleis tgl = (gleis)it.next();
         if (!tgl.isDisplayTriggerSelectable()) {
            it.remove();
         }
      }

      return conlist.isEmpty();
   }
}

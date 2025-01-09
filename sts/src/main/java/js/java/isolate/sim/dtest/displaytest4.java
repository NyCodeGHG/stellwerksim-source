package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.displayBar.displayBar;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class displaytest4 implements dtest {
   @Override
   public String getName() {
      return "Display Verbindungszahl";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5254 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it = glb.findIterator(new Object[]{gleis.ALLE_ZUGDISPLAYS});

      while (it.hasNext()) {
         gleis gl = (gleis)it.next();
         int cnt;
         if (glb.getDisplayBar().isLegacy()) {
            cnt = this.legacyTest(gl, glb, r);
         } else {
            cnt = this.displayBarTest(gl, glb, r);
         }

         if (cnt > 6) {
            dtestresult d = new dtestresult(1, "Ein Display hat mehr als 5 Auslöser (Kontakt/Einfahrt/Bahnsteig).", gl, cnt);
            r.add(d);
         }
      }

      return r;
   }

   private int legacyTest(gleis gl, gleisbildModelSts glb, LinkedList<dtestresult> r) {
      LinkedList<gleis> m = new LinkedList();
      String sw = gl.getSWWert();
      Iterator<gleis> it = glb.findIterator(new Object[]{gleis.ALLE_GLEISE, sw});

      while (it.hasNext()) {
         gleis gl2 = (gleis)it.next();
         m.add(gl2);
      }

      return m.size();
   }

   private int displayBarTest(gleis gl, gleisbildModelSts glb, LinkedList<dtestresult> r) {
      displayBar db = glb.getDisplayBar();
      LinkedList<gleis> conlist = db.getConnectedItems(gl);
      return conlist.size();
   }
}

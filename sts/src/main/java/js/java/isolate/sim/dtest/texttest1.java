package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisTypContainer;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class texttest1 implements dtest {
   @Override
   public String getName() {
      return "Textrichtung";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      gleisTypContainer gtc = gleisTypContainer.getInstance();
      Map<gleisElements.RICHTUNG, String> richtungen = gtc.getRichtungen();
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ALLE_TEXTE});

      while (it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         element e = gl.getElement();
         if (!e.getAllowedRichtung().contains(gl.getRichtung())) {
            dtestresult d = new dtestresult(2, "Textelement hat nicht erlaubte Richtung: " + (String)richtungen.get(gl.getRichtung()), gl);
            r.add(d);
         }
      }

      return r;
   }
}

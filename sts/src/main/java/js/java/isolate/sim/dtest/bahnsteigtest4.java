package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class bahnsteigtest4 implements dtest {
   @Override
   public String getName() {
      return "Bahnsteig bis Signal";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_BAHNSTEIG});

      while (it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         gleis pos_gl = gl.nextByRichtung(false);
         gleis before_gl = gl;

         while (pos_gl != null) {
            gleis next_gl = pos_gl.next(before_gl);
            if (next_gl == null || next_gl == pos_gl || next_gl.getElement().matches(gleis.ELEMENT_SIGNAL) && next_gl.forUs(pos_gl)) {
               break;
            }

            if (gleis.ALLE_WEICHEN.matches(next_gl.getElement())) {
               dtestresult d = new dtestresult(1, "Weiche zwischen Hauptsignal und Bahnsteig " + gl.getSWWert() + "!", next_gl);
               r.add(d);
               break;
            }

            if (gleis.ELEMENT_ZWERGSIGNAL.matches(next_gl.getElement()) && next_gl.forUs(pos_gl)) {
               dtestresult d = new dtestresult(1, "Zwergsignal zwischen Hauptsignal und Bahnsteig " + gl.getSWWert() + "!", next_gl);
               r.add(d);
               break;
            }

            if (next_gl.getElement().matches(gleis.ALLE_STARTSIGNALE) && !next_gl.forUs(pos_gl)) {
               dtestresult d = new dtestresult(1, "Gegensignal zwischen Hauptsignal und Bahnsteig " + gl.getSWWert() + "!", next_gl);
               r.add(d);
               break;
            }

            if (gleis.ALLE_BAHNÜBERGÄNGE.matches(next_gl.getElement())) {
               dtestresult d = new dtestresult(1, "Bahnübergang zwischen Hauptsignal und Bahnsteig " + gl.getSWWert() + "!", next_gl);
               r.add(d);
               break;
            }

            if (gleis.ELEMENT_KREUZUNG.matches(next_gl.getElement())) {
               dtestresult d = new dtestresult(1, "Kreuzung zwischen Hauptsignal und Bahnsteig " + gl.getSWWert() + "!", next_gl);
               r.add(d);
               break;
            }

            before_gl = pos_gl;
            pos_gl = next_gl;
         }
      }

      return r;
   }
}

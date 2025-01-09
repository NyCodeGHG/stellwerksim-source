package js.java.isolate.sim.dtest;

import java.util.HashMap;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.colorSystem.gleisColor;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class farbtest1 implements dtest {
   @Override
   public String getName() {
      return "Farbanteile";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      int h = glb.getGleisHeight();
      int w = glb.getGleisWidth();
      int s = 0;
      HashMap<String, Integer> colcnt = new HashMap();

      for (String k : gleisColor.getInstance().getBGcolors().keySet()) {
         colcnt.put(k, 0);
      }

      for (gleis gl : glb) {
         if (gleis.ALLE_GLEISE.matches(gl.getElement())
            || gleis.ALLE_DISPLAYS.matches(gl.getElement())
            || gleis.ALLE_TEXTE.matches(gl.getElement())
            || gleis.ALLE_KNÖPFE.matches(gl.getElement())) {
            s++;
         }

         String c = gl.getExtendFarbe();
         colcnt.put(c, (Integer)colcnt.get(c) + 1);
      }

      this.anteil(s, colcnt, "orange", 5, r);
      this.anteil(s, colcnt, "rot", 5, r);
      this.anteil(s, colcnt, "grün", 50, r);
      this.anteil(s, colcnt, "gelb", 50, r);
      this.anteil(s, colcnt, "blau", 70, r);

      for (String k : colcnt.keySet()) {
         int cnt = (Integer)colcnt.get(k);
         double p = (double)cnt * 100.0 / (double)s;
         System.out.println("Der Farbanteil von " + k + ": " + p + "%.");
      }

      return r;
   }

   private void anteil(int s, HashMap<String, Integer> colcnt, String k, int p, LinkedList<dtestresult> r) {
      int cnt = (Integer)colcnt.get(k);
      if (cnt > s * p / 100) {
         dtestresult d = new dtestresult(1, "Der Farbanteil von " + k + " ist größer als " + p + "%.");
         r.add(d);
      }
   }
}

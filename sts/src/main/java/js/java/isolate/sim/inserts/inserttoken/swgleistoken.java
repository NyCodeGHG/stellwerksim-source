package js.java.isolate.sim.inserts.inserttoken;

import java.util.HashMap;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

public class swgleistoken extends gleistoken {
   private final String swname;

   public swgleistoken(element element, gleisElements.RICHTUNG _richtung, String sw) {
      super(element, _richtung);
      this.swname = sw;
   }

   public swgleistoken(element element, gleisElements.RICHTUNG _richtung, String _bgcolor, String sw) {
      super(element, _richtung, _bgcolor);
      this.swname = sw;
   }

   @Override
   public void work(gleis gl, HashMap<String, String> storage, boolean demo, boolean leftright) {
      super.work(gl, storage, demo, leftright);
      String w = (String)storage.get(this.swname);
      gl.setSWWert(w);
   }
}

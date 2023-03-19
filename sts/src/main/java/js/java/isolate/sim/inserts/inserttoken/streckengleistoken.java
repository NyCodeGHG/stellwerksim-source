package js.java.isolate.sim.inserts.inserttoken;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

public class streckengleistoken extends gleistoken {
   public streckengleistoken() {
      super(gleis.ELEMENT_STRECKE, gleisElements.RICHTUNG.right);
   }

   public streckengleistoken(String _bgcolor) {
      super(gleis.ELEMENT_STRECKE, gleisElements.RICHTUNG.right, _bgcolor);
   }
}

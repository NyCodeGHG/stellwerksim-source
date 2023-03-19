package js.java.isolate.sim.inserts.inserttoken;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

public class enrgleistoken extends gleistoken {
   private final String enrname;

   public enrgleistoken(element element, gleisElements.RICHTUNG _richtung, String e) {
      super(element, _richtung);
      this.enrname = e;
   }

   public enrgleistoken(element element, gleisElements.RICHTUNG _richtung, String _bgcolor, String e) {
      super(element, _richtung, _bgcolor);
      this.enrname = e;
   }

   @Override
   public void work(gleis gl, HashMap<String, String> storage, boolean demo, boolean leftright) {
      super.work(gl, storage, demo, leftright);

      try {
         int w = Integer.parseInt((String)storage.get(this.enrname));
         gl.setENR(w);
      } catch (Exception var6) {
         System.out.println(this.enrname + " Ex: " + var6.getMessage());
         Logger.getLogger("stslogger").log(Level.SEVERE, "Caught", var6);
      }
   }
}

package js.java.isolate.sim.inserts.inserttoken;

import java.util.HashMap;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.inserts.insert;

public class gleistoken extends inserttoken {
   public element element;
   public String bgcolor = "normal";
   public gleisElements.RICHTUNG richtung = gleisElements.RICHTUNG.right;

   public gleistoken(element _element, gleisElements.RICHTUNG _richtung) {
      super();
      this.element = _element;
      this.richtung = _richtung;
   }

   public gleistoken(element _element, gleisElements.RICHTUNG _richtung, String _bgcolor) {
      super();
      this.element = _element;
      this.richtung = _richtung;
      this.bgcolor = _bgcolor;
   }

   @Override
   public boolean isElement() {
      return true;
   }

   @Override
   public boolean isVisible() {
      return true;
   }

   @Override
   public void work(gleis gl, HashMap<String, String> storage, boolean demo, boolean leftright) {
      String c = (String)storage.get(this.bgcolor);
      if (c == null) {
         c = this.bgcolor;
      }

      gl.init(this.element, insert.calcRichtung(this.richtung, leftright));
      gl.setExtendFarbe(c);
   }
}

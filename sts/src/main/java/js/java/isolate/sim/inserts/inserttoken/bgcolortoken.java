package js.java.isolate.sim.inserts.inserttoken;

import java.util.HashMap;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

public class bgcolortoken extends inserttoken {
   public String bgcolor = "normal";

   public bgcolortoken(String c) {
      this.bgcolor = c;
   }

   @Override
   public boolean isElement() {
      return true;
   }

   @Override
   public boolean isVisible() {
      return false;
   }

   @Override
   public void work(gleis gl, HashMap<String, String> storage, boolean demo, boolean leftright) {
      String c = (String)storage.get(this.bgcolor);
      if (c == null) {
         c = this.bgcolor;
      }

      gl.init(gleis.ELEMENT_LEER, gleisElements.RICHTUNG.right);
      gl.setExtendFarbe(c);
   }
}

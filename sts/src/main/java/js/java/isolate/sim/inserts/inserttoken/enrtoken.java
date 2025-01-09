package js.java.isolate.sim.inserts.inserttoken;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

public class enrtoken extends inserttoken {
   public element element;
   public String bgcolor = "normal";
   private final String enrname;

   public enrtoken(element _element, String e) {
      this.element = _element;
      this.enrname = e;
   }

   public enrtoken(element _element, String _bgcolor, String e) {
      this.element = _element;
      this.bgcolor = _bgcolor;
      this.enrname = e;
   }

   @Override
   public void work(gleis gl, HashMap<String, String> storage, boolean demo, boolean leftright) {
      super.work(gl, storage, demo, leftright);
      String c = (String)storage.get(this.bgcolor);
      if (c == null) {
         c = this.bgcolor;
      }

      gl.init(this.element, gleisElements.RICHTUNG.right);
      gl.setExtendFarbe(c);

      try {
         int w = Integer.parseInt((String)storage.get(this.enrname));
         gl.setENR(w);
      } catch (Exception var7) {
         System.out.println(this.enrname + " Ex: " + var7.getMessage());
         Logger.getLogger("stslogger").log(Level.SEVERE, "Caught", var7);
      }
   }

   @Override
   public boolean isElement() {
      return true;
   }

   @Override
   public boolean isVisible() {
      return true;
   }
}

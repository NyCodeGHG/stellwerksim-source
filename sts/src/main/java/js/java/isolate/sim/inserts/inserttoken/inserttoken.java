package js.java.isolate.sim.inserts.inserttoken;

import java.util.HashMap;
import js.java.isolate.sim.gleis.gleis;

public abstract class inserttoken {
   public void init() {
   }

   public void work(gleis gl, HashMap<String, String> storage, boolean demo, boolean leftright) {
   }

   public abstract boolean isElement();

   public abstract boolean isVisible();
}

package js.java.isolate.sim.gleisbild.gleisbildWorker;

import java.util.HashSet;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModel;

public class bstflaecheAllConnectionSearch extends bstflaecheConnectionSearch {
   private final HashSet<gleis> result = new HashSet();

   public bstflaecheAllConnectionSearch(gleisbildModel gl, GleisAdapter main, gleis start, String name, boolean highlight) {
      super(gl, main, start, name, highlight);
   }

   @Override
   protected gleis prepareReturn(gleis gl) {
      this.result.add(gl);
      return null;
   }

   public HashSet<gleis> getResult() {
      return this.result;
   }
}

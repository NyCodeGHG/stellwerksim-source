package js.java.isolate.sim.gleisbild.gleisbildWorker;

import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModel;

public class bstflaecheOtherConnectionSearch extends bstflaecheConnectionSearch {
   private final String other;

   public bstflaecheOtherConnectionSearch(gleisbildModel gl, GleisAdapter main, gleis start, String name, String other, boolean highlight) {
      super(gl, main, start, name, highlight);
      this.other = other;
   }

   @Override
   protected gleis prepareReturn(gleis gl) {
      return this.other.equalsIgnoreCase(gl.getSWWert()) ? gl : null;
   }
}

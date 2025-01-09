package js.java.isolate.sim.gleisbild.gleisbildWorker;

import java.util.HashSet;
import java.util.LinkedList;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModel;

public class bstflaecheConnectionSearch extends gleisbildWorkerBase<gleisbildModel> {
   private final String name;
   private final HashSet<gleis> visited = new HashSet();
   private final LinkedList<gleis> searchlist = new LinkedList();
   private final boolean highlight;

   public bstflaecheConnectionSearch(gleisbildModel gl, GleisAdapter main, gleis start, String name, boolean highlight) {
      super(gl, main);
      this.name = name;
      this.highlight = highlight;
      this.searchlist.add(start);
   }

   public gleis find() {
      gleis current = (gleis)this.searchlist.pollFirst();
      if (current != null && !this.visited.contains(current)) {
         this.visited.add(current);
         int x = current.getCol();
         gleis res = this.continueSearch(this.glbModel.getXY_null(x, current.getRow() - 1), this.name);
         if (res == null) {
            res = this.continueSearch(this.glbModel.getXY_null(x, current.getRow() + 1), this.name);
         }

         if (res != null) {
            return res;
         } else {
            this.continueSearch(this.glbModel.getXY_null(x + 1, current.getRow()), this.name);
            this.continueSearch(this.glbModel.getXY_null(x - 1, current.getRow()), this.name);

            while (!this.searchlist.isEmpty()) {
               res = this.find();
               if (res != null) {
                  return res;
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   private gleis continueSearch(gleis gl, String name) {
      if (gl == null) {
         return null;
      } else {
         if (gl.getElement().matches(gleis.ELEMENT_BAHNSTEIGFLÄCHE)) {
            if (!this.visited.contains(gl)) {
               this.searchlist.add(gl);
               if (this.highlight) {
                  this.glbModel.addRolloverGleis(gl);
               }
            }
         } else if (gl.getElement().matches(gleis.ALLE_BAHNSTEIGE) && !gl.getSWWert().equalsIgnoreCase(name)) {
            return this.prepareReturn(gl);
         }

         return null;
      }
   }

   protected gleis prepareReturn(gleis gl) {
      return gl;
   }
}

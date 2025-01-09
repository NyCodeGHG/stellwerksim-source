package js.java.isolate.sim.gleisbild.gleisbildWorker;

import java.util.LinkedList;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModel;

public class emptyLineFinder extends gleisbildWorkerBase<gleisbildModel> {
   public emptyLineFinder(gleisbildModel gl, GleisAdapter main) {
      super(gl, main);
   }

   public void findEmptyColRows() {
      this.findEmptyColRows(new LinkedList(), new LinkedList(), true);
   }

   public void findEmptyColRows(LinkedList<Integer> emptyRows, LinkedList<Integer> emptyCols, boolean mark) {
      if (mark) {
         this.glbModel.clearMarkedGleis();
      }

      if (emptyRows == null) {
         throw new IllegalArgumentException("emptyRow==null");
      } else if (emptyCols == null) {
         throw new IllegalArgumentException("emptyCol==null");
      } else {
         emptyRows.clear();
         emptyCols.clear();
         LinkedList<Integer> noneemptyCols = new LinkedList();

         for (int y = 0; y < this.glbModel.getGleisHeight(); y++) {
            boolean lineEmpty = true;

            for (int x = 0; x < this.glbModel.getGleisWidth(); x++) {
               gleis gl = this.glbModel.getXY_null(x, y);
               if (gl == null || !gleis.ELEMENT_LEER.matches(gl.getElement()) || !gl.getExtendFarbe().equalsIgnoreCase("normal")) {
                  lineEmpty = false;
                  noneemptyCols.add(x);
               }
            }

            if (lineEmpty) {
               emptyRows.add(y);
            }
         }

         for (int xx = 0; xx < this.glbModel.getGleisWidth(); xx++) {
            if (!noneemptyCols.contains(xx)) {
               emptyCols.add(xx);
            }
         }

         if (mark) {
            for (int y = 0; y < this.glbModel.getGleisHeight(); y++) {
               for (int xxx = 0; xxx < this.glbModel.getGleisWidth(); xxx++) {
                  gleis gl = this.glbModel.getXY_null(xxx, y);
                  if (emptyCols.contains(xxx) || emptyRows.contains(y)) {
                     this.glbModel.addMarkedGleis(gl);
                  }
               }
            }
         }
      }
   }
}

package js.java.isolate.fahrplaneditor;

class markerSolver extends solutionInterface {
   private markerBox box;

   markerSolver(String m, markerBox cb) {
      super(m);
      this.box = cb;
   }

   @Override
   void solve() {
      this.box.setSelected(false);
   }
}

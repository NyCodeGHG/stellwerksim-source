package js.java.isolate.fahrplaneditor;

class tmSolver extends solutionInterface {
   private final markerBox[] tmarker;

   tmSolver(String m, markerBox[] _tmarker) {
      super(m);
      this.tmarker = _tmarker;
   }

   @Override
   void solve() {
      for (markerBox tmarker1 : this.tmarker) {
         tmarker1.setSelected(true);
      }
   }
}

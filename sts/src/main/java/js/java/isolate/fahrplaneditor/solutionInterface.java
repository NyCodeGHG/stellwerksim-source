package js.java.isolate.fahrplaneditor;

import js.java.tools.gui.warningPopup.warningItems;

abstract class solutionInterface extends warningItems {
   solutionInterface(String m) {
      super(m);
   }

   abstract void solve();
}

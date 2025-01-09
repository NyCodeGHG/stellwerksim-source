package js.java.isolate.fahrplaneditor;

import java.util.LinkedList;
import js.java.tools.gui.warningPopup.IconPopupButton;

class dataFailures {
   private IconPopupButton button = null;
   private String message = "";
   private LinkedList<solutionInterface> solutions = null;

   dataFailures(IconPopupButton b, String m) {
      this.button = b;
      this.message = m;
      this.solutions = new LinkedList();
   }

   dataFailures(IconPopupButton b, String m, LinkedList<solutionInterface> s) {
      this.button = b;
      this.message = m;
      this.solutions = s;
   }

   public IconPopupButton getButton() {
      return this.button;
   }

   public void setButton(IconPopupButton button) {
      this.button = button;
   }

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public LinkedList<solutionInterface> getSolutions() {
      return this.solutions;
   }

   public void addSolution(solutionInterface w) {
      this.solutions.add(w);
   }
}

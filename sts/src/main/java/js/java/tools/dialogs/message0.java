package js.java.tools.dialogs;

import java.awt.Frame;

public class message0 extends messageDialog {
   public message0(Frame parent, String title, String text) {
      super(parent, false, title);
      this.setText(text);
   }
}

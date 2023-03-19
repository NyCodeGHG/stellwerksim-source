package js.java.tools.gui;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class HTMLEditorKitCustomCss extends HTMLEditorKit {
   private StyleSheet style = null;

   public HTMLEditorKitCustomCss() {
      super();
      this.style = new StyleSheet();
      this.style.addStyleSheet(super.getStyleSheet());
   }

   public void setStyleSheet(StyleSheet s) {
      this.style = s;
   }

   public StyleSheet getStyleSheet() {
      return this.style;
   }
}

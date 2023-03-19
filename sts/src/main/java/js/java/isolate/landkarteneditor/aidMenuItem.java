package js.java.isolate.landkarteneditor;

import javax.swing.JMenuItem;

class aidMenuItem extends JMenuItem {
   final bahnhofList.bahnhofListData data;

   aidMenuItem(String text, bahnhofList.bahnhofListData data) {
      super(text);
      this.data = data;
   }
}

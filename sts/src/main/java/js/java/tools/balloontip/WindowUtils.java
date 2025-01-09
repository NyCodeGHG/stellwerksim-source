package js.java.tools.balloontip;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

public class WindowUtils {
   private WindowUtils() {
   }

   public static void centerWindow(Container window) {
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      int w = window.getSize().width;
      int h = window.getSize().height;
      int x = (dim.width - w) / 2;
      int y = (dim.height - h) / 2;
      window.setLocation(x, y);
   }
}

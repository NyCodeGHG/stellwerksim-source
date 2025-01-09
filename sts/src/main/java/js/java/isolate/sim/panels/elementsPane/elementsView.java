package js.java.isolate.sim.panels.elementsPane;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.LinkedList;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.Scrollable;

public class elementsView extends JPanel implements Scrollable {
   private final LinkedList<elementsDynPanel> elements = new LinkedList();

   public elementsView() {
      this.setLayout(new BoxLayout(this, 0));
   }

   public void add(elementsDynPanel d) {
      super.add(d);
      this.elements.add(d);
   }

   public void remove(elementsDynPanel d) {
      super.remove(d);
      this.elements.remove(d);
   }

   public void removeAll() {
      super.removeAll();
      this.elements.clear();
   }

   public Dimension getPreferredScrollableViewportSize() {
      return this.getPreferredSize();
   }

   public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
      return this.calcScrollWidth(visibleRect, direction > 0);
   }

   public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
      return this.calcScrollWidth(visibleRect, direction > 0);
   }

   public boolean getScrollableTracksViewportWidth() {
      return false;
   }

   public boolean getScrollableTracksViewportHeight() {
      return true;
   }

   private int calcScrollWidth(Rectangle visibleRect, boolean toRight) {
      int n = this.getComponentCount();
      if (n <= 1) {
         return 1;
      } else {
         int w = Integer.MAX_VALUE;

         for (int i = 0; i < n; i++) {
            w = Math.min(w, this.getComponent(i).getWidth());
         }

         return w;
      }
   }
}

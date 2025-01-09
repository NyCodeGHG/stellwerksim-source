package js.java.tools.gui.mdi.scrollabledesktop.components;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

public class ResizableToolBar extends JToolBar implements ComponentListener {
   private ButtonGroup buttonGroup = new ButtonGroup();
   private int minButtonWidth;
   private int maxButtonWidth;

   public ResizableToolBar(int minButtonWidth, int maxButtonWidth) {
      this.setFloatable(false);
      this.minButtonWidth = minButtonWidth;
      this.maxButtonWidth = maxButtonWidth;
      this.addComponentListener(this);
   }

   public void add(AbstractButton button) {
      this.buttonGroup.add(button);
      super.add(button);
      button.setSelected(true);
      this.resizeButtons();
   }

   public void remove(AbstractButton button) {
      super.remove(button);
      this.buttonGroup.remove(button);
      this.resizeButtons();
      this.repaint();
   }

   public Enumeration getElements() {
      return this.buttonGroup.getElements();
   }

   public int getButtonCount() {
      return this.buttonGroup.getButtonCount();
   }

   private void resizeButtons() {
      final float exactButtonWidth = this.getCurrentButtonWidth();
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            JToggleButton b = null;
            Enumeration e = ResizableToolBar.this.getElements();

            for (float currentButtonXLocation = 0.0F; e.hasMoreElements(); currentButtonXLocation += exactButtonWidth) {
               b = (JToggleButton)e.nextElement();
               int buttonWidth = Math.round(currentButtonXLocation + exactButtonWidth) - Math.round(currentButtonXLocation);
               ResizableToolBar.this.assignWidth(b, buttonWidth);
            }

            ResizableToolBar.this.revalidate();
         }
      });
   }

   private float getCurrentButtonWidth() {
      int width = this.getWidth() - this.getInsets().left - this.getInsets().right;
      float buttonWidth = (float)(width <= 0 ? this.maxButtonWidth : width);
      int numButtons = this.getButtonCount();
      if (numButtons > 0) {
         buttonWidth /= (float)numButtons;
      }

      if (buttonWidth < (float)this.minButtonWidth) {
         buttonWidth = (float)this.minButtonWidth;
      } else if (buttonWidth > (float)this.maxButtonWidth) {
         buttonWidth = (float)this.maxButtonWidth;
      }

      return buttonWidth;
   }

   private void assignWidth(JToggleButton b, int buttonWidth) {
      b.setMinimumSize(new Dimension(buttonWidth - 2, b.getPreferredSize().height));
      b.setPreferredSize(new Dimension(buttonWidth, b.getPreferredSize().height));
      Dimension newSize = b.getPreferredSize();
      b.setMaximumSize(newSize);
      b.setSize(newSize);
   }

   public void componentResized(ComponentEvent e) {
      this.resizeButtons();
   }

   public void componentShown(ComponentEvent e) {
   }

   public void componentMoved(ComponentEvent e) {
   }

   public void componentHidden(ComponentEvent e) {
   }
}

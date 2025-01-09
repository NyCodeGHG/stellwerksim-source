package js.java.tools.gui.mdi.scrollabledesktop.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyVetoException;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;

public class JScrollInternalFrame extends JInternalFrame {
   private JToggleButton associatedButton;
   private JRadioButtonMenuItem associatedMenuButton;
   private boolean isClosable;
   private int initialWidth;
   private int initialHeight;

   public JScrollInternalFrame(String title, ImageIcon icon, JPanel frameContents, boolean isClosable) {
      super(title, true, isClosable, true, true);
      this.isClosable = isClosable;
      this.setBackground(Color.white);
      this.setForeground(Color.blue);
      if (icon != null) {
         this.setFrameIcon(icon);
      }

      this.getContentPane().add(frameContents);
      this.pack();
      this.saveSize();
      this.setVisible(true);
   }

   public void saveSize() {
      this.initialWidth = this.getWidth();
      this.initialHeight = this.getHeight();
   }

   public JScrollInternalFrame() {
      this.saveSize();
   }

   public void setAssociatedMenuButton(JRadioButtonMenuItem associatedMenuButton) {
      this.associatedMenuButton = associatedMenuButton;
   }

   public JRadioButtonMenuItem getAssociatedMenuButton() {
      return this.associatedMenuButton;
   }

   public void setAssociatedButton(JToggleButton associatedButton) {
      this.associatedButton = associatedButton;
   }

   public JToggleButton getAssociatedButton() {
      return this.associatedButton;
   }

   public Dimension getInitialDimensions() {
      return new Dimension(this.initialWidth, this.initialHeight);
   }

   public String toString() {
      return "JScrollInternalFrame: " + this.getTitle();
   }

   public void selectFrameAndAssociatedButtons() {
      if (this.associatedButton != null) {
         this.associatedButton.setSelected(true);
         ((RootToggleButton)this.associatedButton).flagContentsChanged(false);
      }

      if (this.associatedMenuButton != null) {
         this.associatedMenuButton.setSelected(true);
      }

      try {
         this.setSelected(true);
         this.setIcon(false);
      } catch (PropertyVetoException var2) {
         System.out.println(var2.getMessage());
      }

      this.setVisible(true);
   }

   public void addNotify() {
      super.addNotify();
      if (this.initialWidth == 0 && this.initialHeight == 0) {
         this.saveSize();
      }
   }
}

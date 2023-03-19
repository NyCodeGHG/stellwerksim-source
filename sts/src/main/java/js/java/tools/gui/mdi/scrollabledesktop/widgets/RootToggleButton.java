package js.java.tools.gui.mdi.scrollabledesktop.widgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JToggleButton;

public class RootToggleButton extends JToggleButton implements DesktopConstants, FrameAccessorInterface {
   private JScrollInternalFrame associatedFrame;
   private Color defaultColor;

   public RootToggleButton(String title) {
      super(title);
      this.setButtonFormat();
      this.setToolTipText(title);
      this.defaultColor = this.getForeground();
   }

   private void setButtonFormat() {
      Font buttonFont = this.getFont();
      this.setFont(new Font(buttonFont.getFontName(), buttonFont.getStyle(), buttonFont.getSize() - 1));
      this.setMargin(new Insets(0, 0, 0, 0));
   }

   @Override
   public void setAssociatedFrame(JScrollInternalFrame associatedFrame) {
      this.associatedFrame = associatedFrame;
   }

   @Override
   public JScrollInternalFrame getAssociatedFrame() {
      return this.associatedFrame;
   }

   public void flagContentsChanged(boolean changed) {
      if (changed) {
         this.setForeground(CONTENTS_CHANGED_COLOR);
      } else {
         this.setForeground(this.defaultColor);
      }
   }
}

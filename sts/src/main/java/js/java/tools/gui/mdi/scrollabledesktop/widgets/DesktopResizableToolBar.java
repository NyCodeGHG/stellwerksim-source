package js.java.tools.gui.mdi.scrollabledesktop.widgets;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import js.java.tools.gui.mdi.scrollabledesktop.components.ResizableToolBar;

public class DesktopResizableToolBar extends ResizableToolBar implements DesktopConstants, ActionListener {
   private DesktopMediator desktopMediator;

   public DesktopResizableToolBar(DesktopMediator desktopMediator) {
      super(30, 80);
      this.desktopMediator = desktopMediator;
      RootToggleButton testButton = new RootToggleButton("test");
      this.addSeparator(new Dimension(0, testButton.getMinimumSize().height));
   }

   public RootToggleButton add(String title) {
      RootToggleButton toolButton = new RootToggleButton(" " + title + " ");
      toolButton.addActionListener(this);
      super.add(toolButton);
      return toolButton;
   }

   public void actionPerformed(ActionEvent e) {
      this.desktopMediator.actionPerformed(e);
   }
}

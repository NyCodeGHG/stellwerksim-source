package js.java.tools.gui.mdi.scrollabledesktop;

import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import js.java.tools.gui.mdi.scrollabledesktop.widgets.DesktopConstants;
import js.java.tools.gui.mdi.scrollabledesktop.widgets.DesktopMediator;
import js.java.tools.gui.mdi.scrollabledesktop.widgets.JScrollInternalFrame;

public class JScrollDesktopPane extends JPanel implements DesktopConstants {
   private static int count;
   private DesktopMediator desktopMediator;
   private ImageIcon defaultFrameIcon;

   public JScrollDesktopPane(JMenuBar mb, ImageIcon defaultFrameIcon) {
      this();
      this.registerMenuBar(mb);
      this.defaultFrameIcon = defaultFrameIcon;
   }

   public JScrollDesktopPane(JMenuBar mb) {
      this();
      this.registerMenuBar(mb);
   }

   public JScrollDesktopPane() {
      this.setLayout(new BorderLayout());
      this.desktopMediator = new DesktopMediator(this);
   }

   public JInternalFrame add(JPanel frameContents) {
      return this.add("Untitled " + count++, this.defaultFrameIcon, frameContents, true, -1, -1);
   }

   public JInternalFrame add(String title, JPanel frameContents) {
      return this.add(title, this.defaultFrameIcon, frameContents, true, -1, -1);
   }

   public JInternalFrame add(String title, JPanel frameContents, boolean isClosable) {
      return this.add(title, this.defaultFrameIcon, frameContents, isClosable, -1, -1);
   }

   public JInternalFrame add(String title, ImageIcon icon, JPanel frameContents, boolean isClosable) {
      return this.add(title, icon, frameContents, isClosable, -1, -1);
   }

   public JInternalFrame add(String title, ImageIcon icon, JPanel frameContents, boolean isClosable, int x, int y) {
      return this.desktopMediator.add(title, icon, frameContents, isClosable, x, y);
   }

   public void add(JInternalFrame f) {
      this.add(this.getWrappedFrame(f), -1, -1);
   }

   public void add(JInternalFrame f, int x, int y) {
      this.desktopMediator.add(this.getWrappedFrame(f), x, y);
   }

   private JScrollInternalFrame getWrappedFrame(JInternalFrame f) {
      if (!(f instanceof JScrollInternalFrame)) {
         JScrollInternalFrame b = new JScrollInternalFrame();
         b.setContentPane(f.getContentPane());
         b.setTitle(f.getTitle());
         b.setResizable(f.isResizable());
         b.setClosable(f.isClosable());
         b.setMaximizable(f.isMaximizable());
         b.setIconifiable(f.isIconifiable());
         b.setFrameIcon(f.getFrameIcon());
         b.pack();
         b.saveSize();
         b.setVisible(f.isVisible());
         return b;
      } else {
         return (JScrollInternalFrame)f;
      }
   }

   public void remove(JInternalFrame f) {
      f.doDefaultCloseAction();
   }

   public void registerMenuBar(JMenuBar mb) {
      this.desktopMediator.registerMenuBar(mb);
   }

   public void registerDefaultFrameIcon(ImageIcon defaultFrameIcon) {
      this.defaultFrameIcon = defaultFrameIcon;
   }

   public JInternalFrame getSelectedFrame() {
      return this.desktopMediator.getSelectedFrame();
   }

   public void setSelectedFrame(JInternalFrame f) {
      this.desktopMediator.setSelectedFrame(f);
   }

   public void flagContentsChanged(JInternalFrame f) {
      this.desktopMediator.flagContentsChanged(f);
   }
}

package js.java.tools.gui.mdi.scrollabledesktop.widgets;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

public class DesktopScrollPane extends JScrollPane {
   private DesktopMediator desktopMediator;
   private RootDesktopPane desktopPane;
   private FramePositioning positioning;

   public DesktopScrollPane(DesktopMediator desktopMediator) {
      this.desktopMediator = desktopMediator;
      this.desktopPane = new RootDesktopPane(this);
      this.setViewportView(this.desktopPane);
      this.positioning = new FramePositioning(this);
      this.setVerticalScrollBarPolicy(22);
      this.setHorizontalScrollBarPolicy(32);
      this.getHorizontalScrollBar().setUnitIncrement(5);
      this.getVerticalScrollBar().setUnitIncrement(5);
   }

   public JScrollInternalFrame add(DesktopListener dListener, String title, ImageIcon icon, JPanel frameContents, boolean isClosable, int x, int y) {
      JScrollInternalFrame f = new JScrollInternalFrame(title, icon, frameContents, isClosable);
      f.addComponentListener(dListener);
      this.initAndAddFrame(f, x, y);
      return f;
   }

   public void add(DesktopListener dListener, JInternalFrame f, int x, int y) {
      f.addComponentListener(dListener);
      this.initAndAddFrame(f, x, y);
   }

   private void initAndAddFrame(JInternalFrame f, int x, int y) {
      if (x != -1 && y != -1) {
         f.setLocation(x, y);
      } else if (!this.getAutoTile()) {
         f.setLocation(this.cascadeInternalFrame(f));
      }

      this.desktopPane.add(f);

      try {
         f.setSelected(true);
      } catch (PropertyVetoException var5) {
      }

      this.resizeDesktop();
   }

   public JInternalFrame[] getAllFrames() {
      return this.desktopPane.getAllFrames();
   }

   public JInternalFrame getSelectedFrame() {
      return this.desktopPane.getSelectedFrame();
   }

   public void closeSelectedFrame() {
      JInternalFrame f = this.getSelectedFrame();
      if (f != null) {
         f.doDefaultCloseAction();
      }
   }

   public void setSelectedFrame(JInternalFrame f) {
      try {
         JInternalFrame currentFrame = this.desktopPane.getSelectedFrame();
         if (currentFrame != null) {
            currentFrame.setSelected(false);
         }

         f.setSelected(true);
         f.setIcon(false);
      } catch (PropertyVetoException var3) {
         System.out.println(var3.getMessage());
      }
   }

   public void flagContentsChanged(JInternalFrame f) {
      if (this.desktopPane.getSelectedFrame() != f) {
         RootToggleButton button = (RootToggleButton)((JScrollInternalFrame)f).getAssociatedButton();
         button.flagContentsChanged(true);
      }
   }

   public void selectNextFrame() {
      JInternalFrame[] frames = this.getAllFrames();
      if (frames.length > 0) {
         try {
            frames[0].setSelected(true);
         } catch (PropertyVetoException var3) {
            System.out.println("Bean veto: " + var3.getMessage());
         }
      }
   }

   public int getNumberOfFrames() {
      return this.desktopPane.getComponentCount();
   }

   public void setDesktopSize(Dimension dim) {
      this.desktopPane.setPreferredSize(dim);
      this.desktopPane.revalidate();
   }

   public Dimension getDesktopSize() {
      return this.desktopPane.getPreferredSize();
   }

   public void setAutoTile(boolean autoTile) {
      this.positioning.setAutoTile(autoTile);
   }

   public boolean getAutoTile() {
      return this.positioning.getAutoTile();
   }

   public Point cascadeInternalFrame(JInternalFrame f) {
      return this.positioning.cascadeInternalFrame(f);
   }

   public void cascadeInternalFrames() {
      this.positioning.cascadeInternalFrames();
   }

   public void tileInternalFrames() {
      this.positioning.tileInternalFrames();
   }

   public void centerView(JScrollInternalFrame f) {
      Rectangle viewP = this.getViewport().getViewRect();
      int xCoords = f.getX() + f.getWidth() / 2 - viewP.width / 2;
      int yCoords = f.getY() + f.getHeight() / 2 - viewP.height / 2;
      Dimension desktopSize = this.getDesktopSize();
      if (xCoords + viewP.width > desktopSize.width) {
         xCoords = desktopSize.width - viewP.width;
      } else if (xCoords < 0) {
         xCoords = 0;
      }

      if (yCoords + viewP.height > desktopSize.height) {
         yCoords = desktopSize.height - viewP.height;
      } else if (yCoords < 0) {
         yCoords = 0;
      }

      this.getViewport().setViewPosition(new Point(xCoords, yCoords));
   }

   public void resizeDesktop() {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            Rectangle viewP = DesktopScrollPane.this.getViewport().getViewRect();
            int maxX = viewP.width + viewP.x;
            int maxY = viewP.height + viewP.y;
            int minX = viewP.x;
            int minY = viewP.y;
            JInternalFrame f = null;
            JInternalFrame[] frames = DesktopScrollPane.this.getAllFrames();

            for (int i = 0; i < frames.length; i++) {
               f = frames[i];
               if (f.getX() < minX) {
                  minX = f.getX();
               }

               if (f.getX() + f.getWidth() > maxX) {
                  maxX = f.getX() + f.getWidth();
               }

               if (f.getY() < minY) {
                  minY = f.getY();
               }

               if (f.getY() + f.getHeight() > maxY) {
                  maxY = f.getY() + f.getHeight();
               }
            }

            DesktopScrollPane.this.setVisible(false);
            if (minX != 0 || minY != 0) {
               for (int i = 0; i < frames.length; i++) {
                  f = frames[i];
                  f.setLocation(f.getX() - minX, f.getY() - minY);
               }

               JViewport view = DesktopScrollPane.this.getViewport();
               view.setViewSize(new Dimension(maxX - minX, maxY - minY));
               view.setViewPosition(new Point(viewP.x - minX, viewP.y - minY));
               DesktopScrollPane.this.setViewport(view);
            }

            DesktopScrollPane.this.setDesktopSize(new Dimension(maxX - minX, maxY - minY));
            DesktopScrollPane.this.setVisible(true);
         }
      });
   }

   public void removeAssociatedComponents(JScrollInternalFrame f) {
      this.desktopMediator.removeAssociatedComponents(f);
   }
}

package js.java.tools.gui;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.JTextComponent;
import js.java.schaltungen.adapter.plafPrefs.Parts;
import js.java.tools.logging.ExceptionDialog;

public class SwingTools {
   public static void setPLAF() {
      setPLAF(true);
   }

   private static void setPLAF(boolean flat) {
      try {
         ToolTipManager.sharedInstance().setInitialDelay(500);
         ToolTipManager.sharedInstance().setDismissDelay(10000);
         if (flat) {
            flat();
         }

         if (!flat) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         }
      } catch (InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | ClassNotFoundException var4) {
      }

      InputMap map = (InputMap)UIManager.get("SplitPane.ancestorInputMap");
      KeyStroke keyStrokeF6 = KeyStroke.getKeyStroke(117, 0);
      KeyStroke keyStrokeF8 = KeyStroke.getKeyStroke(119, 0);
      map.remove(keyStrokeF6);
      map.remove(keyStrokeF8);
      System.getProperties().put("sun.awt.exception.handler", ExceptionDialog.class.getName());
      Thread.setDefaultUncaughtExceptionHandler(new ExceptionDialog());
   }

   private static boolean flat() {
      try {
         UIManager.setLookAndFeel(new FlatIntelliJLaf());
         UIManager.put("MenuItem.selectionType", "underline");
         Parts.EMBEDD_MENU.configure();
         Parts.SCROLL_BUTTONS.configure();
         Parts.LARGE_SCROLLERS.configure();
         FlatLaf.updateUI();
         return true;
      } catch (Exception var1) {
         return false;
      }
   }

   public static void addStandardEditingPopupMenu(JTextComponent... fields) {
      final JPopupMenu popupMenu = new JPopupMenu();
      final JMenuItem cutMenuItem = new JMenuItem("Cut", 116);
      cutMenuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Component c = popupMenu.getInvoker();
            if (c instanceof JTextComponent) {
               ((JTextComponent)c).cut();
            }
         }
      });
      popupMenu.add(cutMenuItem);
      JMenuItem copyMenuItem = new JMenuItem("Copy", 67);
      copyMenuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Component c = popupMenu.getInvoker();
            if (c instanceof JTextComponent) {
               ((JTextComponent)c).copy();
            }
         }
      });
      popupMenu.add(copyMenuItem);
      final JMenuItem pasteMenuItem = new JMenuItem("Paste", 80);
      pasteMenuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Component c = popupMenu.getInvoker();
            if (c instanceof JTextComponent) {
               ((JTextComponent)c).paste();
            }
         }
      });
      popupMenu.add(pasteMenuItem);
      popupMenu.addSeparator();
      JMenuItem selectAllMenuItem = new JMenuItem("Select All", 65);
      selectAllMenuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Component c = popupMenu.getInvoker();
            if (c instanceof JTextComponent) {
               ((JTextComponent)c).selectAll();
            }
         }
      });
      popupMenu.add(selectAllMenuItem);

      for (final JTextComponent f : fields) {
         f.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
               this.processMouseEvent(e);
            }

            public void mouseReleased(MouseEvent e) {
               this.processMouseEvent(e);
            }

            private void processMouseEvent(MouseEvent e) {
               if (e.isPopupTrigger()) {
                  cutMenuItem.setEnabled(f.isEditable());
                  pasteMenuItem.setEnabled(f.isEditable());
                  popupMenu.show(e.getComponent(), e.getX(), e.getY());
                  popupMenu.setInvoker(f);
               }
            }
         });
      }
   }

   public static void addLongTextTooltips(JComponent comp) {
      ViewTooltips.register(comp);
   }

   public static GraphicsConfiguration getCurrentGraphicsConfiguration(Point location) {
      GraphicsConfiguration gc = null;
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice[] gd = ge.getScreenDevices();

      for (int i = 0; i < gd.length; i++) {
         if (gd[i].getType() == 0) {
            GraphicsConfiguration dgc = gd[i].getDefaultConfiguration();
            if (dgc.getBounds().contains(location)) {
               gc = dgc;
               break;
            }
         }
      }

      return gc;
   }

   public static Rectangle getScreenBounds(Point location) {
      GraphicsConfiguration gc = getCurrentGraphicsConfiguration(location);
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Rectangle scrBounds;
      if (gc != null) {
         scrBounds = gc.getBounds();
      } else {
         scrBounds = new Rectangle(toolkit.getScreenSize());
      }

      return scrBounds;
   }

   public static Rectangle getScreenBounds(Component location) {
      Point fp = location.getLocation();
      SwingUtilities.convertPointToScreen(fp, location);
      return getScreenBounds(fp);
   }

   public static void toFront(Frame window) {
      int state = window.getExtendedState();
      state &= -2;
      window.setExtendedState(state);
      window.setAlwaysOnTop(true);
      window.toFront();
      window.requestFocus();
      window.setAlwaysOnTop(false);
   }

   public static void toFront(Dialog window) {
      window.setAlwaysOnTop(true);
      window.toFront();
      window.requestFocus();
      window.setAlwaysOnTop(false);
   }
}

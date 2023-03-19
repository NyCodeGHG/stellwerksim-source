package js.java.tools.gui;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class WindowStateSaver implements WindowListener {
   private final WindowStateSaver.WindowAdapter frame;
   private final WindowStateSaver.STORESTATES storestate;
   private final Preferences node;
   private boolean saved = false;

   public WindowStateSaver(final JFrame frame, WindowStateSaver.STORESTATES storestate) {
      super();
      this.frame = new WindowStateSaver.WindowAdapter() {
         @Override
         public int getX() {
            return frame.getX();
         }

         @Override
         public int getY() {
            return frame.getY();
         }

         @Override
         public int getWidth() {
            return frame.getWidth();
         }

         @Override
         public int getHeight() {
            return frame.getHeight();
         }

         @Override
         public int getState() {
            return frame.getExtendedState();
         }

         @Override
         public void setBounds(int x, int y, int w, int h) {
            frame.setBounds(x, y, w, h);
         }

         @Override
         public void setLocation(int x, int y) {
            frame.setLocation(x, y);
         }

         @Override
         public void setSize(int w, int h) {
            frame.setSize(w, h);
         }

         @Override
         public void setState(int s) {
            frame.setExtendedState(s);
         }

         @Override
         public void removeWindowListener() {
            frame.removeWindowListener(WindowStateSaver.this);
         }
      };
      this.storestate = storestate;
      Preferences root = Preferences.userNodeForPackage(this.getClass());
      this.node = root.node("frame").node(frame.getName());
      frame.addWindowListener(this);
      frame.getExtendedState();
      this.set();
   }

   public WindowStateSaver(final JDialog frame, WindowStateSaver.STORESTATES storestate) {
      super();
      this.frame = new WindowStateSaver.WindowAdapter() {
         @Override
         public int getX() {
            return frame.getX();
         }

         @Override
         public int getY() {
            return frame.getY();
         }

         @Override
         public int getWidth() {
            return frame.getWidth();
         }

         @Override
         public int getHeight() {
            return frame.getHeight();
         }

         @Override
         public int getState() {
            return 0;
         }

         @Override
         public void setBounds(int x, int y, int w, int h) {
            frame.setBounds(x, y, w, h);
         }

         @Override
         public void setLocation(int x, int y) {
            frame.setLocation(x, y);
         }

         @Override
         public void setSize(int w, int h) {
            frame.setSize(w, h);
         }

         @Override
         public void setState(int s) {
         }

         @Override
         public void removeWindowListener() {
            frame.removeWindowListener(WindowStateSaver.this);
         }
      };
      this.storestate = storestate;
      Preferences root = Preferences.userNodeForPackage(this.getClass());
      this.node = root.node("dialog").node(frame.getName());
      frame.addWindowListener(this);
      this.set();
   }

   private void set() {
      int x = this.node.getInt("x", -1);
      int y = this.node.getInt("y", -1);
      int w = this.node.getInt("w", -1);
      int h = this.node.getInt("h", -1);
      int s = this.node.getInt("state", 0);
      if (s != 0 || w != -1 && h != -1) {
         if (this.storestate == WindowStateSaver.STORESTATES.SIZE) {
            if (w != -1 && h != -1) {
               this.frame.setSize(w, h);
            }

            if (s == 6) {
               this.frame.setState(s);
            }
         } else if (this.storestate == WindowStateSaver.STORESTATES.LOCATION_AND_SIZE) {
            if (x != -1 && y != -1 && w != -1 && h != -1) {
               if (this.ensureOnScreen(x, y)) {
                  this.frame.setBounds(x, y, w, h);
               } else {
                  this.frame.setSize(w, h);
               }
            }

            if (s == 6) {
               this.frame.setState(s);
            }
         } else if (this.storestate == WindowStateSaver.STORESTATES.LOCATION && x != -1 && y != -1 && this.ensureOnScreen(x, y)) {
            this.frame.setLocation(x, y);
         }
      }
   }

   public static Rectangle getVirtualBounds() {
      Rectangle bounds = new Rectangle(0, 0, 0, 0);
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice[] lstGDs = ge.getScreenDevices();

      for(GraphicsDevice gd : lstGDs) {
         bounds.add(gd.getDefaultConfiguration().getBounds());
      }

      return bounds;
   }

   private boolean ensureOnScreen(int x, int y) {
      Rectangle screen = getVirtualBounds();
      return screen.contains(x, y);
   }

   private void save() {
      int s = this.frame.getState();
      if (s != 1) {
         if (!this.saved) {
            this.node.putInt("state", s);
            if (s == 0) {
               this.node.putInt("x", this.frame.getX());
               this.node.putInt("y", this.frame.getY());
               this.node.putInt("w", this.frame.getWidth());
               this.node.putInt("h", this.frame.getHeight());
            }

            try {
               this.node.sync();
               this.saved = true;
            } catch (BackingStoreException var3) {
               Logger.getLogger(WindowStateSaver.class.getName()).log(Level.SEVERE, null, var3);
            }

            this.frame.removeWindowListener();
         }
      }
   }

   public void windowOpened(WindowEvent e) {
   }

   public void windowClosing(WindowEvent e) {
      this.save();
   }

   public void windowClosed(WindowEvent e) {
      this.save();
   }

   public void windowIconified(WindowEvent e) {
   }

   public void windowDeiconified(WindowEvent e) {
   }

   public void windowActivated(WindowEvent e) {
   }

   public void windowDeactivated(WindowEvent e) {
   }

   public static enum STORESTATES {
      LOCATION,
      SIZE,
      LOCATION_AND_SIZE;
   }

   private interface WindowAdapter {
      int getX();

      int getY();

      int getWidth();

      int getHeight();

      int getState();

      void setBounds(int var1, int var2, int var3, int var4);

      void setLocation(int var1, int var2);

      void setSize(int var1, int var2);

      void setState(int var1);

      void removeWindowListener();
   }
}

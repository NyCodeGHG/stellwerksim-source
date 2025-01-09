package js.java.tools.gui.prefs;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.Preferences;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

public class SplitPanePrefsSaver implements PropertyChangeListener {
   private final Preferences saveRootNode;
   private final JSplitPane spane;

   public SplitPanePrefsSaver(JSplitPane spane, Preferences saveRootNode) {
      this.spane = spane;
      this.saveRootNode = saveRootNode;
      SwingUtilities.invokeLater(() -> {
         double loc = this.getStoredLocation();
         if (loc >= 0.0) {
            spane.setDividerLocation(loc);
         }

         spane.addPropertyChangeListener("dividerLocation", this);
      });
   }

   public SplitPanePrefsSaver(JSplitPane spane, Preferences saveRootNode, String saveRootNodeName) {
      this(spane, saveRootNode.node(saveRootNodeName));
   }

   public SplitPanePrefsSaver(JSplitPane spane, String saveRootNodeName) {
      this(spane, Preferences.userNodeForPackage(SplitPanePrefsSaver.class).node(saveRootNodeName));
   }

   public SplitPanePrefsSaver(JSplitPane spane, Class saveRootNodeClass, String saveRootNodeName) {
      this(spane, Preferences.userNodeForPackage(saveRootNodeClass).node(saveRootNodeName));
   }

   public void propertyChange(PropertyChangeEvent e) {
      if (this.spane.getWidth() > 0 && this.spane.getHeight() > 0) {
         double relative;
         if (this.spane.getOrientation() == 1) {
            relative = (double)this.spane.getDividerLocation() / (double)this.spane.getWidth();
         } else {
            relative = (double)this.spane.getDividerLocation() / (double)this.spane.getHeight();
         }

         this.setStoredLocation(relative);
      }
   }

   private Preferences getPrefsRoot() {
      return this.saveRootNode;
   }

   private double getStoredLocation() {
      return this.getPrefsRoot().getDouble("location", -1.0);
   }

   private void setStoredLocation(double location) {
      this.getPrefsRoot().putDouble("location", location);
   }
}

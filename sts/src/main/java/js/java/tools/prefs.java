package js.java.tools;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

public class prefs extends Preferences {
   private Preferences prefs_main;
   private boolean waschreated = false;

   public prefs(String root) {
      super();

      try {
         this.prefs_main = Preferences.userRoot().node(root);
         this.waschreated = true;
      } catch (Exception var3) {
      }
   }

   public void put(String key, String value) {
      if (this.waschreated) {
         this.prefs_main.put(key, value);
      }
   }

   public String get(String key, String def) {
      return this.waschreated ? this.prefs_main.get(key, def) : def;
   }

   public void remove(String key) {
      if (this.waschreated) {
         this.prefs_main.remove(key);
      }
   }

   public void clear() {
      if (this.waschreated) {
         try {
            this.prefs_main.clear();
         } catch (BackingStoreException var2) {
            Logger.getLogger(prefs.class.getName()).log(Level.SEVERE, null, var2);
         }
      }
   }

   public void putInt(String key, int value) {
      if (this.waschreated) {
         this.prefs_main.putInt(key, value);
      }
   }

   public int getInt(String key, int def) {
      return this.waschreated ? this.prefs_main.getInt(key, def) : def;
   }

   public void putLong(String key, long value) {
      if (this.waschreated) {
         this.prefs_main.putLong(key, value);
      }
   }

   public long getLong(String key, long def) {
      return this.waschreated ? this.prefs_main.getLong(key, def) : def;
   }

   public void putBoolean(String key, boolean value) {
      if (this.waschreated) {
         this.prefs_main.putBoolean(key, value);
      }
   }

   public boolean getBoolean(String key, boolean def) {
      return this.waschreated ? this.prefs_main.getBoolean(key, def) : def;
   }

   public void putFloat(String key, float value) {
      if (this.waschreated) {
         this.prefs_main.putFloat(key, value);
      }
   }

   public float getFloat(String key, float def) {
      return this.waschreated ? this.prefs_main.getFloat(key, def) : def;
   }

   public void putDouble(String key, double value) {
      if (this.waschreated) {
         this.prefs_main.putDouble(key, value);
      }
   }

   public double getDouble(String key, double def) {
      return this.waschreated ? this.prefs_main.getDouble(key, def) : def;
   }

   public void putByteArray(String key, byte[] value) {
      if (this.waschreated) {
         this.prefs_main.putByteArray(key, value);
      }
   }

   public byte[] getByteArray(String key, byte[] def) {
      return this.waschreated ? this.prefs_main.getByteArray(key, def) : def;
   }

   public String[] keys() {
      if (this.waschreated) {
         try {
            return this.prefs_main.keys();
         } catch (Exception var2) {
            System.out.println(var2.getMessage());
            var2.printStackTrace();
         }
      }

      return new String[0];
   }

   public String[] childrenNames() {
      if (this.waschreated) {
         try {
            return this.prefs_main.childrenNames();
         } catch (BackingStoreException var2) {
            Logger.getLogger(prefs.class.getName()).log(Level.SEVERE, null, var2);
         }
      }

      return new String[0];
   }

   public Preferences parent() {
      return this.waschreated ? this.prefs_main.parent() : null;
   }

   public Preferences node(String pathName) {
      return this.waschreated ? this.prefs_main.node(pathName) : null;
   }

   public boolean nodeExists(String pathName) {
      if (this.waschreated) {
         try {
            return this.prefs_main.nodeExists(pathName);
         } catch (BackingStoreException var3) {
            Logger.getLogger(prefs.class.getName()).log(Level.SEVERE, null, var3);
         }
      }

      return false;
   }

   public void removeNode() {
      if (this.waschreated) {
         try {
            this.prefs_main.removeNode();
         } catch (BackingStoreException var2) {
            Logger.getLogger(prefs.class.getName()).log(Level.SEVERE, null, var2);
         }
      }
   }

   public String name() {
      return this.waschreated ? this.prefs_main.name() : "";
   }

   public String absolutePath() {
      return this.waschreated ? this.prefs_main.absolutePath() : "";
   }

   public boolean isUserNode() {
      return this.waschreated ? this.prefs_main.isUserNode() : false;
   }

   public String toString() {
      return this.waschreated ? this.prefs_main.toString() : "";
   }

   public void flush() {
      if (this.waschreated) {
         try {
            this.prefs_main.flush();
         } catch (BackingStoreException var2) {
            Logger.getLogger(prefs.class.getName()).log(Level.SEVERE, null, var2);
         }
      }
   }

   public void sync() {
      if (this.waschreated) {
         try {
            this.prefs_main.sync();
         } catch (BackingStoreException var2) {
            Logger.getLogger(prefs.class.getName()).log(Level.SEVERE, null, var2);
         }
      }
   }

   public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
      if (this.waschreated) {
         this.prefs_main.addPreferenceChangeListener(pcl);
      }
   }

   public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
      if (this.waschreated) {
         this.prefs_main.removePreferenceChangeListener(pcl);
      }
   }

   public void addNodeChangeListener(NodeChangeListener ncl) {
      if (this.waschreated) {
         this.prefs_main.addNodeChangeListener(ncl);
      }
   }

   public void removeNodeChangeListener(NodeChangeListener ncl) {
      if (this.waschreated) {
         this.prefs_main.removeNodeChangeListener(ncl);
      }
   }

   public void exportNode(OutputStream os) {
      if (this.waschreated) {
         try {
            this.prefs_main.exportNode(os);
         } catch (BackingStoreException | IOException var3) {
            Logger.getLogger(prefs.class.getName()).log(Level.SEVERE, null, var3);
         }
      }
   }

   public void exportSubtree(OutputStream os) {
      if (this.waschreated) {
         try {
            this.prefs_main.exportSubtree(os);
         } catch (BackingStoreException | IOException var3) {
            Logger.getLogger(prefs.class.getName()).log(Level.SEVERE, null, var3);
         }
      }
   }
}

package net.miginfocom.layout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public final class LinkHandler {
   public static final int X = 0;
   public static final int Y = 1;
   public static final int WIDTH = 2;
   public static final int HEIGHT = 3;
   public static final int X2 = 4;
   public static final int Y2 = 5;
   private static final ArrayList<WeakReference<Object>> LAYOUTS = new ArrayList(4);
   private static final ArrayList<HashMap<String, int[]>> VALUES = new ArrayList(4);
   private static final ArrayList<HashMap<String, int[]>> VALUES_TEMP = new ArrayList(4);

   private LinkHandler() {
      super();
   }

   public static synchronized Integer getValue(Object layout, String key, int type) {
      Integer ret = null;
      boolean cont = true;

      for(int i = LAYOUTS.size() - 1; i >= 0; --i) {
         Object l = ((WeakReference)LAYOUTS.get(i)).get();
         if (ret == null && l == layout) {
            int[] rect = (int[])((HashMap)VALUES_TEMP.get(i)).get(key);
            if (cont && rect != null && rect[type] != -2147471302) {
               ret = rect[type];
            } else {
               rect = (int[])((HashMap)VALUES.get(i)).get(key);
               ret = rect != null && rect[type] != -2147471302 ? rect[type] : null;
            }

            cont = false;
         }

         if (l == null) {
            LAYOUTS.remove(i);
            VALUES.remove(i);
            VALUES_TEMP.remove(i);
         }
      }

      return ret;
   }

   public static synchronized boolean setBounds(Object layout, String key, int x, int y, int width, int height) {
      return setBounds(layout, key, x, y, width, height, false, false);
   }

   static synchronized boolean setBounds(Object layout, String key, int x, int y, int width, int height, boolean temporary, boolean incCur) {
      for(int i = LAYOUTS.size() - 1; i >= 0; --i) {
         Object l = ((WeakReference)LAYOUTS.get(i)).get();
         if (l == layout) {
            HashMap<String, int[]> map = (HashMap)(temporary ? VALUES_TEMP : VALUES).get(i);
            int[] old = (int[])map.get(key);
            if (old != null && old[0] == x && old[1] == y && old[2] == width && old[3] == height) {
               return false;
            }

            if (old != null && incCur) {
               boolean changed = false;
               if (x != -2147471302) {
                  if (old[0] == -2147471302 || x < old[0]) {
                     old[0] = x;
                     old[2] = old[4] - x;
                     changed = true;
                  }

                  if (width != -2147471302) {
                     int x2 = x + width;
                     if (old[4] == -2147471302 || x2 > old[4]) {
                        old[4] = x2;
                        old[2] = x2 - old[0];
                        changed = true;
                     }
                  }
               }

               if (y != -2147471302) {
                  if (old[1] == -2147471302 || y < old[1]) {
                     old[1] = y;
                     old[3] = old[5] - y;
                     changed = true;
                  }

                  if (height != -2147471302) {
                     int y2 = y + height;
                     if (old[5] == -2147471302 || y2 > old[5]) {
                        old[5] = y2;
                        old[3] = y2 - old[1];
                        changed = true;
                     }
                  }
               }

               return changed;
            }

            map.put(key, new int[]{x, y, width, height, x + width, y + height});
            return true;
         }
      }

      LAYOUTS.add(new WeakReference(layout));
      int[] bounds = new int[]{x, y, width, height, x + width, y + height};
      HashMap<String, int[]> values = new HashMap(4);
      if (temporary) {
         values.put(key, bounds);
      }

      VALUES_TEMP.add(values);
      values = new HashMap(4);
      if (!temporary) {
         values.put(key, bounds);
      }

      VALUES.add(values);
      return true;
   }

   public static synchronized void clearWeakReferencesNow() {
      LAYOUTS.clear();
   }

   public static synchronized boolean clearBounds(Object layout, String key) {
      for(int i = LAYOUTS.size() - 1; i >= 0; --i) {
         Object l = ((WeakReference)LAYOUTS.get(i)).get();
         if (l == layout) {
            return ((HashMap)VALUES.get(i)).remove(key) != null;
         }
      }

      return false;
   }

   static synchronized void clearTemporaryBounds(Object layout) {
      for(int i = LAYOUTS.size() - 1; i >= 0; --i) {
         Object l = ((WeakReference)LAYOUTS.get(i)).get();
         if (l == layout) {
            ((HashMap)VALUES_TEMP.get(i)).clear();
            return;
         }
      }
   }
}

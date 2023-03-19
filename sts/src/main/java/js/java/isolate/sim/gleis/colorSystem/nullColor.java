package js.java.isolate.sim.gleis.colorSystem;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.TreeMap;

public class nullColor extends colorStruct {
   public nullColor() {
      super();
      Field[] fs = this.getClass().getDeclaredFields();

      for(Field f : fs) {
         try {
            Object o = null;
            if (f.getDeclaringClass().isAssignableFrom(Color.class)) {
               o = new Color(0, 0, 0);
            } else if (f.getDeclaringClass().isAssignableFrom(TreeMap.class)) {
               o = new TreeMap();
            }

            f.set(this, o);
         } catch (IllegalAccessException | IllegalArgumentException var7) {
         }
      }
   }
}

package js.java.schaltungen.switchbase;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SwitchValueManipulator {
   private final DataSwitch dataSwitch;

   public SwitchValueManipulator(DataSwitch dataSwitch) {
      this.dataSwitch = dataSwitch;
   }

   public void dump() {
      Field[] fields = this.dataSwitch.getClass().getFields();

      for (Field f : fields) {
         SwitchOption so = (SwitchOption)f.getAnnotation(SwitchOption.class);
         if (so != null) {
            try {
               System.out.println(so.value() + " = " + f.getBoolean(this.dataSwitch));
            } catch (IllegalAccessException | IllegalArgumentException var8) {
               Logger.getLogger(SwitchValueManipulator.class.getName()).log(Level.SEVERE, null, var8);
            }
         }
      }
   }

   public void set(String name, boolean enabled) {
      Field[] fields = this.dataSwitch.getClass().getFields();

      for (Field f : fields) {
         SwitchOption so = (SwitchOption)f.getAnnotation(SwitchOption.class);
         if (so != null && so.value().equalsIgnoreCase(name)) {
            this.set(f, enabled);
            break;
         }
      }
   }

   private void set(Field so, boolean enabled) {
      try {
         so.setBoolean(this.dataSwitch, enabled);
      } catch (IllegalAccessException | IllegalArgumentException var4) {
         Logger.getLogger(SwitchValueManipulator.class.getName()).log(Level.SEVERE, null, var4);
      }
   }
}

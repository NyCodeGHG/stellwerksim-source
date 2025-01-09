package js.java.isolate.sim.toolkit;

public class specialEntry {
   public String text = "";
   public boolean special = true;

   public specialEntry(String t) {
      this.text = t;
   }

   public specialEntry(String t, boolean sp) {
      this.text = t;
      this.special = sp;
   }

   public String toString() {
      return this.text;
   }
}

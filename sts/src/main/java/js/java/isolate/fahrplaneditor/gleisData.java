package js.java.isolate.fahrplaneditor;

class gleisData implements Comparable {
   public String name = "";
   public boolean haltepunkt = false;

   gleisData(String n, boolean h) {
      this.name = n;
      this.haltepunkt = h;
   }

   gleisData(String n) {
      this.name = n;
   }

   public String toString() {
      return this.name;
   }

   public int compareTo(Object o) {
      gleisData g = (gleisData)o;
      return this.name.compareToIgnoreCase(g.name);
   }
}

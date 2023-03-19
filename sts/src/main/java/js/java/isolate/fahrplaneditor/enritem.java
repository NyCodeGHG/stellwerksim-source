package js.java.isolate.fahrplaneditor;

class enritem implements Comparable {
   public String text;
   public int enr;

   enritem(String t, int e) {
      super();
      this.text = t;
      this.enr = e;
   }

   public String toString() {
      return this.text + " (ENR " + this.enr + ")";
   }

   public boolean equals(Object o) {
      if (o == null) {
         return false;
      } else if (o instanceof enritem) {
         enritem e = (enritem)o;
         return e.enr == this.enr;
      } else {
         return false;
      }
   }

   public int compareTo(Object o) {
      enritem e = (enritem)o;
      if (this.enr == e.enr) {
         return 0;
      } else if (this.enr == 0) {
         return -1;
      } else {
         return e.enr == 0 ? 1 : this.text.compareToIgnoreCase(e.text);
      }
   }
}

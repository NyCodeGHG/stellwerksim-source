package js.java.tools;

public class NumColorText extends ColorText implements Comparable {
   private NumString n;

   public NumColorText(NumString _n) {
      super();
      this.n = _n;
   }

   @Override
   public String getText() {
      return this.n.getString();
   }

   @Override
   public int compareTo(Object o) {
      if (o instanceof NumColorText) {
         NumColorText other = (NumColorText)o;
         return this.n.compareTo(other.n);
      } else {
         return -1;
      }
   }
}

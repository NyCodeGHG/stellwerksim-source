package js.java.tools;

public class NumString implements Comparable, CharSequence {
   private String s;

   public NumString(String _s) {
      this.s = _s;
   }

   public String toString() {
      return this.s;
   }

   public String getString() {
      return this.s;
   }

   public boolean equals(Object o) {
      return o instanceof NumString ? this.s.equals(((NumString)o).s) : this.s.equals(o);
   }

   public int hashCode() {
      return this.s.hashCode();
   }

   public int compareTo(Object o) {
      String s2 = o.toString();
      int l = Math.max(this.s.length(), s2.length());
      int ret = 0;
      int i1 = 0;

      for (int i2 = 0; i1 < l; i2++) {
         char b1;
         try {
            b1 = this.s.charAt(i1);
         } catch (IndexOutOfBoundsException var14) {
            ret = -1;
            break;
         }

         char b2;
         try {
            b2 = s2.charAt(i2);
         } catch (IndexOutOfBoundsException var13) {
            ret = 1;
            break;
         }

         if (Character.isDigit(b1) && Character.isDigit(b2)) {
            StringBuilder digit1;
            for (digit1 = new StringBuilder(); i1 < this.s.length() && Character.isDigit(this.s.charAt(i1)); i1++) {
               digit1.append(this.s.charAt(i1));
            }

            i1--;

            StringBuilder digit2;
            for (digit2 = new StringBuilder(); i2 < s2.length() && Character.isDigit(s2.charAt(i2)); i2++) {
               digit2.append(s2.charAt(i2));
            }

            i2--;
            int z1 = Integer.parseInt(digit1.toString());
            int z2 = Integer.parseInt(digit2.toString());
            if (z1 != z2) {
               ret = z1 - z2;
               break;
            }
         } else if (b1 != b2) {
            ret = b1 - b2;
            break;
         }

         i1++;
      }

      return ret;
   }

   public int length() {
      return this.s.length();
   }

   public char charAt(int index) {
      return this.s.charAt(index);
   }

   public CharSequence subSequence(int start, int end) {
      return this.s.subSequence(start, end);
   }
}

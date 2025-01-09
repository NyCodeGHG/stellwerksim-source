package org.relayirc.util;

public class ComparableString implements IComparable {
   private String _str = null;

   public ComparableString(String str) {
      this._str = str;
   }

   @Override
   public int compareTo(IComparable other) {
      if (other instanceof ComparableString) {
         ComparableString compString = (ComparableString)other;
         String otherString = compString.getString();
         return this._str.compareTo(otherString);
      } else {
         return -1;
      }
   }

   public String getString() {
      return this._str;
   }

   public void setString(String str) {
      this._str = str;
   }
}

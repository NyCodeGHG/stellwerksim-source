package org.relayirc.util;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

public class Utilities {
   public Utilities() {
      super();
   }

   public static String[] stringToStringArray(String instr, String delim) {
      String[] sa = null;

      try {
         StringTokenizer toker = new StringTokenizer(instr, delim);
         Vector v = new Vector();

         while(toker.hasMoreTokens()) {
            String s = toker.nextToken();
            v.addElement(s);
         }

         sa = new String[v.size()];

         for(int i = 0; i < v.size(); ++i) {
            sa[i] = (String)v.elementAt(i);
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }

      return sa;
   }

   public static int[] stringToIntArray(String instr, String delim) throws NoSuchElementException, NumberFormatException {
      int[] intArray = null;
      StringTokenizer toker = new StringTokenizer(instr, delim);
      Vector ints = new Vector();

      while(toker.hasMoreTokens()) {
         String sInt = toker.nextToken();
         int nInt = Integer.parseInt(sInt);
         ints.addElement(new Integer(nInt));
      }

      intArray = new int[ints.size()];

      for(int i = 0; i < ints.size(); ++i) {
         intArray[i] = ints.elementAt(i);
      }

      return intArray;
   }

   public static String intArrayToString(int[] intArray) {
      String ret = new String();

      for(int i = 0; i < intArray.length; ++i) {
         if (ret.length() > 0) {
            ret = ret + "," + Integer.toString(intArray[i]);
         } else {
            ret = Integer.toString(intArray[i]);
         }
      }

      return ret;
   }
}

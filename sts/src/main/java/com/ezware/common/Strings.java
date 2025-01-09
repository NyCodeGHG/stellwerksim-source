package com.ezware.common;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Strings {
   private Strings() {
   }

   public static final boolean isEmpty(String s) {
      return s == null || s.trim().length() == 0;
   }

   public static final String safe(String s) {
      return isEmpty(s) ? "" : s;
   }

   public static final String capitalize(String s) {
      if (isEmpty(s)) {
         return s;
      } else {
         StringBuilder sb = new StringBuilder();

         for (String w : s.split(" ")) {
            sb.append(capitalizeWord(w));
            sb.append(' ');
         }

         return sb.toString().trim();
      }
   }

   private static String capitalizeWord(String word) {
      if (isEmpty(word)) {
         return word;
      } else {
         String capital = word.substring(0, 1).toUpperCase();
         return word.length() == 1 ? capital : capital + word.substring(1).toLowerCase();
      }
   }

   public static final String stackStraceAsString(Throwable ex) {
      StringWriter sw = new StringWriter();
      ex.printStackTrace(new PrintWriter(sw));
      return sw.toString();
   }
}

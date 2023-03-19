package org.relayirc.util;

public class Debug {
   private static boolean _debug = false;

   public Debug() {
      super();
   }

   public static void setDebug(boolean flag) {
      _debug = flag;
      if (_debug) {
         println("Debug is ON");
      }
   }

   public static boolean isDebug() {
      return _debug;
   }

   public static void println(String msg) {
      if (_debug) {
         System.out.println(msg);
      }
   }

   public static void printStackTrace(Exception e) {
      e.printStackTrace();
   }
}

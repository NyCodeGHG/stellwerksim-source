package js.java.isolate.sim.toolkit;

import java.util.Collection;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import js.java.isolate.sim.structServ.structinfo;

public class threadHelper {
   public threadHelper() {
      super();
   }

   public static Collection addThreadInfo(Vector v, StackTraceElement[] value) {
      for(int i = 0; i < value.length; ++i) {
         StackTraceElement e = value[i];
         v.add(i + ".");
         v.add(e.toString());
      }

      return v;
   }

   public static Collection addThreadInfo(Vector v, Thread t) {
      try {
         StackTraceElement[] value = t.getStackTrace();
         addThreadInfo(v, value);
      } catch (SecurityException var3) {
      }

      return v;
   }

   public static Collection getStructInfo() {
      Vector ret = new Vector();

      try {
         Map<Thread, StackTraceElement[]> m = Thread.getAllStackTraces();

         for(Entry<Thread, StackTraceElement[]> e : m.entrySet()) {
            Vector v = new Vector();
            v.addElement("Thread");
            v.addElement(((Thread)e.getKey()).toString());
            v.addElement(new threadHelper.threadData(((Thread)e.getKey()).toString(), (StackTraceElement[])e.getValue()));
            ret.add(v);
         }
      } catch (SecurityException var5) {
         Vector v = new Vector();
         v.addElement("Thread");
         v.addElement(var5.getMessage());
         v.addElement(null);
         ret.add(v);
      }

      return ret;
   }

   private static class threadData implements structinfo {
      private final String name;
      private final StackTraceElement[] value;

      private threadData(String name, StackTraceElement[] value) {
         super();
         this.name = name;
         this.value = value;
      }

      @Override
      public Vector getStructure() {
         Vector v = new Vector();
         threadHelper.addThreadInfo(v, this.value);
         return v;
      }

      @Override
      public String getStructName() {
         return this.name;
      }
   }
}

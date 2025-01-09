package js.java.tools;

public class analysisWriter {
   private String moduleName = "";

   public analysisWriter() {
   }

   public analysisWriter(String m) {
      this.moduleName = m;
   }

   protected final String getModuleName() {
      return this.moduleName;
   }

   public void writeln(String text) {
      this.writeln(this.moduleName, text);
   }

   public void writeln(String module, String text) {
      System.out.println("analysisWriter [" + module + "](" + Thread.currentThread().getId() + "): " + text);
   }

   public void dumpStack() {
      this.dumpStack(this.moduleName);
   }

   public void dumpStack(String module) {
      StackTraceElement[] se = Thread.currentThread().getStackTrace();
      String r = "Stack dump:\n";

      for (int i = 0; i < se.length; i++) {
         r = r + se[i].toString() + "\n";
      }

      this.writeln(module, r);
   }
}

package js.java.tools;

public class JavaKind {
   public JavaKind() {
      super();
   }

   public static boolean isOpenJdk() {
      return System.getProperties().getProperty("java.runtime.name").toLowerCase().contains("openjdk");
   }
}

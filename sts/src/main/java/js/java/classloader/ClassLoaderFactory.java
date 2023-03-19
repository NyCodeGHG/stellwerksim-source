package js.java.classloader;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.tools.JavaVersion;
import js.java.tools.SystemUtils;

public class ClassLoaderFactory {
   private final String title;

   public ClassLoaderFactory(String title) {
      super();
      this.title = title;
   }

   public IsolateClassLoader getClassLoader() {
      if (this.use9loader() || SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9)) {
         try {
            System.out.println("u9 loader");
            return new SubJarClassLoader(this.getClass().getClassLoader(), this.title);
         } catch (IOException var3) {
            Logger.getLogger(ClassLoaderFactory.class.getName()).log(Level.SEVERE, null, var3);
         }
      }

      URLClassLoader jnlpClassLoader = (URLClassLoader)this.getClass().getClassLoader();
      return new CustomClassLoader(jnlpClassLoader.getURLs(), jnlpClassLoader, this.title);
   }

   private boolean use9loader() {
      File u9 = new File(System.getProperty("user.home"), "sim.sts.use9");
      return u9.exists();
   }
}

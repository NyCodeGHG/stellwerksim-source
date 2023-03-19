package js.java.classloader;

import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.tools.JavaKind;

public class CustomClassLoader extends IsolateClassLoader {
   private final ChildClassLoader childClassLoader;

   public CustomClassLoader(List<URL> classpath, String name) {
      super(Thread.currentThread().getContextClassLoader());
      URL[] urls = (URL[])classpath.toArray(new URL[classpath.size()]);
      this.childClassLoader = new ChildClassLoader(urls, new DetectClass(this.getParent()), name);
   }

   public CustomClassLoader(URL[] urls, String name) {
      super(Thread.currentThread().getContextClassLoader());
      this.childClassLoader = new ChildClassLoader(urls, new DetectClass(this.getParent()), name);
   }

   public CustomClassLoader(URL[] urls, ClassLoader parent, String name) {
      super(parent);
      this.childClassLoader = new ChildClassLoader(urls, new DetectClass(this.getParent()), name);
   }

   @Override
   public void close() {
      try {
         if (!JavaKind.isOpenJdk()) {
            this.childClassLoader.close();
         }
      } catch (Exception var2) {
         Logger.getLogger(CustomClassLoader.class.getName()).log(Level.SEVERE, null, var2);
      }
   }

   protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
      try {
         return this.childClassLoader.findClass(name);
      } catch (ClassNotFoundException var4) {
         return super.loadClass(name, resolve);
      }
   }
}

package js.java.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

class ChildClassLoader extends URLClassLoader {
   private final DetectClass realParent;
   private final String name;

   public ChildClassLoader(URL[] urls, DetectClass realParent, String name) {
      super(urls, null);
      this.realParent = realParent;
      this.name = name;
      Logger.getLogger(ChildClassLoader.class.getName()).log(Level.INFO, "CCL start {0}", name);
   }

   protected void finalize() throws Throwable {
      super.finalize();
      Logger.getLogger(ChildClassLoader.class.getName()).log(Level.INFO, "CCL finished {0}", this.name);
   }

   public URL getResource(String name) {
      return !name.contains("/isolate/") ? this.realParent.getResource(name) : super.getResource(name);
   }

   public InputStream getResourceAsStream(String name) {
      return !name.contains("/isolate/") ? this.realParent.getResourceAsStream(name) : super.getResourceAsStream(name);
   }

   public Enumeration<URL> getResources(String name) throws IOException {
      return !name.contains("/isolate/") ? this.realParent.getResources(name) : super.getResources(name);
   }

   public Class<?> findClass(String name) throws ClassNotFoundException {
      if (!name.contains(".isolate.")) {
         return this.realParent.loadClass(name);
      } else {
         try {
            Class<?> loaded = super.findLoadedClass(name);
            return loaded != null ? loaded : super.findClass(name);
         } catch (ClassNotFoundException var3) {
            return this.realParent.loadClass(name);
         }
      }
   }

   protected PermissionCollection getPermissions(CodeSource codesource) {
      return this.getClass().getProtectionDomain().getPermissions();
   }
}

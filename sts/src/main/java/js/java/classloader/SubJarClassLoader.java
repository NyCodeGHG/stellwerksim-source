package js.java.classloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.tools.JavaKind;

public class SubJarClassLoader extends IsolateClassLoader {
   private final ChildClassLoader childClassLoader;
   private final Path jarfile = Files.createTempFile("isolate", "sts-jj");

   public SubJarClassLoader(String name) throws IOException {
      this(Thread.currentThread().getContextClassLoader(), name);
   }

   public SubJarClassLoader(ClassLoader parent, String name) throws IOException {
      super(parent);
      InputStream in = this.getClass().getResourceAsStream("/isolate.jar");
      Throwable var4 = null;

      try {
         if (in != null) {
            Files.copy(in, this.jarfile, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
         }
      } catch (Throwable var13) {
         var4 = var13;
         throw var13;
      } finally {
         if (in != null) {
            if (var4 != null) {
               try {
                  in.close();
               } catch (Throwable var12) {
                  var4.addSuppressed(var12);
               }
            } else {
               in.close();
            }
         }
      }

      URL[] urls = new URL[]{this.jarfile.toUri().toURL(), new File("./build/classes/").toURI().toURL()};
      this.childClassLoader = new ChildClassLoader(urls, new DetectClass(this.getParent()), name);
   }

   @Override
   public void close() {
      try {
         if (!JavaKind.isOpenJdk()) {
            this.childClassLoader.close();
         }
      } catch (Exception var3) {
         Logger.getLogger(SubJarClassLoader.class.getName()).log(Level.SEVERE, null, var3);
      }

      try {
         Files.delete(this.jarfile);
      } catch (IOException var2) {
         Logger.getLogger(SubJarClassLoader.class.getName()).log(Level.SEVERE, null, var2);
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

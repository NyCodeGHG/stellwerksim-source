package js.java.classloader;

class DetectClass extends ClassLoader {
   public DetectClass(ClassLoader parent) {
      super(parent);
   }

   public Class<?> findClass(String name) throws ClassNotFoundException {
      return super.findClass(name);
   }
}

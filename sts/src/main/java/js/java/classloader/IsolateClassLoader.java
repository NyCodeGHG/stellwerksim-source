package js.java.classloader;

import js.java.schaltungen.moduleapi.SessionClose;

public abstract class IsolateClassLoader extends ClassLoader implements SessionClose {
   protected IsolateClassLoader(ClassLoader parent) {
      super(parent);
   }
}

package com.ezware.common;

public enum OperatingSystem {
   WINDOWS("windows"),
   MACOS("mac"),
   LINUX("linux"),
   UNIX("nix"),
   SOLARIS("solaris"),
   UNKNOWN("unknown") {
      @Override
      protected boolean isReal() {
         return false;
      }
   };

   private String tag;

   private OperatingSystem(String tag) {
      this.tag = tag;
   }

   public boolean isCurrent() {
      return this.isReal() && getName().toLowerCase().indexOf(this.tag) >= 0;
   }

   public static final String getName() {
      return System.getProperty("os.name");
   }

   public static final String getVersion() {
      return System.getProperty("os.version");
   }

   public static final String getArchitecture() {
      return System.getProperty("os.arch");
   }

   public final String toString() {
      return String.format("%s v%s (%s)", getName(), getVersion(), getArchitecture());
   }

   protected boolean isReal() {
      return true;
   }

   public static final OperatingSystem getCurrent() {
      for(OperatingSystem os : values()) {
         if (os.isCurrent()) {
            return os;
         }
      }

      return UNKNOWN;
   }
}

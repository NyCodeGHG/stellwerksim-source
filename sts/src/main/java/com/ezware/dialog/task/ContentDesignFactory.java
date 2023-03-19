package com.ezware.dialog.task;

import com.ezware.common.OperatingSystem;
import com.ezware.dialog.task.design.DefaultContentDesign;
import com.ezware.dialog.task.design.LinuxContentDesign;
import com.ezware.dialog.task.design.MacOsContentDesign;

public class ContentDesignFactory {
   private ContentDesignFactory() {
      super();
   }

   public static final IContentDesign getDesignByOperatingSystem() {
      switch(OperatingSystem.getCurrent()) {
         case MACOS:
            return new MacOsContentDesign();
         case LINUX:
            return new LinuxContentDesign();
         default:
            return new DefaultContentDesign();
      }
   }
}

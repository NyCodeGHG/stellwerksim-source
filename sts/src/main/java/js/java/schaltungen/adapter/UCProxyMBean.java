package js.java.schaltungen.adapter;

import java.util.Map;

public interface UCProxyMBean {
   boolean isModuleRunning();

   String getRunningModuleName();

   void finishModule(String var1);

   int getRegisteredClosersCount();

   Map<String, String> getParameters();

   boolean isNoLoaderClose();
}

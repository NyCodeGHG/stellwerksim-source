package js.java.schaltungen.adapter;

public interface MessageAdapterMBean {
   boolean isModuleRunning();

   String[] getRunningModuleNames();

   void launchModule(String var1);

   void setNoLoaderClose(boolean var1);

   boolean isNoLoaderClose();
}

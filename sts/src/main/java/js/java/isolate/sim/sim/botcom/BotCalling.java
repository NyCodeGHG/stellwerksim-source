package js.java.isolate.sim.sim.botcom;

public interface BotCalling {
   void handleIRC(String var1, String var2, boolean var3);

   void handleIRCresult(String var1, int var2, String var3, boolean var4);

   void checkAutoMsg(String var1, String var2, String var3);

   void chatDisconnected(String var1);
}

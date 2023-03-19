package js.java.isolate.sim.sim.plugin;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public interface responseSender {
   void sendPcData(BufferedWriter var1, String var2, Map<String, String> var3, String var4) throws IOException;

   void sendLine(BufferedWriter var1, String var2, Map<String, String> var3) throws IOException;

   void sendOpeningLine(BufferedWriter var1, String var2, Map<String, String> var3) throws IOException;

   void sendClosingLine(BufferedWriter var1, String var2) throws IOException;

   void sendLine(BufferedWriter var1, String var2, String... var3) throws IOException;

   void sendOpeningLine(BufferedWriter var1, String var2, String... var3) throws IOException;

   void sendLine(BufferedWriter var1, String var2, long var3, String... var5) throws IOException;

   void sendEOR(BufferedWriter var1) throws IOException;
}

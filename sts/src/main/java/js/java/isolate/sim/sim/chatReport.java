package js.java.isolate.sim.sim;

import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.zug.zug;
import js.java.schaltungen.chatcomng.OCCU_KIND;

public interface chatReport {
   void reportSignalStellung(int var1, gleisElements.Stellungen var2, fahrstrasse var3);

   void reportZugPosition(int var1, int var2, int var3);

   void reportZugPosition(int var1, String var2, gleis var3);

   void reportZugPosition(int var1);

   void reportElementOccurance(gleis var1, OCCU_KIND var2, String var3, String var4);

   void reportOccurance(String var1, OCCU_KIND var2, String var3, String var4);

   void updateHeat(long var1);

   void reserveENR(int var1);

   void sendEnterSignalMessage(int var1, gleisElements.Stellungen var2);

   void sendEnterSignalMessage(LinkedList<gleis> var1);

   void reportFahrplanAb(int var1, int var2, int var3);

   void reportFahrplanAn(int var1, int var2, String var3, boolean var4, int var5, int var6);

   void syncZug(zug var1);

   void syncZug1(zug var1);

   void unreserveENR(int var1);

   void zugMessage(int var1, zug var2);

   void zugResponseMessage(String var1, int var2, int var3);

   void zugBlockMessage(int var1, zug var2);
}

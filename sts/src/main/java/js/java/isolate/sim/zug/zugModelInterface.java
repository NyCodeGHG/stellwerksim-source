package js.java.isolate.sim.zug;

import java.util.Collection;
import js.java.isolate.sim.autoMsg.control;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.bahnsteigDetailStore;
import js.java.isolate.sim.sim.TEXTTYPE;
import js.java.isolate.sim.sim.chatReport;
import js.java.isolate.sim.sim.fsallocator;
import js.java.isolate.sim.toolkit.HyperlinkCaller;
import js.java.schaltungen.timesystem.timedelivery;

public interface zugModelInterface extends timedelivery, chatReport {
   zug findZugByShortName(String var1);

   zug findZugByFullName(String var1);

   boolean isFirstRun();

   zug findZug(int var1);

   zug findZug(String var1);

   Collection<zug> findZugPointingMe(int var1);

   boolean haveWeSeenIt(zug var1);

   zug addZug(zug var1);

   void delZug(zug var1);

   void setZugOnBahnsteig(String var1, zug var2, gleis var3);

   void hideZug(zug var1);

   void updateZug(zug var1);

   void refreshZug();

   void showFahrplan(zug var1);

   void showText(String var1, TEXTTYPE var2, Object var3);

   void showText(String var1, TEXTTYPE var2, Object var3, HyperlinkCaller var4);

   void finishText(Object var1);

   void playAnruf();

   @Override
   void reportZugPosition(int var1, int var2, int var3);

   @Override
   void reportZugPosition(int var1, String var2, gleis var3);

   @Override
   void reportFahrplanAb(int var1, int var2, int var3);

   @Override
   void reportFahrplanAn(int var1, int var2, String var3, boolean var4, int var5, int var6);

   @Override
   void updateHeat(long var1);

   void exchangeZug(zug var1, zug var2, int var3, int var4);

   void renameZug(zug var1, int var2, int var3);

   fsallocator getFSallocator();

   void playZug();

   control getMsgControl();

   @Override
   void syncZug(zug var1);

   @Override
   void syncZug1(zug var1);

   Collection<zug> getZugList();

   bahnsteigDetailStore getBahnsteige();

   boolean isRealistic();
}

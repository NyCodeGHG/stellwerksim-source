package js.java.isolate.sim;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.sim.fsallocator;
import js.java.schaltungen.adapter.MessagingAdapter;
import js.java.schaltungen.audio.AudioController;
import js.java.schaltungen.timesystem.timedelivery;
import js.java.tools.prefs;
import js.java.tools.actions.AbstractEvent;

public interface GleisAdapter extends MessagingAdapter {
   String getParameter(String var1);

   void setUI(gleis.gleisUIcom var1);

   void readUI(gleis.gleisUIcom var1);

   void repaintGleisbild();

   void incZählwert();

   void interPanelCom(AbstractEvent var1);

   void setGUIEnable(boolean var1);

   int getBuild();

   timedelivery getTimeSystem();

   fsallocator getFSallocator();

   AudioController getAudio();

   Simulator getSim();

   gleisbildModelSts getGleisbild();

   prefs getSimPrefs();
}

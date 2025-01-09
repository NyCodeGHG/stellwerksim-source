package js.java.isolate.sim.sim;

import java.util.LinkedList;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.schaltungen.audio.AudioController;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.schaltungen.timesystem.timedelivery;
import js.java.tools.prefs;
import js.java.tools.actions.AbstractEvent;

public class GleisAdapterRouter implements GleisAdapter, SessionClose {
   private final LinkedList<GleisAdapter> adapters = new LinkedList();

   @Override
   public void close() {
      this.adapters.clear();
   }

   public GleisAdapterRouter(GleisAdapter first) {
      this.adapters.add(first);
   }

   public void add(GleisAdapter e) {
      this.adapters.add(e);
   }

   public void remove(GleisAdapter e) {
      this.adapters.remove(e);
   }

   public void drop() {
      this.adapters.removeLast();
   }

   @Override
   public String getParameter(String typ) {
      return ((GleisAdapter)this.adapters.getLast()).getParameter(typ);
   }

   @Override
   public void setUI(gleis.gleisUIcom gl) {
      ((GleisAdapter)this.adapters.getLast()).setUI(gl);
   }

   @Override
   public void readUI(gleis.gleisUIcom gl) {
      ((GleisAdapter)this.adapters.getLast()).readUI(gl);
   }

   @Override
   public void repaintGleisbild() {
      ((GleisAdapter)this.adapters.getLast()).repaintGleisbild();
   }

   @Override
   public void incZählwert() {
      ((GleisAdapter)this.adapters.getLast()).incZählwert();
   }

   @Override
   public void interPanelCom(AbstractEvent e) {
      ((GleisAdapter)this.adapters.getLast()).interPanelCom(e);
   }

   @Override
   public void setGUIEnable(boolean e) {
      ((GleisAdapter)this.adapters.getLast()).setGUIEnable(e);
   }

   @Override
   public int getBuild() {
      return ((GleisAdapter)this.adapters.getLast()).getBuild();
   }

   @Override
   public timedelivery getTimeSystem() {
      return ((GleisAdapter)this.adapters.getLast()).getTimeSystem();
   }

   @Override
   public void showStatus(String s, int type) {
      ((GleisAdapter)this.adapters.getLast()).showStatus(s, type);
   }

   @Override
   public void showStatus(String s) {
      ((GleisAdapter)this.adapters.getLast()).showStatus(s);
   }

   @Override
   public void setProgress(int p) {
      ((GleisAdapter)this.adapters.getLast()).setProgress(p);
   }

   @Override
   public fsallocator getFSallocator() {
      return ((GleisAdapter)this.adapters.getLast()).getFSallocator();
   }

   @Override
   public AudioController getAudio() {
      return ((GleisAdapter)this.adapters.getLast()).getAudio();
   }

   @Override
   public Simulator getSim() {
      return ((GleisAdapter)this.adapters.getLast()).getSim();
   }

   @Override
   public gleisbildModelSts getGleisbild() {
      return ((GleisAdapter)this.adapters.getLast()).getGleisbild();
   }

   @Override
   public prefs getSimPrefs() {
      return ((GleisAdapter)this.adapters.getLast()).getSimPrefs();
   }
}

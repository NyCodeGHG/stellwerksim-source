package js.java.isolate.sim.eventsys.events;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.weicheevent;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.schaltungen.chatcomng.OCCU_KIND;

public class weichenheizungstoerung extends weicheevent {
   private static final String NAME = "weichenheizungstoerung";
   private String text = null;
   private final ConcurrentHashMap<gleis, Long> lastMove = new ConcurrentHashMap();
   private final HashSet<gleis> blockedList = new HashSet();
   private int dauer;

   public weichenheizungstoerung(Simulator sim) {
      super(sim);
   }

   @Override
   public String getText() {
      return this.text;
   }

   @Override
   protected boolean init(eventContainer e) {
      this.dauer = this.my_main.isRealistic() ? random(5, 25) : random(5, 10);
      long now = System.currentTimeMillis() + 600000L;
      Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ALLE_WEICHEN});

      while(it.hasNext()) {
         gleis gl = (gleis)it.next();
         this.registerForStellung(gl);
         this.lastMove.put(gl, now);
      }

      this.text = "Auf Grund des anhaltenden Schneefalls des Winters müssen alle Weichen regelmäßig bewegt werden. Alle relevanten Weichen sollten mindestens einmal pro Stunde bewegt werden!";
      this.showMessageNow(this.text);
      this.my_main.reportOccurance(this.getCode(), OCCU_KIND.HOOKED, "weichenheizungstoerung", this.code);
      return true;
   }

   @Override
   public boolean init(int enr, int _dauer) {
      return false;
   }

   @Override
   public boolean hookStellung(gleis g, gleisElements.Stellungen st, fahrstrasse f) {
      if (this.blockedList.contains(g)) {
         return true;
      } else {
         int lastMoved = (int)((System.currentTimeMillis() - this.lastMove.get(g)) / 1000L);
         if (lastMoved > 60 * random(57, 70) && st != g.getFluentData().getStellung() && random(0, 10) > 6) {
            weichenausfall wa = new weichenausfall(this.my_main);
            wa.setParent(this);
            wa.glbModel = this.glbModel;
            wa.init(g.getENR(), this.dauer);
            if (!wa.hookStellung(g, st, f)) {
               this.blockedList.add(g);
               return false;
            }
         }

         if (st != g.getFluentData().getStellung()) {
            this.lastMove.put(g, System.currentTimeMillis());
         }

         return true;
      }
   }

   @Override
   public void done(event child) {
      this.lastMove.put(((weichenausfall)child).getWeiche(), System.currentTimeMillis());
      this.blockedList.remove(((weichenausfall)child).getWeiche());
   }

   @Override
   public void abort() {
   }

   @Override
   public String funkName() {
      return "Schwerer Winter: Weichen";
   }

   @Override
   public String funkAntwort() {
      return this.text;
   }

   @Override
   public Vector getStructure() {
      Vector v = super.getStructure();
      long now = System.currentTimeMillis();

      for(Entry<gleis, Long> e : this.lastMove.entrySet()) {
         v.addElement("Weiche " + ((gleis)e.getKey()).getElementName());
         v.addElement((now - e.getValue()) / 1000L + " s");
      }

      return v;
   }
}

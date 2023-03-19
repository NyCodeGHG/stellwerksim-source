package js.java.isolate.sim.eventsys.events;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.zugevent;
import js.java.isolate.sim.zug.zug;

public abstract class zugStoerungBase extends zugevent {
   protected String zname;
   protected String bname;
   protected int dauer;
   protected int anteil;
   protected boolean silent;
   protected String dtext;
   protected HashSet<zug> registered = new HashSet();
   protected EnumSet<eventGenerator.TYPES> rtypes;
   protected String text = "";
   protected zug runningZug = null;

   protected zugStoerungBase(Simulator sim) {
      super(sim);
   }

   protected boolean init(eventContainer ev, eventGenerator.TYPES... ts) {
      this.zname = ev.getValue("zugname");
      this.bname = ev.getValue("bahnsteigname");
      this.dauer = ev.getIntValue("dauer", 2);
      this.anteil = Math.max(ev.getIntValue("anteil", 100), 1);
      this.silent = ev.getBoolValue("silent", false);
      this.dtext = ev.getValue("text");
      this.rtypes = EnumSet.noneOf(eventGenerator.TYPES.class);

      for(eventGenerator.TYPES t : ts) {
         List<zug> zl = this.findAZug(t);
         if (zl.isEmpty()) {
            this.unregisterAll();
            this.eventDone();
            return false;
         }

         this.rtypes.add(t);

         for(zug z : zl) {
            this.registered.add(z);
            this.register(t, z);
         }
      }

      if (this.registered.isEmpty()) {
         this.eventDone();
         return false;
      } else {
         return true;
      }
   }

   protected final void unregisterAll() {
      for(zug z : this.registered) {
         for(eventGenerator.TYPES t : this.rtypes) {
            z.unregisterHook(t, this);
         }

         for(eventGenerator.TYPES t : eventGenerator.TYPES.values()) {
            z.unregisterHook(t, this);
         }
      }

      this.registered.clear();
   }

   protected final String dauerFormated() {
      String ret;
      if (this.dauer < 5) {
         ret = "einige";
      } else {
         ret = "ca. " + this.dauer / 5 * 5;
      }

      return ret;
   }

   protected List<zug> findAZug(eventGenerator.TYPES t) {
      LinkedList<zug> ret = new LinkedList();

      for(zug z : this.my_main.getZugList()) {
         boolean add = true;
         add &= !z.isFertig();
         add &= this.zname == null || this.zname.isEmpty() || z.getSpezialName().startsWith(this.zname);
         add &= this.bname == null || this.bname.isEmpty() || z.getZielGleis().startsWith(this.bname);
         if (add) {
            ret.add(z);
         }
      }

      if (ret.isEmpty()) {
         return ret;
      } else {
         Collections.shuffle(ret);
         int max = Math.min(ret.size(), ret.size() * 100 / this.anteil);
         if (max < 1) {
            max = 1;
         }

         return ret.subList(0, max);
      }
   }

   @Override
   public String getText() {
      return this.runningZug != null ? this.funkAntwort() : "";
   }

   @Override
   public String funkName() {
      return this.runningZug != null ? this.runningZug.getSpezialName() : null;
   }

   @Override
   public String funkAntwort() {
      return this.text;
   }

   @Override
   public Vector getStructure() {
      Vector v = super.getStructure();
      v.addElement("zname");
      v.addElement(this.zname);
      v.addElement("bname");
      v.addElement(this.bname);
      v.addElement("dauer");
      v.addElement(this.dauer + "");
      v.addElement("anteil");
      v.addElement(this.anteil + " %");
      v.addElement("silent");
      v.addElement(Boolean.toString(this.silent));
      v.addElement("runningZug");
      v.addElement(this.runningZug != null ? this.runningZug.getName() : "---");
      v.addElement("registered");
      v.addElement(this.registered.size() + "");
      v.addElement("registered z");
      String zs = "";

      for(zug z : this.registered) {
         zs = zs + "[" + z.getName() + "] ";
      }

      v.addElement(zs);
      v.addElement("rtypes");
      v.addElement(this.rtypes.toString());
      return v;
   }
}

package js.java.isolate.sim.eventsys.events;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.gleisevent;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.gleisbild.fahrstrassen.fsAllocs;

public class bahnuebergangoffenfrage extends gleisevent {
   private HashMap<Integer, bahnuebergangoffenfrage.timeStore> closeTime = new HashMap();
   private boolean caller_running = false;

   public bahnuebergangoffenfrage(Simulator sim) {
      super(sim);
   }

   @Override
   public String getText() {
      return null;
   }

   @Override
   protected boolean init(eventContainer e) {
      Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ELEMENT_BAHNÜBERGANG, gleis.ELEMENT_AUTOBAHNÜBERGANG});

      while(it.hasNext()) {
         this.registerForStellung((gleis)it.next());
      }

      return true;
   }

   @Override
   public boolean hookStellung(gleis g, gleisElements.Stellungen st, fahrstrasse f) {
      if (st == gleisElements.ST_BAHNÜBERGANG_OFFEN) {
         try {
            bahnuebergangoffenfrage.timeStore ts = (bahnuebergangoffenfrage.timeStore)this.closeTime.get(g.getENR());
            --ts.cnt;
            if (ts.cnt <= 0) {
               ts.openUpdate();
            }
         } catch (Exception var5) {
         }
      } else if (st == gleisElements.ST_BAHNÜBERGANG_GESCHLOSSEN) {
         if (!this.closeTime.containsKey(g.getENR())) {
            this.closeTime.put(g.getENR(), new bahnuebergangoffenfrage.timeStore(g));
            if (!this.caller_running) {
               this.callMeIn(1);
               this.caller_running = true;
            }
         } else {
            bahnuebergangoffenfrage.timeStore l = (bahnuebergangoffenfrage.timeStore)this.closeTime.get(g.getENR());
            if (l.cnt <= 0) {
               l.closeUpdate();
            }

            ++l.cnt;
         }
      }

      return true;
   }

   @Override
   public boolean pong() {
      if (!this.closeTime.isEmpty()) {
         long current = this.my_main.getSimutime();
         int c = 0;
         Iterator<bahnuebergangoffenfrage.timeStore> it = this.closeTime.values().iterator();

         while(it.hasNext()) {
            bahnuebergangoffenfrage.timeStore l = (bahnuebergangoffenfrage.timeStore)it.next();
            if (l.nextcalltime <= current) {
               if (l.open) {
                  it.remove();
               } else {
                  String n = l.gl.getElementName();
                  String text = "";
                  if (l.cc < 1) {
                     text = "Schönen guten Tag, hier spricht die Ortspolizei. Der Bahnübergang mit der Nummer "
                        + n
                        + " ist seit über 15 Minuten geschlossen und hier ist inzwischen ein größeres Verkehrschaos, gibt es dafür einen Grund? Bitte den Bahnübergang öffnen.";
                  } else if (l.cc < 2) {
                     text = "Guten Tag! Hier spricht nochmal die Ortspolizei. Bitte öffnen Sie unverzüglich den Bahnübergang mit der Nummer "
                        + n
                        + "! Ansonsten sehen wir uns gezwungen Ihre vorgesetzte Dienststelle zu informieren.";
                  } else if (l.cc < 3) {
                     text = "Hier spricht die Ortspolizei. Öffnen Sie unverzüglich den Bahnübergang mit der Nummer "
                        + n
                        + "! Ihre vorgesetzte Dienststelle haben wir bereits informiert. Sollten Sie dem nicht nachkommen, sehen wir uns gezwungen weitere Maßnahmen zu ergreifen.";
                     c = 0;
                  } else {
                     Iterator<gleis> itbü = this.glbModel.findIterator(new Object[]{l.enr, gleis.ELEMENT_BAHNÜBERGANG});
                     gleis hadBü = null;
                     boolean success = true;

                     while(itbü.hasNext()) {
                        gleis bügl = (gleis)itbü.next();
                        hadBü = bügl;
                        fahrstrasse fs = bügl.getFluentData().getCurrentFS();
                        if (fs != null) {
                           success &= this.my_main.getFSallocator().getFS(fs, fsAllocs.ALLOCM_FREE);
                        }
                     }

                     if (success && hadBü != null) {
                        text = "Hier spricht die Ortspolizei. Wir öffnen den Bahnübergang mit der Nummer " + n + " jetzt gewaltsam!";
                        eventContainer ev = new eventContainer(this.glbModel, bahnuebergangstoerung.class);
                        ev.setGleis(hadBü);
                        ev.setValue("dauer", random(8, 20));
                        event.createEvent(ev, this.glbModel, this.my_main);
                        this.my_main.finishText(this);
                        l.openUpdate();
                     }

                     c = 0;
                  }

                  l.update();
                  if (text != null) {
                     this.showCallMessageNow(text);
                  }

                  if (++c > 2) {
                     break;
                  }
               }
            }
         }

         this.callMeIn(1);
         this.caller_running = true;
      } else {
         this.caller_running = false;
      }

      return false;
   }

   @Override
   public Vector getStructure() {
      Vector v = super.getStructure();

      for(bahnuebergangoffenfrage.timeStore ts : this.closeTime.values()) {
         v.addElement(ts.enr + ": enr");
         v.addElement(Integer.toString(ts.enr));
         v.addElement(ts.enr + ": name");
         v.addElement(ts.gl.getElementName());
         v.addElement(ts.enr + ": open");
         v.addElement(Boolean.toString(ts.open));
         v.addElement(ts.enr + ": closetime");
         v.addElement(Long.toString(ts.closetime));
         v.addElement(ts.enr + ": cc");
         v.addElement(Integer.toString(ts.cc));
         v.addElement(ts.enr + ": lastcalltime");
         v.addElement(Long.toString(ts.lastcalltime));
         v.addElement(ts.enr + ": nextcalltime");
         v.addElement(Long.toString(ts.nextcalltime));
         v.addElement(ts.enr + ": cnt");
         v.addElement(Integer.toString(ts.cnt));
      }

      return v;
   }

   private class timeStore {
      long closetime = 0L;
      long lastcalltime = 0L;
      long nextcalltime = 0L;
      gleis gl;
      int enr = 0;
      int cc = 0;
      int cnt = 1;
      boolean open = true;

      timeStore(gleis g, long c) {
         super();
         this.gl = g;
         this.enr = this.gl.getENR();
         this.closetime = c;
         this.nextcalltime = c + 900000L;
      }

      timeStore(gleis g) {
         this(g, bahnuebergangoffenfrage.this.my_main.getSimutime());
      }

      void update() {
         this.lastcalltime = bahnuebergangoffenfrage.this.my_main.getSimutime();
         this.nextcalltime = this.lastcalltime + 180000L;
         this.open = false;
         ++this.cc;
      }

      void closeUpdate() {
         long c = bahnuebergangoffenfrage.this.my_main.getSimutime();
         this.closetime = c;
         if (this.cc > 0) {
            this.nextcalltime = c + 180000L;
            this.cc = 1;
         } else {
            this.nextcalltime = c + 900000L;
            this.cc = 0;
         }

         this.open = false;
         this.cnt = 0;
      }

      void openUpdate() {
         this.lastcalltime = bahnuebergangoffenfrage.this.my_main.getSimutime();
         this.nextcalltime = this.lastcalltime + 120000L;
         this.open = true;
         this.cnt = 0;
      }
   }
}

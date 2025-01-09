package js.java.isolate.sim.eventsys.events;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.gleis.gleis;
import js.java.schaltungen.chatcomng.OCCU_KIND;

public class displayausfall extends event {
   private static final String NAME = "displayausfall";
   private static boolean oneIsRunning = false;
   private eventContainer ec;
   private String text = null;
   private int state;
   protected int dauer;
   private final LinkedList<displayausfall.ddata> displays = new LinkedList();
   private int pos = 0;
   private int len = 0;
   private int counter;

   public displayausfall(Simulator sim) {
      super(sim);
   }

   @Override
   public String getText() {
      return this.text;
   }

   @Override
   protected boolean init(eventContainer e) {
      if (oneIsRunning) {
         this.eventDone();
         return false;
      } else {
         oneIsRunning = true;
         this.ec = e;
         this.dauer = e.getIntValue("dauer") + (this.my_main.isRealistic() ? random(5, 20) : 0);
         Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ALLE_ZUGDISPLAYS});

         while (it.hasNext()) {
            gleis gl = (gleis)it.next();
            this.displays.add(new displayausfall.ddata(gl));
         }

         if (this.displays.isEmpty()) {
            this.eventDone();
            return true;
         } else {
            Collections.shuffle(this.displays);
            int nsize = this.displays.size() / 4;
            if (nsize < 2) {
               nsize = 2;
            }

            while (this.displays.size() > nsize) {
               this.displays.remove();
            }

            this.state = 0;
            this.callMe();
            String call = "";
            if (!this.hasParent()) {
               call = this.getCallText();
               this.acceptingCall();
            } else {
               this.state = 2;
            }

            this.text = "Displays ausgefallen." + call;
            this.showMessageNow(this.text);
            this.my_main.reportOccurance(this.getCode(), OCCU_KIND.OCCURED, "displayausfall", this.code);
            return true;
         }
      }
   }

   private void fillDisplay(gleis g, int nr, int l) {
      String dtext = "";

      for (int i = 0; i < l; i++) {
         dtext = dtext + nr;
      }

      g.getFluentData().displaySet(dtext);
   }

   @Override
   public void startCall(String token) {
      String ctext = this.funkName() + ": Danke für den Anruf, die Arbeiten beginnen sofort.";
      this.showMessageNow(ctext);
      this.state = 2;
   }

   @Override
   public boolean pong() {
      int dcc = 0;
      switch (this.state) {
         case 0:
            Collections.shuffle(this.displays);

            for (displayausfall.ddata dxx : this.displays) {
               dcc++;
               String n = "";

               for (int i = 0; i < 8; i++) {
                  String c = "\\ ";
                  if (i < dxx.swwert.length()) {
                     c = dxx.swwert.charAt(i) + "";
                  }

                  if (Math.random() > 0.7) {
                     n = n + (int)(Math.random() * 10.0);
                  } else {
                     n = n + c;
                  }

                  if (Math.random() > 0.3 && !n.isEmpty()) {
                     n = n + "\\<";
                  } else if (Math.random() > 0.8) {
                     n = n + " ";
                  }
               }

               dxx.display.getFluentData().displaySet(n);
               if (dcc > this.displays.size() / 2) {
                  break;
               }
            }

            this.state = 1;
            this.counter = 20;
            this.callMe();
            break;
         case 1:
            this.counter--;
            if (this.counter <= 0) {
               Collections.shuffle(this.displays);

               for (displayausfall.ddata dx : this.displays) {
                  dcc++;
                  this.fillDisplay(dx.display, (int)(Math.random() * 10.0), (int)(Math.random() * 8.0));
                  if (dcc > this.displays.size() / 2) {
                     break;
                  }
               }

               this.state = 0;
            }

            this.callMe();
            break;
         case 2:
            for (displayausfall.ddata dx : this.displays) {
               dx.display.getFluentData().displayClear(true);
            }

            this.callMeIn(this.dauer);
            this.state = 3;
            break;
         case 3:
            this.callMe();

            for (displayausfall.ddata dx : this.displays) {
               this.fillDisplay(dx.display, this.pos, this.len);
            }

            this.pos++;
            if (this.pos > 9) {
               this.pos = 0;
               this.len++;
               if (this.len > 8) {
                  this.state = 4;
               }
            }
            break;
         case 4:
            displayausfall.ddata d = (displayausfall.ddata)this.displays.poll();
            if (d != null) {
               this.callMe();
               d.display.getFluentData().displaySet(d.swwert);
               d.display.getFluentData().displayBlink(true);
            } else {
               this.my_main.reportOccurance(this.getCode(), OCCU_KIND.NORMAL, "displayausfall", this.code);
               this.eventDone();
               oneIsRunning = false;
               this.text = "Displays wieder einsatzbereit, Anzeigen ggf. noch veraltet";
               this.showMessageNow(this.text);
            }
      }

      return false;
   }

   @Override
   public String funkName() {
      return "Displayausfall!";
   }

   @Override
   public String funkAntwort() {
      if (!this.isCalled() && !this.hasParent()) {
         String call = this.getCallText();
         return "Displays ausgefallen." + call;
      } else {
         return "Reparatur dauert noch ca. " + (this.restTime() + 1) + " Minuten.";
      }
   }

   private class ddata {
      gleis display;
      String swwert;

      ddata(gleis g) {
         this.display = g;
         this.swwert = g.getFluentData().displayGetValue();
      }
   }
}

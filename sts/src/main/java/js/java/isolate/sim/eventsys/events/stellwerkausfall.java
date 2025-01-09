package js.java.isolate.sim.eventsys.events;

import java.util.Iterator;
import java.util.Vector;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.callBehaviour;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.eventHaeufigkeiten;
import js.java.isolate.sim.eventsys.eventParent;
import js.java.isolate.sim.eventsys.eventmsg;
import js.java.isolate.sim.eventsys.fahrstrassemsg;
import js.java.isolate.sim.eventsys.gleismsg;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.colorSystem.gleisColor;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.gleisbild.fahrstrassen.fsAllocs;
import js.java.isolate.sim.zug.zug;
import js.java.schaltungen.chatcomng.OCCU_KIND;

public class stellwerkausfall extends event {
   private static final String NAME = "stellwerkausfall";
   private static boolean oneIsRunning = false;
   private levelDisplay levelD = null;
   private String text;
   private String funkname = null;
   private String funktext;
   private stellwerkausfall.stwsBase current = null;
   private boolean wascalled = false;

   public stellwerkausfall(Simulator sim) {
      super(sim);
   }

   @Override
   public Vector getStructure() {
      Vector v = super.getStructure();
      v.addElement("wascalled?");
      v.addElement(Boolean.toString(this.wascalled));
      if (this.current != null) {
         v.addElement("current");
         v.addElement(this.current.getClass().getSimpleName());
      }

      if (this.levelD != null) {
         v.addElement("level");
         v.addElement(this.levelD.getLevel());
      }

      return v;
   }

   @Override
   protected boolean init(eventContainer e) {
      if (oneIsRunning) {
         this.eventDone();
         return false;
      } else {
         oneIsRunning = true;
         this.my_main.reportOccurance(this.getCode(), OCCU_KIND.OCCURED, "stellwerkausfall", this.code);
         this.current = new stellwerkausfall.stwsStart();
         this.levelD = new levelDisplay(this.my_main.getFrame(), this);

         for (gleis gl : this.glbModel) {
            if (gl.getElement() == gleis.ELEMENT_SIGNAL
               || gl.getElement() == gleis.ELEMENT_ZWERGSIGNAL
               || gl.getElement() == gleis.ELEMENT_WEICHEOBEN
               || gl.getElement() == gleis.ELEMENT_WEICHEUNTEN) {
               gl.registerHook(eventGenerator.T_GLEIS_STELLUNG, this);
               if (gl.getElement() == gleis.ELEMENT_SIGNAL) {
                  gl.getFluentData().clear_FW_speicher();
                  gl.disableAutoFW();
               }
            }
         }

         this.glbModel.disableAllAutoFS();
         int f = this.glbModel.countFahrwege();

         for (int i = 0; i < f; i++) {
            fahrstrasse fs = this.glbModel.getFahrweg(i);
            fs.registerHook(eventGenerator.T_FS_SETZEN, this);
         }

         return true;
      }
   }

   @Override
   public void abort() {
      this.setState(new stellwerkausfall.stwsAnlaufen());
   }

   public void eventFinished() {
      oneIsRunning = false;
      this.eventDone();
   }

   private void unregister() {
      for (gleis gl : this.glbModel) {
         if (gl.getElement() == gleis.ELEMENT_SIGNAL
            || gl.getElement() == gleis.ELEMENT_ZWERGSIGNAL
            || gl.getElement() == gleis.ELEMENT_WEICHEOBEN
            || gl.getElement() == gleis.ELEMENT_WEICHEUNTEN) {
            gl.unregisterHook(eventGenerator.T_GLEIS_STELLUNG, this);
         }
      }

      int f = this.glbModel.countFahrwege();

      for (int i = 0; i < f; i++) {
         fahrstrasse fs = this.glbModel.getFahrweg(i);
         fs.unregisterHook(eventGenerator.T_FS_SETZEN, this);
      }
   }

   private void powerOff(boolean off) {
      for (gleis gl : this.glbModel) {
         gl.getFluentData().setPowerOff(off);
         if (off && gl.getElement() == gleis.ELEMENT_SIGNAL) {
            gl.getFluentData().clear_FW_speicher();
            gl.disableAutoFW();
         }
      }

      if (off) {
         this.glbModel.disableAllAutoFS();
      }

      this.glbModel.repaint();
   }

   private void redAll() {
      Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ALLE_STARTSIGNALE});

      while (it.hasNext()) {
         gleis gl = (gleis)it.next();

         try {
            this.my_main.getFSallocator().getFS(gl.getFluentData().getStartingFS(), fsAllocs.ALLOCM_FREE);
         } catch (Exception var4) {
         }
      }

      this.glbModel.repaint();
   }

   private void setState(stellwerkausfall.stwsBase n) {
      this.current = n;
   }

   @Override
   public void clicked(String url) {
      this.current.clicked(url);
   }

   @Override
   public final boolean hookCall(eventGenerator.TYPES typ, eventmsg e) {
      if (e != null && e instanceof gleismsg) {
         gleismsg ge = (gleismsg)e;
         if (typ == eventGenerator.T_GLEIS_STATUS) {
            return this.current.hookStatus(ge.g, ge.s, ge.z);
         }

         if (typ == eventGenerator.T_GLEIS_STELLUNG) {
            return this.current.hookStellung(ge.g, ge.st, ge.f);
         }
      } else if (e != null && e instanceof fahrstrassemsg) {
         fahrstrassemsg gex = (fahrstrassemsg)e;
         if (typ == eventGenerator.T_FS_SETZEN) {
            return this.current.hookSet(gex.f);
         }

         if (typ == eventGenerator.T_FS_LOESCHEN) {
            return this.current.hookClear(gex.f);
         }
      }

      return true;
   }

   void levelReached(int i, boolean minlevel) {
      this.current.levelReached(i, minlevel);
   }

   @Override
   public boolean pong() {
      this.current.pong();
      return true;
   }

   @Override
   public String getText() {
      return this.text;
   }

   @Override
   public String funkName() {
      return this.funkname;
   }

   @Override
   public String funkAntwort() {
      return this.funktext;
   }

   private void folgeFehler() {
      eventHaeufigkeiten eh = eventHaeufigkeiten.create(this.glbModel);

      for (int i = 0; i < 4; i++) {
         eventContainer ev = new eventContainer(this.glbModel, randomsignalstoerung.class);
         ev.setIntValue("dauer", 7 + i * 2);
         ev.setValue("stark", true);
         eh.add(eventHaeufigkeiten.HAEUFIGKEITEN.oft, ev);
      }

      if (Math.random() < 0.5) {
         for (int i = 0; i < 3; i++) {
            eventContainer ev = new eventContainer(this.glbModel, randomweichestoerung.class);
            ev.setIntValue("dauer", 6 + i * 2);
            ev.setValue("stark", true);
            eh.add(eventHaeufigkeiten.HAEUFIGKEITEN.oft, ev);
         }
      }

      if (Math.random() < 0.4) {
         eventContainer ev = new eventContainer(this.glbModel, weichenfsstoerung.class);
         ev.setIntValue("dauer", 10);
         eh.add(eventHaeufigkeiten.HAEUFIGKEITEN.regelmaessig, ev);
      }

      if (Math.random() < 0.6) {
         eventContainer ev = new eventContainer(this.glbModel, fsspeicherstoerung.class);
         ev.setIntValue("dauer", 10);
         eh.add(eventHaeufigkeiten.HAEUFIGKEITEN.oft, ev);
      }

      if (Math.random() < 0.5) {
         eventContainer ev = new eventContainer(this.glbModel, weichenueberwachung.class);
         ev.setIntValue("dauer", 12);
         eh.add(eventHaeufigkeiten.HAEUFIGKEITEN.gelegentlich, ev);
      } else if (Math.random() < 0.2) {
         eventContainer ev = new eventContainer(this.glbModel, displayausfall.class);
         ev.setIntValue("dauer", 17);
         eh.add(eventHaeufigkeiten.HAEUFIGKEITEN.gelegentlich, ev);
      }

      for (int i = 0; i < 2; i++) {
         eventContainer ev = new eventContainer(this.glbModel, relaisgruppestoerung.class);
         ev.setIntValue("dauer", 11 + i * 2);
         ev.setValue("stark", true);
         eh.add(eventHaeufigkeiten.HAEUFIGKEITEN.regelmaessig, ev);
      }
   }

   private class stwsAbfrage extends stellwerkausfall.stwsBase {
      private int callcount = 10;

      stwsAbfrage() {
         this.setFunk(
            "Stellwerk ausgefallen",
            "Bitte die Frage beantworten.<ul><li><a href='yes'>JA, testen</a></li><li><a href='no'>NEIN, das wird schon alles ohne gehen</a></li></ul>"
         );
         stellwerkausfall.this.callMeIn(1);
         stellwerkausfall.this.showCallMessageNow(this.callbackText());
      }

      private String callbackText() {
         return "Die Notstromversorgung ist angelaufen, alle Umformer laufen. Der Stellbezirk ist wieder betriebsbereit.<p>Wir können jetzt eine umfassende Prüfung durchführen, um Folgestörungen zu vermeiden. Dazu müssen wir allerdings nacheinander<ul><li>den automatischen Weichenlauf für 10 Minuten abschalten</li><li>den Fahrstraßenspeicher für 4 Minuten abschalten</li><li>die Displays für 1 Minute abschalten</li></ul>Bitte entscheiden:<ul><li><a href='yes'>JA, testen</a></li><li><a href='no'>NEIN, das wird schon alles ohne gehen</a></li></ul>";
      }

      @Override
      public void clicked(String url) {
         stellwerkausfall.this.resetTimer();
         if (url.equals("yes")) {
            stellwerkausfall.this.setState(stellwerkausfall.this.new stwsSystemtest());
         } else {
            stellwerkausfall.this.folgeFehler();
            stellwerkausfall.this.eventFinished();
         }
      }

      @Override
      public void pong() {
         this.callcount--;
         if (this.callcount <= 0) {
            this.showAndSetText("Alles klar, wir deuten das Nicht-Beantworten mal als nein. Ist aber nicht die feinste Art zu antworten!");
            stellwerkausfall.this.folgeFehler();
            stellwerkausfall.this.eventFinished();
         } else {
            stellwerkausfall.this.showCallMessageNow(this.callbackText());
            stellwerkausfall.this.callMeIn(1);
         }
      }
   }

   private class stwsAbgeschlossen extends stellwerkausfall.stwsBase {
      stwsAbgeschlossen() {
         this.showAndSetText("Die Notstromversorgung ist angelaufen, alle Umformer laufen. Der Stellbezirk ist wieder betriebsbereit.");
         stellwerkausfall.this.my_main.reportOccurance(stellwerkausfall.this.getCode(), OCCU_KIND.NORMAL, "stellwerkausfall", stellwerkausfall.this.code);
         stellwerkausfall.this.eventFinished();
      }

      @Override
      public void pong() {
      }
   }

   private class stwsAnlaufen extends stellwerkausfall.stwsBase {
      private Iterator<gleis> it = stellwerkausfall.this.glbModel.iterator();

      stwsAnlaufen() {
         this.setFunk("Stellwerk ausgefallen", "Alle Systeme gehen langsam wieder in Betrieb.");
         stellwerkausfall.this.callMe();
      }

      @Override
      boolean hookStellung(gleis g, gleisElements.Stellungen stellungen, fahrstrasse st) {
         if (gleis.ALLE_WEICHEN.matches(g.getElement()) && g.getFluentData().getStellung() != stellungen) {
            g.decrementBlinkcc(20);
         }

         return gleis.ALLE_STARTSIGNALE.matches(g.getElement()) ? stellungen == gleis.ST_SIGNAL_ROT : false;
      }

      @Override
      public void pong() {
         boolean next;
         for (int i = 0; (next = this.it.hasNext()) && i < 80; i++) {
            ((gleis)this.it.next()).getFluentData().setPowerOff(false);
         }

         if (!next) {
            this.showAndSetText("Notstromversorgung angelaufen, alle Umformer laufen. Stellbezirk wieder betriebsbereit.");
            stellwerkausfall.this.unregister();
            stellwerkausfall.this.resetTimer();
            stellwerkausfall.this.setState(stellwerkausfall.this.new stwsAbfrage());
         }
      }
   }

   private abstract class stwsBase {
      private stwsBase() {
      }

      boolean hookStatus(gleis g, int s, zug z) {
         return true;
      }

      boolean hookStellung(gleis g, gleisElements.Stellungen stellungen, fahrstrasse st) {
         return true;
      }

      boolean hookSet(fahrstrasse f) {
         return true;
      }

      boolean hookClear(fahrstrasse f) {
         return true;
      }

      public abstract void pong();

      void levelReached(int l, boolean minlevel) {
      }

      public void clicked(String url) {
      }

      protected final void setText(String t) {
         stellwerkausfall.this.text = t;
      }

      protected final void setFunk(String name, String text) {
         stellwerkausfall.this.funkname = name;
         stellwerkausfall.this.funktext = text;
      }

      protected final void showAndSetText(String t) {
         stellwerkausfall.this.text = t;
         stellwerkausfall.this.showMessageNow(t);
      }

      protected final void showText(String t) {
         stellwerkausfall.this.showMessageNow(t);
      }
   }

   private abstract class stwsBattBetrieb extends stellwerkausfall.stwsBase {
      private boolean lastMinLevel;

      stwsBattBetrieb(boolean f) {
         this.lastMinLevel = f;
      }

      stwsBattBetrieb(stellwerkausfall.stwsBattBetrieb f) {
         try {
            this.lastMinLevel = f.lastMinLevel;
         } catch (NullPointerException var4) {
            this.lastMinLevel = false;
         }
      }

      @Override
      boolean hookStellung(gleis g, gleisElements.Stellungen stellungen, fahrstrasse st) {
         boolean forceOk = false;
         if (gleis.ALLE_STARTSIGNALE.matches(g.getElement()) && stellungen == gleis.ST_SIGNAL_ROT) {
            forceOk = true;
         }

         if (gleis.ALLE_WEICHEN.matches(g.getElement())) {
            if (g.getFluentData().getStellung() != stellungen) {
               stellwerkausfall.this.levelD.addConsumer(levelDisplay.CONSUMENT.HEAVY);
               g.decrementBlinkcc(60);
            } else {
               stellwerkausfall.this.levelD.addConsumer(levelDisplay.CONSUMENT.MEDIUM);
            }
         } else if (gleis.ALLE_BAHNÜBERGÄNGE.matches(g.getElement())) {
            if (g.getFluentData().getStellung() != stellungen) {
               stellwerkausfall.this.levelD.addConsumer(levelDisplay.CONSUMENT.HEAVY);
            } else {
               stellwerkausfall.this.levelD.addConsumer(levelDisplay.CONSUMENT.MEDIUM);
            }
         } else {
            stellwerkausfall.this.levelD.addConsumer(levelDisplay.CONSUMENT.LIGHT);
         }

         return forceOk || !this.lastMinLevel;
      }

      @Override
      boolean hookStatus(gleis g, int s, zug z) {
         stellwerkausfall.this.levelD.addConsumer(levelDisplay.CONSUMENT.LIGHT);
         return true;
      }

      @Override
      boolean hookSet(fahrstrasse f) {
         stellwerkausfall.this.levelD.addConsumer(levelDisplay.CONSUMENT.MEDIUM);
         return !this.lastMinLevel;
      }

      @Override
      void levelReached(int l, boolean minlevel) {
         if (minlevel != this.lastMinLevel) {
            stellwerkausfall.this.powerOff(minlevel);
            if (minlevel) {
               stellwerkausfall.this.my_main.playAlarm(1);
               stellwerkausfall.this.redAll();
               this.showText("Kritische Ladung unterschritten. Alle Signale auf HALT gefallen!");
            } else {
               this.showText("Ladung wieder ok.");
            }

            this.lastMinLevel = minlevel;
         }
      }
   }

   private class stwsBattBetrieb1 extends stellwerkausfall.stwsBattBetrieb {
      stwsBattBetrieb1() {
         super(false);
         stellwerkausfall.this.callMeIn(5);
      }

      @Override
      public void pong() {
         stellwerkausfall.this.setState(stellwerkausfall.this.new stwsBattBetrieb_startGen(this));
      }
   }

   private class stwsBattBetrieb_aus extends stellwerkausfall.stwsBase {
      stwsBattBetrieb_aus() {
         stellwerkausfall.this.my_main.playAlarm(4);
         stellwerkausfall.this.callMeIn(stellwerkausfall.this.wascalled ? 1 : 4);
         stellwerkausfall.this.powerOff(true);
         gleisColor.getInstance().dimmLight(0);
         stellwerkausfall.this.levelD.setLevel(0);
         if (stellwerkausfall.this.wascalled) {
            this.showAndSetText(
               "Stromversorgung komplett zusammengebrochen! Ein gesicherter Zugverkehr ist zur Zeit nicht möglich. Ein Techniker ist bereits vor Ort und untersucht die Sache."
            );
         } else {
            this.showAndSetText(
               "Stromversorgung komplett zusammengebrochen! Ein gesicherter Zugverkehr ist zur Zeit nicht möglich. Ein Techniker untersucht die Sache."
            );
         }

         this.setFunk("Stellwerk ausgefallen", "Die Ursache wird gesucht, dies wird einige Minuten dauern.");
         stellwerkausfall.this.levelD.hide();
         stellwerkausfall.this.levelD = null;
      }

      @Override
      boolean hookSet(fahrstrasse f) {
         return false;
      }

      @Override
      boolean hookStellung(gleis g, gleisElements.Stellungen stellungen, fahrstrasse st) {
         return gleis.ALLE_STARTSIGNALE.matches(g.getElement()) ? stellungen == gleis.ST_SIGNAL_ROT : false;
      }

      @Override
      public void pong() {
         stellwerkausfall.this.setState(stellwerkausfall.this.new stwsAnlaufen());
      }
   }

   private class stwsBattBetrieb_startGen extends stellwerkausfall.stwsBattBetrieb {
      stwsBattBetrieb_startGen(stellwerkausfall.stwsBattBetrieb f) {
         super(f);
         stellwerkausfall.this.callMeIn(1);
         this.showAndSetText("Notstromgenerator wird gestartet.");
      }

      @Override
      public void pong() {
         if (stellwerkausfall.random(0, 10) > 3) {
            stellwerkausfall.this.setState(stellwerkausfall.this.new stwsBattBetrieb_zs1weiche(this));
         } else {
            stellwerkausfall.this.powerOff(false);
            stellwerkausfall.this.unregister();
            stellwerkausfall.this.setState(stellwerkausfall.this.new stwsAbgeschlossen());
            stellwerkausfall.this.levelD.hide();
            stellwerkausfall.this.levelD = null;
         }
      }
   }

   private class stwsBattBetrieb_zs1 extends stellwerkausfall.stwsBase {
      private int level = 0;
      private int levelcc = 0;
      private final int vorlauf_levelcc = 12;

      stwsBattBetrieb_zs1() {
         stellwerkausfall.this.callMe();
      }

      @Override
      public void pong() {
         this.levelcc++;
         if (this.levelcc > this.vorlauf_levelcc) {
            this.levelcc = 0;
            if (this.level < 20) {
               this.level++;
               gleisColor.getInstance().dimmLight(this.level);
               stellwerkausfall.this.levelD.setLevel(40 - this.level);
            } else {
               stellwerkausfall.this.resetTimer();
               stellwerkausfall.this.registerCallBehaviour(null);
               stellwerkausfall.this.setState(stellwerkausfall.this.new stwsBattBetrieb_aus());
            }
         }
      }

      @Override
      boolean hookSet(fahrstrasse f) {
         return false;
      }

      @Override
      boolean hookStellung(gleis g, gleisElements.Stellungen stellungen, fahrstrasse st) {
         boolean ret = true;
         if (gleis.ALLE_WEICHEN.matches(g.getElement())) {
            ret = false;
         } else if (gleis.ALLE_STARTSIGNALE.matches(g.getElement())) {
            ret = stellungen == gleis.ST_SIGNAL_ZS1 || stellungen == gleis.ST_SIGNAL_ROT;
         }

         return ret;
      }
   }

   private class stwsBattBetrieb_zs1weiche extends stellwerkausfall.stwsBattBetrieb implements callBehaviour {
      private boolean weion = true;
      private boolean gotoNextState = false;
      private int levelcc = 9;

      stwsBattBetrieb_zs1weiche(stellwerkausfall.stwsBattBetrieb f) {
         super(f);
         stellwerkausfall.this.resetTimer();
         stellwerkausfall.this.callMeIn(1);
         stellwerkausfall.this.my_main.playAlarm(2);
         stellwerkausfall.this.levelD.setLevel(90);
         String call = stellwerkausfall.this.getCallText();
         stellwerkausfall.this.registerCallBehaviour(this);
         stellwerkausfall.this.wascalled = false;
         stellwerkausfall.this.acceptingCall();
         this.showAndSetText(
            "Notstromgenerator konnte nicht gestartet werden! Fahrbetrieb wird eingeschränkt! Zugverkehr nur noch auf Ersatzsignal (ErsGT), Weichen sind manuell zu bewegen."
               + call
         );
         this.setFunk("Stellwerk ausgefallen", stellwerkausfall.this.text);
         stellwerkausfall.this.powerOff(false);
      }

      @Override
      boolean hookSet(fahrstrasse f) {
         return false;
      }

      @Override
      boolean hookStellung(gleis g, gleisElements.Stellungen stellungen, fahrstrasse st) {
         boolean ret = false;
         if (gleis.ALLE_WEICHEN.matches(g.getElement())) {
            ret = this.weion && st == null;
         } else if (gleis.ALLE_STARTSIGNALE.matches(g.getElement())) {
            ret = stellungen == gleis.ST_SIGNAL_ZS1 || stellungen == gleis.ST_SIGNAL_ROT;
         }

         if (ret) {
            ret = super.hookStellung(g, stellungen, st);
         }

         return ret;
      }

      @Override
      void levelReached(int l, boolean minlevel) {
         if (minlevel && this.weion) {
            this.weion = false;
            stellwerkausfall.this.my_main.playAlarm(2);
            this.showAndSetText("Restenergie zu gering, Weichenantriebsumformer abgeschaltet! Weichen können nicht mehr bewegt werden!");
         }

         if (this.gotoNextState && minlevel) {
            stellwerkausfall.this.resetTimer();
            stellwerkausfall.this.setState(stellwerkausfall.this.new stwsBattBetrieb_zs1());
         }
      }

      @Override
      public void pong() {
         this.levelcc--;
         if (this.levelcc <= 7) {
            this.gotoNextState = true;
         }

         stellwerkausfall.this.levelD.setLevel(10 * Math.max(this.levelcc, 0));
         stellwerkausfall.this.callMeIn(1);
      }

      @Override
      public void called(event e, String token) {
         this.showAndSetText("Ein Techniker wird sich um die Notstromversorgung kümmern!");
         stellwerkausfall.this.wascalled = true;
      }
   }

   private class stwsStart extends stellwerkausfall.stwsBase {
      private int waitC = 3;

      stwsStart() {
         this.showAndSetText(
            "Externe Stromversorgung unterbrochen, Hilfsstromversorgung aufgeschaltet. Eingeschränker Fahrbetrieb möglich. Jede Schaltung belastet Hilfsstrom sehr stark! Den Versorgungszeiger im Auge behalten! Speichersysteme wie FS-Speicher und AutoFS belasten sehr stark!"
         );
         stellwerkausfall.this.my_main.playAlarm(2);
         stellwerkausfall.this.powerOff(true);
         stellwerkausfall.this.callMe();
      }

      @Override
      boolean hookStellung(gleis g, gleisElements.Stellungen stellungen, fahrstrasse st) {
         return false;
      }

      @Override
      boolean hookSet(fahrstrasse f) {
         return false;
      }

      @Override
      public void pong() {
         this.waitC--;
         if (this.waitC <= 0) {
            stellwerkausfall.this.powerOff(false);
            stellwerkausfall.this.resetTimer();
            stellwerkausfall.this.setState(stellwerkausfall.this.new stwsBattBetrieb1());
         }
      }
   }

   private class stwsSystemtest extends stellwerkausfall.stwsBase implements eventParent {
      stwsSystemtest() {
         String t = "Es werden folgende Systeme nacheinander abgeschaltet:<ul><li>der automatischen Weichenlauf für 10 Minuten</li><li>der Fahrstraßenspeicher für 4 Minuten</li><li>die Displays für 1 Minute</li></ul>";
         this.setFunk("Stellwerk ausgefallen", t);
         this.showAndSetText(t);
         eventContainer ev = new eventContainer(stellwerkausfall.this.glbModel, weichenfsstoerung.class);
         ev.setIntValue("dauer", 10);
         event e = event.createEvent(ev, stellwerkausfall.this.glbModel, this, stellwerkausfall.this.my_main);
         if (e == null) {
            stellwerkausfall.this.eventFinished();
         }
      }

      @Override
      public void pong() {
      }

      @Override
      public void done(event p) {
         if (p instanceof weichenfsstoerung) {
            eventContainer ev = new eventContainer(stellwerkausfall.this.glbModel, fsspeicherstoerung.class);
            ev.setIntValue("dauer", 4);
            event e = event.createEvent(ev, stellwerkausfall.this.glbModel, this, stellwerkausfall.this.my_main);
            if (e == null) {
               stellwerkausfall.this.eventFinished();
            }
         } else if (p instanceof fsspeicherstoerung) {
            eventContainer ev = new eventContainer(stellwerkausfall.this.glbModel, displayausfall.class);
            ev.setIntValue("dauer", 1);
            event e = event.createEvent(ev, stellwerkausfall.this.glbModel, this, stellwerkausfall.this.my_main);
            if (e == null) {
               stellwerkausfall.this.eventFinished();
            }
         } else if (p instanceof displayausfall) {
            stellwerkausfall.this.showCallMessageNow("Fehlersuche nach dem Stromausfall erfolgreich abgeschlossen!");
            stellwerkausfall.this.eventFinished();
         }
      }
   }
}

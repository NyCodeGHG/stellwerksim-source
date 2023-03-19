package js.java.isolate.sim.sim.plugin;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.eventHaeufigkeiten;
import js.java.isolate.sim.eventsys.eventmsg;
import js.java.isolate.sim.eventsys.gleismsg;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasseSelection;
import js.java.isolate.sim.gleisbild.fahrstrassen.fsAllocs;
import js.java.isolate.sim.sim.stellwerksim_main;
import org.xml.sax.Attributes;

public class controlServ extends ServImpl {
   private final element[] elmsSearch = new element[]{
      gleisElements.ELEMENT_SIGNAL,
      gleisElements.ELEMENT_WEICHEOBEN,
      gleisElements.ELEMENT_WEICHEUNTEN,
      gleisElements.ELEMENT_ZWERGSIGNAL,
      gleisElements.ELEMENT_EINFAHRT,
      gleisElements.ELEMENT_AUSFAHRT
   };
   private final element[] elmsRegister = new element[]{
      gleisElements.ELEMENT_SIGNAL,
      gleisElements.ELEMENT_WEICHEOBEN,
      gleisElements.ELEMENT_WEICHEUNTEN,
      gleisElements.ELEMENT_ZWERGSIGNAL,
      gleisElements.ELEMENT_EINFAHRT
   };
   private ConcurrentHashMap<BufferedWriter, controlServ.cmdHook> hookList = new ConcurrentHashMap();
   private CopyOnWriteArraySet<gleis> gleisHookList = new CopyOnWriteArraySet();
   protected final stellwerksim_main my_main;
   private responseSender sender = new xmlResponse();

   public controlServ(stellwerksim_main m) {
      super(3791);
      this.my_main = m;
      this.xmlr.registerTag("switch", this);
      this.xmlr.registerTag("element2enr", this);
      this.xmlr.registerTag("enr2element", this);
      this.xmlr.registerTag("setfs", this);
      this.xmlr.registerTag("registerzug", this);
      this.xmlr.registerTag("unregisterzug", this);
   }

   @Override
   protected void addNewClient(Socket client) {
      super.addNewClient(client);
      System.out.println("Fernsteuerverbindung hergestellt mit " + client.getInetAddress().getHostAddress());
      this.my_main.message("Fernsteuerverbindung hergestellt mit " + client.getInetAddress().getHostAddress(), stellwerksim_main.MSGLEVELS.IMPORTANT);
   }

   @Override
   protected void finish(ServImpl.OutputWriter output) {
      this.unregisterZug(output);
      System.out.println("Fernsteuerverbindung beendet");
      this.my_main.message("Fernsteuerverbindung beendet", stellwerksim_main.MSGLEVELS.IMPORTANT);
   }

   @Override
   protected void xmlError(Exception e) {
   }

   @Override
   public void parseStartTag(String tag, Attributes attrs) {
      this.my_main.simulateClick();
      long ref = Long.parseLong(attrs.getValue("ref"));
      if (tag.equals("switch")) {
         this.switchTag(this.currentBuffer, ref, attrs);
      } else if (tag.equals("element2enr")) {
         this.element2enr(this.currentBuffer, ref, Integer.parseInt(attrs.getValue("element")));
      } else if (tag.equals("enr2element")) {
         this.enr2element(this.currentBuffer, ref, Integer.parseInt(attrs.getValue("enr")));
      } else if (tag.equals("setfs")) {
         this.setfs(this.currentBuffer, ref, Integer.parseInt(attrs.getValue("start")), Integer.parseInt(attrs.getValue("stop")));
      } else if (tag.equals("manualauto")) {
         this.manualAuto(this.currentBuffer, ref, Integer.parseInt(attrs.getValue("start")), Integer.parseInt(attrs.getValue("stop")));
      } else if (tag.equals("registerzug")) {
         this.registerZug(this.currentBuffer, ref, Integer.parseInt(attrs.getValue("enr")));
      } else if (tag.equals("unregisterzug")) {
         this.unregisterZug(this.currentBuffer, ref, Integer.parseInt(attrs.getValue("enr")));
      }
   }

   private void switchTag(BufferedWriter currentBuffer, long ref, Attributes attrs) {
      String item = attrs.getValue("item");
      if (item.equals("stoerung")) {
         eventHaeufigkeiten.stoerungenein = !attrs.getValue("mode").equals("off");
      } else if (item.equals("bue")) {
         gleis.michNervenBüs = attrs.getValue("mode").equals("off");
      } else if (item.equals("autofs")) {
         this.my_main.getGleisbild().enableAllAutoFS(attrs.getValue("mode").equals("all"));
      }
   }

   private void element2enr(BufferedWriter output, long ref, int e) {
      int enr = 0;
      Iterator<gleis> it = this.my_main.getGleisbild().findIteratorWithElementName(Integer.toString(e), new Object[]{this.elmsSearch});
      if (it.hasNext()) {
         enr = ((gleis)it.next()).getENR();
      }

      try {
         this.sender.sendLine(output, "enr4", ref, "element", Integer.toString(e), "enr", Integer.toString(enr));
      } catch (IOException var8) {
      }
   }

   private void enr2element(BufferedWriter output, long ref, int e) {
      String element = "0";
      gleis v = this.my_main.getGleisbild().findFirst(new Object[]{e, this.elmsSearch});

      try {
         element = v.getShortElementName();
      } catch (NullPointerException var9) {
      }

      try {
         this.sender.sendLine(output, "element4", ref, "element", element, "enr", Integer.toString(e));
      } catch (IOException var8) {
      }
   }

   private void setfs(BufferedWriter output, long ref, int enr1, int enr2) {
      gleis start = this.my_main.getGleisbild().findFirst(new Object[]{enr1, this.elmsSearch});
      gleis stop = this.my_main.getGleisbild().findFirst(new Object[]{enr2, this.elmsSearch});

      try {
         fahrstrasseSelection fs = this.my_main.getGleisbild().findFahrweg(start, stop, false);
         fs.addHook(
            new controlServ.fsSetResponseHook(output, ref, enr1, enr2, ServBase.FAILREASON.SUCCESS), EnumSet.of(fahrstrasseSelection.StateChangeTypes.GOT_FS)
         );
         fs.addHook(
            new controlServ.fsSetResponseHook(output, ref, enr1, enr2, ServBase.FAILREASON.BUSY),
            EnumSet.of(fahrstrasseSelection.StateChangeTypes.CANTGET_FS, fahrstrasseSelection.StateChangeTypes.ERROR_GETFS)
         );
         if (this.my_main.getGleisbild().gleisbildextend.getSignalversion() > 0
            ? this.my_main.getFSallocator().getFS(fs, fsAllocs.ALLOCM_USER_GETORSTORE)
            : this.my_main.getFSallocator().getFS(fs, fsAllocs.ALLOCM_USER_GET)) {
         }
      } catch (NullPointerException var11) {
         try {
            this.sender
               .sendLine(output, "fsset", ref, "start", Integer.toString(enr1), "stop", Integer.toString(enr2), "result", ServBase.FAILREASON.UNKNOWN.name());
         } catch (IOException var10) {
         }
      }
   }

   private void manualAuto(BufferedWriter output, long ref, int enr1, int enr2) {
      ServBase.FAILREASON success = ServBase.FAILREASON.UNKNOWN;
      gleis start = this.my_main.getGleisbild().findFirst(new Object[]{enr1, this.elmsSearch});
      gleis stop = this.my_main.getGleisbild().findFirst(new Object[]{enr2, this.elmsSearch});

      try {
         fahrstrasseSelection fs = this.my_main.getGleisbild().findFahrweg(start, stop, false);
         fs.getStart().setTriggeredAutoFW(fs);
         success = ServBase.FAILREASON.SUCCESS;
      } catch (NullPointerException var11) {
         success = ServBase.FAILREASON.UNKNOWN;
      }

      try {
         this.sender.sendLine(output, "autoon", ref, "start", Integer.toString(enr1), "stop", Integer.toString(enr2), "result", success.name());
      } catch (IOException var10) {
      }
   }

   private void registerZug(BufferedWriter output, long ref, int enr) {
      ServBase.FAILREASON success = ServBase.FAILREASON.WRONGMODE;
      if (!eventHaeufigkeiten.stoerungenein) {
         gleis sig = this.my_main.getGleisbild().findFirst(new Object[]{enr, this.elmsRegister});

         try {
            controlServ.cmdHook hook = (controlServ.cmdHook)this.hookList.get(output);
            if (hook == null) {
               hook = new controlServ.cmdHook(output);
               this.hookList.put(output, hook);
            }

            if (!sig.hasThisHookRegistered(eventGenerator.HOOKKIND.POSTINFO, eventGenerator.T_GLEIS_STATUS, hook)) {
               sig.registerHook(eventGenerator.HOOKKIND.POSTINFO, eventGenerator.T_GLEIS_STATUS, hook);
               this.gleisHookList.add(sig);
            }

            success = ServBase.FAILREASON.SUCCESS;
         } catch (NullPointerException var9) {
            success = ServBase.FAILREASON.UNKNOWN;
         }
      }

      try {
         this.sender.sendLine(output, "zugregistered", ref, "enr", Integer.toString(enr), "result", success.name());
      } catch (IOException var8) {
      }
   }

   private void unregisterZug(BufferedWriter output, long ref, int enr) {
      ServBase.FAILREASON success = ServBase.FAILREASON.WRONGMODE;
      if (!eventHaeufigkeiten.stoerungenein) {
         gleis sig = this.my_main.getGleisbild().findFirst(new Object[]{enr, this.elmsRegister});

         try {
            controlServ.cmdHook hook = (controlServ.cmdHook)this.hookList.get(output);
            if (hook != null) {
               sig.unregisterHook(eventGenerator.T_GLEIS_STATUS, hook);
               success = ServBase.FAILREASON.SUCCESS;
            }
         } catch (NullPointerException var9) {
            success = ServBase.FAILREASON.UNKNOWN;
         }
      }

      try {
         this.sender.sendLine(output, "zugunregistered", ref, "enr", Integer.toString(enr), "result", success.name());
      } catch (IOException var8) {
      }
   }

   private void unregisterZug(BufferedWriter output) {
      controlServ.cmdHook hook = (controlServ.cmdHook)this.hookList.get(output);
      if (hook != null) {
         for(gleis g : this.gleisHookList) {
            g.unregisterHook(eventGenerator.T_GLEIS_STATUS, hook);
         }
      }
   }

   private class cmdHook implements eventGenerator.eventCall {
      private final BufferedWriter output;

      cmdHook(BufferedWriter output) {
         super();
         this.output = output;
      }

      @Override
      public boolean hookCall(eventGenerator.TYPES typ, eventmsg e) {
         if (typ == eventGenerator.TYPES.T_GLEIS_STATUS) {
            gleismsg gm = (gleismsg)e;
            if (gm.z != null && gm.s == 2 && gm.g.getFluentData().getStatus() != 2) {
               try {
                  gleis a = controlServ.this.my_main.getGleisbild().findFirst(new Object[]{gm.z.getAusEnr(), gleisElements.ELEMENT_AUSFAHRT});
                  String ausf = a != null ? a.getSWWert_special() : "";
                  controlServ.this.sender
                     .sendLine(
                        this.output,
                        "zug",
                        "enr",
                        Integer.toString(gm.g.getENR()),
                        "name",
                        gm.z.getName(),
                        "gleis",
                        gm.z.getZielGleis(),
                        "ausfahrt",
                        ausf,
                        "zid",
                        gm.z.getZID()
                     );
               } catch (IOException var6) {
               }
            }
         }

         return true;
      }

      @Override
      public String funkName() {
         return null;
      }

      public boolean equals(Object o) {
         if (o instanceof controlServ.cmdHook) {
            return ((controlServ.cmdHook)o).output == this.output;
         } else {
            return false;
         }
      }

      public int compareTo(Object o) {
         if (o instanceof eventGenerator.eventCall) {
            eventGenerator.eventCall e = (eventGenerator.eventCall)o;
            String s1 = this.funkName();
            String s2 = e.funkName();
            if (s1 != null && s2 != null) {
               return s1.compareToIgnoreCase(s2);
            } else {
               return s1 != null ? -1 : 1;
            }
         } else {
            return -1;
         }
      }
   }

   private class fsSetResponseHook implements fahrstrasseSelection.ChangeHook {
      private final ServBase.FAILREASON success;
      private final int enr1;
      private final int enr2;
      private final long ref;
      private final BufferedWriter output;

      fsSetResponseHook(BufferedWriter output, long ref, int enr1, int enr2, ServBase.FAILREASON success) {
         super();
         this.success = success;
         this.enr1 = enr1;
         this.enr2 = enr2;
         this.ref = ref;
         this.output = output;
      }

      @Override
      public void call(fahrstrasseSelection s) {
         try {
            controlServ.this.sender
               .sendLine(
                  this.output, "fsset", this.ref, "start", Integer.toString(this.enr1), "stop", Integer.toString(this.enr2), "result", this.success.name()
               );
         } catch (IOException var3) {
         }
      }
   }
}

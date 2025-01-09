package js.java.schaltungen.adapter;

import de.deltaga.eb.EventBus;
import de.deltaga.eb.EventBusService;
import de.deltaga.eb.EventHandler;
import java.util.EnumSet;
import java.util.concurrent.ConcurrentHashMap;
import js.java.schaltungen.UserContextMini;
import js.java.schaltungen.chatcomng.BotCommandMessage;
import js.java.schaltungen.chatcomng.BotMessageEvent;
import js.java.schaltungen.chatcomng.PrivateMessageEvent;
import js.java.schaltungen.chatcomng.SimControlMessage;

public class MessageAdapter implements MessageAdapterMBean {
   private final UserContextMini uc;
   private final EventBus bus;
   private final ConcurrentHashMap<UCProxy, Module> runningModules = new ConcurrentHashMap();
   private final EnumSet<Module> runningModule = EnumSet.noneOf(Module.class);
   private boolean noLoaderClose = false;

   public MessageAdapter(UserContextMini uc) {
      this.uc = uc;
      this.bus = EventBusService.getInstance();
      this.bus.subscribe(this);
   }

   @EventHandler
   public void botEvents(BotMessageEvent event) {
      if (event.text.startsWith("[")) {
         this.bus.publish(new SimControlMessage(event.text, event.ispublic, event.instanz));
      } else if (event.text.startsWith(":") && !event.ispublic) {
         this.bus.publish(new BotCommandMessage(event.text));
      }
   }

   @EventHandler
   public void privMsgEvents(PrivateMessageEvent event) {
   }

   @EventHandler
   public void commandEvents(BotCommandMessage event) {
      if (event.msg.equals(":ACCEPT")) {
         this.uc.showTopLevelMessage("Bereit für den Start.", 3);
      } else if (event.msg.equals(":NOTACCEPT")) {
         this.uc.showTopLevelMessage("Anmeldung nicht erfolgreich, JNLP neu laden und starten.", 10);
      } else if (event.msg.startsWith(":LAUNCH ")) {
         String[] opts = event.msg.split(" ");
         if (opts.length >= 3) {
            this.launch(opts[1], opts[2]);
         }
      }
   }

   @Override
   public void launchModule(String command) {
      String[] opts = command.split(" ");
      if (opts.length >= 2) {
         this.launch(opts[0], opts[1]);
      }
   }

   private void launch(String modul, String parameterUrl) {
      try {
         Module mod = Module.valueOf(modul);
         this.bus.publish(new LaunchModule(mod, parameterUrl));
      } catch (IllegalArgumentException var4) {
      }
   }

   @EventHandler
   public void launch(LaunchModule event) {
      Module mod = event.modul;
      if (!mod.multiInstances && this.runningModule.contains(mod)) {
         this.uc.showTopLevelMessage("Modul " + mod.title + " kann nur einmal laufen.", 5);
         this.bus.publish(new SpoolLaunchEvent(event));
      } else {
         if (!this.runningModules.isEmpty()) {
            for (Module m : this.runningModule) {
               if (m.singleModule && mod.singleModule) {
                  this.uc.showTopLevelMessage("Das laufende Modul " + m.title + " kann nicht zusammen mit " + mod.title + " laufen.", 5);
                  this.bus.publish(new SpoolLaunchEvent(event));
                  return;
               }
            }
         }

         synchronized (this.runningModules) {
            UCProxy ucp = new UCProxy(this.uc, this.noLoaderClose);
            this.runningModules.put(ucp, mod);
            this.runningModule.add(mod);
            ucp.launch(event);
            this.bus.publish(new RunningModulesCountEvent(this.runningModules.size(), mod.singleModule));
         }
      }
   }

   @EventHandler
   public void endEvent(EndModule event) {
      if (event.uc instanceof UCProxy) {
         UCProxy ucp = (UCProxy)event.uc;
         synchronized (this.runningModules) {
            this.runningModules.remove(ucp);
            if (!this.runningModules.values().contains(event.modul)) {
               this.runningModule.remove(event.modul);
            }

            this.bus.publish(new RunningModulesCountEvent(this.runningModules.size(), false));
         }
      }
   }

   @Override
   public boolean isModuleRunning() {
      return !this.runningModule.isEmpty();
   }

   @Override
   public String[] getRunningModuleNames() {
      synchronized (this.runningModules) {
         String[] ret = new String[this.runningModules.size()];
         int i = 0;

         for (Module m : this.runningModules.values()) {
            ret[i] = m.title;
            i++;
         }

         return ret;
      }
   }

   @Override
   public void setNoLoaderClose(boolean e) {
      this.noLoaderClose = e;
   }

   @Override
   public boolean isNoLoaderClose() {
      return this.noLoaderClose;
   }
}

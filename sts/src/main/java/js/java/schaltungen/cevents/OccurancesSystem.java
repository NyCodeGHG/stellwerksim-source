package js.java.schaltungen.cevents;

import de.deltaga.eb.EventBus;
import de.deltaga.eb.EventBusService;
import de.deltaga.eb.EventHandler;
import java.awt.TrayIcon.MessageType;
import js.java.schaltungen.UserContextMini;
import js.java.schaltungen.adapter.ModuleCloseEvent;
import js.java.schaltungen.chatcomng.JoinChannelEvent;
import js.java.schaltungen.chatcomng.XmlObject;

public class OccurancesSystem {
   private final UserContextMini uc;
   private final EventBus bus;
   private boolean sentBuild = false;
   private boolean newerBuild = false;
   private long lastMessage = 0L;
   private long firstMessage = 0L;

   public OccurancesSystem(UserContextMini uc) {
      this.uc = uc;
      this.bus = EventBusService.getInstance();
      this.bus.subscribe(this);
   }

   @EventHandler
   public void joinChannel(JoinChannelEvent ch) {
      if (!this.sentBuild) {
         this.sentBuild = true;
         this.bus.publish(new XmlObject(new BuildEvent(this.uc.getBuild())));
      }
   }

   @EventHandler
   public void buildReceiver(BuildEvent event) {
      if (this.newerBuild && this.firstMessage > 0L && this.firstMessage + 21600000L < System.currentTimeMillis()) {
         RestartRequiredMessageWindow.showMessage(this.uc);
      }

      if (!this.newerBuild || this.lastMessage + 3600000L <= System.currentTimeMillis()) {
         if (1 < event.apiLevel || this.uc.getBuild() < event.build - 50) {
            this.newerBuild = true;
            this.lastMessage = System.currentTimeMillis();
            if (this.firstMessage == 0L) {
               this.firstMessage = this.lastMessage;
            }

            this.uc.showTrayMessage("Neue Version verfügbar", "Bitte das Programm zur Aktualisierung bitte binnen 10 Minuten neu starten.", MessageType.INFO);
            this.uc.showTopLevelMessage("Eine neue Version ist verfügbar, bitte das Programm bitte binnen 10 Minuten neu starten.", 15);
            RestartRequiredMessageWindow.showMessage(this.uc);
         } else if (this.uc.getBuild() < event.build) {
            this.newerBuild = true;
            this.lastMessage = System.currentTimeMillis();
            if (this.firstMessage == 0L) {
               this.firstMessage = this.lastMessage;
            }

            this.uc.showTopLevelMessage("Eine neue Version ist verfügbar, bitte das Programm zur Aktualisierung neu starten.", 15);
         }
      }
   }

   @EventHandler
   public void moduleClose(ModuleCloseEvent event) {
      if (this.newerBuild) {
         this.uc.showTopLevelMessage("Eine neue Version ist verfügbar, bitte das Programm zur Aktualisierung neu starten.", 15);
      }
   }
}

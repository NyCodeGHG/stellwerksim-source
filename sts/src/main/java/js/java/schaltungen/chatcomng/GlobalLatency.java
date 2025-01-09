package js.java.schaltungen.chatcomng;

import de.deltaga.eb.DelayEvent;
import de.deltaga.eb.EventBusService;
import de.deltaga.eb.EventHandler;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.schaltungen.UserContextMini;
import js.java.schaltungen.timesystem.timeSync;
import js.java.schaltungen.webservice.StoreLatencies;
import js.java.tools.gui.clock.timerecipient;

public class GlobalLatency implements timerecipient {
   final UserContextMini uc;
   private final ScheduledExecutorService executor;
   private String nextLatenzTo = null;
   private timeSync secondLatence;
   private int tlatency;

   public GlobalLatency(UserContextMini uc) {
      this.uc = uc;

      try {
         this.secondLatence = new timeSync(uc.getParameter(UserContextMini.DATATYPE.TIMESERVER), 0, this);
         Thread t = new Thread(this.secondLatence);
         t.start();
      } catch (IOException var3) {
         Logger.getLogger(GlobalLatency.class.getName()).log(Level.SEVERE, null, var3);
      }

      EventBusService.getInstance().subscribe(this);
      this.executor = Executors.newSingleThreadScheduledExecutor();
      this.executor.scheduleAtFixedRate(() -> this.sendBotPing(), 1L, 5L, TimeUnit.MINUTES);
      this.executor.scheduleAtFixedRate(() -> this.sendIrcPing(), 1L, 2L, TimeUnit.MINUTES);
   }

   private void sendIrcPing() {
      EventBusService.getInstance().publish(new IrcPingEvent());
      this.secondLatence.sync();
   }

   private void sendBotPing() {
      EventBusService.getInstance().publish(new ChatMessageEvent(this.uc.getControlBot(), ".UPING " + System.currentTimeMillis()));
      this.secondLatence.sync();
   }

   @EventHandler
   public void commandEvents(CheckLatencyNowEvent event) {
      this.sendBotPing();
   }

   @EventHandler
   public void privMsg(PrivateMessageEvent event) {
      String msg = event.text;
      if (msg.startsWith("INFO")) {
         String hostname = "Unknown";

         try {
            InetAddress addr = InetAddress.getLocalHost();
            hostname = addr.getHostName() + "/" + addr.toString();
         } catch (UnknownHostException var5) {
            hostname = "Hostname can not be resolved";
         }

         String retmsg = "BUILD "
            + this.uc.getBuild()
            + "; Java  "
            + System.getProperty("java.version")
            + "; Arch "
            + System.getProperty("sun.arch.data.model")
            + "bit; "
            + Runtime.getRuntime().availableProcessors()
            + " cores; OS "
            + System.getProperty("os.name")
            + "; version "
            + System.getProperty("os.version")
            + "Hostname "
            + hostname;
         EventBusService.getInstance().publish(new ChatMessageEvent(event.sender, retmsg));
      } else if (msg.startsWith("MESSEN")) {
         EventBusService.getInstance().publish(new ChatMessageEvent(event.sender, "Es wird jetzt 5x in einem Abstand von 10 Sekunden ein Text verschickt!"));

         for (int i = 0; i < 5; i++) {
            EventBusService.getInstance().publish(new DelayEvent(new ChatMessageEvent(event.sender, "Test Text " + (i + 1)), i * 10));
         }
      } else if (msg.startsWith("LATENZ")) {
         this.nextLatenzTo = event.sender;
         this.sendBotPing();
      }
   }

   @EventHandler
   public void commandEvents(BotCommandMessage event) {
      if (event.msg.startsWith(":UPONG")) {
         long sendTime = Long.parseLong(event.msg.substring(6).trim());
         int delay = (int)(System.currentTimeMillis() - sendTime);
         if (this.nextLatenzTo != null) {
            EventBusService.getInstance()
               .publish(new ChatMessageEvent(this.nextLatenzTo, "Latenz gemessen: " + delay + " ms, Time Latenz: " + this.tlatency + " ms"));
            this.nextLatenzTo = null;
         }

         if (delay > 500) {
            Logger.getLogger(GlobalLatency.class.getName()).log(Level.WARNING, "IRC Latenz: {0} ms", delay);
            EventBusService.getInstance().publish(new StoreLatencies("irc", "uping", delay));
            EventBusService.getInstance().publish(new StoreLatencies("time", "latenz", this.tlatency));
         }
      }
   }

   public void timeChange(long offsetToLocal, short tagescode, int latency) {
      this.tlatency = latency;
   }

   public static int pingHost(String hostname) throws IOException {
      Socket t = new Socket(hostname, 7);
      Throwable var3 = null;

      int latency;
      try {
         DataInputStream dis = new DataInputStream(t.getInputStream());
         PrintStream ps = new PrintStream(t.getOutputStream());
         long now = System.currentTimeMillis();
         ps.println("P" + now);
         String str = dis.readLine();
         if (str.startsWith("P")) {
            latency = (int)(System.currentTimeMillis() - now);
         } else {
            latency = -1;
         }
      } catch (Throwable var16) {
         var3 = var16;
         throw var16;
      } finally {
         if (t != null) {
            if (var3 != null) {
               try {
                  t.close();
               } catch (Throwable var15) {
                  var3.addSuppressed(var15);
               }
            } else {
               t.close();
            }
         }
      }

      return latency;
   }
}

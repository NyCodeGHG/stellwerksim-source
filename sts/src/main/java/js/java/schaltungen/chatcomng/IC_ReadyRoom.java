package js.java.schaltungen.chatcomng;

import de.deltaga.eb.DelayEvent;
import de.deltaga.eb.EventBusService;
import de.deltaga.eb.EventHandler;
import de.deltaga.eb.UniqueDelayEvent;
import de.deltaga.serial.Base64;
import de.deltaga.serial.XmlMarshal;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import js.java.schaltungen.adapter.EndModule;
import js.java.schaltungen.adapter.StartingModule;
import js.java.schaltungen.cevents.BuildEvent;
import js.java.schaltungen.webservice.StoreTextData;
import org.relayirc.chatengine.ChannelEvent;

public class IC_ReadyRoom extends IrcChannel {
   private static int MAX_USERS_PER_LIST = 10;
   private static final int MAX_IRC_LENGTH = 395;
   private static boolean MASTERSERVER = true;
   private static boolean MASTERSERVER_ONCE = false;
   private static boolean MASTERSERVER_IAM = false;
   private static boolean SENDLOG = false;
   private final XmlMarshal xmlConverter = new XmlMarshal(new Class[]{BuildEvent.class, CurrentUserList.class, CurrentUserListUserEntry.class});
   private String currentGame = null;
   private long currentGameTime;
   private final StringBuilder tracelog = new StringBuilder();
   private final long entrytime = System.currentTimeMillis();
   private final UUID myUuid;
   private final ConcurrentHashMap<String, Set<String>> userListReceived = new ConcurrentHashMap();
   private final ScheduledThreadPoolExecutor executor;
   private ScheduledFuture<?> future = null;
   private static final String XMLHEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";

   public IC_ReadyRoom(ChatNG chat, String name, String title) {
      super(chat, name, title, false);
      this.myUuid = ChatUser.generateNickUUID(chat.mySelf());
      this.executor = new ScheduledThreadPoolExecutor(1);
      chat.bus.subscribe(this);
      ChatUser cu = chat.getKnownUser(chat.mySelf());
      cu.setDistance(0);
      cu.setEntrytime(this.entrytime);
      cu.setName(chat.uc.getUsername());
   }

   @Override
   public void onMessage(ChannelEvent event) {
      String msg = event.getValue().toString();
      String nick = event.getOriginNick2();
      this.onMessage(nick, msg, this.channel.name);
   }

   @EventHandler
   public void onMessage(PrivateMessageEvent event) {
      String msg = event.text;
      String nick = event.sender;
      this.onMessage(nick, msg, nick);
   }

   private void onMessage(String nick, String msg, String replyTo) {
      if (msg.startsWith("IAM ")) {
         this.chat.addKnownUser(nick, msg.trim().substring(4));
      } else if (msg.startsWith("IPLAY ")) {
         ChatUser cu = this.chat.getKnownUser(nick);
         cu.setPlay(msg.trim().substring(6));
      } else if (msg.startsWith("ILEFT")) {
         ChatUser cu = this.chat.getKnownUser(nick);
         cu.setPlay(null);
      } else if (msg.startsWith("VERSION")) {
         int r = (int)(Math.random() * 10000.0);
         String retmsg = "BUILD "
            + this.chat.uc.getBuild()
            + "; Java  "
            + System.getProperty("java.version")
            + "; Arch "
            + System.getProperty("sun.arch.data.model")
            + "bit; "
            + Runtime.getRuntime().availableProcessors()
            + " cores; OS "
            + System.getProperty("os.name")
            + "; version "
            + System.getProperty("os.version");
         this.chat.bus.publish(new DelayEvent(new ChatMessageEvent(replyTo, retmsg), r, TimeUnit.MILLISECONDS));
      } else if (msg.startsWith("EV ")) {
         this.publishAsEvent(nick, msg.substring(3));
      } else if (msg.startsWith("UMAP")) {
         int r = (int)(Math.random() * 500.0);
         TreeSet<ChatUser> s = new TreeSet((o1, o2) -> {
            long d = o1.getEntrytime() - o2.getEntrytime();
            if (d == 0L) {
               d = (long)o1.nick.compareToIgnoreCase(o2.nick);
            }

            return (int)d;
         });

         for(String unick : this.userNicks.keySet()) {
            ChatUser cu = this.chat.getKnownUser(unick);
            s.add(cu);
         }

         for(ChatUser cu : s) {
            String retmsg = cu.nick + "=" + cu.getName() + "/" + cu.getEntrytime() + "/D=" + cu.getDistance();
            this.chat.bus.publish(new DelayEvent(new ChatMessageEvent(replyTo, retmsg), r, TimeUnit.MILLISECONDS));
            r += 100;
         }
      } else if (msg.startsWith("MS ")) {
         String opt = msg.substring(3);
         if (opt.equalsIgnoreCase("full")) {
            this.activate();
         } else if (opt.equalsIgnoreCase("once")) {
            MASTERSERVER_ONCE = true;
         } else if (opt.equalsIgnoreCase("am")) {
            MASTERSERVER_IAM = true;
         } else if (opt.equalsIgnoreCase("off")) {
            MASTERSERVER = false;
            MASTERSERVER_ONCE = false;
            MASTERSERVER_IAM = true;
         } else if (opt.equalsIgnoreCase("share")) {
            this.sendUserlist(this.channel.name);
         }
      } else if (msg.startsWith("SLOG ")) {
         String opt = msg.substring(5);
         boolean newSendlog = opt.equalsIgnoreCase("y");
         if (SENDLOG && !newSendlog) {
            EventBusService.getInstance().publish(new StoreTextData("Event Log LOG", this.tracelog.toString()));
            this.tracelog.setLength(0);
         }

         SENDLOG = newSendlog;
      } else if (msg.startsWith("MAUS ")) {
         String opt = msg.substring(5);

         try {
            MAX_USERS_PER_LIST = Math.max(5, Math.min(50, Integer.parseInt(opt)));
         } catch (NumberFormatException var9) {
            MAX_USERS_PER_LIST = 20;
         }
      } else if (msg.equals("MASTER")) {
         int r = (int)(Math.random() * 5000.0);
         String m = this.getMasterNick();
         String retmsg = "MASTER=" + m + (this.chat.mySelf().equals(m) ? " *" : "");
         this.chat.bus.publish(new DelayEvent(new ChatMessageEvent(replyTo, retmsg), r, TimeUnit.MILLISECONDS));
      } else if (msg.equals("MASTER" + this.chat.uc.getBuild())) {
         int r = (int)(Math.random() * 5000.0);
         String m = this.getMasterNick();
         String retmsg = "MASTER=" + m + (this.chat.mySelf().equals(m) ? " *" : "");
         this.chat.bus.publish(new DelayEvent(new ChatMessageEvent(replyTo, retmsg), r, TimeUnit.MILLISECONDS));
      }
   }

   @Override
   public void onJoin(ChannelEvent event) {
      super.onJoin(event);
      this.sendIAM(this.channel.name, false);
      if (event.getOriginNick().equals(this.chat.uc.getControlBot())) {
         this.chat.authenticateBot();
      }
   }

   @Override
   public void onJoins(ChannelEvent event) {
      super.onJoins(event);
      if (MASTERSERVER_IAM) {
         this.sendIAM(this.channel.name, false);
      }
   }

   @Override
   public void onConnect(ChannelEvent event) {
      super.onConnect(event);
      this.sendIAM(this.channel.name, true);
   }

   private void activate() {
      MASTERSERVER = true;
      MASTERSERVER_IAM = false;
   }

   private void sendIAM(String channel, boolean alwaysSend) {
      if (MASTERSERVER_IAM) {
         int r = (int)(alwaysSend ? Math.random() * 1000.0 + 500.0 : Math.random() * 9000.0 + 3000.0);
         this.chat
            .bus
            .publish(new UniqueDelayEvent(new ChatMessageEvent(channel, "IAM " + this.chat.uc.getUsername()), r, TimeUnit.MILLISECONDS, "IAM" + channel));
         synchronized(this) {
            if (this.currentGame != null) {
               r += (int)(Math.random() * 15000.0) + 10000;
               this.chat.bus.publish(new UniqueDelayEvent(new IPlayEvent(channel), r, TimeUnit.MILLISECONDS, "IPLAY" + channel));
            }
         }

         if (this.userNicks.size() > 10 && this.amIMaster()) {
            this.activate();
         }
      }

      if (alwaysSend || MASTERSERVER_IAM) {
         CurrentUserList cul = new CurrentUserList(0);
         cul.users.add(new CurrentUserListUserEntry(this.chat.mySelf(), this.chat.uc.getUsername(), this.currentGame, this.entrytime, this.currentGameTime));
         cul.finalList = true;
         this.sendXmlStatusToChannel(channel, cul);
      }
   }

   @EventHandler
   public void sendIPlay(IPlayEvent event) {
      synchronized(this) {
         if (this.currentGame != null) {
            this.chat.bus.publish(new ChatMessageEvent(event.channel, "IPLAY " + this.currentGame));
         }
      }
   }

   @EventHandler
   public void gameMessage(GameInfoEvent event) {
      synchronized(this) {
         this.currentGame = event.currentGame;
         this.currentGameTime = System.currentTimeMillis();
      }

      if (event.currentGame == null) {
         this.chat.bus.publish(new DelayEvent(new ChatMessageEvent(this.channel.name, "ILEFT"), 10, TimeUnit.SECONDS));
      } else {
         this.chat.bus.publish(new ChatMessageEvent(this.channel.name, "IPLAY " + event.currentGame));
      }
   }

   @EventHandler
   public void gameMessage(StartingModule event) {
      this.chat.bus.publish(new ChatMessageEvent(this.channel.name, "MODULESTART " + event.module.title));
   }

   @EventHandler
   public void gameMessage(EndModule event) {
      this.chat.bus.publish(new ChatMessageEvent(this.channel.name, "MODULEEND " + event.modul.title));
   }

   @EventHandler
   public void xmlMessage(XmlObject event) {
      this.sendXmlStatusToChannel(event.object);
   }

   private void sendXmlStatusToChannel(Object message) {
      this.sendXmlStatusToChannel(this.channel.name, message);
   }

   public static String shortenXml(String xml) {
      if (xml.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")) {
         xml = xml.substring("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>".length());
      }

      StringBuilder out = new StringBuilder(xml);
      int i = 1;

      while(i < out.length()) {
         if (out.charAt(i - 1) == '<' && out.charAt(i) == '/') {
            int start = i;

            while(i < out.length()) {
               if (out.charAt(++i) == '>') {
                  out.delete(start, i + 1);
                  out.insert(start, '|');
                  break;
               }
            }

            i = start;
         } else {
            ++i;
         }
      }

      return out.toString();
   }

   public static String refillXml(String xml) {
      StringBuilder out = new StringBuilder(xml);
      LinkedList<String> openTags = new LinkedList();

      for(int i = 1; i < out.length(); ++i) {
         if (out.charAt(i - 1) == '<') {
            if (out.charAt(i) == '|') {
               out.deleteCharAt(i);
               String tag = (String)openTags.pollLast();
               out.insert(i, "/" + tag + ">");
            } else {
               int start = i;

               while(i < out.length()) {
                  if (out.charAt(++i) == '>') {
                     openTags.addLast(out.substring(start, i));
                     break;
                  }
               }
            }
         }
      }

      return out.toString();
   }

   private String toXml(Object message) throws JAXBException {
      String msg = this.xmlConverter.serialize(message);
      if (!msg.isEmpty()) {
         this.tracelog.append(msg);
         msg = shortenXml(msg);
         return Base64.toBase64(msg, 10);
      } else {
         return "";
      }
   }

   private void sendXmlStatusToChannel(String destination, Object message) {
      try {
         String b64 = this.toXml(message);
         if (!b64.isEmpty()) {
            this.chat.bus.publish(new ChatMessageEvent(destination, "EV " + b64));
         }
      } catch (JAXBException var4) {
         Logger.getLogger(IC_ReadyRoom.class.getName()).log(Level.SEVERE, null, var4);
      }
   }

   private void publishAsEvent(String nick, String message) {
      try {
         Object o = this.xmlConverter.deserialize(refillXml(Base64.fromBase64(message)));
         if (o != null) {
            if (o instanceof IncludeSender) {
               ((IncludeSender)o).setSender(nick);
            }

            this.chat.bus.publish(o);
         }
      } catch (Exception var4) {
      }
   }

   private void sendUserlist(String tonick) {
      this.userListReceived.clear();
      CurrentUserList cul = new CurrentUserList(0);
      cul.users.add(new CurrentUserListUserEntry(this.chat.mySelf(), this.chat.uc.getUsername(), this.currentGame, this.entrytime, this.currentGameTime));

      for(Entry<String, ChatUser> e : this.chat.getKnownUsers2().entrySet()) {
         if (!((String)e.getKey()).equals(this.chat.mySelf())
            && ((ChatUser)e.getValue()).knowsName()
            && this.userNicks.containsKey(e.getKey())
            && ((ChatUser)e.getValue()).getDistance() >= 0) {
            CurrentUserListUserEntry u = new CurrentUserListUserEntry((ChatUser)e.getValue());
            cul.users.add(u);

            try {
               String b64 = this.toXml(cul);
               if (b64.length() > 395) {
                  cul.users.remove(u);
                  this.sendXmlStatusToChannel(tonick, cul);
                  cul = new CurrentUserList(cul.listNum + 1);
                  cul.users.add(u);
               }
            } catch (JAXBException var7) {
               Logger.getLogger(IC_ReadyRoom.class.getName()).log(Level.SEVERE, null, var7);
            }

            if (cul.users.size() > MAX_USERS_PER_LIST) {
               this.sendXmlStatusToChannel(tonick, cul);
               cul = new CurrentUserList(cul.listNum + 1);
            }
         }
      }

      cul.finalList = true;
      if (this.amIMaster() && MASTERSERVER) {
         cul.masterMode = true;
      }

      this.sendXmlStatusToChannel(tonick, cul);
      this.tracelog.append("Sent list in " + (cul.listNum + 1) + " parts (" + MAX_USERS_PER_LIST + " users per part)\n");
      if (SENDLOG) {
         EventBusService.getInstance().publish(new StoreTextData("Event Log LOG", this.tracelog.toString()));
      }

      this.tracelog.setLength(0);
      MASTERSERVER_ONCE = false;
   }

   @EventHandler
   public void gotuserlist(CurrentUserList event) {
      if (!event.getSender().equals(this.chat.mySelf())) {
         this.tracelog.append("Got list from " + event.getSender() + "\n");
         if (event.listNum == 0) {
            this.userListReceived.put(event.getSender(), new HashSet());
         } else if (!this.userListReceived.containsKey(event.getSender())) {
            return;
         }

         Set<String> received = (Set)this.userListReceived.get(event.getSender());

         for(CurrentUserListUserEntry u : event.users) {
            received.add(u.nick);
            ChatUser cu = this.chat.getKnownUser(u.nick);
            if (!u.nick.equals(this.chat.mySelf())) {
               if (!cu.knowsName()) {
                  cu.setName(u.name);
               }

               if (u.playentrytime != null && cu.getPlayEntrytime() < u.playentrytime) {
                  cu.setPlay(u.playing);
               }

               if (u.distance >= 0) {
                  if (!u.nick.equals(event.getSender()) && u.distance == 0) {
                     u.distance = 100;
                  }

                  if (cu.getDistance() < 0 || u.distance == 0 || u.distance < cu.getDistance()) {
                     cu.setDistance(u.distance);
                     cu.setEntrytime(u.entrytime);
                  } else if (u.distance == cu.getDistance()) {
                     cu.setDistance(u.distance);
                     if (u.entrytime != null && cu.getEntrytime() < u.entrytime) {
                        cu.setEntrytime(u.entrytime);
                     }
                  } else if (cu.getEntrytime() > u.entrytime && cu.getDistance() < u.distance && u.nick.startsWith("U")) {
                     received.remove(u.nick);
                     this.tracelog
                        .append(
                           "R: "
                              + u.nick
                              + ":"
                              + cu.getDistance()
                              + "#"
                              + u.distance
                              + "#"
                              + cu.getEntrytime()
                              + "#"
                              + u.entrytime
                              + ":"
                              + (cu.getEntrytime() > u.entrytime)
                              + "/"
                              + (cu.getDistance() < u.distance)
                              + "/"
                              + u.nick.startsWith("U")
                              + "\n"
                        );
                  }
               }
            }
         }

         if (event.finalList) {
            if (!this.amIMaster()) {
               MASTERSERVER |= event.masterMode;
               if (MASTERSERVER) {
                  MASTERSERVER_IAM = false;
               }
            }

            boolean knowsMore = !received.contains(this.chat.mySelf());
            if (!knowsMore) {
               for(ChatUser nick : this.chat.getKnownUsers2().values()) {
                  if (nick.knowsName() && this.userNicks.containsKey(nick.nick) && !received.contains(nick.nick) && nick.getDistance() >= 0) {
                     knowsMore = true;
                     this.tracelog.append("KM: " + nick.nick + "\n");
                     break;
                  }
               }
            } else {
               this.tracelog.append("KM: (me)\n");
            }

            this.userListReceived.remove(event.getSender());
            this.tracelog.append("List from " + event.getSender() + ", KM: " + knowsMore + "\n");
            if (knowsMore && (MASTERSERVER || MASTERSERVER_ONCE)) {
               if (this.amIMaster()) {
                  this.tracelog.append("I am master\n");
                  if (this.future == null || this.future.isDone()) {
                     this.future = this.executor.schedule(() -> this.sendUserlist(this.channel.name), 2L, TimeUnit.SECONDS);
                  }
               } else {
                  this.tracelog.append("Not master, master is " + this.getMasterNick() + "\n");
                  if (this.future != null && !this.future.isDone()) {
                     this.future.cancel(false);
                  }

                  this.future = this.executor
                     .schedule(() -> this.sendUserlist(this.channel.name), (long)(Math.random() * 3000.0) + 3000L, TimeUnit.MILLISECONDS);
               }
            } else if (this.future != null && !this.future.isDone()) {
               this.future.cancel(false);
               this.future = null;
               this.tracelog.append("Cancel sending\n");
            } else if (this.future != null && this.future.isDone()) {
               this.future = null;
            }
         }
      }
   }

   private String getMasterNick() {
      String master = this.chat.mySelf();
      long minEntryTime = this.entrytime;

      for(String nick : this.userNicks.keySet()) {
         ChatUser cu = this.chat.getKnownUser(nick);
         if (cu.nick.startsWith("U") && cu.getEntrytime() < minEntryTime) {
            minEntryTime = cu.getEntrytime();
            master = cu.nick;
         }
      }

      return master;
   }

   private boolean amIMaster() {
      for(String nick : this.userNicks.keySet()) {
         ChatUser cu = this.chat.getKnownUser(nick);
         if (!cu.nick.equals(this.chat.mySelf()) && cu.nick.startsWith("U") && cu.getEntrytime() < this.entrytime) {
            return false;
         }
      }

      return true;
   }
}

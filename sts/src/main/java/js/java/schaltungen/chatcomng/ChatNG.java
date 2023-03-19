package js.java.schaltungen.chatcomng;

import de.deltaga.eb.DelayEvent;
import de.deltaga.eb.EventBus;
import de.deltaga.eb.EventBusService;
import de.deltaga.eb.EventHandler;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.UserContextMini;
import org.relayirc.chatengine.Channel;
import org.relayirc.chatengine.Server;
import org.relayirc.chatengine.ServerEvent;
import org.relayirc.chatengine.ServerListener;
import org.relayirc.chatengine.User;
import org.relayirc.util.Debug;

public class ChatNG implements ServerListener, ChatNGMBean {
   private Server server;
   private static final int IRCPORT = 6676;
   final UserContextMini uc;
   final User u;
   final EventBus bus;
   private final ConcurrentHashMap<String, IrcChannel> joinedChannels = new ConcurrentHashMap();
   private final ConcurrentHashMap<String, ChatUser> knownUsers = new ConcurrentHashMap();
   private final HashSet<String> readyRoomStateChannels = new HashSet();
   private final HashSet<String> leaveChannels = new HashSet();
   private final HashMap<String, ICFactory<? extends IrcChannel>> nonPublicChannels = new HashMap();
   private final ConcurrentHashMap<String, ChannelsNameParser.ChannelName> channelNameTranslate = new ConcurrentHashMap();
   private String channelToEnter = null;

   public ChatNG(UserContextMini uc) {
      super();
      this.uc = uc;
      this.bus = EventBusService.getInstance();
      String nick = "U" + uc.getUid().replace(' ', '_');
      String altNick = nick + "_|";
      this.u = new User(nick, altNick, nick, nick);
      this.u.setDescription("Spieler: " + nick + "\n");
      this.bus.subscribe(this);
      this.readyRoomStateChannels.add(uc.getReadyRoom());

      for(ChannelsNameParser.ChannelName cc : new ChannelsNameParser(uc.getParameter(UserContextMini.DATATYPE.READYROOMCHANNELS), 20)) {
         this.channelNameTranslate.put(cc.name, cc);
         this.readyRoomStateChannels.add(cc.name);
      }

      this.createServer(uc.getIrcServer());
      this.server.connect(this.u);
   }

   private void createServer(String s) {
      this.server = new Server(s, 6676, "n/a", "n/a");
      this.server.setAppName("StellwerkSim IRC Chat engine");
      this.server.setAppVersion("5.2 predicted web edition/build " + this.uc.getBuild());
      this.server.addServerListener(this);
      this.server.setPass("privacy");
   }

   public boolean isV6() {
      return this.server.isV6();
   }

   public void setRoomState(UserContextMini fuc, StartRoomState s) {
      this.nonPublicChannels.clear();
      switch(s) {
         case INIT:
            this.server.sendJoin(fuc.getReadyRoom());
            break;
         case EXIT:
            this.server.sendPrivateMessage(fuc.getReadyRoom(), "EXIT normal");
            this.server.sendQuit("Alles muss mal enden.");
            this.server.disconnect();
      }
   }

   public void setRoomState(UserContext fuc, RoomState s) {
      this.nonPublicChannels.clear();
      switch(s) {
         case SANDBOXGAME:
            this.setChannelToEnter(fuc.getParameter("startchannel"));
            String channelName = fuc.getParameter("channel");
            if (channelName != null) {
               for(ChannelsNameParser.ChannelName cc : new ChannelsNameParser(channelName, 10)) {
                  cc.customdata = "channel";
                  this.channelNameTranslate.put(cc.name, cc);
                  this.leaveChannels.add(cc.name);
                  this.server.sendJoin(cc.name);
               }
            }
            break;
         case ONLINEGAME:
            this.setChannelToEnter(fuc.getParameter("startchannel"));
            String controlName = fuc.getParameter("ochannel");
            if (controlName != null) {
               this.nonPublicChannels.put(controlName.toLowerCase(), new IC_BotControlRoom.Factory());
               this.leaveChannels.add(controlName);
               this.server.sendJoin(controlName);
               String exchangeName = controlName + "e";
               this.nonPublicChannels.put(exchangeName.toLowerCase(), new IC_ExchangeRoom.Factory());
               this.leaveChannels.add(exchangeName);
               this.server.sendJoin(exchangeName);
            }

            String positionName = fuc.getParameter("mchannel");
            if (positionName != null) {
               this.nonPublicChannels.put(positionName.toLowerCase(), new IC_PositionChannel.Factory());
               this.leaveChannels.add(positionName);
               this.server.sendJoin(positionName);
            }

            String channelName = fuc.getParameter("channel");
            if (channelName != null) {
               for(ChannelsNameParser.ChannelName cc : new ChannelsNameParser(channelName, 10)) {
                  cc.customdata = "channel";
                  this.channelNameTranslate.put(cc.name, cc);
                  this.leaveChannels.add(cc.name);
                  this.server.sendJoin(cc.name);
               }
            }

            String morechannels = fuc.getParameter("dchannel");
            if (morechannels != null) {
               for(ChannelsNameParser.ChannelName cc : new ChannelsNameParser(morechannels, 50)) {
                  cc.customdata = "dchannel";
                  this.channelNameTranslate.put(cc.name, cc);
                  this.leaveChannels.add(cc.name);
                  this.server.sendJoin(cc.name);
               }
            }
            break;
         case STATUS:
            String controlName = fuc.getParameter("ochannel");
            if (controlName != null) {
               for(int i = 0; i < 2; ++i) {
                  String controlNameI = controlName + i;
                  this.nonPublicChannels.put(controlNameI.toLowerCase(), new IC_BotControlRoom.Factory());
                  this.leaveChannels.add(controlNameI);
                  this.server.sendJoin(controlNameI);
               }
            }
            break;
         case READYROOM:
            this.setChannelToEnter(fuc.getParameter(UserContextMini.DATATYPE.STARTCHANNEL));

            for(String n : this.readyRoomStateChannels) {
               this.server.sendJoin(n);
            }

            for(String c : this.leaveChannels) {
               this.server.sendPart(c);
            }

            this.leaveChannels.clear();
      }
   }

   private void setChannelToEnter(String channelname) {
      if (channelname != null) {
         channelname = channelname.toLowerCase();
         if (this.joinedChannels.containsKey(channelname)) {
            this.channelToEnter = null;
            this.bus.publish(new EnterChannel(channelname));
         } else {
            this.channelToEnter = channelname;
         }
      }
   }

   public boolean isKnownUser(String nick) {
      return this.knownUsers.containsKey(nick);
   }

   public synchronized ChatUser addKnownUser(String nick, String name) {
      ChatUser cu = (ChatUser)this.knownUsers.get(nick);
      if (cu == null) {
         cu = new ChatUser(this, nick);
         this.knownUsers.put(nick, cu);
      }

      cu.setName(name);
      return cu;
   }

   public synchronized ChatUser getKnownUser(String nick) {
      if (this.knownUsers.containsKey(nick)) {
         return (ChatUser)this.knownUsers.get(nick);
      } else {
         ChatUser cu = new ChatUser(this, nick);
         this.knownUsers.put(nick, cu);
         return cu;
      }
   }

   @EventHandler
   public void joinChannel(JoinChannelEvent ch) {
      if (ch.customHandler != null) {
         this.nonPublicChannels.put(ch.channel.toLowerCase(), ch.customHandler);
      }

      if (ch.sessionBound) {
         this.leaveChannels.add(ch.channel);
      }

      this.server.sendJoin(ch.channel.trim());
   }

   @EventHandler
   public void pingServer(IrcPingEvent ch) {
      this.server.sendCommand("PING " + this.server.getNick() + " " + this.uc.getIrcServer());
   }

   @Override
   public void pingServer() {
      this.bus.publish(new IrcPingEvent());
   }

   void authenticateBot() {
      this.bus.publish(new ChatMessageEvent(this.uc.getControlBot(), ".CONNECTED " + this.uc.getToken()));
   }

   private IrcChannel createChannelAdapter(Channel chl, String name) {
      IrcChannel ic = null;
      if (name.equals(this.uc.getReadyRoom())) {
         ic = new IC_ReadyRoom(this, name, name);
         this.authenticateBot();
         this.setChannelToEnter(this.uc.getParameter(UserContextMini.DATATYPE.STARTCHANNEL));
         int cc = 0;

         for(String n : this.readyRoomStateChannels) {
            this.bus.publish(new DelayEvent(new JoinChannelEvent(n), 10 + cc, TimeUnit.SECONDS));
            ++cc;
         }
      } else if (this.nonPublicChannels.containsKey(name.toLowerCase())) {
         ICFactory<? extends IrcChannel> factory = (ICFactory)this.nonPublicChannels.get(name.toLowerCase());
         ic = factory.newInstance(this, name);
      } else {
         ChannelsNameParser.ChannelName cnt = (ChannelsNameParser.ChannelName)this.channelNameTranslate.get(name.toLowerCase());
         if (cnt != null) {
            ic = new IC_PublicChannel(chl, this, cnt);
         } else {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "No channel translate for {0} at this point. NPE catcher.", name);
         }
      }

      return ic;
   }

   public boolean isConnected() {
      return this.server.isConnected();
   }

   public boolean isConnecting() {
      return this.server.isConnecting();
   }

   @EventHandler
   public void message(ChatMessageEvent msg) {
      Channel chn = this.server.getChannel(msg.channel, !msg.channel.startsWith("#"));
      if (chn != null) {
         byte[] utf8 = msg.msg.trim().getBytes(StandardCharsets.UTF_8);
         String t2 = new String(utf8);
         chn.sendMessage(t2 + "\n");
      }
   }

   public void onConnect(ServerEvent event) {
      this.addKnownUser(this.server.getNick(), this.uc.getUsername());
      this.bus.publish(new IrcConnectedEvent(this.server.isV6()));
   }

   public void onDisconnect(ServerEvent event) {
      this.bus.publish(new IrcDisconnectedEvent(this.server.isDisconnecting(), event.getMessage()));
   }

   @Override
   public void disconnect() {
      this.server.disconnect();
   }

   @Override
   public void simulateDisconnect() {
      this.bus.publish(new IrcDisconnectedEvent(false, "Böse böse"));
   }

   private void newChannel(ServerEvent event) {
      Channel chn = event.getChannel();
      String name = chn.getName().toLowerCase();
      if (this.joinedChannels.containsKey(name)) {
         IrcChannel ic = (IrcChannel)this.joinedChannels.get(name);
         chn.removeChannelListener(ic);
      }

      IrcChannel ic;
      if (name.startsWith("#")) {
         ic = this.createChannelAdapter(chn, name);
      } else if (name.equals(this.uc.getControlBot())) {
         ic = new IC_Bot(this, name);
      } else {
         ic = new IC_PrivMsg(this, name);
      }

      if (ic != null) {
         this.joinedChannels.put(name, ic);
         chn.addChannelListener(ic);
         if (name.startsWith("#")) {
            this.bus.publish(new ConnectedChannelsEvent(new LinkedList(this.joinedChannels.values())));
            if (this.channelToEnter != null && name.equals(this.channelToEnter)) {
               this.channelToEnter = null;
               this.bus.publish(new EnterChannel(name));
            }
         }
      }
   }

   public void onChannelJoin(ServerEvent event) {
      Channel chn = event.getChannel();
      String name = chn.getName();
      if (!name.startsWith("#")) {
         this.newChannel(event);
      }
   }

   public void onChannelAdd(ServerEvent event) {
      Channel chn = event.getChannel();
      String name = chn.getName();
      if (name.startsWith("#")) {
         this.newChannel(event);
      }
   }

   public void onChannelPart(ServerEvent event) {
      if (event != null) {
         Channel chn = event.getChannel();
         String name = chn.getName().toLowerCase();
         if (this.joinedChannels.containsKey(name)) {
            IrcChannel ic = (IrcChannel)this.joinedChannels.get(name);
            chn.removeChannelListener(ic);
            this.joinedChannels.remove(name);
            if (name.startsWith("#")) {
               this.bus.publish(new ConnectedChannelsEvent(new LinkedList(this.joinedChannels.values())));
            }
         }
      }
   }

   public void onStatus(ServerEvent event) {
      if (Debug.isDebug()) {
         System.out.println("IRC onStatus: " + event.getMessage());
      }
   }

   public void onWhoIs(ServerEvent event) {
   }

   @Override
   public Map<String, String> getKnownUsers() {
      TreeMap<String, String> ret = new TreeMap();

      for(ChatUser cu : this.knownUsers.values()) {
         ret.put(cu.nick, cu.toString());
      }

      return ret;
   }

   public Map<String, ChatUser> getKnownUsers2() {
      return Collections.unmodifiableMap(this.knownUsers);
   }

   @Override
   public Map<String, String> getChannels() {
      HashMap<String, String> ret = new HashMap();

      for(Entry<String, IrcChannel> e : this.joinedChannels.entrySet()) {
         ret.put(e.getKey(), ((IrcChannel)e.getValue()).getClass().getSimpleName());
      }

      return ret;
   }

   public void joinChannel(String channel, String userVisibleChannelName) {
      if (!channel.startsWith("#")) {
         channel = "#" + channel;
      }

      this.channelNameTranslate.put(channel, new ChannelsNameParser.ChannelName(channel, userVisibleChannelName, 0));
      this.bus.publish(new JoinChannelEvent(channel));
   }

   @Override
   public void joinChannel(String channel) {
      if (!channel.startsWith("#")) {
         channel = "#" + channel;
      }

      this.bus.publish(new JoinChannelEvent(channel));
   }

   @Override
   public void leaveChannel(String channel) {
      if (!channel.startsWith("#")) {
         channel = "#" + channel;
      }

      if (this.joinedChannels.containsKey(channel)) {
         IrcChannel ich = (IrcChannel)this.joinedChannels.get(channel);
         Channel ch = this.server.getChannel(channel);
         if (ch != null) {
            ch.removeChannelListener(ich);
         }

         this.server.sendPart(channel);
      }
   }

   public String mySelf() {
      return this.server.getNick();
   }
}

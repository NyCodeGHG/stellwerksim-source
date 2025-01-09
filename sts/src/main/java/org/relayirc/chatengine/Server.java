package org.relayirc.chatengine;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.relayirc.core.IRCConnection;
import org.relayirc.core.IRCConnectionListener;
import org.relayirc.util.Debug;

public class Server implements IChatObject, Serializable {
   static final long serialVersionUID = 129734017070381266L;
   private String _appName = "Relay IRC Chat Engine";
   private String _appVersion = "0.8.1 (Unreleased)";
   private User _user;
   private String _name = "";
   private String _title = "";
   private String _desc = "";
   private int[] _ports = null;
   private String _network = "";
   private boolean _favorite = false;
   private int _channelCount = 0;
   private String _pass = null;
   private transient IRCConnection _connection;
   private transient ChannelSearch _search = null;
   private transient Server._ServerMux _mux = null;
   private transient Server._ChannelMux _channelMux = null;
   private transient Hashtable _channels = null;
   private transient Vector _listeners = null;
   private transient Hashtable _userWaitingList = null;
   private transient PropertyChangeSupport _propChangeSupport = null;

   public Server(String name, int port, String network, String title) {
      this._name = name;
      this._network = network;
      this._title = title;
      this._ports = new int[1];
      this._ports[0] = port;
   }

   public String toString() {
      return this._network != null && this._network.length() > 0 ? this._name + " (" + this._network + ")" : this._name;
   }

   public InetAddress getLocalAddress() {
      try {
         return this._connection.getLocalAddress();
      } catch (Exception var2) {
         return null;
      }
   }

   public String getAppName() {
      return this._appName;
   }

   public void setAppName(String name) {
      this._appName = name;
   }

   public String getAppVersion() {
      return this._appVersion;
   }

   public void setAppVersion(String version) {
      this._appVersion = version;
   }

   public String getPass() {
      return this._pass;
   }

   public void setPass(String passwd) {
      this._pass = passwd;
   }

   public boolean isConnected() {
      return this._connection == null ? false : this._connection.getState() == 0;
   }

   public boolean isDisconnecting() {
      return this._connection == null ? false : this._connection.getState() == 3;
   }

   public boolean isV6() {
      return this._connection == null ? false : this._connection.isV6();
   }

   public boolean isConnecting() {
      return this._connection == null ? false : this._connection.getState() == 1;
   }

   @Override
   public String getDescription() {
      return this._desc;
   }

   @Override
   public void setDescription(String desc) {
      this._desc = desc;
   }

   public boolean isFavorite() {
      return this._favorite;
   }

   public void setFavorite(boolean fave) {
      boolean old = this._favorite;
      this._favorite = fave;
      this.getPropChangeSupport().firePropertyChange("Favorite", new Boolean(old), new Boolean(this._favorite));
   }

   public String getTitle() {
      return this._name;
   }

   public void setTitle(String title) {
      this._title = title;
   }

   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      this._name = name;
   }

   public String getNetwork() {
      return this._network;
   }

   public void setNetwork(String network) {
      String old = this._network;
      this._network = network;
      this.getPropChangeSupport().firePropertyChange("Network", old, this._network);
   }

   public String getNick() {
      return this._connection.getNick();
   }

   public int getPort() {
      if (this._ports == null) {
         this._ports = new int[1];
         this._ports[0] = 0;
      }

      return this._ports[0];
   }

   public void setPort(int port) {
      int old = 0;
      if (this._ports == null) {
         this._ports = new int[1];
      } else {
         old = this._ports[0];
      }

      this._ports[0] = port;
      this.getPropChangeSupport().firePropertyChange("Port", new Integer(old), new Integer(this._ports[0]));
   }

   public int[] getPorts() {
      return this._ports;
   }

   public void setPorts(int[] ports) {
      this._ports = ports;
   }

   public void addPropertyChangeListener(PropertyChangeListener listener) {
      this.getPropChangeSupport().addPropertyChangeListener(listener);
   }

   public void removePropertyChangeListener(PropertyChangeListener listener) {
      this.getPropChangeSupport().removePropertyChangeListener(listener);
   }

   public synchronized void addServerListener(ServerListener listener) {
      this.getListeners().addElement(listener);
   }

   public synchronized void removeServerListener(ServerListener listener) {
      this.getListeners().removeElement(listener);
   }

   public void connect(String nick, String altNick, String userName, String fullName) {
      this.connect(new User(nick, altNick, userName, fullName));
   }

   public void connect(User user) {
      if (!this.isConnected() && !this.isConnecting()) {
         this._user = user;
         IRCConnection previousConnection = this._connection;
         this._connection = new IRCConnection(
            this._name, this._ports[0], this._user.getName(), this._user.getAltNick(), this._user.getUserName(), this._user.getFullName()
         );
         if (this._pass != null) {
            this._connection.setPass(this._pass);
         }

         this._connection.setIRCConnectionListener(this.getServerMux());
         this._connection.open(previousConnection);
      } else {
         this.fireStatusEvent("Cannot connect: already connected.");
      }
   }

   public void disconnect() {
      if (this.isConnected()) {
         this.fireStatusEvent("Sending quit command...");
         this._connection.writeln("QUIT :goodbye!");
      } else if (this.isConnecting()) {
         this.fireStatusEvent("Forcing a disconnect...");
         this._connection.close();
      } else {
         this.fireStatusEvent("Cannot disconnect: not connected.");
      }
   }

   public void startChannelSearch(ChannelSearch search) {
      this._search = search;
      String cmd = "LIST ";
      this.sendCommand(cmd);
   }

   public synchronized void sendCommand(String str) {
      if (this._connection.getState() == 0) {
         try {
            this._connection.writeln(str + "\r\n");
         } catch (Exception var3) {
            this.fireStatusEvent("Error sending command");
         }
      }
   }

   public synchronized void sendPrivateMessage(String chan, String message) {
      if (this._connection.getState() != 0) {
         this.fireStatusEvent("Ignoring PRIVMSG command, not connected.");
      } else {
         if (!chan.startsWith("#")) {
            Channel channel = this.getChannel(chan, false);
            if (channel == null) {
               channel = this.getChannel(chan, true);
               channel.getChannelMux().onJoin("", this.getNick(), chan, false);
               channel.getChannelMux().onJoins(chan + " " + this.getNick(), "");
               channel.getChannelMux().onPrivateMessage(this.getNick(), chan, message);
            }
         }

         this.sendCommand("PRIVMSG " + chan + " :" + message);
      }
   }

   public synchronized void sendJoin(Channel chan) {
      if (this._connection.getState() != 0) {
         this.fireStatusEvent("Ignoring JOIN command, not connected.");
      } else if (!this.getChannels().contains(chan)) {
         this.addChannel(chan);
         this.sendCommand("JOIN " + chan.getName());
      } else {
         this.fireStatusEvent("Ignoring JOIN command, already in channel.");
      }
   }

   public synchronized void sendJoin(String name) {
      if (this._connection.getState() != 0) {
         this.fireStatusEvent("Ignoring JOIN command, not connected.");
      } else {
         name = name.trim().toLowerCase();
         Channel chan = (Channel)this.getChannels().get(name);
         if (chan == null) {
            this.sendCommand("JOIN " + name);
         } else {
            this.fireStatusEvent("Ignoring JOIN command, already in channel.");
         }
      }
   }

   public void sendNick(String nick) {
      this._connection.sendNick(nick);
   }

   public void sendPart(Channel chan) {
      this.sendPart(chan.getName());
   }

   public void sendPart(String chanName) {
      this.sendCommand("PART " + chanName);
   }

   public void sendVersion(String user) {
      this.sendCommand("PRIVMSG " + user + " :\u0001VERSION\u0001");
   }

   public void sendQuit(String str) {
      this.sendCommand("QUIT :" + str);
   }

   public void sendWhoIs(String nick) {
      this.sendCommand("WHOIS " + nick);
   }

   public void sendWhoIs(User user) {
      this.getUserWaitingList().put(user.getNick(), user);
      String nick = user.getNick();
      if (nick.startsWith("@") && nick.length() > 1) {
         nick = nick.substring(1);
      }

      this.sendCommand("WHOIS " + nick);
   }

   public void fireStatusEvent(String msg) {
      final ServerEvent event = new ServerEvent(this, msg);
      this.notifyListeners(new Server._ServerEventNotifier() {
         @Override
         public void notify(ServerListener listener) {
            listener.onStatus(event);
         }
      });
   }

   private boolean isChannelActive(String channel) {
      Channel chan = (Channel)this.getChannels().get(channel);
      return chan != null;
   }

   public Channel getChannel(String name) {
      return this.getChannel(name, false);
   }

   public synchronized Channel getChannel(String name, boolean force) {
      name = name.trim().toLowerCase();
      Channel chan = (Channel)this.getChannels().get(name);
      if (chan == null && force) {
         chan = new Channel(name, this);
         this.addChannel(chan);
      }

      return chan;
   }

   private User pullUserFromWaitingList(String nick) {
      User user = this.getUserFromWaitingList(nick);
      this.getUserWaitingList().remove(nick);
      return user;
   }

   private User getUserFromWaitingList(String nick) {
      User user = (User)this.getUserWaitingList().get(nick);
      if (user == null) {
         user = new User(nick);
         this.getUserWaitingList().put(nick, user);
      } else {
         this.getUserWaitingList().remove(user);
      }

      return user;
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      try {
         in.defaultReadObject();
      } catch (NotActiveException var3) {
         var3.printStackTrace();
      }
   }

   private void addChannel(Channel chan) {
      this.getChannels().put(chan.getName(), chan);
      chan.setConnected(true);
      final ServerEvent event = new ServerEvent(this, chan);
      this.notifyListeners(new Server._ServerEventNotifier() {
         @Override
         public void notify(ServerListener listener) {
            listener.onChannelAdd(event);
         }
      });
      chan.addChannelListener(this.getChannelMux());
   }

   private void removeAllChannels() {
      Enumeration keys = this.getChannels().keys();

      while (keys.hasMoreElements()) {
         String key = (String)keys.nextElement();
         Channel chan = (Channel)this.getChannels().get(key);
         this.removeChannel(chan);
      }
   }

   private void removeChannel(Channel chan) {
      this.getChannels().remove(chan.getName());
      chan.removeChannelListener(this.getChannelMux());
      chan.getChannelMux().onDisconnect();
   }

   private Hashtable getChannels() {
      return this._channels != null ? this._channels : (this._channels = new Hashtable());
   }

   private Vector getListeners() {
      return this._listeners != null ? this._listeners : (this._listeners = new Vector());
   }

   private Hashtable getUserWaitingList() {
      return this._userWaitingList != null ? this._userWaitingList : (this._userWaitingList = new Hashtable());
   }

   private PropertyChangeSupport getPropChangeSupport() {
      return this._propChangeSupport != null ? this._propChangeSupport : (this._propChangeSupport = new PropertyChangeSupport(this));
   }

   private Server._ServerMux getServerMux() {
      return this._mux != null ? this._mux : (this._mux = new Server._ServerMux());
   }

   private Server._ChannelMux getChannelMux() {
      return this._channelMux != null ? this._channelMux : (this._channelMux = new Server._ChannelMux());
   }

   private synchronized void notifyListeners(Server._ServerEventNotifier notifier) {
      int count = this.getListeners().size();
      Debug.println("Server.notifyListeners " + count + " listeners");

      for (int i = 0; i < this.getListeners().size(); i++) {
         ServerListener listener = (ServerListener)this.getListeners().elementAt(i);
         notifier.notify(listener);
      }
   }

   private class _ChannelMux extends ChannelAdapter {
      private _ChannelMux() {
      }

      @Override
      public void onDisconnect(ChannelEvent event) {
         Server.this.removeChannel((Channel)event.getSource());
         final ServerEvent ce_event = new ServerEvent(Server.this, (Channel)event.getSource());
         Server.this.notifyListeners(new Server._ServerEventNotifier() {
            @Override
            public void notify(ServerListener listener) {
               listener.onChannelPart(ce_event);
            }
         });
      }

      @Override
      public void onConnect(ChannelEvent event) {
         final ServerEvent ce_event = new ServerEvent(Server.this, (Channel)event.getSource());
         Server.this.notifyListeners(new Server._ServerEventNotifier() {
            @Override
            public void notify(ServerListener listener) {
               listener.onChannelJoin(ce_event);
            }
         });
      }
   }

   interface _ServerEventNotifier {
      void notify(ServerListener var1);
   }

   private class _ServerMux implements IRCConnectionListener {
      private _ServerMux() {
      }

      @Override
      public void onAction(String user, String chan, String txt) {
         Server.this.getChannel(chan, true).getChannelMux().onAction(user, chan, txt);
      }

      @Override
      public void onBan(String banned, String chan, String banner) {
         Server.this.getChannel(chan, true).getChannelMux().onBan(banned, chan, banner);
      }

      @Override
      public void onClientInfo(String orgnick) {
         String response = "NOTICE " + orgnick + " :\u0001CLIENTINFO :Supported queries are VERSION and SOURCE\u0001";
         Server.this._connection.writeln(response);
      }

      @Override
      public void onClientSource(String orgnick) {
         String response = "NOTICE " + orgnick + " :\u0001SOURCE :http://relayirc.netpedia.net\u0001";
         Server.this._connection.writeln(response);
      }

      @Override
      public void onClientVersion(String orgnick) {
         String osdesc = System.getProperty("os.name").replace(':', '-')
            + "/"
            + System.getProperty("os.version").replace(':', '-')
            + "/"
            + System.getProperty("os.arch").replace(':', '-');
         String vmdesc = "Java " + System.getProperty("java.version").replace(':', '-') + " (" + osdesc + ")";
         String response = "NOTICE " + orgnick + " :\u0001VERSION " + Server.this._appName + ":" + Server.this._appVersion + ":" + vmdesc + "\u0001";
         Server.this.fireStatusEvent("\nSending VERSION information to " + orgnick + "\n");
         Server.this._connection.writeln(response);
      }

      @Override
      public void onConnect() {
         final ServerEvent event = new ServerEvent(Server.this);
         Server.this.notifyListeners(new Server._ServerEventNotifier() {
            @Override
            public void notify(ServerListener listener) {
               listener.onConnect(event);
            }
         });
      }

      @Override
      public void onDisconnect() {
         final ServerEvent event = new ServerEvent(Server.this);
         Server.this.notifyListeners(new Server._ServerEventNotifier() {
            @Override
            public void notify(ServerListener listener) {
               listener.onDisconnect(event);
            }
         });
         Server.this.removeAllChannels();
      }

      @Override
      public void onJoin(String user, String nick, String chan, boolean create) {
         Server.this.getChannel(chan, true).getChannelMux().onJoin(user, nick, chan, create);
      }

      @Override
      public void onJoins(String users, String chan) {
         Server.this.getChannel(chan, true).getChannelMux().onJoins(users, chan);
      }

      @Override
      public void onKick(String kicked, String chan, String kicker, String txt) {
         Server.this.getChannel(chan, true).getChannelMux().onKick(kicked, chan, kicker, txt);
      }

      @Override
      public void onMessage(String message) {
         Server.this.fireStatusEvent(message + "\n");
      }

      @Override
      public void onPrivateMessage(String orgnick, String chan, String txt) {
         Channel channel = null;
         if (chan.startsWith("#")) {
            channel = Server.this.getChannel(chan, true);
         } else {
            channel = Server.this.getChannel(orgnick, true);
            channel.getChannelMux().onJoin("", orgnick, chan, false);
            channel.getChannelMux().onJoin("", Server.this.getNick(), chan, false);
         }

         channel.getChannelMux().onPrivateMessage(orgnick, chan, txt);
      }

      @Override
      public void onNick(String user, String oldnick, String newnick) {
         Server.this.fireStatusEvent(oldnick + " now known as " + newnick);
         Enumeration e = Server.this.getChannels().elements();

         while (e.hasMoreElements()) {
            Channel chan = (Channel)e.nextElement();
            chan.getChannelMux().onNick(user, oldnick, newnick);
         }
      }

      @Override
      public void onNotice(String text) {
         Server.this.fireStatusEvent("NOTICE: " + text);
      }

      @Override
      public void onPart(String user, String nick, String chan) {
         Channel channel = Server.this.getChannel(chan, false);
         if (channel != null) {
            if (!nick.equals(Server.this.getNick())) {
               channel.getChannelMux().onPart(user, nick, chan);
            } else {
               channel.getChannelMux().onPart(user, nick, chan);
               channel.disconnect();
            }
         }
      }

      @Override
      public void onOp(String oper, String chan, String oped) {
         Server.this.getChannel(chan, true).getChannelMux().onOp(oper, chan, oped);
      }

      @Override
      public void onParsingError(String message) {
         Server.this.fireStatusEvent("Error parsing message: " + message);
      }

      @Override
      public void onPing(String params) {
         Server.this._connection.writeln("PONG " + params.trim() + "\r\n");
      }

      @Override
      public void onPong(String params) {
      }

      @Override
      public void onStatus(String msg) {
         Server.this.fireStatusEvent(msg);
      }

      @Override
      public void onVersionNotice(String orgnick, String origin, String version) {
         Server.this.fireStatusEvent("\nVERSION Information for " + orgnick + "(" + origin + ")\n");
      }

      @Override
      public void onQuit(String user, String nick, String txt) {
         Server.this.fireStatusEvent(nick + " (" + user + ") has QUIT: " + txt + "\n");
         Enumeration e = Server.this.getChannels().elements();

         while (e.hasMoreElements()) {
            Channel chan = (Channel)e.nextElement();
            chan.getChannelMux().onQuit(user, nick, txt);
         }
      }

      @Override
      public void onReplyVersion(String version) {
         Server.this.fireStatusEvent("Server Version: " + version);
      }

      @Override
      public void onReplyListUserChannels(int channelCount) {
         Server.this._channelCount = channelCount;
      }

      @Override
      public void onReplyListStart() {
         if (Server.this._search != null) {
            Server.this._search.searchStarted(Server.this._channelCount);
         }
      }

      @Override
      public void onReplyList(String channel, int userCount, String topic) {
         Channel channelObject = new Channel(channel, topic, userCount, Server.this);
         Server.this._search.processChannel(channelObject);
      }

      @Override
      public void onReplyListEnd() {
         if (Server.this._search != null) {
            Server.this._search.searchEnded();
            Server.this._search.setComplete(true);
            Server.this._search = null;
         }
      }

      @Override
      public void onReplyListUserClient(String msg) {
         Server.this.fireStatusEvent(msg);
      }

      @Override
      public void onReplyWhoIsUser(String nick, String user, String name, String host) {
         Server.this.fireStatusEvent(nick + " is " + user + "@" + host);
         User userObj = Server.this.getUserFromWaitingList(nick);
         userObj.setUserName(user);
         userObj.setFullName(name);
         userObj.setHostName(host);
         userObj.setOnline(true);
      }

      @Override
      public void onReplyWhoIsServer(String nick, String server, String info) {
         Server.this.fireStatusEvent(nick + " is on server " + server + " (" + info + ")");
         User user = Server.this.getUserFromWaitingList(nick);
         user.setServerName(server);
         user.setServerDesc(info);
      }

      @Override
      public void onReplyWhoIsOperator(String info) {
         Server.this.fireStatusEvent(info);
      }

      @Override
      public void onReplyWhoIsIdle(String nick, int idle, Date signon) {
         Server.this.fireStatusEvent(nick + " idle for " + idle + " seconds");
         Server.this.fireStatusEvent(nick + " on since " + signon);
         User user = Server.this.getUserFromWaitingList(nick);
         user.setIdleTime(idle);
         user.setSignonTime(signon);
      }

      @Override
      public void onReplyEndOfWhoIs(String nick) {
         User user = Server.this.pullUserFromWaitingList(nick);
         final ServerEvent event = new ServerEvent(Server.this, user);
         Server.this.notifyListeners(new Server._ServerEventNotifier() {
            @Override
            public void notify(ServerListener listener) {
               listener.onWhoIs(event);
            }
         });
      }

      @Override
      public void onReplyWhoIsChannels(String nick, String channels) {
         Server.this.fireStatusEvent(nick + " is on " + channels);
      }

      @Override
      public void onReplyMOTDStart() {
      }

      @Override
      public void onReplyMOTD(String msg) {
         Server.this.fireStatusEvent(msg);
      }

      @Override
      public void onReplyMOTDEnd() {
      }

      @Override
      public void onReplyNameReply(String channel, String users) {
         this.onJoins(users, channel);
      }

      @Override
      public void onTopic(String channel, String topic) {
         Server.this.getChannel(channel, true).getChannelMux().onTopic(channel, topic);
      }

      @Override
      public void onReplyTopic(String channel, String topic) {
         Server.this.getChannel(channel, true).getChannelMux().onReplyTopic(channel, topic);
      }

      @Override
      public void onErrorNoMOTD() {
         Server.this.fireStatusEvent("\nERROR: No message of the day.\n");
      }

      @Override
      public void onErrorNeedMoreParams() {
         Server.this.fireStatusEvent("\nERROR: Meed more parameters.\n");
      }

      @Override
      public void onErrorNoNicknameGiven() {
         this.onErrorNeedMoreParams();
      }

      @Override
      public void onErrorNickNameInUse(String badNick) {
         this.onErrorNickCollision(badNick);
      }

      @Override
      public void onErrorNickCollision(String badNick) {
         if (badNick.equals(Server.this._user.getNick())) {
            Server.this.fireStatusEvent("\nWARNING: Nick name already in use, using alternate...\n");
            Server.this._connection.sendNick(Server.this._user.getAltNick());
         } else if (badNick.equals(Server.this._user.getAltNick())) {
            Server.this.fireStatusEvent("\nERROR: Alternate nick name already in use, disconnecting...\n");
            Server.this._connection.close();
         } else {
            Server.this.fireStatusEvent("\nERROR: Nick name already in use, reverting to " + Server.this._user.getNick());
            Server.this._connection.sendNick(Server.this._user.getNick());
         }
      }

      @Override
      public void onErrorErroneusNickname(String badNick) {
         if (badNick.equals(Server.this._user.getNick())) {
            Server.this.fireStatusEvent("\nERROR: Error in nick name, using alternate...\n");
            Server.this._connection.sendNick(Server.this._user.getAltNick());
         } else if (badNick.equals(Server.this._user.getAltNick())) {
            Server.this.fireStatusEvent("\nERROR: Error in alternate nick name, disconnecting...\n");
            Server.this._connection.close();
         } else {
            Server.this.fireStatusEvent("\nERROR: Error in nick name, reverting to " + Server.this._user.getNick());
            Server.this._connection.sendNick(Server.this._user.getNick());
         }
      }

      @Override
      public void onErrorAlreadyRegistered() {
         Server.this.fireStatusEvent("\nERROR: you are already connected to this server!\n");
         Server.this.disconnect();
      }

      @Override
      public void onErrorUnknown(String message) {
         Server.this.fireStatusEvent("UNKNOWN: " + message + "\n");
      }

      @Override
      public void onErrorUnsupported(String message) {
         Server.this.fireStatusEvent("UNSUPPORTED: " + message + "\n");
      }
   }
}

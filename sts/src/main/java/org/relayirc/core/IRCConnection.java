package org.relayirc.core;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.relayirc.util.Debug;
import org.relayirc.util.ParsedToken;

public class IRCConnection implements Runnable, IRCConstants {
   public static final int CONNECTED = 0;
   public static final int CONNECTING = 1;
   public static final int DISCONNECTED = 2;
   public static final int DISCONNECTING = 3;
   private int _state = 2;
   private final String _server;
   private final int _port;
   private String _pass = null;
   private String _nick;
   private final String _altNick;
   private final String _userName;
   private final String _fullName;
   private Socket _socket;
   private String _localHost;
   private Thread _messageLoopThread;
   private BufferedReader _inputStream;
   private DataOutputStream _outputStream;
   private IRCConnectionListener _listener;
   private Thread _previousMessageLoopThread;
   private transient IdentServer _identd = null;
   private final IRCConnection._IRCConnectionMux _mux = new IRCConnection._IRCConnectionMux();

   public IRCConnection(String server, int port, String nick, String altNick, String userName, String fullName) {
      super();
      this._server = server;
      this._port = port;
      this._nick = nick;
      this._altNick = altNick;
      this._userName = userName;
      this._fullName = fullName;
      this._listener = new IRCConnectionAdapter();
   }

   public int getState() {
      return this._state;
   }

   public void setState(int state) {
      this._state = state;
   }

   public void setPass(String passwd) {
      this._pass = passwd;
   }

   public void open() {
      if (this.getState() == 2) {
         this.setState(1);
         this._messageLoopThread = new Thread(this);
         this._messageLoopThread.setName("IRCConnection.messageLoopThread");
         this._messageLoopThread.start();
      }
   }

   public void open(IRCConnection previousConnection) {
      if (previousConnection != null && previousConnection._identd != null) {
         previousConnection._identd.stop();
         this._previousMessageLoopThread = previousConnection._messageLoopThread;
      }

      this.open();
   }

   public InetAddress getLocalAddress() {
      return this._socket.getLocalAddress();
   }

   public void close() {
      this._mux.onStatus("Closing connection...");
      this.setState(3);
      if (this._identd != null) {
         this._identd.stop();
      }

      try {
         this._outputStream.writeBytes("QUIT");
      } catch (Exception var4) {
      }

      try {
         this._socket.close();
      } catch (Exception var3) {
      }

      Debug.println("Waiting for message thread to die...");

      try {
         this._messageLoopThread.join();
      } catch (Exception var2) {
      }

      Debug.println("...Thread is dead");
      this._socket = null;
      this.setState(2);
      this._mux.onDisconnect();
   }

   public void setIRCConnectionListener(IRCConnectionListener listener) {
      this._listener = listener;
   }

   public String getNick() {
      return this._nick;
   }

   public String getPass() {
      return this._pass;
   }

   public void sendNick(String nick) {
      this._nick = nick;
      this.writeln("NICK " + this._nick + "\r\n");
   }

   private String parseOrgnick(String origin) {
      String orgnick = null;
      if (origin.length() > 0) {
         StringTokenizer toker2 = new StringTokenizer(origin, "!");

         try {
            orgnick = toker2.nextToken();
         } catch (NoSuchElementException var5) {
            orgnick = null;
         }
      }

      return orgnick;
   }

   public void writeln(String message) {
      String m2 = message + "\r\n";
      byte[] btmp = m2.getBytes();

      try {
         synchronized(this) {
            this._outputStream.write(btmp);
         }
      } catch (SocketException var7) {
         this._mux.onStatus("Connection error: " + var7.getMessage());
         this.setState(2);
         this._mux.onDisconnect();
      } catch (Exception var8) {
         var8.printStackTrace();
      }
   }

   public boolean isV6() {
      return this._socket != null ? this._socket.getInetAddress() instanceof Inet6Address : false;
   }

   public void run() {
      if (this._previousMessageLoopThread != null) {
         try {
            this._previousMessageLoopThread.join();
         } catch (InterruptedException var11) {
         }
      }

      try {
         if (this._identd != null) {
            this._identd.stop();
         }

         this._identd = new IdentServer(this._userName);
      } catch (Exception var17) {
         this._mux.onStatus("Unable to start Ident server");
         this.setState(2);
         this._mux.onDisconnect();
         return;
      }

      try {
         this._mux.onStatus("Contacting server [" + this._server + ":" + this._port + "]");
         this._socket = null;
         InetAddress[] addresses = InetAddress.getAllByName(this._server);

         for(InetAddress inet : addresses) {
            if (inet instanceof Inet6Address) {
               try {
                  this._socket = new Socket(inet, this._port);
               } catch (Exception var10) {
               }
               break;
            }
         }

         if (this._socket == null) {
            this._socket = new Socket(this._server, this._port);
         }

         this._socket.setKeepAlive(true);
         this._socket.setSoTimeout(0);
         this._socket.setTcpNoDelay(true);
      } catch (Exception var16) {
         this._mux.onStatus("Unable to contact server [" + this._server + ":" + this._port + "] " + var16.getMessage());
         this.setState(2);
         this._mux.onDisconnect();
         return;
      }

      this._mux.onStatus("Contacted server [" + this._server + ":" + this._port + "]");

      try {
         this._mux.onStatus("Opening IO streams to server [" + this._server + ":" + this._port + "]");
         this._inputStream = new BufferedReader(new InputStreamReader(new DataInputStream(this._socket.getInputStream()), "UTF-8"));
         this._outputStream = new DataOutputStream(this._socket.getOutputStream());

         try {
            this._localHost = this._socket.getLocalAddress().getHostName();
         } catch (Exception var9) {
            this._localHost = "localhost";
         }

         if (this.getPass() != null) {
            this.writeln("PASS :" + this.getPass() + "\r\n");
         }

         this._mux.onStatus("Registering nick [" + this._nick + "] with server [" + this._server + ":" + this._port + "]");
         this.writeln("NICK " + this.getNick() + "\r\n");
         this._mux.onStatus("Registering user name [" + this._userName + "] with server [" + this._server + ":" + this._port + "]");
         this.writeln("USER " + this._userName + " " + this._localHost + " " + this._server + " :" + this._fullName + "\r\n");
      } catch (Exception var15) {
         if (this.getState() != 3) {
            this._mux.onStatus("Exception: " + var15);
            this.setState(2);
            this._mux.onDisconnect();
         } else {
            this._mux.onStatus("Disconnected.");
         }

         return;
      }

      this._mux.onStatus("Waiting for response from server [" + this._server + ":" + this._port + "]");

      try {
         while(true) {
            String message;
            while(true) {
               try {
                  message = this._inputStream.readLine();
                  break;
               } catch (SocketTimeoutException var12) {
               }
            }

            if (message == null) {
               break;
            }

            Debug.println("message=" + message);
            String origin = "";
            String command = "";
            ParsedToken[] tokens = ParsedToken.stringToParsedTokens(message, " ");
            if (tokens.length > 0) {
               ParsedToken tok = tokens[0];
               if (tok.token.substring(0, 1).equals(":")) {
                  try {
                     origin = tokens[0].token.substring(1);
                     command = tokens[1].token;
                  } catch (ArrayIndexOutOfBoundsException var8) {
                     command = tokens[0].token.substring(1);
                     origin = "";
                  }
               } else {
                  command = tokens[0].token;
               }

               if (!this.handleCommand(message, command, origin, tokens) && !this.handleReply(message, command, origin, tokens)) {
                  Debug.println("ERROR: message not handled\n");
               }
            } else {
               Debug.println("ERROR: ill-formed message\n");
            }
         }
      } catch (IRCException var13) {
         this._mux.onStatus(var13.getMessage());
         this._mux.onStatus("Closing connection...");
      } catch (Exception var14) {
         if (this.getState() != 3) {
            this._mux.onStatus("Closing connection due to uncaught exception.");
            var14.printStackTrace();

            try {
               this._socket.close();
            } catch (Exception var7) {
            }
         }
      }

      this.setState(2);
      this._mux.onDisconnect();
   }

   private boolean handleCommand(String message, String command, String origin, ParsedToken[] tokens) {
      boolean handled = false;
      if (command.equals("PING")) {
         String params = message.substring(message.indexOf("PING") + 4);
         this._mux.onPing(params);
         handled = true;
      } else if (command.equals("PRIVMSG")) {
         String channel = tokens[2].token;
         String text = message.substring(tokens[3].index).trim();
         String orgnick = this.parseOrgnick(origin);
         if (orgnick != null) {
            handled = true;
            if (text.indexOf("\u0001VERSION") != -1) {
               this._mux.onClientVersion(orgnick);
            } else if (text.indexOf("\u0001SOURCE") != -1) {
               this._mux.onClientSource(orgnick);
            } else if (text.indexOf("\u0001CLIENTINFO") != -1) {
               this._mux.onClientInfo(orgnick);
            } else if (text.indexOf("ACTION") != -1) {
               this._mux.onAction(orgnick, channel, text.substring(6));
            } else {
               this._mux.onPrivateMessage(orgnick, channel, text.substring(1));
            }
         }
      } else if (command.equals("NOTICE") && tokens.length >= 5) {
         String orgnick = this.parseOrgnick(origin);
         String text = message.substring(tokens[4].index).trim();

         try {
            handled = true;
            if (text.substring(0, 9).equals(":\u0001VERSION")) {
               this._mux.onVersionNotice(orgnick, origin, text.substring(9));
            } else {
               this._mux.onNotice(text);
            }
         } catch (StringIndexOutOfBoundsException var12) {
         }
      } else if (command.equals("MODE")) {
         this._mux.onStatus("MODE: " + message);
         String orgnick = this.parseOrgnick(origin);
         if (orgnick != null) {
            String chan = tokens[2].token;
            String mode = tokens[3].token;
            if (mode.equals("+o")) {
               String oped = tokens[4].token;
               this._mux.onOp(orgnick, chan, oped);
               handled = true;
            } else if (mode.equals("+b")) {
               String banned = tokens[4].token;
               this._mux.onBan(banned, chan, orgnick);
               handled = true;
            }
         }
      } else if (command.equals("JOIN")) {
         String channel = tokens[2].token;
         String orgnick = this.parseOrgnick(origin);
         if (orgnick != null) {
            this._mux.onJoin(origin, orgnick, channel.substring(1), false);
            handled = true;
         }
      } else if (command.equals("PART")) {
         Debug.println("PART: " + message);
         String channel = tokens[2].token;
         String orgnick = this.parseOrgnick(origin);
         if (orgnick != null) {
            this._mux.onPart(origin, orgnick, channel);
            handled = true;
         }
      } else if (command.equals("KICK")) {
         Debug.println("KICK: " + message);

         try {
            String orgnick = this.parseOrgnick(origin);
            String channel = tokens[2].token;
            String kicked = tokens[3].token;
            String reason = tokens[4].token;
            if (orgnick != null) {
               this._mux.onKick(kicked, channel, orgnick, reason);
               handled = true;
            }
         } catch (NoSuchElementException var10) {
         } catch (Exception var11) {
            var11.printStackTrace();
         }
      } else if (command.equals("QUIT")) {
         String channel = tokens[2].token;
         String text = message.substring(tokens[2].index + 1);
         String orgnick = this.parseOrgnick(origin);
         if (orgnick != null) {
            this._mux.onQuit(origin, orgnick, text);
            handled = true;
         }
      } else if (command.equals("NICK")) {
         String channel = tokens[2].token;
         String orgnick = this.parseOrgnick(origin);
         if (orgnick != null) {
            this._mux.onNick(origin, orgnick, channel.substring(1));
            handled = true;
         }
      } else if (command.equals("TOPIC")) {
         String channel = tokens[2].token;
         String topic = tokens[3].token.substring(1);
         this._mux.onTopic(channel, topic);
         handled = true;
      } else if (command.equals("MSG")) {
         this._mux.onMessage(message);
         handled = true;
      }

      return handled;
   }

   private boolean handleReply(String message, String command, String origin, ParsedToken[] tokens) throws IRCException {
      int cmdid = -1;

      try {
         cmdid = Integer.parseInt(command);
      } catch (Exception var24) {
         cmdid = -1;
         this._mux.onParsingError(message);
         return true;
      }

      boolean handled = false;
      switch(cmdid) {
         case 1:
         case 2:
         case 3:
         case 4:
            this._mux.onConnect();
            if (tokens.length > 3) {
               this._mux.onStatus(message.substring(tokens[3].index + 1));
            }

            handled = true;
            break;
         case 250:
         case 252:
         case 255:
         case 333:
         case 366:
            this._mux.onErrorUnsupported(message + "\n");
            handled = true;
            break;
         case 251: {
            String msg = message.substring(tokens[3].index + 1);
            this._mux.onReplyListUserClient(msg);
            handled = true;
            break;
         }
         case 254:
            int channelCount = 0;

            try {
               channelCount = Integer.parseInt(tokens[3].token);
               this._mux.onReplyListUserChannels(channelCount);
            } catch (Exception var23) {
               this._mux.onParsingError(message);
            }

            handled = true;
            break;
         case 311: {
            String nick = tokens[3].token;
            String user = tokens[4].token;
            String host = tokens[5].token;
            String fullName = message.substring(tokens[7].index + 1);
            this._mux.onReplyWhoIsUser(nick, user, fullName, host);
            handled = true;
            break;
         }
         case 312: {
            String nick = tokens[3].token;
            String server = tokens[4].token;
            String info = message.substring(tokens[5].index + 1);
            this._mux.onReplyWhoIsServer(nick, server, info);
            handled = true;
            break;
         }
         case 313:
            String opmsg = message.substring(tokens[3].index);
            this._mux.onReplyWhoIsOperator(opmsg);
            handled = true;
            break;
         case 317: {
            String nick = tokens[3].token;
            int idle = 0;
            String secStr = tokens[4].token;

            try {
               idle = Integer.parseInt(secStr);
            } catch (Exception var21) {
            }

            long signon = 0L;
            String signonStr = tokens[5].token;

            try {
               signon = Long.parseLong(signonStr) * 1000L;
            } catch (Exception var20) {
            }

            Date signonTime = new Date(signon);
            String comments = message.substring(tokens[6].index + 1);
            this._mux.onReplyWhoIsIdle(nick, idle, signonTime);
            handled = true;
            break;
         }
         case 318: {
            String nick = tokens[3].token;
            this._mux.onReplyEndOfWhoIs(nick);
            handled = true;
            break;
         }
         case 319: {
            String nick = tokens[3].token;
            String chans = message.substring(tokens[4].index + 1);
            this._mux.onReplyWhoIsChannels(nick, chans);
            handled = true;
            break;
         }
         case 321:
            this._mux.onReplyListStart();
            handled = true;
            break;
         case 322:
            String channel = tokens[3].token;
            int userCount = 0;

            try {
               userCount = Integer.parseInt(tokens[4].token);
            } catch (Exception var22) {
            }

            String topic = message.substring(tokens[5].index + 1);
            this._mux.onReplyList(channel, userCount, topic);
            handled = true;
            break;
         case 323:
            this._mux.onReplyListEnd();
            handled = true;
            break;
         case 332:
            String chan1 = tokens[3].token;
            String top = message.substring(tokens[4].index).substring(1);
            this._mux.onReplyTopic(chan1, top);
            handled = true;
            break;
         case 351:
            this._mux.onReplyVersion(tokens[3].token);
            handled = true;
            break;
         case 353:
            String chn = tokens[4].token;
            String users = message.substring(tokens[5].index);
            this._mux.onReplyNameReply(chn, users.substring(1));
            handled = true;
            break;
         case 372: {
            String msg = message.substring(tokens[3].index);
            this._mux.onReplyMOTD(msg);
            handled = true;
            break;
         }
         case 375:
            this._mux.onConnect();
            this._mux.onReplyMOTDStart();
            handled = true;
            break;
         case 376: {
            String msg = message.substring(tokens[3].index);
            this._mux.onReplyMOTDEnd();
            handled = true;
            break;
         }
         case 422:
            this._mux.onConnect();
            this._mux.onErrorNoMOTD();
            handled = true;
            break;
         case 431:
            this._mux.onErrorNoNicknameGiven();
            handled = true;
            break;
         case 432:
            this._mux.onErrorErroneusNickname();
            handled = true;
            break;
         case 433:
            this._mux.onErrorNickNameInUse();
            handled = true;
            break;
         case 436:
            this._mux.onErrorNickCollision();
            handled = true;
            break;
         case 461:
            this._mux.onErrorNeedMoreParams();
            handled = true;
            break;
         case 462:
            this._mux.onErrorAlreadyRegistered();
            handled = true;
            break;
         case 465:
            throw new IRCException("ERR_YOURBANNEDCREEP: " + message);
         default:
            this._mux.onErrorUnknown(message + "\n");
            handled = true;
      }

      return handled;
   }

   private class _IRCConnectionMux extends IRCConnectionAdapter {
      private _IRCConnectionMux() {
         super();
      }

      @Override
      public void onAction(String user, String chan, String txt) {
         IRCConnection.this._listener.onAction(user, chan, txt);
      }

      @Override
      public void onBan(String banned, String chan, String banner) {
         IRCConnection.this._listener.onBan(banned, chan, banner);
      }

      @Override
      public void onClientInfo(String orgnick) {
         IRCConnection.this._listener.onClientInfo(orgnick);
      }

      @Override
      public void onClientSource(String orgnick) {
         IRCConnection.this._listener.onClientSource(orgnick);
      }

      @Override
      public void onClientVersion(String orgnick) {
         IRCConnection.this._listener.onClientVersion(orgnick);
      }

      @Override
      public void onConnect() {
         if (IRCConnection.this.getState() != 0) {
            IRCConnection.this.setState(0);
            this.onStatus("Connected to server [" + IRCConnection.this._server + ":" + IRCConnection.this._port + "].\n");
            IRCConnection.this._listener.onConnect();
         }
      }

      @Override
      public void onDisconnect() {
         IRCConnection.this._listener.onDisconnect();
      }

      @Override
      public void onJoin(String user, String nick, String chan, boolean create) {
         IRCConnection.this._listener.onJoin(user, nick, chan, create);
      }

      @Override
      public void onJoins(String users, String chan) {
         IRCConnection.this._listener.onJoins(users, chan);
      }

      @Override
      public void onKick(String kicked, String chan, String kicker, String txt) {
         IRCConnection.this._listener.onKick(kicked, chan, kicker, txt);
      }

      @Override
      public void onMessage(String message) {
         IRCConnection.this._listener.onMessage(message);
      }

      @Override
      public void onPrivateMessage(String orgnick, String chan, String txt) {
         IRCConnection.this._listener.onPrivateMessage(orgnick, chan, txt);
      }

      @Override
      public void onNick(String user, String oldnick, String newnick) {
         IRCConnection.this._listener.onNick(user, oldnick, newnick);
      }

      @Override
      public void onNotice(String text) {
         IRCConnection.this._listener.onNotice(text);
      }

      @Override
      public void onPart(String user, String nick, String chan) {
         IRCConnection.this._listener.onPart(user, nick, chan);
      }

      @Override
      public void onOp(String oper, String chan, String oped) {
         IRCConnection.this._listener.onOp(oper, chan, oped);
      }

      @Override
      public void onParsingError(String message) {
         IRCConnection.this._listener.onParsingError(message);
      }

      @Override
      public void onPing(String params) {
         IRCConnection.this._listener.onPing(params);
      }

      @Override
      public void onPong(String params) {
         IRCConnection.this._listener.onPong(params);
      }

      @Override
      public void onStatus(String msg) {
         IRCConnection.this._listener.onStatus(msg);
      }

      @Override
      public void onTopic(String chanName, String newTopic) {
         IRCConnection.this._listener.onTopic(chanName, newTopic);
      }

      @Override
      public void onVersionNotice(String orgnick, String origin, String version) {
         IRCConnection.this._listener.onVersionNotice(orgnick, origin, version);
      }

      @Override
      public void onQuit(String user, String nick, String txt) {
         IRCConnection.this._listener.onQuit(user, nick, txt);
      }

      @Override
      public void onReplyVersion(String version) {
         IRCConnection.this._listener.onReplyVersion(version);
      }

      @Override
      public void onReplyListUserChannels(int channelCount) {
         IRCConnection.this._listener.onReplyListUserChannels(channelCount);
      }

      @Override
      public void onReplyListStart() {
         IRCConnection.this._listener.onReplyListStart();
      }

      @Override
      public void onReplyList(String channel, int userCount, String topic) {
         IRCConnection.this._listener.onReplyList(channel, userCount, topic);
      }

      @Override
      public void onReplyListEnd() {
         IRCConnection.this._listener.onReplyListEnd();
      }

      @Override
      public void onReplyListUserClient(String msg) {
         IRCConnection.this._listener.onReplyListUserClient(msg);
      }

      @Override
      public void onReplyWhoIsUser(String nick, String user, String name, String host) {
         IRCConnection.this._listener.onReplyWhoIsUser(nick, user, name, host);
      }

      @Override
      public void onReplyWhoIsServer(String nick, String server, String info) {
         IRCConnection.this._listener.onReplyWhoIsServer(nick, server, info);
      }

      @Override
      public void onReplyWhoIsOperator(String info) {
         IRCConnection.this._listener.onReplyWhoIsOperator(info);
      }

      @Override
      public void onReplyWhoIsIdle(String nick, int idle, Date signon) {
         IRCConnection.this._listener.onReplyWhoIsIdle(nick, idle, signon);
      }

      @Override
      public void onReplyEndOfWhoIs(String nick) {
         IRCConnection.this._listener.onReplyEndOfWhoIs(nick);
      }

      @Override
      public void onReplyWhoIsChannels(String nick, String channels) {
         IRCConnection.this._listener.onReplyWhoIsChannels(nick, channels);
      }

      @Override
      public void onReplyMOTDStart() {
         IRCConnection.this._listener.onReplyMOTDStart();
      }

      @Override
      public void onReplyMOTD(String msg) {
         IRCConnection.this._listener.onReplyMOTD(msg);
      }

      @Override
      public void onReplyMOTDEnd() {
         IRCConnection.this._listener.onReplyMOTDEnd();
      }

      @Override
      public void onReplyNameReply(String channel, String users) {
         IRCConnection.this._listener.onReplyNameReply(channel, users);
      }

      @Override
      public void onReplyTopic(String channel, String topic) {
         IRCConnection.this._listener.onReplyTopic(channel, topic);
      }

      @Override
      public void onErrorNoMOTD() {
         IRCConnection.this._listener.onErrorNoMOTD();
      }

      @Override
      public void onErrorNeedMoreParams() {
         IRCConnection.this._listener.onErrorNeedMoreParams();
      }

      @Override
      public void onErrorNoNicknameGiven() {
         IRCConnection.this._listener.onErrorNoNicknameGiven();
      }

      public void onErrorNickNameInUse() {
         IRCConnection.this._listener.onErrorNickNameInUse(IRCConnection.this._nick);
      }

      public void onErrorNickCollision() {
         IRCConnection.this._listener.onErrorNickCollision(IRCConnection.this._nick);
      }

      public void onErrorErroneusNickname() {
         IRCConnection.this._listener.onErrorErroneusNickname(IRCConnection.this._nick);
      }

      @Override
      public void onErrorAlreadyRegistered() {
         IRCConnection.this._listener.onErrorAlreadyRegistered();
      }

      @Override
      public void onErrorUnknown(String message) {
         IRCConnection.this._listener.onErrorUnknown(message);
      }

      @Override
      public void onErrorUnsupported(String message) {
         IRCConnection.this._listener.onErrorUnsupported(message);
      }
   }
}

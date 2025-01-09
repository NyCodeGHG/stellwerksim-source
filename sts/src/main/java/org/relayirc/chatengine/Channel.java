package org.relayirc.chatengine;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.StringTokenizer;
import java.util.Vector;
import org.relayirc.core.IRCConnectionAdapter;
import org.relayirc.core.IRCConnectionListener;
import org.relayirc.util.Debug;

public class Channel implements IChatObject, Serializable {
   static final long serialVersionUID = -7306475367688154363L;
   private String _name = "";
   private String _desc = "";
   private String _topic = new String();
   private int _userCount = 0;
   private int _maxNumBufferedUnsentMessages = 0;
   private transient StringBuffer _messageLineBuffer = new StringBuffer(500);
   private transient Vector _unsentMessagesBuffer = null;
   private transient int _numDroppedMessages = 0;
   private transient PropertyChangeSupport _propChangeSupport = null;
   private transient Server _server = null;
   private transient Vector _listeners = new Vector();
   private transient boolean _isConnected = false;
   private transient Channel._ChannelMux _mux = new Channel._ChannelMux();

   public Channel(String name) {
      this._name = name;
      this._propChangeSupport = new PropertyChangeSupport(this);
   }

   public Channel(String name, Server server) {
      this(name);
      this._server = server;
   }

   public Channel(String name, String topic, int ucount, Server server) {
      this(name);
      this._topic = topic;
      this._userCount = ucount;
      this._server = server;
   }

   public boolean equals(Object object) {
      return object != null && object instanceof Channel && ((Channel)object).getName().equals(this.getName());
   }

   private synchronized void notifyListeners(Channel._ChannelEventNotifier notifier) {
      for (int i = 0; i < this._listeners.size(); i++) {
         ChannelListener listener = (ChannelListener)this._listeners.elementAt(i);
         notifier.notify(listener);
      }
   }

   public synchronized void addChannelListener(ChannelListener listener) {
      this._listeners.addElement(listener);
   }

   public synchronized void removeChannelListener(ChannelListener listener) {
      this._listeners.removeElement(listener);
   }

   public void addPropertyChangeListener(PropertyChangeListener listener) {
      this._propChangeSupport.addPropertyChangeListener(listener);
   }

   public void removePropertyChangeListener(PropertyChangeListener listener) {
      this._propChangeSupport.removePropertyChangeListener(listener);
   }

   public boolean isConnected() {
      return this._isConnected;
   }

   void setConnected(boolean value) {
      this._isConnected = value;
      if (value) {
         final ChannelEvent event2 = new ChannelEvent(this);
         this.notifyListeners(new Channel._ChannelEventNotifier() {
            @Override
            public void notify(ChannelListener listener) {
               listener.onConnect(event2);
            }
         });
      } else {
         final ChannelEvent event2 = new ChannelEvent(this);
         this.notifyListeners(new Channel._ChannelEventNotifier() {
            @Override
            public void notify(ChannelListener listener) {
               listener.onDisconnect(event2);
            }
         });
      }
   }

   public Server getServer() {
      return this._server;
   }

   IRCConnectionListener getChannelMux() {
      if (this._mux == null) {
         this._mux = new Channel._ChannelMux();
      }

      return this._mux;
   }

   public int getMaxNumBufferedUnsentMessages() {
      return this._maxNumBufferedUnsentMessages;
   }

   public void connect() {
      if (this._server != null) {
         this._server.sendJoin(this);
      }
   }

   public void disconnect() {
      if (this._server != null) {
         this._server.sendPart(this);
      }
   }

   public String getName() {
      return this._name;
   }

   public void setServer(Server server) {
      this._server = server;
   }

   public void setName(String name) {
      String old = this._name;
      this._name = name;
      this._propChangeSupport.firePropertyChange("Name", old, this._name);
   }

   public String getTopic() {
      return this._topic;
   }

   @Override
   public String getDescription() {
      return this._desc;
   }

   @Override
   public void setDescription(String desc) {
      this._desc = desc;
   }

   public void setTopic(String topic) {
      String old = this._topic;
      this._topic = topic;
      this._propChangeSupport.firePropertyChange("Topic", old, this._topic);
   }

   public int getUserCount() {
      return this._userCount;
   }

   public void setUserCount(int count) {
      int old = this._userCount;
      this._userCount = count;
      this._propChangeSupport.firePropertyChange("UserCount", new Integer(old), new Integer(this._userCount));
   }

   public String toString() {
      return this._name;
   }

   public void activate() {
      final ChannelEvent event = new ChannelEvent(this);
      this.notifyListeners(new Channel._ChannelEventNotifier() {
         @Override
         public void notify(ChannelListener listener) {
            listener.onActivation(event);
         }
      });
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      try {
         in.defaultReadObject();
      } catch (NotActiveException var3) {
         var3.printStackTrace();
      }

      this._propChangeSupport = new PropertyChangeSupport(this);
      this._listeners = new Vector();
   }

   public void sendAction(String msg) {
      this.sendPrivMsg("\u0001ACTION " + msg + "\u0001");
   }

   private void sendAnyUnsentMessages() {
      if (this._unsentMessagesBuffer != null) {
         int len = this._unsentMessagesBuffer.size();
         if (len > 0) {
            StringBuilder sb = new StringBuilder(100);
            sb.append("<Connection dropped");
            if (this.isConnected()) {
               if (this._numDroppedMessages > 0) {
                  sb.append("; number of dropped messages: " + this._numDroppedMessages);
               }

               sb.append('>');
               this.sendPrivMsgNoBuffer(sb.toString());
               if (this.isConnected()) {
                  this._numDroppedMessages = 0;

                  for (int i = 0; i < len; i++) {
                     this.sendPrivMsgNoBuffer((String)this._unsentMessagesBuffer.firstElement());
                     if (!this.isConnected()) {
                        break;
                     }

                     this._unsentMessagesBuffer.removeElementAt(0);
                  }
               }
            }
         }
      }
   }

   public void sendBan(String nick) {
      this._server.sendCommand("MODE " + this._name + " +b " + nick);
   }

   public void sendDeop(String nick) {
      this._server.sendCommand("MODE " + this._name + " -o " + nick);
   }

   public synchronized void sendJoin() {
      if (!this._isConnected) {
         this._server.sendJoin(this);
      }
   }

   public void sendKick(String nick) {
      this._server.sendCommand("KICK " + this._name + " " + nick);
   }

   public void sendMessage(String message) {
      StringTokenizer lines = new StringTokenizer(message, "\n", true);

      while (lines.hasMoreTokens()) {
         String token = lines.nextToken();
         if (token.equals("\n")) {
            this.sendPrivMsg(this._messageLineBuffer.toString());
            this._messageLineBuffer.setLength(0);
         } else {
            this._messageLineBuffer.append(token);
         }
      }
   }

   /** @deprecated */
   public void sendOp(String nick) {
      this._server.sendCommand("MODE " + this._name + " +o " + nick);
   }

   public void sendPart() {
      this._server.sendPart(this._name);
   }

   public void sendPrivMsg(String str) {
      if (this.isConnected()) {
         this._server.sendPrivateMessage(this._name, str);
      }

      if (!this.isConnected() && this._maxNumBufferedUnsentMessages != 0) {
         if (this._unsentMessagesBuffer == null) {
            this._unsentMessagesBuffer = new Vector(this._maxNumBufferedUnsentMessages + 100);
         }

         this._unsentMessagesBuffer.addElement(str);
         if (this._maxNumBufferedUnsentMessages > 0) {
            while (this._unsentMessagesBuffer.size() > this._maxNumBufferedUnsentMessages) {
               this._unsentMessagesBuffer.removeElementAt(0);
               this._numDroppedMessages++;
            }
         }
      }
   }

   private void sendPrivMsgNoBuffer(String str) {
      this._server.sendCommand("PRIVMSG " + this._name + " :" + str);
   }

   public void setMaxNumBufferedUnsentMessages(int newMaxNumBufferedUnsentMessages) {
      this._maxNumBufferedUnsentMessages = newMaxNumBufferedUnsentMessages;
   }

   interface _ChannelEventNotifier {
      void notify(ChannelListener var1);
   }

   private class _ChannelMux extends IRCConnectionAdapter {
      private _ChannelMux() {
      }

      @Override
      public void onAction(String user, String channel, String txt) {
         final ChannelEvent event = new ChannelEvent(Channel.this, user, "", txt);
         Channel.this.notifyListeners(new Channel._ChannelEventNotifier() {
            @Override
            public void notify(ChannelListener listener) {
               listener.onAction(event);
            }
         });
      }

      @Override
      public void onBan(String banned, String chan, String banner) {
         final ChannelEvent event = new ChannelEvent(Channel.this, banner, "", banned, "", "");
         Channel.this.notifyListeners(new Channel._ChannelEventNotifier() {
            @Override
            public void notify(ChannelListener listener) {
               listener.onBan(event);
            }
         });
         if (banned.equals(Channel.this._server.getNick())) {
            Channel.this._server.fireStatusEvent("\nYou were banned from " + Channel.this._name + "\n");
            Channel.this.disconnect();
         }
      }

      @Override
      public void onDisconnect() {
         Debug.println("Channel.onDisconnect(): listeners = " + Channel.this._listeners.size());
         Channel.this.setConnected(false);
      }

      @Override
      public void onJoin(String user, String nick, String chan, boolean create) {
         if (nick.equals(Channel.this._server.getNick())) {
            Channel.this._server.fireStatusEvent(nick + " joined channel " + Channel.this._name + "\n");
            Channel.this.setConnected(true);
            Channel.this.sendAnyUnsentMessages();
         } else {
            final ChannelEvent event = new ChannelEvent(Channel.this, nick, user, "");
            Channel.this.notifyListeners(new Channel._ChannelEventNotifier() {
               @Override
               public void notify(ChannelListener listener) {
                  listener.onJoin(event);
               }
            });
         }
      }

      @Override
      public void onJoins(String users, String chans) {
         final ChannelEvent event = new ChannelEvent(Channel.this, users);
         Channel.this.notifyListeners(new Channel._ChannelEventNotifier() {
            @Override
            public void notify(ChannelListener listener) {
               listener.onJoins(event);
            }
         });
      }

      @Override
      public void onKick(String kicked, String chan, String kicker, String txt) {
         final ChannelEvent event = new ChannelEvent(Channel.this, kicker, "", kicked, "", txt);
         Channel.this.notifyListeners(new Channel._ChannelEventNotifier() {
            @Override
            public void notify(ChannelListener listener) {
               listener.onKick(event);
            }
         });
         if (kicked.equals(Channel.this._server.getNick())) {
            String reason = (String)event.getValue();
            Channel.this._server.fireStatusEvent("\nYou were kicked from " + Channel.this._name + "(" + reason + ")\n");
            Channel.this.setConnected(false);
         }
      }

      @Override
      public void onTopic(String channel, String txt) {
         Channel.this.setTopic(txt);
      }

      @Override
      public void onMessage(String txt) {
      }

      @Override
      public void onPrivateMessage(String orgnick, String chan, String txt) {
         final ChannelEvent event = new ChannelEvent(Channel.this, orgnick, "", txt);
         Channel.this.notifyListeners(new Channel._ChannelEventNotifier() {
            @Override
            public void notify(ChannelListener listener) {
               listener.onMessage(event);
            }
         });
      }

      @Override
      public void onNick(String user, String oldnick, String newnick) {
         final ChannelEvent event = new ChannelEvent(Channel.this, oldnick, user, newnick);
         Channel.this.notifyListeners(new Channel._ChannelEventNotifier() {
            @Override
            public void notify(ChannelListener listener) {
               listener.onNick(event);
            }
         });
      }

      @Override
      public void onPart(String user, String nick, String chan) {
         final ChannelEvent event = new ChannelEvent(Channel.this, nick, user, "");
         Channel.this.notifyListeners(new Channel._ChannelEventNotifier() {
            @Override
            public void notify(ChannelListener listener) {
               listener.onPart(event);
            }
         });
         if (nick.equals(Channel.this._server.getNick())) {
            Channel.this._server.fireStatusEvent(nick + " parted channel " + Channel.this._name + ")\n");
            Channel.this.setConnected(false);
         }
      }

      @Override
      public void onOp(String oper, String chan, String oped) {
         final ChannelEvent event = new ChannelEvent(Channel.this, oper, "", oped, "", "");
         Channel.this.notifyListeners(new Channel._ChannelEventNotifier() {
            @Override
            public void notify(ChannelListener listener) {
               listener.onOp(event);
            }
         });
      }

      @Override
      public void onQuit(String user, String nick, String txt) {
         final ChannelEvent event = new ChannelEvent(Channel.this, nick, "", txt);
         Channel.this.notifyListeners(new Channel._ChannelEventNotifier() {
            @Override
            public void notify(ChannelListener listener) {
               listener.onQuit(event);
            }
         });
      }
   }
}

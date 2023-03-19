package org.relayirc.chatengine;

import java.util.EventObject;
import org.relayirc.util.Debug;

public class ServerEvent extends EventObject {
   private Channel _channel = null;
   private User _user = null;
   private Server _server = null;
   private String _message = null;

   public ServerEvent(Server src) {
      super(src);
      Debug.println("ServerEvent(" + src + ")");
   }

   public ServerEvent(Server src, Channel channel) {
      super(src);
      this._channel = channel;
      Debug.println("ServerEvent(" + src + "," + channel + ")");
   }

   public ServerEvent(Server src, Server server) {
      super(src);
      this._server = server;
      Debug.println("ServerEvent(" + src + "," + server + ")");
   }

   public ServerEvent(Server src, User user) {
      super(src);
      this._user = user;
      Debug.println("ServerEvent(" + src + "," + user + ")");
   }

   public ServerEvent(Server src, String message) {
      super(src);
      this._message = message;
      Debug.println("ServerEvent(" + src + "," + message + ")");
   }

   public Channel getChannel() {
      return this._channel;
   }

   public Server getServer() {
      return this._server;
   }

   public String getMessage() {
      return this._message;
   }

   public User getUser() {
      return this._user;
   }
}

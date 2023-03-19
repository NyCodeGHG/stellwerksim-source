package org.relayirc.chatengine;

public interface ServerListener {
   void onConnect(ServerEvent var1);

   void onDisconnect(ServerEvent var1);

   void onChannelAdd(ServerEvent var1);

   void onChannelJoin(ServerEvent var1);

   void onChannelPart(ServerEvent var1);

   void onStatus(ServerEvent var1);

   void onWhoIs(ServerEvent var1);
}

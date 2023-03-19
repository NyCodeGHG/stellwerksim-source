package org.relayirc.chatengine;

public interface ChannelListener {
   void onActivation(ChannelEvent var1);

   void onAction(ChannelEvent var1);

   void onConnect(ChannelEvent var1);

   void onDisconnect(ChannelEvent var1);

   void onMessage(ChannelEvent var1);

   void onJoin(ChannelEvent var1);

   void onJoins(ChannelEvent var1);

   void onPart(ChannelEvent var1);

   void onBan(ChannelEvent var1);

   void onKick(ChannelEvent var1);

   void onNick(ChannelEvent var1);

   void onOp(ChannelEvent var1);

   void onQuit(ChannelEvent var1);

   void onTopicChange(ChannelEvent var1);
}

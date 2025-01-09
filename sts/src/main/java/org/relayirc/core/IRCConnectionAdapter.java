package org.relayirc.core;

import java.util.Date;

public class IRCConnectionAdapter implements IRCConnectionListener {
   @Override
   public void onAction(String user, String chan, String txt) {
   }

   @Override
   public void onBan(String banned, String chan, String banner) {
   }

   @Override
   public void onClientInfo(String orgnick) {
   }

   @Override
   public void onClientSource(String orgnick) {
   }

   @Override
   public void onClientVersion(String orgnick) {
   }

   @Override
   public void onConnect() {
   }

   @Override
   public void onDisconnect() {
   }

   @Override
   public void onJoin(String user, String nick, String chan, boolean create) {
   }

   @Override
   public void onJoins(String users, String chan) {
   }

   @Override
   public void onKick(String kicked, String chan, String kicker, String txt) {
   }

   @Override
   public void onMessage(String message) {
   }

   @Override
   public void onPrivateMessage(String orgnick, String chan, String txt) {
   }

   @Override
   public void onNick(String user, String oldnick, String newnick) {
   }

   @Override
   public void onNotice(String text) {
   }

   @Override
   public void onPart(String user, String nick, String chan) {
   }

   @Override
   public void onOp(String oper, String chan, String oped) {
   }

   @Override
   public void onParsingError(String message) {
   }

   @Override
   public void onPing(String params) {
   }

   @Override
   public void onPong(String params) {
   }

   @Override
   public void onStatus(String msg) {
   }

   @Override
   public void onTopic(String chanName, String newTopic) {
   }

   @Override
   public void onVersionNotice(String orgnick, String origin, String version) {
   }

   @Override
   public void onQuit(String user, String nick, String txt) {
   }

   @Override
   public void onReplyVersion(String version) {
   }

   @Override
   public void onReplyListUserChannels(int channelCount) {
   }

   @Override
   public void onReplyListStart() {
   }

   @Override
   public void onReplyList(String channel, int userCount, String topic) {
   }

   @Override
   public void onReplyListEnd() {
   }

   @Override
   public void onReplyListUserClient(String msg) {
   }

   @Override
   public void onReplyWhoIsUser(String nick, String user, String name, String host) {
   }

   @Override
   public void onReplyWhoIsServer(String nick, String server, String info) {
   }

   @Override
   public void onReplyWhoIsOperator(String info) {
   }

   @Override
   public void onReplyWhoIsIdle(String nick, int idle, Date signon) {
   }

   @Override
   public void onReplyEndOfWhoIs(String nick) {
   }

   @Override
   public void onReplyWhoIsChannels(String nick, String channels) {
   }

   @Override
   public void onReplyMOTDStart() {
   }

   @Override
   public void onReplyMOTD(String msg) {
   }

   @Override
   public void onReplyMOTDEnd() {
   }

   @Override
   public void onReplyNameReply(String channel, String users) {
   }

   @Override
   public void onReplyTopic(String channel, String topic) {
   }

   @Override
   public void onErrorNoMOTD() {
   }

   @Override
   public void onErrorNeedMoreParams() {
   }

   @Override
   public void onErrorNoNicknameGiven() {
   }

   @Override
   public void onErrorNickNameInUse(String badNick) {
   }

   @Override
   public void onErrorNickCollision(String badNick) {
   }

   @Override
   public void onErrorErroneusNickname(String badNick) {
   }

   @Override
   public void onErrorAlreadyRegistered() {
   }

   @Override
   public void onErrorUnknown(String message) {
   }

   @Override
   public void onErrorUnsupported(String messag) {
   }
}

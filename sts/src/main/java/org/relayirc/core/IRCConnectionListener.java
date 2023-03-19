package org.relayirc.core;

import java.util.Date;

public interface IRCConnectionListener {
   void onAction(String var1, String var2, String var3);

   void onBan(String var1, String var2, String var3);

   void onClientInfo(String var1);

   void onClientSource(String var1);

   void onClientVersion(String var1);

   void onConnect();

   void onDisconnect();

   void onJoin(String var1, String var2, String var3, boolean var4);

   void onJoins(String var1, String var2);

   void onKick(String var1, String var2, String var3, String var4);

   void onMessage(String var1);

   void onPrivateMessage(String var1, String var2, String var3);

   void onNick(String var1, String var2, String var3);

   void onNotice(String var1);

   void onPart(String var1, String var2, String var3);

   void onOp(String var1, String var2, String var3);

   void onParsingError(String var1);

   void onPing(String var1);

   void onPong(String var1);

   void onStatus(String var1);

   void onTopic(String var1, String var2);

   void onVersionNotice(String var1, String var2, String var3);

   void onQuit(String var1, String var2, String var3);

   void onReplyVersion(String var1);

   void onReplyListUserChannels(int var1);

   void onReplyListStart();

   void onReplyList(String var1, int var2, String var3);

   void onReplyListEnd();

   void onReplyListUserClient(String var1);

   void onReplyWhoIsUser(String var1, String var2, String var3, String var4);

   void onReplyWhoIsServer(String var1, String var2, String var3);

   void onReplyWhoIsOperator(String var1);

   void onReplyWhoIsIdle(String var1, int var2, Date var3);

   void onReplyEndOfWhoIs(String var1);

   void onReplyWhoIsChannels(String var1, String var2);

   void onReplyMOTDStart();

   void onReplyMOTD(String var1);

   void onReplyMOTDEnd();

   void onReplyNameReply(String var1, String var2);

   void onReplyTopic(String var1, String var2);

   void onErrorNoMOTD();

   void onErrorNeedMoreParams();

   void onErrorNoNicknameGiven();

   void onErrorNickNameInUse(String var1);

   void onErrorNickCollision(String var1);

   void onErrorErroneusNickname(String var1);

   void onErrorAlreadyRegistered();

   void onErrorUnknown(String var1);

   void onErrorUnsupported(String var1);
}

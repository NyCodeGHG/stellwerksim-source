package org.relayirc.chatengine;

public interface ChannelSearchListener {
   void searchFound(Channel var1);

   void searchStarted(int var1);

   void searchEnded();
}

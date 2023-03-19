package js.java.schaltungen.chatcomng;

import org.relayirc.chatengine.ChannelEvent;

public class IC_PositionChannel extends IrcChannel {
   public IC_PositionChannel(ChatNG chat, String name) {
      super(chat, name, name, false);
   }

   @Override
   public void onMessage(ChannelEvent event) {
   }

   public static class Factory implements ICFactory<IC_PositionChannel> {
      public Factory() {
         super();
      }

      public IC_PositionChannel newInstance(ChatNG chat, String name) {
         return new IC_PositionChannel(chat, name);
      }
   }
}

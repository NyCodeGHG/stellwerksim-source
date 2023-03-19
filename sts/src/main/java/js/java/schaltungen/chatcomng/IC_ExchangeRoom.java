package js.java.schaltungen.chatcomng;

import org.relayirc.chatengine.ChannelEvent;

public class IC_ExchangeRoom extends IrcChannel {
   public IC_ExchangeRoom(ChatNG chat, String name) {
      super(chat, name, name, false);
   }

   @Override
   public void onMessage(ChannelEvent event) {
      String msg = event.getValue().toString();
      this.chat.bus.publish(new ExchangeMessageEvent(msg));
   }

   public static class Factory implements ICFactory<IC_ExchangeRoom> {
      public Factory() {
         super();
      }

      public IC_ExchangeRoom newInstance(ChatNG chat, String name) {
         return new IC_ExchangeRoom(chat, name);
      }
   }
}

package js.java.schaltungen.chatcomng;

import org.relayirc.chatengine.ChannelEvent;

public class IC_Bot extends IrcChannel {
   public IC_Bot(ChatNG chat, String name) {
      super(chat, name, name, false);
   }

   @Override
   public void onMessage(ChannelEvent event) {
      String msg = event.getValue().toString();
      this.chat.bus.publish(new BotMessageEvent(event.getOriginNick(), msg, false));
   }
}

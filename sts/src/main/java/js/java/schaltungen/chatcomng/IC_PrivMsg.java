package js.java.schaltungen.chatcomng;

import org.relayirc.chatengine.ChannelEvent;

public class IC_PrivMsg extends IrcChannel {
   public IC_PrivMsg(ChatNG chat, String name) {
      super(chat, name, name, false);
   }

   @Override
   public void onMessage(ChannelEvent event) {
      String msg = event.getValue().toString();
      this.chat.bus.publish(new PrivateMessageEvent(event.getOriginNick2(), msg));
   }
}

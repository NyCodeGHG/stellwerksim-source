package js.java.schaltungen.chatcomng;

import org.relayirc.chatengine.ChannelEvent;

public class IC_BotControlRoom extends IrcChannel {
   public IC_BotControlRoom(ChatNG chat, String name) {
      super(chat, name, name, false);
   }

   @Override
   public void onMessage(ChannelEvent event) {
      String msg = event.getValue().toString();

      int instanz;
      try {
         instanz = Integer.parseInt(event.getChannelName().substring(event.getChannelName().length() - 1));
      } catch (NumberFormatException var5) {
         instanz = -1;
      }

      if (event.getOriginNick2().equals(this.chat.uc.getControlBot())) {
         this.chat.bus.publish(new BotMessageEvent(event.getOriginNick2(), msg, true, instanz));
      } else {
         this.chat.bus.publish(new PublicControlMessage(event.getOriginNick2(), msg, true, instanz));
      }
   }

   public static class Factory implements ICFactory<IC_BotControlRoom> {
      public Factory() {
         super();
      }

      public IC_BotControlRoom newInstance(ChatNG chat, String name) {
         return new IC_BotControlRoom(chat, name);
      }
   }
}

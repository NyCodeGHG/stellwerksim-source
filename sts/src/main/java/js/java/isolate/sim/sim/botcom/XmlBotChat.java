package js.java.isolate.sim.sim.botcom;

import de.deltaga.serial.Base64;
import de.deltaga.serial.XmlMarshal;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.sim.botcom.events.BPosChange;
import js.java.isolate.sim.sim.botcom.events.EPosChange;
import js.java.isolate.sim.sim.botcom.events.ElementOccurance;
import js.java.isolate.sim.sim.botcom.events.SBldChange;
import js.java.isolate.sim.sim.botcom.events.XPosChange;
import js.java.isolate.sim.sim.botcom.events.ZugUserText;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.chatcomng.ChatMessageEvent;

public abstract class XmlBotChat implements chatInterface {
   protected final UserContext uc;
   private final XmlMarshal xmlConverter = new XmlMarshal(
      new Class[]{ElementOccurance.class, SBldChange.class, EPosChange.class, BPosChange.class, XPosChange.class, ZugUserText.class}
   );

   protected XmlBotChat(UserContext uc) {
      this.uc = uc;
   }

   protected void send(String to, String text) {
      ChatMessageEvent event = new ChatMessageEvent(to, text);
      this.uc.busPublish(event);
   }

   @Override
   public void sendStatusToChannel(String channel, String message) {
      if (!channel.startsWith("#")) {
         channel = "#" + channel;
      }

      this.send(channel, message);
   }

   @Override
   public void sendXmlStatusToChannel(String channel, Object message) {
      try {
         String msg = this.xmlConverter.serialize(message);
         if (!msg.isEmpty()) {
            String b64 = Base64.toBase64(msg, 10);
            if (!b64.isEmpty()) {
               this.sendStatusToChannel(channel, b64);
            }
         }
      } catch (Exception var5) {
         Logger.getLogger(XmlBotChat.class.getName()).log(Level.SEVERE, null, var5);
      }
   }

   protected void publishAsEvent(String message) {
      try {
         Object o = this.xmlConverter.deserialize(Base64.fromBase64(message));
         if (o != null) {
            this.uc.busPublish(o);
         }
      } catch (Exception var3) {
      }
   }

   public Object deserialize(String message) {
      try {
         Object o = this.xmlConverter.deserialize(Base64.fromBase64(message));
         if (o != null) {
            return o;
         }
      } catch (Exception var3) {
         Logger.getLogger(XmlBotChat.class.getName()).log(Level.SEVERE, null, var3);
      }

      return null;
   }
}

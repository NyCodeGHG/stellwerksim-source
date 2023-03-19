package js.java.schaltungen;

import java.awt.Image;
import java.awt.TrayIcon.MessageType;
import js.java.schaltungen.audio.AudioSettings;
import js.java.schaltungen.chatcomng.ChatNG;
import js.java.schaltungen.switchbase.DataSwitch;

public interface UserContextMini {
   String getToken();

   String getUid();

   String getUsername();

   String getIrcServer();

   String getControlBot();

   String getReadyRoom();

   Image getWindowIcon();

   String getParameter(UserContextMini.DATATYPE var1);

   void showTrayMessage(String var1);

   void showTrayMessage(String var1, String var2);

   void showTrayMessage(String var1, String var2, MessageType var3);

   void exit();

   void forceExit();

   void showAbout();

   int getBuild();

   ChatNG getChat();

   void showTopLevelMessage(String var1, int var2);

   void closeTopLevelMessage();

   void showException(Exception var1);

   AudioSettings getAudioSettings();

   void moduleClosed();

   DataSwitch getDataSwitch();

   public static enum DATATYPE {
      IRCSERVER("jnlp.ircserver", "www.stellwerksim.de"),
      WEBSERVER("jnlp.webserver", "www.stellwerksim.de"),
      TIMESERVER("jnlp.timeserver", "www.stellwerksim.de"),
      CONTROLBOT("jnlp.controlbot", "sts-dev"),
      CONTROLROOMPREFIX("jnlp.controlroomPrefix", "#control"),
      READYROOMCHANNELS("jnlp.readyroomchannels", "#Dev2Lobby:Lobby;#sts-dev"),
      READYROOM("jnlp.readyroom", "#dev_readyroom"),
      STARTCHANNEL("jnlp.startchannel", "#Dev2Lobby"),
      MIXFILTERCHANNELS("jnlp.mixfilterchannels", "#Dev2Lobby;#sts-dev"),
      WEBSERVICE("jnlp.service", "https://js.sandbox.stellwerksim.de/soap.php"),
      LOG("jnlp.log", "http://www.stellwerksim.de/javalog.php"),
      JOIN_ANY_CHANNEL("jnlp.allowjoin", "false");

      public final String propertyName;
      public final String defaultValue;

      private DATATYPE(String p, String d) {
         this.propertyName = p;
         this.defaultValue = d;
      }
   }
}

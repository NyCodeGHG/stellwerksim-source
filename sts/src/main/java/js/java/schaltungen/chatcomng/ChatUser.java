package js.java.schaltungen.chatcomng;

import de.deltaga.eb.EventBus;
import java.util.UUID;

public class ChatUser implements Comparable<ChatUser> {
   public final String nick;
   private String displayName = null;
   private String currentGame = null;
   private final EventBus bus;
   private String toStringByName = "";
   private String toStringByPlay = "";
   private long entrytime = System.currentTimeMillis();
   private long playEntrytime = 0L;
   private int distance = -1;
   private final UUID uuid;

   public static UUID generateNickUUID(String nick) {
      return UUID.nameUUIDFromBytes(nick.getBytes());
   }

   public ChatUser(ChatNG chat, String nick) {
      super();
      this.bus = chat.bus;
      this.nick = nick;
      this.uuid = generateNickUUID(nick);
      this.buildString();
   }

   private void buildString() {
      this.toStringByName = this.getName();
      this.toStringByPlay = "";
      if (this.currentGame != null) {
         this.toStringByName = this.toStringByName + " (" + this.currentGame + ")";
         this.toStringByPlay = this.currentGame;
      } else {
         this.toStringByPlay = "-";
      }

      this.toStringByPlay = this.toStringByPlay + ": " + this.getName();
      this.bus.publish(this);
   }

   void setName(String n) {
      if (!n.equals(this.displayName)) {
         this.displayName = n;
         this.buildString();
      }
   }

   void setPlay(String n) {
      this.currentGame = n;
      this.setPlayEntrytime(System.currentTimeMillis());
      this.buildString();
   }

   public String getPlay() {
      return this.currentGame;
   }

   public String getNick() {
      return this.nick;
   }

   public String getName() {
      return this.displayName != null ? this.displayName : this.nick;
   }

   public String getNameNoA() {
      return this.getName().replaceAll("^\\W+|\\W+$", "");
   }

   public boolean knowsName() {
      return this.displayName != null;
   }

   public String toString() {
      return this.toStringByName;
   }

   public String toStringByName() {
      return this.toStringByName;
   }

   public String toStringByPlay() {
      return this.toStringByPlay;
   }

   public int compareTo(ChatUser o) {
      int r = this.toStringByName.compareToIgnoreCase(o.toStringByName);
      if (r == 0) {
         r = this.nick.compareToIgnoreCase(o.nick);
      }

      return r;
   }

   public long getEntrytime() {
      return this.entrytime;
   }

   public void setEntrytime(long entrytime) {
      this.entrytime = entrytime;
   }

   public int getDistance() {
      return this.distance;
   }

   public void setDistance(int distance) {
      this.distance = distance;
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public long getPlayEntrytime() {
      return this.playEntrytime;
   }

   public void setPlayEntrytime(long playEntrytime) {
      this.playEntrytime = playEntrytime;
   }
}

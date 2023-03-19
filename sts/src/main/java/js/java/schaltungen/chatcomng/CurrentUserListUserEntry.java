package js.java.schaltungen.chatcomng;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class CurrentUserListUserEntry {
   @XmlElement(
      name = "n"
   )
   public String name;
   @XmlElement(
      name = "c"
   )
   public String nick;
   @XmlElement(
      name = "p"
   )
   public String playing;
   @XmlElement(
      name = "t"
   )
   @XmlJavaTypeAdapter(EntryTimeAdapter.class)
   public Long entrytime;
   @XmlElement(
      name = "pt"
   )
   @XmlJavaTypeAdapter(EntryTimeAdapter.class)
   public Long playentrytime;
   @XmlElement(
      name = "d"
   )
   public int distance;

   public CurrentUserListUserEntry(ChatUser user) {
      super();
      this.nick = user.nick;
      this.name = user.getName();
      this.playing = user.getPlay();
      this.entrytime = user.getEntrytime();
      this.playentrytime = user.getPlayEntrytime();
      this.distance = user.getDistance() + 1;
   }

   public CurrentUserListUserEntry(String nick, String name, String playing, long entrytime, long playentrytime) {
      super();
      this.nick = nick;
      this.name = name;
      this.playing = playing;
      this.entrytime = entrytime;
      this.playentrytime = playentrytime;
      this.distance = 0;
   }

   public CurrentUserListUserEntry() {
      super();
   }
}

package js.java.schaltungen.cevents;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(
   name = "be"
)
public class BuildEvent {
   public static final int APILEVEL = 1;
   @XmlElement(
      name = "b"
   )
   public int build;
   @XmlElement(
      name = "l"
   )
   public int apiLevel;

   public BuildEvent() {
   }

   public BuildEvent(int b) {
      this.build = b;
      this.apiLevel = 1;
   }
}

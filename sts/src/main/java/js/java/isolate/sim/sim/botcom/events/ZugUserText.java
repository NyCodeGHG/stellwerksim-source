package js.java.isolate.sim.sim.botcom.events;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ZugUserText {
   public int zid;
   public String text;
   public String sender;

   public ZugUserText(int zid, String t, String sender) {
      this.zid = zid;
      this.text = t;
      this.sender = sender;
   }

   public ZugUserText(int zid) {
      this.zid = zid;
      this.text = "";
      this.sender = "";
   }

   public ZugUserText() {
   }
}

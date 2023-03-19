package js.java.isolate.sim.sim.botcom.events;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ZugUserText {
   public int zid;
   public String text;
   public String sender;

   public ZugUserText(int zid, String t, String sender) {
      super();
      this.zid = zid;
      this.text = t;
      this.sender = sender;
   }

   public ZugUserText(int zid) {
      super();
      this.zid = zid;
      this.text = "";
      this.sender = "";
   }

   public ZugUserText() {
      super();
   }
}

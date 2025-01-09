package js.java.isolate.sim.sim.botcom.events;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class XPosChange {
   public int zid;

   public XPosChange() {
   }

   public XPosChange(int zid) {
      this.zid = zid;
   }
}

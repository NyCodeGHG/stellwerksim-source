package js.java.isolate.sim.sim.botcom.events;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class XPosChange {
   public int zid;

   public XPosChange() {
      super();
   }

   public XPosChange(int zid) {
      super();
      this.zid = zid;
   }
}

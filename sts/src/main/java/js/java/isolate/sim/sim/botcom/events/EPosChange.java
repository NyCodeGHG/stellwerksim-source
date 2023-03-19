package js.java.isolate.sim.sim.botcom.events;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EPosChange {
   public int zid;
   public int enr1;
   public int enr2;

   public EPosChange() {
      super();
   }

   public EPosChange(int zid, int enr1, int enr2) {
      super();
      this.zid = zid;
      this.enr1 = enr1;
      this.enr2 = enr2;
   }
}

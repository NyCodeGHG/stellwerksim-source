package js.java.isolate.sim.sim.botcom.events;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SBldChange {
   public int aid;
   public String fsname;
   public int enr;
   public int st;

   public SBldChange() {
      super();
   }

   public SBldChange(int aid, int enr, int st, String fsname) {
      super();
      this.aid = aid;
      this.enr = enr;
      this.st = st;
      this.fsname = fsname;
   }
}

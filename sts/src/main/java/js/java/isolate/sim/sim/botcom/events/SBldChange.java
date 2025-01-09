package js.java.isolate.sim.sim.botcom.events;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SBldChange {
   public int aid;
   public String fsname;
   public int enr;
   public int st;

   public SBldChange() {
   }

   public SBldChange(int aid, int enr, int st, String fsname) {
      this.aid = aid;
      this.enr = enr;
      this.st = st;
      this.fsname = fsname;
   }
}

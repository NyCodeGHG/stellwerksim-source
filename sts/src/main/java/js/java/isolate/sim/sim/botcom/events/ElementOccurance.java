package js.java.isolate.sim.sim.botcom.events;

import javax.xml.bind.annotation.XmlRootElement;
import js.java.schaltungen.chatcomng.OCCU_KIND;

@XmlRootElement
public class ElementOccurance {
   public int aid;
   public int enr;
   public OCCU_KIND kind;

   public ElementOccurance() {
   }

   public ElementOccurance(int aid, int hash, OCCU_KIND kind) {
      this.aid = aid;
      this.enr = hash;
      this.kind = kind;
   }
}

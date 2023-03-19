package js.java.isolate.sim.sim.botcom.events;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BPosChange {
   public int zid;
   public int x = -1;
   public int y = -1;
   public String bstg;

   public BPosChange() {
      super();
   }

   public BPosChange(int zid, String bstg) {
      super();
      this.zid = zid;
      this.bstg = bstg;
   }

   public BPosChange(int zid, String bstg, int x, int y) {
      super();
      this.zid = zid;
      this.bstg = bstg;
      this.x = x;
      this.y = y;
   }
}

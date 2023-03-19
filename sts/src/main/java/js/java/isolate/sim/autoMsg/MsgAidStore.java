package js.java.isolate.sim.autoMsg;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
class MsgAidStore {
   public int aid;
   public LinkedList<StoredItem> items = new LinkedList();
   public long checksum;

   MsgAidStore() {
      super();
   }

   long calcChecksum() {
      long ch = (long)(this.aid * this.items.size());

      for(StoredItem si : this.items) {
         ch += (long)si.signal;
      }

      return ch;
   }
}

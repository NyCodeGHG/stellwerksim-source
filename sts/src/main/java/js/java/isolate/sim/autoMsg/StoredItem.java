package js.java.isolate.sim.autoMsg;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
class StoredItem {
   public int signal;
   public String ziel;
   public String zielnachbar;

   public StoredItem() {
      super();
   }

   StoredItem(msgItem item) {
      super();
      this.signal = item.signal.getENR();
      this.ziel = item.ziel;
      this.zielnachbar = item.zielnachbar;
   }
}

package js.java.isolate.sim.gleisbild;

import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.sim.phonebook;
import org.xml.sax.Attributes;

public class gleisbildModelPhone extends gleisbildModelEventsys {
   private final phonebook my_phone = new phonebook(this, this.theapplet);

   public gleisbildModelPhone(GleisAdapter _theapplet) {
      super(_theapplet);
   }

   @Override
   public void close() {
      super.close();
      this.my_phone.close();
   }

   public boolean canCallPhone() {
      return this.my_phone.canCall();
   }

   public void callPhone(String tel) {
      this.my_phone.call(tel);
   }

   public phonebook getPhonebook() {
      return this.my_phone;
   }

   @Override
   protected void registerTags() {
      super.registerTags();
      this.xmlr.registerTag("telefon", this);
   }

   @Override
   public void parseStartTag(String tag, Attributes attrs) {
      if (tag.equals("telefon")) {
         this.my_phone.xml(attrs);
      } else {
         super.parseStartTag(tag, attrs);
      }
   }
}

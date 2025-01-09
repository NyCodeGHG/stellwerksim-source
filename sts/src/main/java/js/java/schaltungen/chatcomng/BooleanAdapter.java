package js.java.schaltungen.chatcomng;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class BooleanAdapter extends XmlAdapter<String, Boolean> {
   public Boolean unmarshal(String v) throws Exception {
      return "T".equals(v) ? Boolean.TRUE : Boolean.FALSE;
   }

   public String marshal(Boolean v) throws Exception {
      return v ? "T" : "F";
   }
}

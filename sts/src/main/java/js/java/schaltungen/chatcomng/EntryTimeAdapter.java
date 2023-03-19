package js.java.schaltungen.chatcomng;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class EntryTimeAdapter extends XmlAdapter<String, Long> {
   private static final long STARTOFFSET = 1507400743000L;
   private static final int RADIX = 36;

   public EntryTimeAdapter() {
      super();
   }

   public Long unmarshal(String v) throws Exception {
      long ret = Long.parseLong(v, 36);
      return ret + 1507400743000L;
   }

   public String marshal(Long v) throws Exception {
      long ret = v - 1507400743000L;
      return Long.toString(ret, 36);
   }
}

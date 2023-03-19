package js.java.tools.wol;

public class BadEthernetAddressException extends Exception {
   private String address;

   public BadEthernetAddressException(String address) {
      super("Bad Ethernet Address: " + address);
      this.address = address;
   }
}

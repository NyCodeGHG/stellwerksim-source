package js.java.schaltungen.webservice;

public class StoreTextData {
   public final String reason;
   public final String text;

   public StoreTextData(String reason, String text) {
      super();
      this.reason = reason == null ? "" : reason;
      this.text = text;
   }
}

package js.java.schaltungen.webservice;

public class StoreLatencies {
   public final String name;
   public final String params;
   public final int delay;

   public StoreLatencies(String name, String params, int delay) {
      this.name = name;
      this.params = params;
      this.delay = delay;
   }
}

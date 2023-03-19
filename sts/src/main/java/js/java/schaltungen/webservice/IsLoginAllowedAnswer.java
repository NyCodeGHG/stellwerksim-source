package js.java.schaltungen.webservice;

public class IsLoginAllowedAnswer {
   public final boolean allowed;
   public final String uid;
   public final String uname;

   IsLoginAllowedAnswer(boolean allowed, String uname, String uid) {
      super();
      this.allowed = allowed;
      this.uname = uname;
      this.uid = uid;
   }
}

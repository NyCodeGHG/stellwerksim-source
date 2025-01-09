package js.java.schaltungen.webservice;

public class GetFriendsResponse {
   public final String[] friends;
   public final String[] foes;

   GetFriendsResponse(String[] friends, String[] foes) {
      this.friends = friends;
      this.foes = foes;
   }
}

package js.java.schaltungen.chatcomng;

public enum OCCU_KIND {
   NORMAL('N', 1),
   HOOKED('H', 1),
   OCCURED('O', 1),
   LOCKED('L', 2),
   UNLOCKED('U', 2);

   private final char c;
   private final int group;

   private OCCU_KIND(char c, int group) {
      this.c = c;
      this.group = group;
   }

   public char getChar() {
      return this.c;
   }

   public int getGroup() {
      return this.group;
   }

   public static OCCU_KIND find(String k) {
      for(OCCU_KIND o : values()) {
         if (k.charAt(0) == o.c) {
            return o;
         }
      }

      return NORMAL;
   }
}

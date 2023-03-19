package js.java.tools;

import java.util.Comparator;

public class AlphanumComparator implements Comparator<String> {
   public AlphanumComparator() {
      super();
   }

   private boolean isDigit(char ch) {
      return ch >= '0' && ch <= '9';
   }

   private String getChunk(String s, int slength, int marker) {
      StringBuilder chunk = new StringBuilder();
      char c = s.charAt(marker);
      chunk.append(c);
      ++marker;
      if (this.isDigit(c)) {
         while(marker < slength) {
            c = s.charAt(marker);
            if (!this.isDigit(c)) {
               break;
            }

            chunk.append(c);
            ++marker;
         }
      } else {
         while(marker < slength) {
            c = s.charAt(marker);
            if (this.isDigit(c)) {
               break;
            }

            chunk.append(c);
            ++marker;
         }
      }

      return chunk.toString();
   }

   public int compare(String o1, String o2) {
      String s1 = o1.toLowerCase();
      String s2 = o2.toLowerCase();
      int thisMarker = 0;
      int thatMarker = 0;
      int s1Length = s1.length();
      int s2Length = s2.length();

      while(thisMarker < s1Length && thatMarker < s2Length) {
         String thisChunk = this.getChunk(s1, s1Length, thisMarker);
         thisMarker += thisChunk.length();
         String thatChunk = this.getChunk(s2, s2Length, thatMarker);
         thatMarker += thatChunk.length();
         int result = 0;
         if (this.isDigit(thisChunk.charAt(0)) && this.isDigit(thatChunk.charAt(0))) {
            int thisChunkLength = thisChunk.length();
            result = thisChunkLength - thatChunk.length();
            if (result == 0) {
               for(int i = 0; i < thisChunkLength; ++i) {
                  result = thisChunk.charAt(i) - thatChunk.charAt(i);
                  if (result != 0) {
                     return result;
                  }
               }
            }
         } else {
            result = thisChunk.compareTo(thatChunk);
         }

         if (result != 0) {
            return result;
         }
      }

      return s1Length - s2Length;
   }
}

package org.relayirc.util;

import java.util.StringTokenizer;
import java.util.Vector;

public class ParsedToken {
   public String token;
   public int index;

   public static ParsedToken[] stringToParsedTokens(String s, String delim) {
      ParsedToken[] tokens = null;

      try {
         int pos = 0;
         StringTokenizer toker = new StringTokenizer(s, delim);
         Vector v = new Vector();

         while (toker.hasMoreTokens()) {
            ParsedToken tok = new ParsedToken();
            tok.token = toker.nextToken();
            tok.index = pos;
            pos += tok.token.length() + 1;
            v.addElement(tok);
         }

         tokens = new ParsedToken[v.size()];

         for (int i = 0; i < v.size(); i++) {
            tokens[i] = (ParsedToken)v.elementAt(i);
         }
      } catch (Exception var7) {
         var7.printStackTrace();
      }

      return tokens;
   }
}

package js.java.tools;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class TextHelper {
   public TextHelper() {
      super();
   }

   public static String deAccent(String str) {
      String nfdNormalizedString = Normalizer.normalize(str, Form.NFD);
      Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
      return pattern.matcher(nfdNormalizedString).replaceAll("");
   }

   public static String urlEncode(String str) {
      try {
         return URLEncoder.encode(str, "UTF-8");
      } catch (UnsupportedEncodingException var2) {
         var2.printStackTrace();
         return str;
      }
   }

   public static String[] cmdSplit(String cmd) {
      ArrayList<String> ret = new ArrayList();
      String s = "";
      boolean inQuote = false;

      for(int i = 0; i < cmd.length(); ++i) {
         char c = cmd.charAt(i);
         if (c == '"') {
            inQuote = !inQuote;
            s = s.trim();
            if (!s.isEmpty()) {
               ret.add(s);
            }

            s = "";
         } else if (c == ' ' && !inQuote) {
            s = s.trim();
            if (!s.isEmpty()) {
               ret.add(s);
            }

            s = "";
         } else {
            s = s + c;
         }
      }

      if (!s.isEmpty()) {
         ret.add(s);
      }

      String[] reta = new String[ret.size()];
      return (String[])ret.toArray(reta);
   }

   public static String[] tokenizerToArray(StringTokenizer zst) {
      String[] ret = new String[zst.countTokens()];

      for(int i = 0; zst.hasMoreTokens(); ++i) {
         ret[i] = zst.nextToken();
      }

      return ret;
   }

   public static String prettyXmlString(String input, int indent) {
      try {
         Source xmlInput = new StreamSource(new StringReader(input));
         StringWriter stringWriter = new StringWriter();
         StreamResult xmlOutput = new StreamResult(stringWriter);
         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         transformerFactory.setAttribute("indent-number", indent);
         Transformer transformer = transformerFactory.newTransformer();
         transformer.setOutputProperty("indent", "yes");
         transformer.transform(xmlInput, xmlOutput);
         return xmlOutput.getWriter().toString();
      } catch (Exception var7) {
         throw new RuntimeException(var7);
      }
   }

   public static float toFloat(String str, float defaultValue) {
      if (str == null) {
         return defaultValue;
      } else {
         try {
            return Float.parseFloat(str);
         } catch (NumberFormatException var3) {
            return defaultValue;
         }
      }
   }
}

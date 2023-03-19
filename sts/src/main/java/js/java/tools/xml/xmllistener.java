package js.java.tools.xml;

import org.xml.sax.Attributes;

public interface xmllistener {
   void parseStartTag(String var1, Attributes var2);

   void parseEndTag(String var1, Attributes var2, String var3);
}

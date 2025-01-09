package js.java.tools.formhtml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.HTMLFactory;

public class HTMLEditorKit extends javax.swing.text.html.HTMLEditorKit {
   private final HashMap<String, FormView> elements = new HashMap();

   public Document createDefaultDocument() {
      StyleSheet styles = this.getStyleSheet();
      StyleSheet ss = new StyleSheet();
      ss.addStyleSheet(styles);
      HTMLDocument doc = new HTMLDocument(ss);
      doc.setParser(this.getParser());
      doc.setAsynchronousLoadPriority(4);
      doc.setTokenThreshold(100);
      return doc;
   }

   public ViewFactory getViewFactory() {
      return new HTMLFactory() {
         public View create(Element elem) {
            Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
            if (o instanceof Tag) {
               Tag kind = (Tag)o;
               if (kind == Tag.INPUT || kind == Tag.SELECT || kind == Tag.TEXTAREA) {
                  String name = (String)elem.getAttributes().getAttribute(Attribute.NAME);
                  FormView f = new FormView(elem);
                  HTMLEditorKit.this.elements.put(name, f);
                  return f;
               }
            }

            return super.create(elem);
         }
      };
   }

   public void save() {
      FormView one = null;

      for (Entry<String, FormView> e : this.elements.entrySet()) {
         one = (FormView)e.getValue();
         System.out.println((String)e.getKey() + ":" + ((FormView)e.getValue()).getName() + " = " + ((FormView)e.getValue()).getData());
      }

      System.out.println("---------------------------");

      for (Entry<String, String> e : one.getValues().entrySet()) {
         System.out.println((String)e.getKey() + ":" + (String)e.getValue());
      }

      StringBuilder buffer = new StringBuilder();
      one.getFormData(buffer);
      System.out.println("Form: " + buffer.toString());
   }

   public void setValues(Map<String, String> values) {
      Iterator var2 = this.elements.values().iterator();
      if (var2.hasNext()) {
         FormView e = (FormView)var2.next();
         e.setValues(values);
      }
   }

   public Map<String, String> getValues() {
      Iterator var1 = this.elements.values().iterator();
      if (var1.hasNext()) {
         FormView e = (FormView)var1.next();
         return e.getValues();
      } else {
         return null;
      }
   }
}

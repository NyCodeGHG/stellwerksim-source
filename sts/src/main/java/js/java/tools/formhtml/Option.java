package js.java.tools.formhtml;

import java.io.Serializable;
import javax.swing.text.AttributeSet;
import javax.swing.text.html.HTML.Attribute;

public class Option implements Serializable {
   private boolean selected;
   private String label;
   private AttributeSet attr;

   public Option(AttributeSet attr) {
      this.attr = attr.copyAttributes();
      this.selected = attr.getAttribute(Attribute.SELECTED) != null;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public String getLabel() {
      return this.label;
   }

   public AttributeSet getAttributes() {
      return this.attr;
   }

   public String toString() {
      return this.label;
   }

   protected void setSelection(boolean state) {
      this.selected = state;
   }

   public boolean isSelected() {
      return this.selected;
   }

   public String getValue() {
      String value = (String)this.attr.getAttribute(Attribute.VALUE);
      if (value == null) {
         value = this.label;
      }

      return value;
   }
}

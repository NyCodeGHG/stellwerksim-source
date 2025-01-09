package js.java.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "TipAnswer",
   propOrder = {}
)
public class TipAnswer {
   @XmlElement(
      required = true
   )
   protected String title;
   @XmlElement(
      required = true
   )
   protected String text;

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String value) {
      this.title = value;
   }

   public String getText() {
      return this.text;
   }

   public void setText(String value) {
      this.text = value;
   }
}

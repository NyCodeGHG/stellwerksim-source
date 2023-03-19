package js.java.soap;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
   public ObjectFactory() {
      super();
   }

   public TipAnswer createTipAnswer() {
      return new TipAnswer();
   }
}

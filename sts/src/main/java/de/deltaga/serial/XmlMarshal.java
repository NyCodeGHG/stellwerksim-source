package de.deltaga.serial;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class XmlMarshal {
   private final Class[] classes;
   private JAXBContext context = null;

   public XmlMarshal(Class... classes) {
      this.classes = classes;
   }

   private JAXBContext getContext() {
      if (this.context == null) {
         try {
            this.context = JAXBContext.newInstance(this.classes);
         } catch (JAXBException var2) {
            Logger.getLogger(XmlMarshal.class.getName()).log(Level.SEVERE, null, var2);
         }
      }

      return this.context;
   }

   public boolean isAllowedPackage(Class c) {
      for (Class cs : this.classes) {
         if (cs == c) {
            return true;
         }
      }

      return false;
   }

   public void serialize(Object o, Writer out) throws JAXBException {
      Marshaller m = this.getContext().createMarshaller();
      m.setProperty("jaxb.formatted.output", Boolean.FALSE);
      m.setProperty("jaxb.fragment", Boolean.FALSE);
      m.setProperty("jaxb.encoding", "UTF-8");
      m.marshal(o, out);
   }

   public String serialize(Object o) throws JAXBException {
      StringWriter out = new StringWriter();
      this.serialize(o, out);
      return out.toString();
   }

   public <T> T deserialize(Reader o) throws JAXBException {
      Unmarshaller m = this.getContext().createUnmarshaller();
      return (T)m.unmarshal(o);
   }

   public <T> T deserialize(InputStream o) throws JAXBException {
      Unmarshaller m = this.getContext().createUnmarshaller();
      return (T)m.unmarshal(o);
   }

   public <T> T deserialize(String o) throws JAXBException {
      return this.deserialize(new StringReader(o));
   }
}

package js.java.soap;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(
   name = "STSService",
   targetNamespace = "http://www.stellwerksim.de/wsdl/STS",
   wsdlLocation = "/STSWSDL.wsdl"
)
public class STSService extends Service {
   private static final URL STSSERVICE_WSDL_LOCATION = STSService.class.getResource("/STSWSDL.wsdl");
   private static final WebServiceException STSSERVICE_EXCEPTION;
   private static final QName STSSERVICE_QNAME = new QName("http://www.stellwerksim.de/wsdl/STS", "STSService");

   public STSService() {
      super(__getWsdlLocation(), STSSERVICE_QNAME);
   }

   public STSService(WebServiceFeature... features) {
      super(__getWsdlLocation(), STSSERVICE_QNAME, features);
   }

   public STSService(URL wsdlLocation) {
      super(wsdlLocation, STSSERVICE_QNAME);
   }

   public STSService(URL wsdlLocation, WebServiceFeature... features) {
      super(wsdlLocation, STSSERVICE_QNAME, features);
   }

   public STSService(URL wsdlLocation, QName serviceName) {
      super(wsdlLocation, serviceName);
   }

   public STSService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
      super(wsdlLocation, serviceName, features);
   }

   @WebEndpoint(
      name = "STSPort"
   )
   public STSPort getSTSPort() {
      return (STSPort)super.getPort(new QName("http://www.stellwerksim.de/wsdl/STS", "STSPort"), STSPort.class);
   }

   @WebEndpoint(
      name = "STSPort"
   )
   public STSPort getSTSPort(WebServiceFeature... features) {
      return (STSPort)super.getPort(new QName("http://www.stellwerksim.de/wsdl/STS", "STSPort"), STSPort.class, features);
   }

   private static URL __getWsdlLocation() {
      if (STSSERVICE_EXCEPTION != null) {
         throw STSSERVICE_EXCEPTION;
      } else {
         return STSSERVICE_WSDL_LOCATION;
      }
   }

   static {
      WebServiceException e = null;
      if (STSSERVICE_WSDL_LOCATION == null) {
         e = new WebServiceException("Cannot find '/STSWSDL.wsdl' wsdl. Place the resource correctly in the classpath.");
      }

      STSSERVICE_EXCEPTION = e;
   }
}

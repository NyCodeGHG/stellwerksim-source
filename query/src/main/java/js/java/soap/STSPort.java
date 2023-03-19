package js.java.soap;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSeeAlso;

@WebService(
   name = "STSPort",
   targetNamespace = "http://www.stellwerksim.de/wsdl/STS"
)
@SOAPBinding(
   style = Style.RPC
)
@XmlSeeAlso({ObjectFactory.class})
public interface STSPort {
   @XmlList
   @WebMethod
   @WebResult(
      partName = "return"
   )
   String[] getFriendFoe(@WebParam(name = "token",partName = "token") String var1, @WebParam(name = "friend",partName = "friend") boolean var2);

   @WebMethod
   @WebResult(
      partName = "return"
   )
   String getName(@WebParam(name = "part1",partName = "part1") String var1);

   @WebMethod
   @WebResult(
      partName = "return"
   )
   TipAnswer getTip(@WebParam(name = "token",partName = "token") String var1);

   @WebMethod
   @WebResult(
      partName = "return"
   )
   boolean isLoginAllowed(@WebParam(name = "token",partName = "token") String var1);

   @WebMethod
   @Oneway
   void storeLatency(
      @WebParam(name = "token",partName = "token") String var1,
      @WebParam(name = "type",partName = "type") String var2,
      @WebParam(name = "params",partName = "params") String var3,
      @WebParam(name = "delay",partName = "delay") int var4
   );

   @WebMethod
   @Oneway
   void userData(@WebParam(name = "token",partName = "token") String var1, @WebParam(name = "user",partName = "user") String var2);

   @WebMethod
   @Oneway
   void userConsole(
      @WebParam(name = "token",partName = "token") String var1,
      @WebParam(name = "reason",partName = "reason") String var2,
      @WebParam(name = "content",partName = "content") String var3
   );

   @WebMethod
   @Oneway
   void eventOccured(
      @WebParam(name = "part1",partName = "part1") String var1,
      @WebParam(name = "aid",partName = "aid") int var2,
      @WebParam(name = "text",partName = "text") String var3,
      @WebParam(name = "code",partName = "code") String var4
   );

   @WebMethod
   @WebResult(
      partName = "return"
   )
   String getUid(@WebParam(name = "part1",partName = "part1") String var1);
}

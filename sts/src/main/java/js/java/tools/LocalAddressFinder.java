package js.java.tools;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class LocalAddressFinder {
   public InetAddress find(LocalAddressFinder.LAFfilter f) throws SocketException {
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

      while (interfaces.hasMoreElements()) {
         NetworkInterface current = (NetworkInterface)interfaces.nextElement();
         if (current.isUp() && !current.isLoopback() && !current.isVirtual()) {
            Enumeration<InetAddress> addresses = current.getInetAddresses();

            while (addresses.hasMoreElements()) {
               InetAddress current_addr = (InetAddress)addresses.nextElement();
               if (!current_addr.isLoopbackAddress() && f.filter(current_addr)) {
                  return current_addr;
               }
            }
         }
      }

      return null;
   }

   public static class IPv4Filter implements LocalAddressFinder.LAFfilter {
      @Override
      public boolean filter(InetAddress current_addr) {
         return current_addr instanceof Inet4Address;
      }
   }

   public static class IPv6Filter implements LocalAddressFinder.LAFfilter {
      @Override
      public boolean filter(InetAddress current_addr) {
         return current_addr instanceof Inet6Address;
      }
   }

   public interface LAFfilter {
      boolean filter(InetAddress var1);
   }
}

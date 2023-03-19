package js.java.tools.wol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class WOL {
   public static final int WOL_PORT = 7;

   public WOL() {
      super();
   }

   public static void wakeup(EthernetAddress etheraddress, InetAddress inetaddress) throws SocketException, IOException {
      byte[] data = PacketFactory.createMagicPacket(etheraddress);
      DatagramPacket packet = new DatagramPacket(data, data.length, inetaddress, 7);
      DatagramSocket socket = new DatagramSocket();
      Throwable var5 = null;

      try {
         socket.send(packet);
      } catch (Throwable var14) {
         var5 = var14;
         throw var14;
      } finally {
         if (socket != null) {
            if (var5 != null) {
               try {
                  socket.close();
               } catch (Throwable var13) {
                  var5.addSuppressed(var13);
               }
            } else {
               socket.close();
            }
         }
      }
   }
}

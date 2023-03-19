package js.java.tools.wol;

public class PacketFactory {
   public PacketFactory() {
      super();
   }

   public static byte[] createMagicPacket(EthernetAddress address) {
      byte[] packet = new byte[102];
      packet[0] = -1;
      packet[1] = -1;
      packet[2] = -1;
      packet[3] = -1;
      packet[4] = -1;
      packet[5] = -1;
      byte[] address_bytes = address.getAddressAsBytes();

      for(int outer_loop = 1; outer_loop < 17; ++outer_loop) {
         for(int inner_loop = 0; inner_loop < 6; ++inner_loop) {
            packet[outer_loop * 6 + inner_loop] = address_bytes[inner_loop];
         }
      }

      return packet;
   }
}

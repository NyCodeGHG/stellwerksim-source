package js.java.tools.wol;

import java.io.Serializable;
import java.util.StringTokenizer;

public class EthernetAddress implements Serializable {
   private int[] bytes;

   public EthernetAddress(String address) throws BadEthernetAddressException {
      if (address != null && address.length() == 17) {
         this.bytes = new int[6];
         StringTokenizer st = new StringTokenizer(address, ":");
         int index = 0;

         while (st.hasMoreTokens()) {
            String current_token = st.nextToken();

            try {
               this.bytes[index++] = Integer.parseInt(current_token, 16);
            } catch (NumberFormatException var6) {
               throw new BadEthernetAddressException(address);
            }
         }
      } else {
         throw new BadEthernetAddressException(address);
      }
   }

   public String getAddressAsString() {
      StringBuilder buffer = new StringBuilder();

      for (int loop = 0; loop < this.bytes.length; loop++) {
         buffer.append(Integer.toHexString(this.bytes[loop]));
         buffer.append("-");
      }

      buffer.setLength(buffer.length() - 1);
      return buffer.toString();
   }

   public byte[] getAddressAsBytes() {
      byte[] temp_bytes = new byte[6];

      for (int loop = 0; loop < this.bytes.length; loop++) {
         temp_bytes[loop] = (byte)this.bytes[loop];
      }

      return temp_bytes;
   }
}

package js.java.tools;

import de.deltaga.serial.Base64;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

public class imageencoder {
   public static void encode(BufferedImage img, OutputStream out) throws IOException {
      Iterator<ImageWriter> iwit = ImageIO.getImageWritersByFormatName("jpeg");
      ImageWriter writer = (ImageWriter)iwit.next();
      if (writer == null) {
         ImageIO.write(img, "jpeg", out);
      } else {
         ImageWriteParam iwp = writer.getDefaultWriteParam();
         iwp.setCompressionMode(2);
         iwp.setCompressionQuality(0.75F);
         writer.setOutput(new MemoryCacheImageOutputStream(out));
         writer.write(null, new IIOImage(img, null, null), iwp);
         writer.dispose();
      }
   }

   public static void encode(imagesaver jpanel, OutputStream out) throws IOException {
      Dimension d = jpanel.getSaveSize();
      int w = (int)d.getWidth();
      int h = (int)d.getHeight();
      BufferedImage bi = new BufferedImage(w, h, 1);
      Graphics2D big2d = bi.createGraphics();
      big2d.setBackground(Color.WHITE);
      big2d.clearRect(0, 0, w, h);
      jpanel.paintSave(big2d);
      encode(bi, out);
   }

   public static String encode64(imagesaver jpanel) throws IOException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      encode(jpanel, out);
      return Base64.encodeBytes(out.toByteArray());
   }

   public static String encodeurl(imagesaver jpanel) throws IOException {
      return TextHelper.urlEncode(encode64(jpanel));
   }
}

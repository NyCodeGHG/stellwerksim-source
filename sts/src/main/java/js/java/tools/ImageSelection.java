package js.java.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageSelection implements Transferable {
   private Image image;

   public static void setClipboard(Image image) {
      ImageSelection imgSel = new ImageSelection(image);
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
   }

   public static void setClipboard(imagesaver jpanel) {
      Dimension d = jpanel.getSaveSize();
      int w = (int)d.getWidth();
      int h = (int)d.getHeight();
      BufferedImage bi = new BufferedImage(w, h, 1);
      Graphics2D big2d = bi.createGraphics();
      big2d.setBackground(Color.WHITE);
      big2d.clearRect(0, 0, w, h);
      jpanel.paintSave(big2d);
      ImageSelection imgSel = new ImageSelection(bi);
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
   }

   private ImageSelection(Image image) {
      this.image = image;
   }

   public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[]{DataFlavor.imageFlavor};
   }

   public boolean isDataFlavorSupported(DataFlavor flavor) {
      return DataFlavor.imageFlavor.equals(flavor);
   }

   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
      if (!DataFlavor.imageFlavor.equals(flavor)) {
         throw new UnsupportedFlavorException(flavor);
      } else {
         return this.image;
      }
   }
}

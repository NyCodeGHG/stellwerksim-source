package js.java.tools.gui.renderer;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import js.java.tools.ColorText;
import js.java.tools.ColorTextIcon;

public class ComboColorRenderer extends JLabel implements ListCellRenderer {
   ColorTextIcon cti = new ColorTextIcon();

   public ComboColorRenderer() {
      this.setOpaque(true);
      this.setHorizontalAlignment(2);
      this.setVerticalAlignment(0);
   }

   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      if (index == -1) {
         this.setOpaque(false);
      } else if (isSelected) {
         this.setBackground(list.getSelectionBackground());
         this.setForeground(list.getSelectionForeground());
         this.setOpaque(true);
      } else {
         this.setBackground(list.getBackground());
         this.setForeground(list.getForeground());
         this.setOpaque(true);
      }

      if (value != null) {
         if (value instanceof ColorText) {
            ColorText ct = (ColorText)value;
            if (ct.getBGColor() != null) {
               if (list.getFixedCellHeight() < this.cti.getIconHeight()) {
                  int h = list.getFixedCellHeight() - 2;
                  if (h > 1) {
                     this.cti = new ColorTextIcon(h, h);
                  }
               }

               this.cti.setText(ct);
               this.setIcon(this.cti);
            } else {
               this.setIcon(null);
            }

            this.setFont(list.getFont());
            this.setText(ct.getText());
         } else {
            this.setIcon(null);
            this.setText((String)value);
         }
      } else {
         this.setIcon(null);
         this.setText("");
      }

      return this;
   }
}

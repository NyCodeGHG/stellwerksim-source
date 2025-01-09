package js.java.isolate.sim.toolkit;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import js.java.isolate.sim.zug.zug;
import js.java.isolate.sim.zug.zugMeasure;

public class ComboZugRenderer extends JLabel implements ListCellRenderer {
   public ComboZugRenderer() {
      this.setOpaque(true);
      this.setHorizontalAlignment(2);
      this.setVerticalAlignment(0);
   }

   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      if (isSelected) {
         this.setBackground(list.getSelectionBackground());
         this.setForeground(list.getSelectionForeground());
      } else {
         this.setBackground(list.getBackground());
         this.setForeground(list.getForeground());
      }

      if (value != null) {
         zug z = null;
         if (value instanceof zug) {
            z = (zug)value;
         } else if (value instanceof zugMeasure) {
            z = ((zugMeasure)value).getZug();
         }

         if (z != null) {
            this.setFont(list.getFont());
            this.setText(z.getName() + " (" + z.getZID() + ")");
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

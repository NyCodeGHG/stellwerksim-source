package js.java.isolate.sim.toolkit;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisTypContainer;

public class ComboGleisRenderer extends JLabel implements ListCellRenderer {
   public ComboGleisRenderer() {
      this.setOpaque(true);
      this.setHorizontalAlignment(2);
      this.setVerticalAlignment(0);
   }

   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      this.setFont(list.getFont());
      if (isSelected) {
         this.setBackground(list.getSelectionBackground());
         this.setForeground(list.getSelectionForeground());
      } else {
         this.setBackground(list.getBackground());
         this.setForeground(list.getForeground());
      }

      if (value != null) {
         if (value instanceof gleis) {
            gleis gl = (gleis)value;
            String sw = "";
            if (gl.typAllowesSWwertedit()) {
               sw = gl.getSWWert();
               if (sw != null && !sw.isEmpty()) {
                  sw = " -- " + sw;
               } else {
                  sw = "";
               }
            }

            this.setText(gl.getENR() + " (" + gleisTypContainer.getInstance().getTypElementName(gl) + sw + ")");
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

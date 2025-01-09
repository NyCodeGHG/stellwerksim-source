package js.java.isolate.sim.toolkit;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import js.java.isolate.sim.eventsys.eventContainer;

public class ComboEventRenderer extends JLabel implements ListCellRenderer {
   public ComboEventRenderer() {
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
         if (value instanceof eventContainer) {
            eventContainer e = (eventContainer)value;
            this.setFont(list.getFont());
            this.setText(e.getName() + " (" + e.getTyp() + ")");
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

package js.java.isolate.sim.toolkit;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;

public class eventMenuRenderer extends JLabel implements ListCellRenderer {
   private JPanel pan = new JPanel();
   private JSeparator sep = new JSeparator(0);
   private JLabel lab = new JLabel();

   public eventMenuRenderer() {
      this.setOpaque(true);
      this.setHorizontalAlignment(2);
      this.setVerticalAlignment(0);
      this.lab.setOpaque(true);
      this.lab.setHorizontalAlignment(2);
      this.lab.setVerticalAlignment(0);
      BorderLayout b = new BorderLayout();
      b.setVgap(5);
      this.pan.setLayout(b);
      this.pan.add(this.sep, "South");
      this.pan.add(this.lab, "Center");
   }

   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      Component ret = this;
      if (isSelected) {
         this.setBackground(list.getSelectionBackground());
         this.setForeground(list.getSelectionForeground());
         this.lab.setBackground(list.getSelectionBackground());
         this.lab.setForeground(list.getSelectionForeground());
      } else {
         this.setBackground(list.getBackground());
         this.setForeground(list.getForeground());
         this.lab.setBackground(list.getBackground());
         this.lab.setForeground(list.getForeground());
      }

      if (value != null) {
         if (value instanceof String) {
            this.setText((String)value);
         } else if (value instanceof specialEntry) {
            if (((specialEntry)value).special) {
               this.pan.setBackground(list.getBackground());
               this.pan.setForeground(list.getForeground());
               this.lab.setText(value.toString());
               ret = this.pan;
            } else {
               this.setText(value.toString());
            }
         } else {
            this.setText(value.toString());
         }
      } else {
         this.setIcon(null);
         this.setText("");
      }

      return ret;
   }
}

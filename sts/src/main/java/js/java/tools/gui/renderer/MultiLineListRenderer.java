package js.java.tools.gui.renderer;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;

public class MultiLineListRenderer extends DefaultListCellRenderer {
   private JLabel txtfield = new JLabel();
   private JPanel panel = new JPanel();
   private JSeparator sep = new JSeparator();

   public MultiLineListRenderer() {
      this.txtfield.setOpaque(true);
      this.panel.setOpaque(true);
      this.panel.setLayout(new BorderLayout());
      this.panel.add(this.txtfield, "Center");
   }

   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      if (isSelected) {
         this.panel.setBackground(list.getSelectionBackground());
         this.panel.setForeground(list.getSelectionForeground());
         this.txtfield.setBackground(list.getSelectionBackground());
         this.txtfield.setForeground(list.getSelectionForeground());
      } else {
         this.panel.setBackground(list.getBackground());
         this.panel.setForeground(list.getForeground());
         this.txtfield.setBackground(list.getBackground());
         this.txtfield.setForeground(list.getForeground());
      }

      if (value != null) {
         this.txtfield.setText("<html>" + value.toString() + "</html>");
      } else {
         this.txtfield.setText("");
      }

      return this.panel;
   }
}

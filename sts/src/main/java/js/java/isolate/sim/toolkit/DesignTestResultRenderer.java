package js.java.isolate.sim.toolkit;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import js.java.isolate.sim.dtest.dtestresult;

public class DesignTestResultRenderer extends DefaultListCellRenderer {
   private JLabel txtfield = new JLabel();
   private JLabel iconfield = new JLabel();
   private JPanel panel = new JPanel();
   private JSeparator sep = new JSeparator();

   public DesignTestResultRenderer() {
      this.txtfield.setOpaque(true);
      this.panel.setOpaque(true);
      this.panel.setLayout(new BorderLayout());
      this.panel.add(this.txtfield, "Center");
      this.panel.add(this.iconfield, "West");
      this.panel.add(this.sep, "South");
   }

   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      if (isSelected) {
         this.panel.setBackground(list.getSelectionBackground());
         this.panel.setForeground(list.getSelectionForeground());
         this.txtfield.setBackground(list.getSelectionBackground());
         this.txtfield.setForeground(list.getSelectionForeground());
         this.iconfield.setBackground(list.getSelectionBackground());
         this.iconfield.setForeground(list.getSelectionForeground());
      } else {
         this.panel.setBackground(list.getBackground());
         this.panel.setForeground(list.getForeground());
         this.txtfield.setBackground(list.getBackground());
         this.txtfield.setForeground(list.getForeground());
         this.iconfield.setBackground(list.getBackground());
         this.iconfield.setForeground(list.getForeground());
      }

      if (value instanceof dtestresult) {
         dtestresult d = (dtestresult)value;
         String n;
         switch (d.getRank()) {
            case 0:
            default:
               n = "info32.png";
               break;
            case 1:
               n = "warning32.png";
               break;
            case 2:
               n = "error32.png";
         }

         try {
            this.iconfield.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/" + n)));
         } catch (Exception var9) {
            System.out.println("Not an icon: " + var9.getMessage());
         }

         this.txtfield.setText("<html>" + d.getText() + "</html>");
         return this.panel;
      } else {
         return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      }
   }
}

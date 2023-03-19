package js.java.isolate.landkarteneditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class itemListRenderer extends JPanel implements ListCellRenderer {
   private Icon nodeIcon;
   private Icon connectIcon;
   private JLabel left;
   private JLabel right;

   public itemListRenderer() {
      super();
      this.setOpaque(true);
      this.setLayout(new BorderLayout());
      this.nodeIcon = new ImageIcon(this.getClass().getResource("/js/java/tools/resources/node16.png"));
      this.connectIcon = new ImageIcon(this.getClass().getResource("/js/java/tools/resources/connect16.png"));
      this.left = new JLabel("");
      this.left.setOpaque(true);
      this.left.setHorizontalAlignment(2);
      this.left.setVerticalAlignment(0);
      this.right = new JLabel("");
      this.right.setOpaque(true);
      this.right.setHorizontalAlignment(4);
      this.right.setVerticalAlignment(0);
      this.add(this.left, "Center");
      this.add(this.right, "East");
   }

   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      this.setFont(list.getFont());
      if (isSelected) {
         this.left.setBackground(list.getSelectionBackground());
         this.left.setForeground(list.getSelectionForeground());
         this.right.setBackground(list.getSelectionBackground());
         this.right.setForeground(list.getSelectionForeground());
      } else {
         this.left.setBackground(list.getBackground());
         this.left.setForeground(list.getForeground());
         this.right.setBackground(list.getBackground());
         this.right.setForeground(list.getForeground());
      }

      this.right.setText("");
      if (value != null) {
         if (value instanceof knoten) {
            this.left.setIcon(this.nodeIcon);
            this.right.setText(((knoten)value).extraString());
         } else if (value instanceof verbindung) {
            this.left.setIcon(this.connectIcon);
            this.right.setText(((verbindung)value).extraString());
         } else {
            this.left.setIcon(null);
         }

         this.left.setText(value.toString());
      } else {
         this.left.setIcon(null);
         this.left.setText("");
      }

      return this;
   }

   public void setFont(Font f) {
      super.setFont(f);
      if (this.left != null) {
         this.left.setFont(f);
      }

      if (this.right != null) {
         this.right.setFont(f);
      }
   }

   public Dimension getMinimumSize() {
      return this.getPreferredSize();
   }

   public Dimension getPreferredSize() {
      Dimension d = super.getPreferredSize();
      d.height += 6;
      return d;
   }
}

package js.java.tools.gui.table;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;
import js.java.tools.ColorText;

public class ColorRenderer extends JLabel implements TableCellRenderer {
   private MatteBorder[] unselectedBorder = new MatteBorder[]{null, null};
   private MatteBorder[] selectedBorder = new MatteBorder[]{null, null};
   private boolean isBordered = true;
   private Color[] bgcolors = new Color[]{new Color(255, 255, 255), new Color(255, 255, 238)};
   private Color[] bgcolors3D = new Color[]{new Color(238, 238, 238), new Color(238, 238, 221)};
   private boolean renderSpecial = false;

   public ColorRenderer() {
      super();
      this.setOpaque(true);
   }

   public ColorRenderer(boolean sp) {
      this();
      this.renderSpecial = sp;
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      int cc = row % 2;
      Color bgcolor = table.getSelectionBackground();
      if (!isSelected) {
         bgcolor = this.bgcolors[cc];
      }

      this.setFont(table.getFont());
      this.setForeground(table.getForeground());
      if (value instanceof ColorText) {
         ColorText ct = (ColorText)value;
         if (ct.getBGColor() != null) {
            this.setBackground(ct.getBGColor());
            this.setForeground(ct.getFGColor());
         } else {
            if (this.renderSpecial && value instanceof ColorText && ((ColorText)value).isSpecial()) {
               this.setBackground(this.bgcolors3D[cc]);
            } else {
               this.setBackground(this.bgcolors[cc]);
            }

            this.setForeground(Color.BLACK);
         }

         this.setText(ct.getText());
      } else if (value != null) {
         this.setText(value.toString());
         this.setBackground(this.bgcolors[cc]);
      } else {
         this.setBackground(table.getBackground());
         this.setText("");
      }

      if (this.isBordered) {
         if (isSelected) {
            if (this.selectedBorder[cc] == null) {
               this.selectedBorder[cc] = BorderFactory.createMatteBorder(2, 4, 2, 4, bgcolor);
            }

            this.setBorder(this.selectedBorder[cc]);
         } else if (this.renderSpecial && value instanceof ColorText && ((ColorText)value).isSpecial()) {
            this.setBorder(
               BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createMatteBorder(0, 2, 0, 2, this.bgcolors3D[cc]))
            );
         } else {
            if (this.unselectedBorder[cc] == null) {
               this.unselectedBorder[cc] = BorderFactory.createMatteBorder(2, 4, 2, 4, bgcolor);
            }

            this.setBorder(this.unselectedBorder[cc]);
         }
      }

      return this;
   }
}

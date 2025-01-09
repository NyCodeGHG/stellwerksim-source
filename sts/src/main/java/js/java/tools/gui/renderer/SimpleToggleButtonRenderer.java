package js.java.tools.gui.renderer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import js.java.tools.gui.SimpleToggleButton;

public class SimpleToggleButtonRenderer extends SimpleToggleButton implements ListCellRenderer {
   private int buttonWidth = 0;

   public SimpleToggleButtonRenderer() {
      this.setMargin(new Insets(0, 1, 1, 1));
   }

   public SimpleToggleButtonRenderer(int buttonwidth) {
      this.setMargin(new Insets(0, 1, 1, 1));
      this.buttonWidth = buttonwidth;
   }

   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      this.setFont(list.getFont());
      this.setText(value.toString());
      if (value instanceof Icon) {
         this.setIcon((Icon)value);
      } else {
         this.setIcon(null);
      }

      this.setSelected(isSelected);
      this.setHasCellFocus(cellHasFocus);
      this.setAlternativColor(index % 2 == 0);
      return this;
   }

   @Override
   public Dimension getMinimumSize() {
      return this.getPreferredSize();
   }

   @Override
   public Dimension getPreferredSize() {
      Dimension d = super.getPreferredSize();
      d.height += 2;
      if (this.buttonWidth > 0) {
         d.width = this.buttonWidth;
      }

      return d;
   }
}

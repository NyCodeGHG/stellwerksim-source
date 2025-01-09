package js.java.isolate.sim.toolkit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public class fahrstrasseListRenderer extends JPanel implements ListCellRenderer {
   public static final String FSTYPE_AUTODISABLED = "pause16.png";
   public static final String FSTYPE_AUTOENABLED = "accept16.png";
   public static final String FSTYPE_DEFAULT = null;
   public static final String FSTYPE_DELETED = "trash16.png";
   public static final String FSTYPE_RFONLY = "disconnect16.png";
   public static final String FSTYPE_FSONLY = "subway16.png";
   private JLabel label;
   private JLabel icon;

   protected fahrstrasseListRenderer(boolean withSep) {
      this.setOpaque(true);
      this.setLayout(new BorderLayout());
      this.label = new JLabel();
      this.label.setOpaque(true);
      this.add(this.label, "Center");
      this.icon = new JLabel();
      this.add(this.icon, "West");
      if (withSep) {
         this.add(new JSeparator(), "South");
      }

      this.setMinimumSize(new Dimension(110, 12));
      this.setPreferredSize(new Dimension(110, 18));
      this.icon.setMinimumSize(new Dimension(16, 16));
      this.icon.setPreferredSize(new Dimension(16, 16));
   }

   public fahrstrasseListRenderer() {
      this(true);
   }

   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      Color fcol;
      Color bcol;
      if (isSelected) {
         bcol = list.getSelectionBackground();
         fcol = list.getSelectionForeground();
      } else {
         bcol = list.getBackground();
         fcol = list.getForeground();
      }

      this.prepareCellRenderer(value, isSelected, cellHasFocus, fcol, bcol);
      return this;
   }

   protected void prepareCellRenderer(Object value, boolean isSelected, boolean cellHasFocus, Color foreground, Color background) {
      this.setBackground(background);
      this.setForeground(foreground);
      this.label.setBackground(background);
      this.label.setForeground(foreground);
      if (value instanceof fahrstrasse) {
         String n = null;
         fahrstrasse f = (fahrstrasse)value;
         String v = " " + f.getName();
         switch (f.getExtend().getFSType()) {
            case 0:
               n = FSTYPE_DEFAULT;
               break;
            case 1:
               n = "pause16.png";
               break;
            case 2:
               n = "accept16.png";
            case 3:
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            default:
               break;
            case 4:
               n = "trash16.png";
               break;
            case 8:
               n = "disconnect16.png";
               break;
            case 16:
               n = "subway16.png";
         }

         this.label.setText(v);
         if (n != null) {
            this.icon.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/" + n)));
         } else {
            this.icon.setIcon(null);
         }
      } else {
         this.icon.setIcon(null);
         this.label.setText(value.toString());
      }
   }
}

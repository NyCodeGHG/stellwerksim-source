package js.java.isolate.statusapplet.karte;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

@Deprecated
class kartenzuglistrenderer extends DefaultListCellRenderer implements TableCellRenderer {
   private JLabel lab = new JLabel();
   private Color[][] col = new Color[2][5];
   private int dcol = 0;

   kartenzuglistrenderer() {
      super();

      for(int i = 0; i < 5; ++i) {
         this.col[0][i] = new Color(255, 255, 255 - i * 8);
         this.col[1][i] = new Color(238, 238, 238 - i * 8);
      }

      this.lab.setOpaque(true);
   }

   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      if (value instanceof karten_zug) {
         String r = "";
         karten_zug f = (karten_zug)value;
         String v = r
            + f.getSpezialName()
            + " "
            + (f.laststopdone ? "<i>" : "")
            + f.getAn()
            + "-"
            + f.getAb()
            + " ("
            + (f.verspaetung > 0 ? "+" : "")
            + f.verspaetung
            + ")"
            + (f.laststopdone ? "</i>" : "");
         return super.getListCellRendererComponent(list, v, index, isSelected, cellHasFocus);
      } else {
         return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      }
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      this.lab.setFont(table.getFont());
      String text = "<html><br></html>";

      try {
         karten_zug z = (karten_zug)value;
         if (z != null) {
            if (column == 0) {
               text = "<html><b>"
                  + (z.visible ? "<u>" : "")
                  + z.getSpezialName()
                  + (z.visible ? "</u>" : "")
                  + "</b><br>&nbsp;"
                  + (z.laststopdone ? "<i>" : "")
                  + z.getAn()
                  + " - "
                  + z.getAb()
                  + " ("
                  + (z.verspaetung > 0 ? "+" : "")
                  + z.verspaetung
                  + ")"
                  + (z.laststopdone ? "</i>" : "")
                  + "</html>";
            } else {
               text = "<html><i>von</i> " + z.getEin() + "<br><i>nach</i> " + z.getAus() + "</html>";
            }

            int c = z.changedDelay() / 45;
            if (c > 4) {
               c = 4;
            }

            this.dcol = 4 - c;
         }
      } catch (Exception var10) {
      }

      this.lab.setText(text);
      this.lab.setBackground(this.col[row % 2][this.dcol]);
      return this.lab;
   }
}

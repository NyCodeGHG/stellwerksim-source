package js.java.isolate.statusapplet.players;

import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import js.java.tools.gui.dataTransferDisplay.LedComponent;
import js.java.tools.gui.dataTransferDisplay.LedComponent.LEDCOLOR;

class playerslistrenderer implements TableCellRenderer {
   private final playersPanel pp;
   private final JLabel lab = new JLabel();
   private final LedComponent stLed = new LedComponent();
   private final LedComponent roLed = new LedComponent(LEDCOLOR.YELLOW);
   private final LedComponent skLed = new LedComponent(LEDCOLOR.RED);
   private final JPanel umPanel = new JPanel(new GridLayout(1, 0));

   playerslistrenderer(playersPanel _pp) {
      super();
      this.pp = _pp;
      this.lab.setOpaque(true);
      this.umPanel.add(this.roLed);
      this.umPanel.add(this.skLed);
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component ret = this.lab;
      this.lab.setFont(table.getFont());
      this.lab.setBackground(table.getBackground());
      this.lab.setForeground(table.getForeground());
      this.lab.setVerticalTextPosition(1);
      this.lab.setVerticalAlignment(1);
      this.lab.setHorizontalAlignment(2);
      this.umPanel.setBackground(table.getBackground());
      String text = "";

      try {
         players_aid z = (players_aid)value;
         if (z != null) {
            switch(column) {
               case 0:
                  text = "<b>" + z.name + "</b>";
                  break;
               case 1:
                  if (z.spieler != null) {
                     text = z.spieler;
                  }
                  break;
               case 2:
                  this.stLed.setLed(z.spieler != null);
                  if (z.getStoerungsCount() == 0) {
                     this.stLed.setColor(LEDCOLOR.GREEN);
                  } else if (z.getStoerungsCount() > 5) {
                     this.stLed.setColor(LEDCOLOR.RED);
                  } else {
                     this.stLed.setColor(LEDCOLOR.YELLOW);
                  }

                  ret = this.stLed;
                  break;
               case 3:
                  this.roLed.setLed(this.pp.hatAidUmleitung(z));
                  this.skLed.setLed(this.pp.wirdAidUmfahren(z));
                  ret = this.umPanel;
                  break;
               case 4:
                  this.lab.setBackground(z.heatColor);
                  text = Long.toString(z.heat);
                  this.lab.setHorizontalAlignment(4);
                  break;
               case 5:
                  for(players_zug pz : z.zuege) {
                     if (pz.visible) {
                        if (!text.isEmpty()) {
                           text = text + ", ";
                        }

                        text = text
                           + (pz.laststopdone ? "<i>" : "")
                           + pz.getSpezialName()
                           + " ("
                           + (pz.verspaetung > 0 ? "+" : "")
                           + pz.verspaetung
                           + ")"
                           + (pz.laststopdone ? "</i>" : "");
                     }
                  }

                  text = "<font size='-3'>" + text + "</font>";
            }
         }
      } catch (Exception var12) {
      }

      this.lab.setText("<html>" + text + "</html>");
      return ret;
   }
}

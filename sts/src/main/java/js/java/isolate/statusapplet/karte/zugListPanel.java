package js.java.isolate.statusapplet.karte;

import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

public class zugListPanel extends JTable {
   private kartePanel zp;
   final CopyOnWriteArrayList<karten_zug> zuegelist = new CopyOnWriteArrayList();
   private statisticsPanel stp;
   private final zugListPanel.Model model = new zugListPanel.Model();

   public zugListPanel() {
      this.setModel(this.model);
      this.setSelectionMode(0);
      this.getColumnModel().getColumn(0).setMaxWidth(80);
      this.getColumnModel().getColumn(1).setMaxWidth(200);
      this.getColumnModel().getColumn(2).setMaxWidth(60);
      ListSelectionModel rowSM = this.getSelectionModel();
      rowSM.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
               ListSelectionModel lsm = (ListSelectionModel)e.getSource();
               if (lsm.isSelectionEmpty()) {
                  zugListPanel.this.zp.showZugWeg(null);
               } else {
                  int selectedRow = zugListPanel.this.convertRowIndexToModel(lsm.getMinSelectionIndex());
                  zugListPanel.this.zp.showZugWeg((karten_zug)zugListPanel.this.zuegelist.get(selectedRow));
               }
            }
         }
      });
      TableRowSorter<zugListPanel.Model> sorter = new TableRowSorter(this.model);
      sorter.setComparator(2, new Comparator<String>() {
         public int compare(String o1, String o2) {
            int i1 = Integer.parseInt(o1);
            int i2 = Integer.parseInt(o2);
            return i1 - i2;
         }
      });
      this.setRowSorter(sorter);
   }

   public void setKartePanel(kartePanel kp) {
      this.zp = kp;
   }

   public void setStatisticsPanel(statisticsPanel stp) {
      this.stp = stp;
   }

   void modifiedData() {
      this.model.fireTableDataChanged();
      this.stp.updateDelayStats(this);
   }

   void modifiedData(karten_zug z) {
      int i = this.zuegelist.indexOf(z, 0);
      if (i >= 0) {
         this.model.fireTableRowsUpdated(i, i);
      }

      this.stp.updateDelayStats(this);
   }

   double[] verspätungsDurchschnitt() {
      double[] ret = new double[]{0.0, 0.0, 0.0};
      int c = 0;
      double sum = 0.0;
      int min = Integer.MAX_VALUE;
      int max = 0;

      for (karten_zug z : this.zuegelist) {
         if (z.visible) {
            sum += (double)z.verspaetung;
            c++;
         }

         if (z.verspaetung > 0) {
            min = Math.min(min, z.verspaetung);
         }

         max = Math.max(max, z.verspaetung);
      }

      if (c > 0) {
         ret[0] = sum / (double)c;
      }

      if (min == Integer.MAX_VALUE) {
         min = 0;
      }

      ret[1] = (double)max;
      ret[2] = (double)min;
      return ret;
   }

   private class Model extends AbstractTableModel {
      private Model() {
      }

      public int getRowCount() {
         return zugListPanel.this.zuegelist.size();
      }

      public int getColumnCount() {
         return 3;
      }

      public Object getValueAt(int rowIndex, int columnIndex) {
         karten_zug kz = (karten_zug)zugListPanel.this.zuegelist.get(rowIndex);
         switch (columnIndex) {
            case 0:
               return kz.getSpezialName();
            case 1:
               if (zugListPanel.this.zp.aids != null) {
                  karten_container s = (karten_container)zugListPanel.this.zp.aids.get(kz.getAid());
                  if (s != null) {
                     return s.namen;
                  }
               }

               return "AID " + kz.getAid();
            case 2:
               return kz.getVerspaetung();
            default:
               return "";
         }
      }

      public Class<?> getColumnClass(int columnIndex) {
         return String.class;
      }

      public String getColumnName(int column) {
         switch (column) {
            case 0:
               return "Zug";
            case 1:
               return "Stellwerk";
            case 2:
               return "Verspätung";
            default:
               return "";
         }
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
         return false;
      }
   }
}

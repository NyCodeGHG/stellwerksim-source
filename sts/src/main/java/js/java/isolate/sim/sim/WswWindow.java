package js.java.isolate.sim.sim;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import js.java.isolate.sim.zug.zug;
import js.java.isolate.sim.zug.zugPositionListener;
import js.java.tools.gui.ViewTooltips;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.WindowStateSaver.STORESTATES;

public class WswWindow extends JDialog implements zugPositionListener {
   private final zugUndPlanPanel zuege;
   private final DefaultTableModel model = new DefaultTableModel(new Object[0][], new String[]{"Zug", "Position"}) {
      Class[] types = new Class[]{WswWindow.zugEntry.class, String.class};

      public Class getColumnClass(int columnIndex) {
         return this.types[columnIndex];
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
         return false;
      }
   };
   private JLabel jLabel1;
   private JScrollPane jScrollPane1;
   private JTable jTable1;

   public WswWindow(zugUndPlanPanel parent) {
      super(SwingUtilities.getWindowAncestor(parent));
      this.zuege = parent;
      this.initComponents();
      this.jTable1.setRowHeight(this.jTable1.getRowHeight() * 2);
      ViewTooltips.register(this.jTable1);

      for(zug z : this.zuege.getZugList()) {
         if (z.isVisible()) {
            WswWindow.zugEntry ze = new WswWindow.zugEntry(z);
            Object[] rowData = new Object[]{ze, "Anfrage läuft"};
            this.model.addRow(rowData);
            z.meldePosition(this);
         }
      }

      this.setName(this.getClass().getSimpleName());
      new WindowStateSaver(this, STORESTATES.LOCATION_AND_SIZE);
   }

   @Override
   public void melde(final zug z, final String text) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            for(int i = 0; i < WswWindow.this.model.getRowCount(); ++i) {
               WswWindow.zugEntry ze = (WswWindow.zugEntry)WswWindow.this.model.getValueAt(i, 0);
               if (ze.z.getZID_num() == z.getZID_num()) {
                  WswWindow.this.model.setValueAt("<html>" + text + "</html>", i, 1);
                  break;
               }
            }
         }
      });
   }

   private void initComponents() {
      this.jScrollPane1 = new JScrollPane();
      this.jTable1 = new JTable();
      this.jLabel1 = new JLabel();
      this.setDefaultCloseOperation(2);
      this.setTitle("Was steht wo?");
      this.setLocationByPlatform(true);
      this.jTable1.setAutoCreateRowSorter(true);
      this.jTable1.setModel(this.model);
      this.jTable1.setAutoResizeMode(1);
      this.jScrollPane1.setViewportView(this.jTable1);
      this.getContentPane().add(this.jScrollPane1, "Center");
      this.jLabel1
         .setText(
            "<html>Dies schickt eine Funk-Anfrage zur Positionsmeldung an alle Züge auf dem Stelltisch. Zu häufige Anfrage führt zu den bekannten Reaktionen.</html>"
         );
      this.jLabel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      this.getContentPane().add(this.jLabel1, "First");
      this.pack();
   }

   private static class zugEntry implements Comparable<WswWindow.zugEntry> {
      final zug z;

      private zugEntry(zug z) {
         super();
         this.z = z;
      }

      public String toString() {
         return this.z.getSpezialName();
      }

      public int compareTo(WswWindow.zugEntry o) {
         return this.z.getSpezialName().compareToIgnoreCase(o.z.getSpezialName());
      }
   }
}

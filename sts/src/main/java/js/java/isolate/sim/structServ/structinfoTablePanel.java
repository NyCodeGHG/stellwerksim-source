package js.java.isolate.sim.structServ;

import java.awt.BorderLayout;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class structinfoTablePanel extends JPanel {
   private JTable infoTable;
   private JScrollPane jScrollPane2;

   public structinfoTablePanel() {
      this.initComponents();
   }

   public void add(Vector v, int line, int totallines) {
      DefaultTableModel tm = (DefaultTableModel)this.infoTable.getModel();
      tm.setRowCount(totallines);
      tm.setValueAt(v.elementAt(0), line, 0);
      tm.setValueAt(v.elementAt(1), line, 1);
   }

   public void add(Vector v) {
      DefaultTableModel tm = (DefaultTableModel)this.infoTable.getModel();
      tm.setRowCount(v.size() / 2);

      for (int i = 0; i < v.size(); i += 2) {
         tm.setValueAt(v.elementAt(i), i / 2, 0);
         tm.setValueAt(v.elementAt(i + 1), i / 2, 1);
      }
   }

   private void initComponents() {
      this.jScrollPane2 = new JScrollPane();
      this.infoTable = new JTable();
      this.setLayout(new BorderLayout());
      this.jScrollPane2.setBorder(BorderFactory.createTitledBorder("Objektdaten"));
      this.jScrollPane2.setVerticalScrollBarPolicy(22);
      this.infoTable.setModel(new DefaultTableModel(new Object[0][], new String[]{"Schlüssel", "Wert"}) {
         Class[] types = new Class[]{String.class, String.class};
         boolean[] canEdit = new boolean[]{false, false};

         public Class getColumnClass(int columnIndex) {
            return this.types[columnIndex];
         }

         public boolean isCellEditable(int rowIndex, int columnIndex) {
            return this.canEdit[columnIndex];
         }
      });
      this.jScrollPane2.setViewportView(this.infoTable);
      this.add(this.jScrollPane2, "Center");
   }
}

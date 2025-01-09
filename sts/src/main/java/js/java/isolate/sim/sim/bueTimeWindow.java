package js.java.isolate.sim.sim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;
import js.java.isolate.sim.gleis.gleis;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.WindowStateSaver.STORESTATES;

public class bueTimeWindow extends JDialog implements ActionListener {
   private final JCheckBoxMenuItem bueTimeWindowMenu;
   private final bueTimeWindow.BueTableModel model = new bueTimeWindow.BueTableModel();
   private final stellwerksim_main my_main;
   private final Timer timer = new Timer(1000, this);
   private JLabel jLabel1;
   private JScrollPane jScrollPane1;
   private JTable jTable1;

   bueTimeWindow(stellwerksim_main parent, JCheckBoxMenuItem bueTimeWindowMenu) {
      super(parent, false);
      this.my_main = parent;
      this.bueTimeWindowMenu = bueTimeWindowMenu;
      this.fillBÜs();
      this.initComponents();
      this.setVisible(true);
      this.timer.start();
      this.setName(this.getClass().getSimpleName());
      new WindowStateSaver(this, STORESTATES.LOCATION_AND_SIZE);
   }

   private void fillBÜs() {
      Iterator<gleis> it = this.my_main.getControl().getModel().findIterator(new Object[]{gleis.ALLE_BAHNÜBERGÄNGE});

      while (it.hasNext()) {
         gleis gl = (gleis)it.next();
         gleis bü = this.my_main.getControl().getModel().findFirst(new Object[]{gl.getENR(), gleis.ALLE_BAHNÜBERGÄNGE});
         this.model.addBue(bü);
      }
   }

   private String getTime(gleis bü) {
      String ret = "";
      if (!bü.getFluentData().isPowerOff()) {
         int c = (int)((System.currentTimeMillis() - bü.getFluentData().getStellungChangeTime()) / 1000L);
         String v = "";
         if (!bü.getElement().matches(gleis.ELEMENT_WBAHNÜBERGANG) && !bü.getFluentData().isGesperrt()) {
            if (c < 60 || bü.getFluentData().getStellung() == gleis.ST_BAHNÜBERGANG_GESCHLOSSEN) {
               v = Integer.toString(c / 5 * 5 + 5);
            }
         } else if (bü.getFluentData().getStellung() == gleis.ST_BAHNÜBERGANG_OFFEN) {
            c = (180 - c) / 5 * 5 + 5;
            if (c > 0) {
               v = "max " + Integer.toString(-c);
            }
         } else if (bü.getFluentData().getStellung() == gleis.ST_BAHNÜBERGANG_GESCHLOSSEN) {
            c = (180 - c) / 5 * 5 + 5;
            v = Integer.toString(-c);
         }

         ret = v;
      }

      return ret;
   }

   void close() {
      this.timer.stop();
   }

   public void actionPerformed(ActionEvent e) {
      this.model.refreshTimes();
   }

   private void initComponents() {
      this.jScrollPane1 = new JScrollPane();
      this.jTable1 = new JTable();
      this.jLabel1 = new JLabel();
      this.setDefaultCloseOperation(0);
      this.setTitle("BÜ Zeiten");
      this.setFocusable(false);
      this.setFocusableWindowState(false);
      this.setLocationByPlatform(true);
      this.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
      this.setMinimumSize(new Dimension(150, 150));
      this.setPreferredSize(new Dimension(200, 200));
      this.setType(Type.UTILITY);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent evt) {
            bueTimeWindow.this.formWindowClosing(evt);
         }
      });
      this.jTable1.setAutoCreateRowSorter(true);
      this.jTable1.setModel(this.model);
      this.jScrollPane1.setViewportView(this.jTable1);
      this.getContentPane().add(this.jScrollPane1, "Center");
      this.jLabel1.setBackground(new Color(0, 0, 0));
      this.jLabel1.setFont(this.jLabel1.getFont().deriveFont(this.jLabel1.getFont().getStyle() | 1));
      this.jLabel1.setForeground(new Color(255, 255, 255));
      this.jLabel1.setText("<html>zeigt Dauer geschlossen bzw. Restzeiten (negativ) für aktuelle Stellung</html>");
      this.jLabel1.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 5));
      this.jLabel1.setOpaque(true);
      this.getContentPane().add(this.jLabel1, "South");
      this.pack();
   }

   private void formWindowClosing(WindowEvent evt) {
      this.bueTimeWindowMenu.setSelected(false);
      this.close();
   }

   private class BueTableModel extends AbstractTableModel {
      private final String[] header = new String[]{"Name", "Dauer (s)"};
      private final Class[] types = new Class[]{String.class, String.class};
      private ArrayList<gleis> bueList = new ArrayList();

      private BueTableModel() {
      }

      public String getColumnName(int column) {
         return this.header[column];
      }

      public Class getColumnClass(int columnIndex) {
         return this.types[columnIndex];
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
         return false;
      }

      public int getRowCount() {
         return this.bueList.size();
      }

      public int getColumnCount() {
         return this.header.length;
      }

      public Object getValueAt(int rowIndex, int columnIndex) {
         gleis gl = (gleis)this.bueList.get(rowIndex);
         switch (columnIndex) {
            case 0:
               return gl.getElementName();
            case 1:
               return bueTimeWindow.this.getTime(gl);
            default:
               return "";
         }
      }

      public void addBue(gleis gl) {
         boolean found = false;

         for (gleis g : this.bueList) {
            if (g.getENR() == gl.getENR()) {
               found = true;
               break;
            }
         }

         if (!found) {
            this.bueList.add(gl);
            this.fireTableRowsInserted(this.bueList.size() - 1, this.bueList.size() - 1);
         }
      }

      void refreshTimes() {
         for (int i = 0; i < this.bueList.size(); i++) {
            this.fireTableCellUpdated(i, 1);
         }
      }
   }
}

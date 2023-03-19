package js.java.isolate.sim.sim;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map.Entry;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import js.java.tools.ColorText;
import js.java.tools.gui.table.CMap;
import js.java.tools.gui.table.ColorRenderer;
import js.java.tools.gui.table.JComponentCellEditor;
import js.java.tools.gui.table.JComponentCellRenderer;
import js.java.tools.gui.table.MultiColumnTableUI;

public class phonebookpanel extends JPanel {
   private DefaultTableModel nsorter;
   private final DefaultTableModel rsorter;
   private phonebook my_phone;
   private JScrollPane jScrollPane1;
   private JTable nachbarPhoneTable;
   private JTable regionPhoneTable;
   private JLabel statusLabel;

   public phonebookpanel(phonebook p) {
      super();
      this.my_phone = p;
      this.initComponents();
      this.nsorter = new DefaultTableModel(new Object[0][], new String[]{"Name", "Nummer", "Spieler", ""}) {
         Class[] types = new Class[]{ColorText.class, ColorText.class, ColorText.class, JButton.class};

         public Class getColumnClass(int columnIndex) {
            return this.types[columnIndex];
         }

         public boolean isCellEditable(int rowIndex, int columnIndex) {
            return this.getColumnClass(columnIndex) == JButton.class;
         }
      };
      this.nachbarPhoneTable.setDefaultRenderer(ColorText.class, new ColorRenderer());
      this.nachbarPhoneTable.setDefaultRenderer(JButton.class, new JComponentCellRenderer());
      this.nachbarPhoneTable.setDefaultEditor(JButton.class, new JComponentCellEditor());
      this.nachbarPhoneTable.setModel(this.nsorter);
      this.nachbarPhoneTable.getTableHeader().setToolTipText("Anklicken zum sortieren");
      this.nachbarPhoneTable.getColumnModel().getColumn(1).setPreferredWidth(70);
      this.nachbarPhoneTable.getColumnModel().getColumn(1).setMaxWidth(90);
      this.nachbarPhoneTable.getColumnModel().getColumn(3).setMaxWidth(14);
      this.rsorter = new DefaultTableModel(new Object[0][], new String[]{"", "Nummer", "", ""}) {
         Class[] types = new Class[]{String.class, String.class, String.class, JButton.class};

         public Class getColumnClass(int columnIndex) {
            return this.types[columnIndex];
         }

         public boolean isCellEditable(int rowIndex, int columnIndex) {
            return this.getColumnClass(columnIndex) == JButton.class;
         }
      };
      this.regionPhoneTable.setDefaultRenderer(ColorText.class, new ColorRenderer());
      this.regionPhoneTable.setDefaultRenderer(JButton.class, new JComponentCellRenderer());
      this.regionPhoneTable.setDefaultEditor(JButton.class, new JComponentCellEditor());
      this.regionPhoneTable.setModel(this.rsorter);
      this.regionPhoneTable.getColumnModel().getColumn(1).setPreferredWidth(70);
      this.regionPhoneTable.getColumnModel().getColumn(1).setMaxWidth(90);
      this.regionPhoneTable.getColumnModel().getColumn(3).setMaxWidth(14);
      this.regionPhoneTable.setUI(new MultiColumnTableUI(new phonebookpanel.regionPhoneTableCmap()));
      if (this.my_phone.getRegionTel() != null) {
         Object[] o = new Object[]{
            "Regionsraumrufnummer: ",
            this.my_phone.getRegionTel(),
            this.my_phone.getRegionName(),
            this.getButton(this.my_phone.getRegionTel(), this.regionPhoneTable)
         };
         this.rsorter.addRow(o);
      }

      if (this.my_phone.getOwn() != null) {
         Object[] o = new Object[]{"Eigene Stellwerksrufnummer: ", this.my_phone.getOwn()};
         this.rsorter.addRow(o);
      }

      if (this.my_phone.getUsertel() != null) {
         Object[] o = new Object[]{"feste Spieler Telefonnummer: ", this.my_phone.getUsertel()};
         this.rsorter.addRow(o);
      }

      if (this.my_phone.getAllgemeintel() != null && this.my_phone.getAllgemeinname() != null) {
         Object[] o = new Object[]{
            this.my_phone.getAllgemeinname() + ": ",
            this.my_phone.getAllgemeintel(),
            "",
            this.getButton(this.my_phone.getAllgemeintel(), this.regionPhoneTable)
         };
         this.rsorter.addRow(o);
      }

      Object[] o = new Object[]{"Störungshotline: ", "7863", "", this.getButton("7863", this.regionPhoneTable)};
      this.rsorter.addRow(o);

      for(Entry<String, String> e : this.my_phone.getAdditionals().entrySet()) {
         Object[] ox = new Object[]{(String)e.getKey() + ": ", e.getValue()};
         this.rsorter.addRow(ox);
      }

      this.my_phone.setGui(this);
      this.updateTel();
   }

   private JButton getButton(String tel, final JTable tab) {
      final JButton b = new JButton();
      b.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/phone16.png")));
      b.setMargin(new Insets(0, 0, 0, 0));
      b.setFocusPainted(false);
      b.setFocusable(false);
      b.setActionCommand(tel);
      if (this.my_phone.canCall()) {
         b.addActionListener(new ActionListener() {
            final JComponentCellEditor ed = (JComponentCellEditor)tab.getDefaultEditor(b.getClass());

            public void actionPerformed(ActionEvent e) {
               phonebookpanel.this.dialClick((JButton)e.getSource(), e.getActionCommand());
               this.ed.stopCellEditing();
            }
         });
         b.setToolTipText(tel + " anrufen");
      } else {
         b.setEnabled(false);
      }

      return b;
   }

   private void dialClick(JButton b, String n) {
      b.setEnabled(false);
      this.statusLabel.setText("Anrufen von " + n);
      this.my_phone.call(n);
      new phonebookpanel.enableTimer(b);
   }

   final void updateTel() {
      this.nsorter.setRowCount(0);

      for(phonebookentry e : this.my_phone) {
         Object[] o = new Object[]{new ColorText(e.stw), new ColorText(e.tel), new ColorText(e.user), this.getButton(e.tel, this.nachbarPhoneTable)};
         this.nsorter.addRow(o);
      }
   }

   private void initComponents() {
      this.regionPhoneTable = new JTable();
      this.jScrollPane1 = new JScrollPane();
      this.nachbarPhoneTable = new JTable();
      this.statusLabel = new JLabel();
      this.setLayout(new BorderLayout());
      this.regionPhoneTable.setFocusable(false);
      this.regionPhoneTable.setRequestFocusEnabled(false);
      this.regionPhoneTable.setRowSelectionAllowed(false);
      this.regionPhoneTable.setShowVerticalLines(false);
      this.add(this.regionPhoneTable, "North");
      this.nachbarPhoneTable.setAutoCreateRowSorter(true);
      this.nachbarPhoneTable.setFocusable(false);
      this.nachbarPhoneTable.setRequestFocusEnabled(false);
      this.nachbarPhoneTable.setRowSelectionAllowed(false);
      this.nachbarPhoneTable.setShowVerticalLines(false);
      this.jScrollPane1.setViewportView(this.nachbarPhoneTable);
      this.add(this.jScrollPane1, "Center");
      this.statusLabel.setText(" ");
      this.add(this.statusLabel, "South");
   }

   private class enableTimer extends Timer implements ActionListener {
      private JComponent c;

      enableTimer(JComponent c) {
         super(2000, null);
         this.c = c;
         this.addActionListener(this);
         this.setRepeats(false);
         this.start();
      }

      public void actionPerformed(ActionEvent e) {
         this.c.setEnabled(true);
         phonebookpanel.this.regionPhoneTable.repaint();
         phonebookpanel.this.nachbarPhoneTable.repaint();
         phonebookpanel.this.statusLabel.setText(" ");
      }
   }

   private class regionPhoneTableCmap implements CMap {
      private regionPhoneTableCmap() {
         super();
      }

      public int span(int row, int column) {
         if (column == 0 || column == 3) {
            return 1;
         } else {
            return row > 0 ? 2 : 1;
         }
      }

      public int visibleCell(int row, int column) {
         if (column == 0 || column == 3) {
            return column;
         } else {
            return row > 0 ? 1 : column;
         }
      }
   }
}

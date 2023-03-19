package js.java.isolate.sim.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.fahrstrasseevent;
import js.java.isolate.sim.eventsys.gleisevent;
import js.java.isolate.sim.eventsys.zugevent;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.GecSelectEvent;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.panels.actionevents.stoerungMessureEvent;
import js.java.isolate.sim.panels.actionevents.stoerungSelectedEvent;
import js.java.tools.actions.AbstractEvent;
import js.java.tools.gui.table.TableSorter;

public class stoerungListPanel extends basePanel {
   private final DefaultTableModel mod;
   private final TableSorter tmod;
   private JButton addButton;
   private JButton delButton;
   private JComboBox eventFilterCB;
   private JTable eventTable;
   private JLabel jLabel1;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JPanel jPanel3;
   private JScrollPane jScrollPane1;
   private JButton messureButton;
   private JButton noupdateButton;
   private JToggleButton updateButton;

   public stoerungListPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
      this.mod = new DefaultTableModel(new Object[0][], new String[]{"Art", "Name"}) {
         Class[] types = new Class[]{Object.class, String.class};
         boolean[] canEdit = new boolean[]{false, false};

         public Class getColumnClass(int columnIndex) {
            return this.types[columnIndex];
         }

         public boolean isCellEditable(int rowIndex, int columnIndex) {
            return this.canEdit[columnIndex];
         }
      };
      this.tmod = new TableSorter(this.mod);
      this.eventTable.setModel(this.tmod);
      ListSelectionModel rowSM = this.eventTable.getSelectionModel();
      rowSM.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            stoerungListPanel.this.eventTable_valueChanged(e);
         }
      });
      e.registerListener(10, this);
   }

   @Override
   public void action(AbstractEvent e) {
      if (e instanceof GecSelectEvent) {
         if (!this.updateButton.isSelected()) {
            if (this.eventFilterCB.getSelectedIndex() == 1) {
               this.updateOutput(true);
            } else {
               this.eventTable.getSelectionModel().clearSelection();
               this.my_main.interPanelCom(new stoerungSelectedEvent(null, false));
            }
         }
      } else if (e instanceof stoerungMessureEvent && this.updateButton.isSelected()) {
         this.updateButton.setSelected(false);
         this.updateButtonActionPerformed(null);
      }
   }

   @Override
   public void shown(String n, gecBase gec) {
      this.updateOutput(false);
      this.glbControl.getMode().addChangeListener(this);
   }

   private void eventTable_valueChanged(ListSelectionEvent e) {
      if (!e.getValueIsAdjusting()) {
         ListSelectionModel lsm = (ListSelectionModel)e.getSource();
         if (lsm.isSelectionEmpty()) {
            this.updateButton.setEnabled(false);
            this.delButton.setEnabled(false);
            this.updateButton.setSelected(false);
            this.noupdateButton.setEnabled(false);
            this.addButton.setEnabled(true);
            this.eventTable.setEnabled(true);
         } else {
            int selectedRow = lsm.getMinSelectionIndex();
            eventContainer ev = (eventContainer)this.tmod.getValueAt(selectedRow, 0);
            ev.setEditMode(false);
            this.my_main.interPanelCom(new stoerungSelectedEvent(ev, false));
            this.updateButton.setEnabled(true);
            this.noupdateButton.setEnabled(false);
            this.delButton.setEnabled(true);
            this.updateButton.setSelected(false);
            this.addButton.setEnabled(true);
            this.eventTable.setEnabled(true);
         }
      }
   }

   private void updateOutput(boolean lastselected) {
      eventContainer ev = null;
      ListSelectionModel lsm = this.eventTable.getSelectionModel();
      int selectedRow = lsm.getMinSelectionIndex();
      if (selectedRow >= 0) {
         ev = (eventContainer)this.tmod.getValueAt(selectedRow, 0);
      }

      while(this.mod.getRowCount() > 0) {
         this.mod.removeRow(0);
      }

      int v = this.eventFilterCB.getSelectedIndex();

      for(eventContainer e : this.glbControl.getModel().events) {
         Object[] rowData = new Object[]{e, e.getName()};
         Class c = event.getEventTyp(e);
         if (v == 0
            || c == null
            || c != null
               && v == 1
               && gleisevent.class.isAssignableFrom(c)
               && this.glbControl.getSelectedGleis() != null
               && this.glbControl.getSelectedGleis().sameGleis(e.getGleis())
            || c != null && v == 2 && gleisevent.class.isAssignableFrom(c)
            || c != null && v == 3 && zugevent.class.isAssignableFrom(c)
            || c != null && v == 4 && fahrstrasseevent.class.isAssignableFrom(c)) {
            this.mod.addRow(rowData);
         }
      }

      if (ev != null && lastselected) {
         selectedRow = -1;

         for(int i = 0; i < this.tmod.getRowCount(); ++i) {
            if (ev == this.tmod.getValueAt(i, 0)) {
               selectedRow = i;
               break;
            }
         }

         if (selectedRow >= 0) {
            lsm.setSelectionInterval(selectedRow, selectedRow);
         }
      }
   }

   private void initComponents() {
      this.jPanel2 = new JPanel();
      this.addButton = new JButton();
      this.updateButton = new JToggleButton();
      this.noupdateButton = new JButton();
      this.delButton = new JButton();
      this.messureButton = new JButton();
      this.jPanel3 = new JPanel();
      this.jScrollPane1 = new JScrollPane();
      this.eventTable = new JTable();
      this.jPanel1 = new JPanel();
      this.jLabel1 = new JLabel();
      this.eventFilterCB = new JComboBox();
      this.setBorder(BorderFactory.createTitledBorder("Störungen"));
      this.setLayout(new BorderLayout(10, 0));
      this.jPanel2.setLayout(new GridLayout(0, 1, 0, 4));
      this.addButton.setText("neu");
      this.addButton.setFocusPainted(false);
      this.addButton.setFocusable(false);
      this.addButton.setMargin(new Insets(2, 2, 2, 2));
      this.addButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stoerungListPanel.this.addButtonActionPerformed(evt);
         }
      });
      this.jPanel2.add(this.addButton);
      this.updateButton.setText("bearbeiten");
      this.updateButton.setToolTipText("Dieser Schalter muss gedrückt sein um eine Störung zu bearbeiten.");
      this.updateButton.setEnabled(false);
      this.updateButton.setFocusPainted(false);
      this.updateButton.setFocusable(false);
      this.updateButton.setMargin(new Insets(2, 2, 2, 2));
      this.updateButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stoerungListPanel.this.updateButtonActionPerformed(evt);
         }
      });
      this.jPanel2.add(this.updateButton);
      this.noupdateButton.setText("bearbeiten Abbruch");
      this.noupdateButton.setToolTipText("Verwirft die Änderungen an der Störung.");
      this.noupdateButton.setEnabled(false);
      this.noupdateButton.setFocusPainted(false);
      this.noupdateButton.setFocusable(false);
      this.noupdateButton.setMargin(new Insets(2, 2, 2, 2));
      this.noupdateButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stoerungListPanel.this.noupdateButtonActionPerformed(evt);
         }
      });
      this.jPanel2.add(this.noupdateButton);
      this.delButton.setText("löschen");
      this.delButton.setEnabled(false);
      this.delButton.setFocusPainted(false);
      this.delButton.setFocusable(false);
      this.delButton.setMargin(new Insets(2, 2, 2, 2));
      this.delButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stoerungListPanel.this.delButtonActionPerformed(evt);
         }
      });
      this.jPanel2.add(this.delButton);
      this.messureButton.setText("Messen...");
      this.messureButton.setToolTipText("Ermittelt eine ungefähre Häufigkeit");
      this.messureButton.setFocusPainted(false);
      this.messureButton.setFocusable(false);
      this.messureButton.setMargin(new Insets(2, 2, 2, 2));
      this.messureButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stoerungListPanel.this.messureButtonActionPerformed(evt);
         }
      });
      this.jPanel2.add(this.messureButton);
      this.add(this.jPanel2, "East");
      this.jPanel3.setLayout(new BorderLayout());
      this.eventTable.setSelectionMode(0);
      this.jScrollPane1.setViewportView(this.eventTable);
      this.jPanel3.add(this.jScrollPane1, "Center");
      this.jPanel1.setLayout(new BoxLayout(this.jPanel1, 2));
      this.jLabel1.setForeground(SystemColor.windowBorder);
      this.jLabel1.setLabelFor(this.eventFilterCB);
      this.jLabel1.setText("zeige ");
      this.jPanel1.add(this.jLabel1);
      this.eventFilterCB.setModel(new DefaultComboBoxModel(new String[]{"alle Störungen", "Störungen des ausgewählten Elements", "Gleisstörungen"}));
      this.eventFilterCB.setFocusable(false);
      this.eventFilterCB.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            stoerungListPanel.this.eventFilterCBItemStateChanged(evt);
         }
      });
      this.jPanel1.add(this.eventFilterCB);
      this.jPanel3.add(this.jPanel1, "North");
      this.add(this.jPanel3, "Center");
   }

   private void eventFilterCBItemStateChanged(ItemEvent evt) {
      this.updateOutput(true);
   }

   private void addButtonActionPerformed(ActionEvent evt) {
      eventContainer ev = new eventContainer(this.glbControl.getModel());
      ev.setGleis(this.glbControl.getSelectedGleis());
      this.updateOutput(false);
      int selectedRow = -1;

      for(int i = 0; i < this.tmod.getRowCount(); ++i) {
         if (ev == this.tmod.getValueAt(i, 0)) {
            selectedRow = i;
            break;
         }
      }

      if (selectedRow >= 0) {
         this.eventTable.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
         this.updateButton.setSelected(true);
         this.addButton.setEnabled(false);
         ev.setEditMode(this.updateButton.isSelected());
         this.noupdateButton.setEnabled(this.updateButton.isSelected());
         this.eventFilterCB.setEnabled(!this.updateButton.isSelected());
         this.eventTable.setEnabled(!this.updateButton.isSelected());
         this.my_main.interPanelCom(new stoerungSelectedEvent(ev, this.updateButton.isSelected(), false));
      }
   }

   private void delButtonActionPerformed(ActionEvent evt) {
      ListSelectionModel lsm = this.eventTable.getSelectionModel();
      int selectedRow = lsm.getMinSelectionIndex();
      eventContainer ev = null;
      if (selectedRow >= 0) {
         ev = (eventContainer)this.tmod.getValueAt(selectedRow, 0);
         ev.remove();
         this.updateOutput(false);
         ev.setEditMode(false);
         this.my_main.interPanelCom(new stoerungSelectedEvent(ev, false, false));
         this.addButton.setEnabled(!this.updateButton.isSelected());
         this.eventTable.setEnabled(!this.updateButton.isSelected());
         this.eventFilterCB.setEnabled(!this.updateButton.isSelected());
      }
   }

   private void updateButtonActionPerformed(ActionEvent evt) {
      ListSelectionModel lsm = this.eventTable.getSelectionModel();
      int selectedRow = lsm.getMinSelectionIndex();
      eventContainer ev = null;
      if (selectedRow >= 0) {
         ev = (eventContainer)this.tmod.getValueAt(selectedRow, 0);
         ev.setEditMode(this.updateButton.isSelected());
         this.my_main.interPanelCom(new stoerungSelectedEvent(ev, this.updateButton.isSelected(), true));
         this.noupdateButton.setEnabled(this.updateButton.isSelected());
         this.addButton.setEnabled(!this.updateButton.isSelected());
         this.eventFilterCB.setEnabled(!this.updateButton.isSelected());
         this.eventTable.setEnabled(!this.updateButton.isSelected());
         if (!this.updateButton.isSelected()) {
            this.updateOutput(true);
         }
      }
   }

   private void noupdateButtonActionPerformed(ActionEvent evt) {
      this.updateButton.setSelected(false);
      this.noupdateButton.setEnabled(this.updateButton.isSelected());
      ListSelectionModel lsm = this.eventTable.getSelectionModel();
      int selectedRow = lsm.getMinSelectionIndex();
      eventContainer ev = null;
      if (selectedRow >= 0) {
         ev = (eventContainer)this.tmod.getValueAt(selectedRow, 0);
         ev.setEditMode(false);
      }

      this.my_main.interPanelCom(new stoerungSelectedEvent(ev, false));
      this.addButton.setEnabled(!this.updateButton.isSelected());
      this.eventFilterCB.setEnabled(!this.updateButton.isSelected());
      this.eventTable.setEnabled(!this.updateButton.isSelected());
   }

   private void messureButtonActionPerformed(ActionEvent evt) {
      stoerungMessDialog.openText(this, this.my_main, this.glbControl.getModel());
   }
}

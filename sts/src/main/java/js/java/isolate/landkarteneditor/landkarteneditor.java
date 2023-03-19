package js.java.isolate.landkarteneditor;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.adapter.AbstractTopFrame;
import js.java.schaltungen.adapter.closePrefs;
import js.java.schaltungen.moduleapi.ModuleObject;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.dialogs.aboutDialog;
import js.java.tools.dialogs.message0;
import js.java.tools.gui.DropDownListButton;
import js.java.tools.gui.SwingTools;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.WindowStateSaver.STORESTATES;
import js.java.tools.gui.table.SortedListModel;

public class landkarteneditor extends AbstractTopFrame implements SessionClose, ModuleObject {
   private landkarte lk;
   private control controller;
   private final DropDownListButton bhfButton;
   private SortedListModel sortModel;
   private message0 waitDialog = null;
   private JButton aboutButton;
   private JButton addKnotenButton;
   private JButton adjustButton;
   private JButton delKnotenButton;
   private JButton delVerbindungButton;
   private JList elementsList;
   private JScrollPane jScrollPane1;
   private Separator jSeparator1;
   private Separator jSeparator2;
   private Separator jSeparator3;
   private JScrollPane karteScrollPane;
   private JToggleButton knotenModus;
   private JPanel mainPanel;
   private ButtonGroup modusButtonGroup;
   private JButton saveButton;
   private JToolBar toolBar;
   private JComboBox verbindungDirection;
   private JToggleButton verbindungModus;

   public landkarteneditor(UserContext uc) {
      super(uc);
      this.bhfButton = new DropDownListButton();
      this.initComponents();
      this.initMyComponents();
      this.pack();
      this.setTitle(this.getParameter("region") + " - Landkarteneditor - StellwerkSim");
      this.setName(this.getClass().getSimpleName());
      new WindowStateSaver(this, STORESTATES.SIZE);
      this.setVisible(true);
      SwingTools.toFront(this);
      uc.addCloseObject(this);
   }

   @Override
   public void close() {
      this.bhfButton.setPopup(new JPopupMenu());
      this.controller = null;
      this.sortModel = null;
      this.lk = null;
   }

   @Override
   public void terminate() {
      this.dispose();
      this.uc.moduleClosed();
   }

   private void initMyComponents() {
      this.bhfButton.setFocusable(false);
      this.bhfButton.setFocusPainted(false);
      this.bhfButton.setMinimumSize(this.saveButton.getPreferredSize());
      this.bhfButton.setToolTipText("Region oder Stellwerk des Knoten");
      this.bhfButton.addItemListener(e -> this.controller.knotenListValueStateChanged(e.getItem()));
      this.lk = new landkarte();
      this.karteScrollPane.setViewportView(this.lk);
      this.blockUI(true);
      this.controller = new control(this.lk, this);
      this.sortModel = new SortedListModel(this.controller);
      this.elementsList.setModel(this.sortModel);
      this.toolBar.remove(this.verbindungDirection);
      this.uc.addCloseObject(this.lk);
      this.uc.addCloseObject(this.controller);
   }

   void enableSave(boolean e) {
      this.saveButton.setEnabled(e);
      this.adjustButton.setEnabled(e);
   }

   void enableAddKnoten(boolean e) {
      this.addKnotenButton.setEnabled(e && this.getKnotenValue() != null && this.knotenModus.isSelected());
   }

   void enableKnotenList(boolean e) {
      this.bhfButton.setEnabled(e && this.knotenModus.isSelected());
   }

   void setKnotenValue(aidMenuItem t) {
      this.bhfButton.setCurrent(t);
   }

   aidMenuItem getKnotenValue() {
      return (aidMenuItem)this.bhfButton.getCurrent();
   }

   void setKnotenListValue(JPopupMenu m) {
      this.bhfButton.setPopup(m);
   }

   void enableDelKnoten(boolean e) {
      this.delKnotenButton.setEnabled(e && this.knotenModus.isSelected());
   }

   void enableDelVerbindung(boolean e) {
      this.delVerbindungButton.setEnabled(e && this.verbindungModus.isSelected());
      this.verbindungDirection.setEnabled(e && this.verbindungModus.isSelected());
   }

   void setVerbindungDirection(int direction) {
      this.verbindungDirection.setSelectedIndex(direction);
   }

   void selectListItem(Object v) {
      this.elementsList.setSelectedValue(v, true);
   }

   private Frame findParentFrame() {
      return this;
   }

   void blockUI(boolean b) {
      if (b) {
         this.mainPanel.setCursor(new Cursor(3));
         if (this.waitDialog == null) {
            Frame f = this.findParentFrame();
            this.waitDialog = new message0(f, "Datentransfer", "Daten werden übertragen...");
            this.waitDialog.show(200, 80);
         }
      } else {
         this.mainPanel.setCursor(new Cursor(0));
         if (this.waitDialog != null) {
            this.waitDialog.close();
            this.waitDialog = null;
         }
      }

      if (!b) {
         this.setKnotenMode(this.knotenModus.isSelected());
      }

      this.knotenModus.setEnabled(!b);
      this.verbindungModus.setEnabled(!b);
      this.enableSave(!b);
      this.enableAddKnoten(false);
      this.enableDelKnoten(false);
      this.enableDelVerbindung(false);
      this.enableKnotenList(false);
      this.lk.setEnabled(!b);
      this.elementsList.setEnabled(!b);
   }

   void setKnotenMode(boolean e) {
      this.knotenModus.setSelected(e);
      this.verbindungModus.setSelected(!e);
   }

   private void initComponents() {
      this.modusButtonGroup = new ButtonGroup();
      this.mainPanel = new JPanel();
      this.toolBar = new JToolBar();
      this.saveButton = new JButton();
      this.jSeparator2 = new Separator();
      this.knotenModus = new JToggleButton();
      this.addKnotenButton = new JButton();
      this.delKnotenButton = new JButton();
      this.jSeparator1 = new Separator();
      this.verbindungModus = new JToggleButton();
      this.delVerbindungButton = new JButton();
      this.verbindungDirection = new JComboBox();
      this.jSeparator3 = new Separator();
      this.adjustButton = new JButton();
      this.aboutButton = new JButton();
      this.jScrollPane1 = new JScrollPane();
      this.elementsList = new JList();
      this.karteScrollPane = new JScrollPane();
      this.setDefaultCloseOperation(0);
      this.setTitle("Landkarteneditor");
      this.setLocationByPlatform(true);
      this.setPreferredSize(new Dimension(800, 500));
      this.addWindowListener(new WindowAdapter() {
         public void windowClosed(WindowEvent evt) {
            landkarteneditor.this.formWindowClosed(evt);
         }

         public void windowClosing(WindowEvent evt) {
            landkarteneditor.this.formWindowClosing(evt);
         }
      });
      this.mainPanel.setLayout(new BorderLayout());
      this.toolBar.setFloatable(false);
      this.toolBar.setRollover(true);
      this.saveButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/save22.png")));
      this.saveButton.setToolTipText("Speichern");
      this.saveButton.setEnabled(false);
      this.saveButton.setFocusable(false);
      this.saveButton.setHorizontalTextPosition(0);
      this.saveButton.setVerticalTextPosition(3);
      this.saveButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            landkarteneditor.this.saveButtonActionPerformed(evt);
         }
      });
      this.toolBar.add(this.saveButton);
      this.toolBar.add(this.jSeparator2);
      this.modusButtonGroup.add(this.knotenModus);
      this.knotenModus.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/node22.png")));
      this.knotenModus.setSelected(true);
      this.knotenModus.setText("Knotenmodus");
      this.knotenModus.setToolTipText("Knoten bearbeiten");
      this.knotenModus.setFocusable(false);
      this.knotenModus.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            landkarteneditor.this.knotenModusItemStateChanged(evt);
         }
      });
      this.toolBar.add(this.knotenModus);
      this.toolBar.add(this.bhfButton);
      this.addKnotenButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/add22.png")));
      this.addKnotenButton.setToolTipText("Knoten hinzufügen");
      this.addKnotenButton.setEnabled(false);
      this.addKnotenButton.setFocusable(false);
      this.addKnotenButton.setHorizontalTextPosition(0);
      this.addKnotenButton.setVerticalTextPosition(3);
      this.addKnotenButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            landkarteneditor.this.addKnotenButtonActionPerformed(evt);
         }
      });
      this.toolBar.add(this.addKnotenButton);
      this.delKnotenButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/del22.png")));
      this.delKnotenButton.setToolTipText("Knoten löschen");
      this.delKnotenButton.setEnabled(false);
      this.delKnotenButton.setFocusable(false);
      this.delKnotenButton.setHorizontalTextPosition(0);
      this.delKnotenButton.setVerticalTextPosition(3);
      this.delKnotenButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            landkarteneditor.this.delKnotenButtonActionPerformed(evt);
         }
      });
      this.toolBar.add(this.delKnotenButton);
      this.toolBar.add(this.jSeparator1);
      this.modusButtonGroup.add(this.verbindungModus);
      this.verbindungModus.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/connect22.png")));
      this.verbindungModus.setText("Verbindungsmodus");
      this.verbindungModus.setToolTipText("Verbindungen zwischen Knoten bearbeiten");
      this.verbindungModus.setFocusable(false);
      this.verbindungModus.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            landkarteneditor.this.knotenModusItemStateChanged(evt);
         }
      });
      this.toolBar.add(this.verbindungModus);
      this.delVerbindungButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/del22.png")));
      this.delVerbindungButton.setToolTipText("Verbindung löschen");
      this.delVerbindungButton.setEnabled(false);
      this.delVerbindungButton.setFocusable(false);
      this.delVerbindungButton.setHorizontalTextPosition(0);
      this.delVerbindungButton.setVerticalTextPosition(3);
      this.delVerbindungButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            landkarteneditor.this.delVerbindungButtonActionPerformed(evt);
         }
      });
      this.toolBar.add(this.delVerbindungButton);
      this.verbindungDirection.setModel(new DefaultComboBoxModel(new String[]{"gerade", "oben", "unten"}));
      this.verbindungDirection.setToolTipText("<html>ACHTUNG!<br>Die ist nur eine Testoption, die noch <b>nicht gespeichert</b> wird!</html>");
      this.verbindungDirection.setEnabled(false);
      this.verbindungDirection.setFocusable(false);
      this.verbindungDirection.setMaximumSize(new Dimension(80, 32767));
      this.verbindungDirection.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            landkarteneditor.this.verbindungDirectionItemStateChanged(evt);
         }
      });
      this.toolBar.add(this.verbindungDirection);
      this.toolBar.add(this.jSeparator3);
      this.adjustButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/topleft22.png")));
      this.adjustButton.setToolTipText("auf 0/0 ausrichten");
      this.adjustButton.setFocusable(false);
      this.adjustButton.setHorizontalTextPosition(0);
      this.adjustButton.setVerticalTextPosition(3);
      this.adjustButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            landkarteneditor.this.adjustButtonActionPerformed(evt);
         }
      });
      this.toolBar.add(this.adjustButton);
      this.aboutButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/question22.png")));
      this.aboutButton.setToolTipText("Über...");
      this.aboutButton.setFocusable(false);
      this.aboutButton.setHorizontalTextPosition(0);
      this.aboutButton.setMargin(new Insets(0, 0, 0, 0));
      this.aboutButton.setVerticalTextPosition(3);
      this.aboutButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            landkarteneditor.this.aboutButtonActionPerformed(evt);
         }
      });
      this.toolBar.add(this.aboutButton);
      this.mainPanel.add(this.toolBar, "North");
      this.jScrollPane1.setMinimumSize(new Dimension(250, 100));
      this.jScrollPane1.setPreferredSize(new Dimension(250, 130));
      this.elementsList.setFont(this.elementsList.getFont().deriveFont((float)this.elementsList.getFont().getSize() + 2.0F));
      this.elementsList.setSelectionMode(0);
      this.elementsList.setCellRenderer(new itemListRenderer());
      this.elementsList.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent evt) {
            landkarteneditor.this.elementsListValueChanged(evt);
         }
      });
      this.jScrollPane1.setViewportView(this.elementsList);
      this.mainPanel.add(this.jScrollPane1, "West");
      this.mainPanel.add(this.karteScrollPane, "Center");
      this.getContentPane().add(this.mainPanel, "Center");
   }

   private void knotenModusItemStateChanged(ItemEvent evt) {
      this.controller.setKnotenMode(this.knotenModus.isSelected());
   }

   private void elementsListValueChanged(ListSelectionEvent evt) {
      this.controller.listSelectionChanged(this.elementsList.getSelectedValue());
   }

   private void addKnotenButtonActionPerformed(ActionEvent evt) {
      this.controller.addKnoten();
   }

   private void delKnotenButtonActionPerformed(ActionEvent evt) {
      this.controller.delKnoten();
   }

   private void delVerbindungButtonActionPerformed(ActionEvent evt) {
      this.controller.delVerbindung();
   }

   private void saveButtonActionPerformed(ActionEvent evt) {
      this.controller.save();
   }

   private void adjustButtonActionPerformed(ActionEvent evt) {
      this.controller.adjust00();
   }

   private void aboutButtonActionPerformed(ActionEvent evt) {
      Frame f = this.findParentFrame();
      aboutDialog d = new aboutDialog(
         f,
         false,
         "Über",
         "Landkarteneditor",
         "1.0 / Build " + this.getBuild(),
         "Jürgen Schmitz & StellwerkSim Betriebsverein e.V.",
         "Jürgen Schmitz",
         "js@js-home.org",
         "http://www.js-home.org",
         "Dieses Programm und Teile daraus darf ausschließlich auf js-home.org und Partnerseiten genutzt werden."
      );
      d.show(400, 320);
   }

   private void verbindungDirectionItemStateChanged(ItemEvent evt) {
      this.controller.setVerbindungDirection(this.verbindungDirection.getSelectedIndex());
   }

   private void formWindowClosed(WindowEvent evt) {
      this.uc.moduleClosed();
   }

   private void formWindowClosing(WindowEvent evt) {
      if (closePrefs.Parts.LANDKARTE.ask(this.uc, this, "Wirklich Karteneditor beenden?")) {
         this.dispose();
      }
   }
}

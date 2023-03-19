package js.java.isolate.sim.autoMsg;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import js.java.schaltungen.chatcomng.ChannelsNameParser;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.WindowStateSaver.STORESTATES;

public class setupFrame extends JDialog {
   private final control msgControl;
   private final DefaultListModel msgModel;
   private final JFileChooser fDialog;
   private JButton cancelButton;
   private JButton delButton;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JMenu jMenu1;
   private JMenuBar jMenuBar1;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JScrollPane jScrollPane1;
   private JSeparator jSeparator1;
   private JMenuItem loadMenu;
   private JList msgList;
   private JComboBox mzielCB;
   private JButton newButton;
   private JMenuItem saveMenu;
   private JTextField signallisteTF;
   private JButton updateButton;
   private JButton useButton;
   private JComboBox zrichtungCB;

   public setupFrame(Frame parent, control m) {
      super(parent, false);
      this.msgControl = m;
      this.msgModel = new DefaultListModel();
      this.initComponents();
      this.mzielCB.removeAllItems();

      for(ChannelsNameParser.ChannelName s : this.msgControl.getNachbarn()) {
         this.mzielCB.addItem(s.title);
      }

      this.zrichtungCB.removeAllItems();
      this.zrichtungCB.addItem("");

      for(String s : this.msgControl.getNach()) {
         this.zrichtungCB.addItem(s);
      }

      this.initValues();
      this.fDialog = new JFileChooser();
      this.setName(this.getClass().getSimpleName());
      new WindowStateSaver(this, STORESTATES.LOCATION_AND_SIZE);
   }

   private void initValues() {
      this.msgModel.clear();

      for(msgItem mi : this.msgControl.msgitems) {
         this.msgModel.addElement(new msgItem(mi));
      }

      this.msgListValueChanged(null);
   }

   private void initComponents() {
      this.jPanel1 = new JPanel();
      this.jLabel4 = new JLabel();
      this.jScrollPane1 = new JScrollPane();
      this.msgList = new JList();
      this.newButton = new JButton();
      this.updateButton = new JButton();
      this.delButton = new JButton();
      this.jPanel2 = new JPanel();
      this.jLabel1 = new JLabel();
      this.signallisteTF = new JTextField();
      this.jLabel2 = new JLabel();
      this.zrichtungCB = new JComboBox();
      this.jLabel3 = new JLabel();
      this.mzielCB = new JComboBox();
      this.jSeparator1 = new JSeparator();
      this.useButton = new JButton();
      this.cancelButton = new JButton();
      this.jMenuBar1 = new JMenuBar();
      this.jMenu1 = new JMenu();
      this.loadMenu = new JMenuItem();
      this.saveMenu = new JMenuItem();
      this.setDefaultCloseOperation(2);
      this.setTitle("Automatische Zugmeldungen konfigurieren");
      this.setLocationByPlatform(true);
      this.jPanel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      this.jPanel1.setLayout(new GridBagLayout());
      this.jLabel4
         .setText(
            "<html>Konfiguriert Automatische Zugmeldungen, die bei Zugfahrt über einbestimmtes Signal<br>an ein Nachbarstellwerk vollautomatisch verschickt werden.<p>\nDie Liste muss <b>nach jedem Start neu</b> konfiguriert werden um diese Funktion zu aktivieren.<br>\nDiese Funktionalität ist \"so wie sie ist\" zu nutzen und nicht weiter dokumentiert!\n</html>"
         );
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.jLabel4, gridBagConstraints);
      this.jScrollPane1.setVerticalScrollBarPolicy(22);
      this.msgList.setModel(this.msgModel);
      this.msgList.setSelectionMode(1);
      this.msgList.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent evt) {
            setupFrame.this.msgListValueChanged(evt);
         }
      });
      this.jScrollPane1.setViewportView(this.msgList);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new Insets(5, 0, 5, 0);
      this.jPanel1.add(this.jScrollPane1, gridBagConstraints);
      this.newButton.setText("neu");
      this.newButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            setupFrame.this.newButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.newButton, gridBagConstraints);
      this.updateButton.setText("ändern");
      this.updateButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            setupFrame.this.updateButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.updateButton, gridBagConstraints);
      this.delButton.setText("löschen");
      this.delButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            setupFrame.this.delButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.delButton, gridBagConstraints);
      this.jPanel2.setLayout(new GridBagLayout());
      this.jLabel1.setText("Signal");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      this.jPanel2.add(this.jLabel1, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 5, 2, 2);
      this.jPanel2.add(this.signallisteTF, gridBagConstraints);
      this.jLabel2.setText("Zug Richtung");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      this.jPanel2.add(this.jLabel2, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(5, 5, 5, 2);
      this.jPanel2.add(this.zrichtungCB, gridBagConstraints);
      this.jLabel3.setText("Meldung an");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      this.jPanel2.add(this.jLabel3, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(5, 5, 5, 2);
      this.jPanel2.add(this.mzielCB, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(5, 0, 5, 0);
      this.jPanel1.add(this.jPanel2, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(4, 0, 4, 0);
      this.jPanel1.add(this.jSeparator1, gridBagConstraints);
      this.useButton.setText("Nutzen");
      this.useButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            setupFrame.this.useButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.useButton, gridBagConstraints);
      this.cancelButton.setText("Abbrechen");
      this.cancelButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            setupFrame.this.cancelButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.cancelButton, gridBagConstraints);
      this.getContentPane().add(this.jPanel1, "Center");
      this.jMenu1.setText("Datei");
      this.loadMenu.setText("Konfiguration laden...");
      this.loadMenu.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            setupFrame.this.loadMenuActionPerformed(evt);
         }
      });
      this.jMenu1.add(this.loadMenu);
      this.saveMenu.setText("Konfiguration speichern...");
      this.saveMenu.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            setupFrame.this.saveMenuActionPerformed(evt);
         }
      });
      this.jMenu1.add(this.saveMenu);
      this.jMenuBar1.add(this.jMenu1);
      this.setJMenuBar(this.jMenuBar1);
      this.pack();
   }

   private void msgListValueChanged(ListSelectionEvent evt) {
      boolean multi = false;

      try {
         multi = this.msgList.getSelectedIndices().length > 1;
      } catch (NullPointerException var6) {
      }

      this.delButton.setEnabled(this.msgList.getSelectedIndex() >= 0);
      this.updateButton.setEnabled(this.msgList.getSelectedIndex() >= 0 && !multi);
      this.mzielCB.setEnabled(this.msgList.getSelectedIndex() >= 0 && !multi);
      this.zrichtungCB.setEnabled(this.msgList.getSelectedIndex() >= 0 && !multi);
      this.signallisteTF.setEnabled(this.msgList.getSelectedIndex() >= 0 && !multi);
      if (this.msgList.getSelectedIndex() >= 0 && !multi) {
         msgItem mi = (msgItem)this.msgList.getSelectedValue();

         try {
            this.signallisteTF.setText(mi.signal.getElementName());
         } catch (NullPointerException var5) {
            this.signallisteTF.setText("");
         }

         this.mzielCB.setSelectedItem(mi.zielnachbar);
         this.zrichtungCB.setSelectedItem(mi.ziel == null ? "" : mi.ziel);
         this.signallisteTF.requestFocusInWindow();
      }
   }

   private void cancelButtonActionPerformed(ActionEvent evt) {
      this.dispose();
   }

   private void updateButtonActionPerformed(ActionEvent evt) {
      if (this.msgList.getSelectedIndex() >= 0) {
         msgItem mi = (msgItem)this.msgList.getSelectedValue();
         String sig = this.signallisteTF.getText();
         mi.zielnachbar = (String)this.mzielCB.getSelectedItem();
         String z = (String)this.zrichtungCB.getSelectedItem();
         if (z.isEmpty()) {
            mi.ziel = null;
         } else {
            mi.ziel = z;
         }

         String[] signals = sig.split(",");
         mi.signal = this.msgControl.findElement(signals[0].trim());
         this.msgListValueChanged(null);
         this.msgList.repaint();
         if (signals.length > 1) {
            for(int i = 1; i < signals.length; ++i) {
               msgItem n = new msgItem();
               n.ziel = mi.ziel;
               n.zielnachbar = mi.zielnachbar;
               n.signal = this.msgControl.findElement(signals[i].trim());
               this.msgModel.addElement(n);
            }
         }
      }
   }

   private void newButtonActionPerformed(ActionEvent evt) {
      msgItem n = new msgItem();
      this.msgModel.addElement(n);
      this.msgList.setSelectedValue(n, true);
   }

   private void delButtonActionPerformed(ActionEvent evt) {
      int[] index = this.msgList.getSelectedIndices();

      for(int i = index.length - 1; i >= 0; --i) {
         this.msgModel.removeElementAt(index[i]);
      }
   }

   private void useButtonActionPerformed(ActionEvent evt) {
      this.updateButtonActionPerformed(null);
      LinkedList<msgItem> newlist = new LinkedList();
      Enumeration<msgItem> e = this.msgModel.elements();

      while(e.hasMoreElements()) {
         msgItem mi = (msgItem)e.nextElement();
         newlist.add(mi);
      }

      this.msgControl.msgitems = newlist;
      this.dispose();
   }

   private void loadMenuActionPerformed(ActionEvent evt) {
      int j = this.fDialog.showOpenDialog(this);
      if (j == 0) {
         if (this.msgControl.load(this.fDialog.getSelectedFile())) {
            this.initValues();
            this.cancelButton.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Erfolgreich geladen.", "Geladen", 1);
         } else {
            JOptionPane.showMessageDialog(
               this,
               "<html><b>Datei konnte nicht geladen werden!</b><br><br>Möglicherweise ist sie für ein anderes Stellwerk oder das<br>Stellwerk wurde umgebaut.</html>",
               "Geladen",
               0
            );
         }
      }
   }

   private void saveMenuActionPerformed(ActionEvent evt) {
      this.updateButtonActionPerformed(null);
      LinkedList<msgItem> newlist = new LinkedList();
      Enumeration<msgItem> e = this.msgModel.elements();

      while(e.hasMoreElements()) {
         msgItem mi = (msgItem)e.nextElement();
         newlist.add(mi);
      }

      int j = this.fDialog.showSaveDialog(this);
      if (j == 0) {
         if (this.fDialog.getSelectedFile().exists()) {
            j = JOptionPane.showConfirmDialog(
               this, "Eine Datei mit dem Namen " + this.fDialog.getSelectedFile().getName() + "\nexistiert bereits, überschreiben?", "Datei existiert", 2, 2
            );
            if (j != 0) {
               return;
            }
         }

         if (this.msgControl.save(newlist, this.fDialog.getSelectedFile())) {
            JOptionPane.showMessageDialog(this, "Erfolgreich gespeichert.", "Gespeichert", 1);
         } else {
            JOptionPane.showMessageDialog(this, "<html><b>Datei konnte nicht gespeichert werden!</b></html>", "Geladen", 0);
         }
      }
   }
}

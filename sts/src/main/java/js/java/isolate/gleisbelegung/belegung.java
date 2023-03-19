package js.java.isolate.gleisbelegung;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.adapter.AbstractTopFrame;
import js.java.schaltungen.moduleapi.ModuleObject;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.gui.SwingTools;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.WindowStateSaver.STORESTATES;
import js.java.tools.xml.xmllistener;
import js.java.tools.xml.xmlreader;
import org.xml.sax.Attributes;

public class belegung extends AbstractTopFrame implements Runnable, xmllistener, SessionClose, ModuleObject {
   private final HashMap<Integer, zuglist> zuege = new HashMap();
   private final TreeMap<String, gleislist> gleise = new TreeMap();
   private final HashMap<Integer, String> ausfahrt = new HashMap();
   private final HashMap<Integer, String> einfahrt = new HashMap();
   private boolean aktivsystem = false;
   private final ExecutorService executor;
   private paintUI ui = null;
   private JLayer<JComponent> layer = null;
   private JToggleButton aktivButton;
   private JTextField anabField;
   private JScrollPane belegungPane;
   private JLabel doku;
   private JTextField einausField;
   private JTextField flagField;
   private JTextField gleisField;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JLabel jLabel5;
   private JLabel jLabel6;
   private JLabel jLabel7;
   private JLabel jLabel8;
   private JPanel jPanel1;
   private JPanel jPanel3;
   private JPanel loadingPanel;
   private JTextField nameField;
   private JTextField searchField;
   private JCheckBox showCollisionsCB;
   private ButtonGroup systemBG;
   private JTextField templateField;
   private JToggleButton testButton;
   private JTextField thematagField;
   private JComboBox zoomCB;

   @Override
   public void close() {
      this.clearLists();
      this.executor.shutdown();
   }

   @Override
   public void terminate() {
      this.dispose();
      this.uc.moduleClosed();
   }

   private void clearLists() {
      this.zuege.values().stream().forEach(TreeSet::clear);
      this.gleise.values().stream().forEach(gleislist::clear);
      this.zuege.clear();
      this.gleise.clear();
   }

   zuggleis addOrFindZug(int zid, gleislist gl, Attributes attrs, belegung p) {
      zuglist z = (zuglist)this.zuege.get(zid);
      if (z == null) {
         z = new zuglist(zid);
         this.zuege.put(zid, z);
      }

      zuggleis zg = new zuggleis(zid, gl, attrs, p, z);
      gl.addZug(zg);
      z.add(zg);
      return zg;
   }

   gleislist addOrFindGleis(String g) {
      gleislist gl = (gleislist)this.gleise.get(g);
      if (gl == null) {
         gl = new gleislist(g);
         this.gleise.put(g, gl);
      }

      return gl;
   }

   private void searchZug(String t) {
      for(gleislist gl : this.gleise.values()) {
         gl.searchZug(t);
      }
   }

   Iterator<gleislist> getGleisList() {
      return this.gleise.values().iterator();
   }

   public void addAusEnr(int enr, String g) {
      this.ausfahrt.put(enr, g);
   }

   public void addEinEnr(int enr, String g) {
      this.einfahrt.put(enr, g);
   }

   public String findEinfahrt(int enr) {
      return enr == 0 ? "von E/F Flag" : (String)this.einfahrt.get(enr);
   }

   public String findAusfahrt(int enr) {
      return enr == 0 ? "nach E/K Flag" : (String)this.ausfahrt.get(enr);
   }

   public belegung(UserContext uc) {
      super(uc);
      this.showMem("Start 1");
      this.initComponents();
      this.initParams();
      this.showMem("Start 2");
      this.pack();
      this.setName(this.getClass().getSimpleName());
      new WindowStateSaver(this, STORESTATES.SIZE);
      this.setVisible(true);
      SwingTools.toFront(this);
      this.executor = Executors.newSingleThreadExecutor();
      this.startAWT();
      uc.addCloseObject(this);
   }

   public void startAWT() {
      System.out.println("Start");
      this.setCursor(new Cursor(3));
      this.enableElements(false);
      this.clearview();
      this.clearLists();
      this.belegungPane.setColumnHeaderView(null);
      this.belegungPane.setRowHeaderView(null);
      this.belegungPane.setViewportView(this.loadingPanel);
      this.executor.submit(this);
      System.out.println("/Start");
   }

   public void run() {
      System.out.println("load");
      String updateurl = this.getParameter("url") + (this.aktivsystem ? "&aktiv=1" : "");
      xmlreader xmlr = new xmlreader();
      xmlr.registerTag("gzug", this);
      xmlr.registerTag("gldata", this);
      xmlr.registerTag("spielzeit", this);

      try {
         xmlr.updateData(updateurl, new StringBuffer());
      } catch (IOException var5) {
         System.out.println("Ex: " + var5.getMessage());
         var5.printStackTrace();
      }

      System.out.println("show");

      try {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               belegung.this.showall();
               belegung.this.setCursor(new Cursor(0));
            }
         });
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      System.out.println("load finished");
   }

   private void initParams() {
   }

   private void enableElements(boolean b) {
      this.aktivButton.setEnabled(b);
      this.testButton.setEnabled(b);
      this.zoomCB.setEnabled(b);
      this.showCollisionsCB.setEnabled(b);
   }

   void show(zuggleis zg) {
      this.nameField.setText(zg.getName());
      this.gleisField.setText(zg.getGleis());
      this.anabField.setText(zg.getAnAb());
      this.flagField.setText(zg.getFlags());
      this.thematagField.setText(zg.getThemamarker());
      this.einausField.setText(zg.getEinAus());
      this.templateField.setText(zg.getTemplate());
   }

   private void showall() {
      this.showCollisionsCB.setSelected(false);
      timeline t = new timeline();
      this.belegungPane.setColumnHeaderView(t);
      JPanel p = new JPanel();
      BoxLayout l = new BoxLayout(p, 1);
      p.setLayout(l);
      boolean odd = true;

      for(Iterator<gleislist> it = this.getGleisList(); it.hasNext(); odd = !odd) {
         gleislist gl = (gleislist)it.next();
         p.add(gl.getNamePane());
         gl.setBGcolnum(odd);
      }

      this.belegungPane.setRowHeaderView(p);
      p = new JPanel();
      l = new BoxLayout(p, 1);
      p.setLayout(l);
      Iterator<gleislist> var9 = this.getGleisList();

      while(var9.hasNext()) {
         gleislist gl = (gleislist)var9.next();
         p.add(gl);
         gl.render();
      }

      this.ui = new paintUI();
      this.layer = new JLayer(p, this.ui);
      this.belegungPane.setViewportView(this.layer);
      this.enableElements(true);
   }

   private void clearview() {
      Iterator<gleislist> it = this.getGleisList();

      while(it.hasNext()) {
         gleislist gl = (gleislist)it.next();
         gl.clear();
      }
   }

   public void parseStartTag(String tag, Attributes attrs) {
   }

   public void parseEndTag(final String tag, final Attributes attrs, final String pcdata) {
      if (EventQueue.isDispatchThread()) {
         this.parseEndTagAWT(tag, attrs, pcdata);
      } else {
         try {
            EventQueue.invokeAndWait(new Runnable() {
               public void run() {
                  belegung.this.parseEndTagAWT(tag, attrs, pcdata);
               }
            });
         } catch (Exception var5) {
            var5.printStackTrace();
         }
      }
   }

   public void parseEndTagAWT(String tag, Attributes attrs, String pcdata) {
      if (tag.equalsIgnoreCase("gzug")) {
         int zid = Integer.parseInt(attrs.getValue("zid"));
         String gleis = attrs.getValue("gleis");
         gleislist gl = this.addOrFindGleis(gleis);
         zuggleis var7 = this.addOrFindZug(zid, gl, attrs, this);
      } else if (tag.equalsIgnoreCase("gldata")) {
         int enr = Integer.parseInt(attrs.getValue("enr"));
         String swwert = attrs.getValue("swwert");
         int element = Integer.parseInt(attrs.getValue("element"));
         if (element == 5 || element == 12) {
            this.addOrFindGleis(swwert);
         } else if (element == 6) {
            this.addEinEnr(enr, swwert);
         } else if (element == 7) {
            this.addAusEnr(enr, swwert);
         }
      } else if (tag.equalsIgnoreCase("spielzeit")) {
         int von = Integer.parseInt(attrs.getValue("von"));
         int bis = Integer.parseInt(attrs.getValue("bis"));
         timeline.VON = von;
         timeline.BIS = bis;
      }
   }

   private void initComponents() {
      this.systemBG = new ButtonGroup();
      this.jPanel1 = new JPanel();
      this.doku = new JLabel();
      this.jLabel1 = new JLabel();
      this.nameField = new JTextField();
      this.jLabel2 = new JLabel();
      this.gleisField = new JTextField();
      this.jLabel3 = new JLabel();
      this.anabField = new JTextField();
      this.jLabel4 = new JLabel();
      this.flagField = new JTextField();
      this.jLabel6 = new JLabel();
      this.thematagField = new JTextField();
      this.jLabel7 = new JLabel();
      this.einausField = new JTextField();
      this.jLabel8 = new JLabel();
      this.templateField = new JTextField();
      this.jPanel3 = new JPanel();
      this.aktivButton = new JToggleButton();
      this.testButton = new JToggleButton();
      this.zoomCB = new JComboBox();
      this.searchField = new JTextField();
      this.showCollisionsCB = new JCheckBox();
      this.belegungPane = new JScrollPane();
      this.loadingPanel = new JPanel();
      this.jLabel5 = new JLabel();
      this.setDefaultCloseOperation(0);
      this.setTitle("Gleisbelegung");
      this.setLocationByPlatform(true);
      this.setPreferredSize(new Dimension(900, 600));
      this.addWindowListener(new WindowAdapter() {
         public void windowClosed(WindowEvent evt) {
            belegung.this.formWindowClosed(evt);
         }

         public void windowClosing(WindowEvent evt) {
            belegung.this.formWindowClosing(evt);
         }
      });
      this.jPanel1.setLayout(new GridBagLayout());
      this.doku.setText("<html>Auf Zug klicken für Details. blau: normal, grün: K-Flag, gelb: E-Flag+E-Ziel, hellblau: F-Flag, rot: Fehler</html>");
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = 2;
      this.jPanel1.add(this.doku, gridBagConstraints);
      this.jLabel1.setText("Name (ZID)");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 2;
      this.jPanel1.add(this.jLabel1, gridBagConstraints);
      this.nameField.setEditable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.nameField, gridBagConstraints);
      this.jLabel2.setText("Gleis");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = 2;
      this.jPanel1.add(this.jLabel2, gridBagConstraints);
      this.gleisField.setEditable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.gleisField, gridBagConstraints);
      this.jLabel3.setText("An/Ab");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = 2;
      this.jPanel1.add(this.jLabel3, gridBagConstraints);
      this.anabField.setEditable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.anabField, gridBagConstraints);
      this.jLabel4.setText("Flags");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 4;
      gridBagConstraints.fill = 2;
      this.jPanel1.add(this.jLabel4, gridBagConstraints);
      this.flagField.setEditable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 4;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.flagField, gridBagConstraints);
      this.jLabel6.setText("Themamarker");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 5;
      gridBagConstraints.fill = 2;
      this.jPanel1.add(this.jLabel6, gridBagConstraints);
      this.thematagField.setEditable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 5;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.thematagField, gridBagConstraints);
      this.jLabel7.setText("Ein-/Ausfahrt");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 6;
      gridBagConstraints.fill = 2;
      this.jPanel1.add(this.jLabel7, gridBagConstraints);
      this.einausField.setEditable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 6;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.einausField, gridBagConstraints);
      this.jLabel8.setText("Zug-Template");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 7;
      gridBagConstraints.fill = 2;
      this.jPanel1.add(this.jLabel8, gridBagConstraints);
      this.templateField.setEditable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 7;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.templateField, gridBagConstraints);
      this.jPanel3.setLayout(new GridLayout(0, 1));
      this.systemBG.add(this.aktivButton);
      this.aktivButton.setText("Aktivsystem");
      this.aktivButton.setEnabled(false);
      this.aktivButton.setFocusPainted(false);
      this.aktivButton.setFocusable(false);
      this.aktivButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            belegung.this.aktivButtonActionPerformed(evt);
         }
      });
      this.jPanel3.add(this.aktivButton);
      this.systemBG.add(this.testButton);
      this.testButton.setSelected(true);
      this.testButton.setText("Testsystem");
      this.testButton.setEnabled(false);
      this.testButton.setFocusPainted(false);
      this.testButton.setFocusable(false);
      this.testButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            belegung.this.testButtonActionPerformed(evt);
         }
      });
      this.jPanel3.add(this.testButton);
      this.zoomCB.setModel(new DefaultComboBoxModel(new String[]{"Zoom 1", "Zoom 2", "Zoom 5", "Zoom 10", "Zoom 20", "Zoom 50"}));
      this.zoomCB.setSelectedIndex(3);
      this.zoomCB.setEnabled(false);
      this.zoomCB.setFocusable(false);
      this.zoomCB.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            belegung.this.zoomCBItemStateChanged(evt);
         }
      });
      this.jPanel3.add(this.zoomCB);
      this.searchField.setToolTipText("Zum Suchen RETURN drücken");
      this.searchField.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            belegung.this.searchFieldActionPerformed(evt);
         }
      });
      this.jPanel3.add(this.searchField);
      this.showCollisionsCB.setText("Gleiskollisionen");
      this.showCollisionsCB.setToolTipText("<html>Zeigt Gleisbelegungskollisionen<br>beachtet E/K/F und unterscheidliche Themamarker</html>");
      this.showCollisionsCB.setEnabled(false);
      this.showCollisionsCB.setFocusPainted(false);
      this.showCollisionsCB.setFocusable(false);
      this.showCollisionsCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            belegung.this.showCollisionsCBActionPerformed(evt);
         }
      });
      this.jPanel3.add(this.showCollisionsCB);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridheight = 7;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weighty = 1.0;
      this.jPanel1.add(this.jPanel3, gridBagConstraints);
      this.getContentPane().add(this.jPanel1, "South");
      this.belegungPane.setFocusable(false);
      this.loadingPanel.setLayout(new BorderLayout());
      this.jLabel5.setFont(this.jLabel5.getFont().deriveFont((float)this.jLabel5.getFont().getSize() + 7.0F));
      this.jLabel5.setHorizontalAlignment(0);
      this.jLabel5.setText("<html>Daten werden geladen, bitte warten...</html>");
      this.loadingPanel.add(this.jLabel5, "Center");
      this.belegungPane.setViewportView(this.loadingPanel);
      this.getContentPane().add(this.belegungPane, "Center");
   }

   private void aktivButtonActionPerformed(ActionEvent evt) {
      this.aktivsystem = true;
      this.startAWT();
   }

   private void testButtonActionPerformed(ActionEvent evt) {
      this.aktivsystem = false;
      this.startAWT();
   }

   private void zoomCBItemStateChanged(ItemEvent evt) {
      String v = (String)this.zoomCB.getSelectedItem();
      v = v.substring(5);
      int z = Integer.parseInt(v);
      timeline.MINUTEWIDTH = z;
      this.setCursor(new Cursor(3));
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            Iterator<gleislist> it = belegung.this.getGleisList();

            while(it.hasNext()) {
               gleislist gl = (gleislist)it.next();
               gl.clear();
               gl.render();
            }

            belegung.this.belegungPane.validate();
            belegung.this.belegungPane.getColumnHeader().validate();
            belegung.this.repaint();
            belegung.this.showCollisionsCBActionPerformed(null);
            EventQueue.invokeLater(new Runnable() {
               public void run() {
                  belegung.this.setCursor(new Cursor(0));
               }
            });
         }
      });
   }

   private void searchFieldActionPerformed(ActionEvent evt) {
      this.searchZug(this.searchField.getText());
   }

   private void showCollisionsCBActionPerformed(ActionEvent evt) {
      try {
         this.ui.clear();
         if (this.showCollisionsCB.isSelected()) {
            Iterator<gleislist> it = this.getGleisList();

            while(it.hasNext()) {
               gleislist gl = (gleislist)it.next();
               this.ui.add(gl.getCollsions());
            }
         }

         this.layer.repaint();
      } catch (Exception var4) {
      }
   }

   private void formWindowClosed(WindowEvent evt) {
      this.uc.moduleClosed();
   }

   private void formWindowClosing(WindowEvent evt) {
      int j = JOptionPane.showConfirmDialog(this, "Wirklich Fenster schließen?", "Sicher?", 2, 3);
      if (j == 0) {
         this.dispose();
      }
   }
}

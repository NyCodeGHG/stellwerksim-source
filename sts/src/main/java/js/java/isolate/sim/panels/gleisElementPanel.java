package js.java.isolate.sim.panels;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.Collator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisTypContainer;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleis.gleisElements.gleisHelper;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.GecSelectEvent;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.panels.actionevents.coordsEvent;
import js.java.isolate.sim.panels.actionevents.readUIEvent;
import js.java.isolate.sim.panels.actionevents.setUIEvent;
import js.java.tools.actions.AbstractEvent;
import js.java.tools.gui.NumberTextField;
import js.java.tools.gui.SimpleToggleButton;
import js.java.tools.gui.border.DropShadowBorder;
import js.java.tools.gui.renderer.SimpleToggleButtonRenderer;
import js.java.tools.gui.table.SortedListModel;

public class gleisElementPanel extends basePanel {
   private final DocumentListener textFieldListener = new DocumentListener() {
      public void insertUpdate(DocumentEvent e) {
         gleisElementPanel.this.lockSet++;
         gleisElementPanel.this.updateGleisbild();
         gleisElementPanel.this.lockSet--;
      }

      public void removeUpdate(DocumentEvent e) {
         gleisElementPanel.this.lockSet++;
         gleisElementPanel.this.updateGleisbild();
         gleisElementPanel.this.lockSet--;
      }

      public void changedUpdate(DocumentEvent e) {
         gleisElementPanel.this.lockSet++;
         gleisElementPanel.this.updateGleisbild();
         gleisElementPanel.this.lockSet--;
      }
   };
   private final ActionListener textFieldAction = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         gleisElementPanel.this.updateGleisbild();
      }
   };
   private boolean setmode = false;
   private final Font itemsFont;
   private final Collator stringComparator = Collator.getInstance();
   private boolean shown = false;
   private HashMap<Integer, JToggleButton> typButtons = new HashMap();
   private HashMap<gleisElements.RICHTUNG, JToggleButton> richtungButtons = new HashMap();
   private DefaultListModel elementsButtons = new DefaultListModel();
   private NumberTextField enrField;
   private int inSet = 0;
   private int lockSet = 0;
   private gleisElementHelp elementHelp = null;
   private int[] keyRow1 = new int[]{81, 87, 69, 82, 84, 90};
   private int[] keyRow2 = new int[]{65, 83, 68, 70};
   private JButton clearENRButton;
   private JButton clearRichtungButton;
   private JButton clearSWwertButton;
   private JButton clearTypButton;
   private JLabel elementLabel;
   private JList elementList;
   private JLabel enrLabel;
   private JButton helpElementButton;
   private JFormattedTextField jFormattedTextField1;
   private JScrollPane jScrollPane1;
   private JSeparator jSeparator1;
   private JLabel positionLabel;
   private JLabel richtungLabel;
   private JPanel richtungPanel;
   private JLabel swLabel;
   private JTextField swwertTF;
   private JLabel typLabel;
   private JPanel typPanel;
   private JLabel usageLabel;

   public gleisElementPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.itemsFont = this.getFont().deriveFont((float)this.getFont().getSize() - 2.0F);
      this.initComponents();
      this.initMyComponents();
      e.registerListener(1, this);
      e.registerListener(12, this);
      e.registerListener(13, this);
   }

   @Override
   public void action(AbstractEvent e) {
      if (e instanceof coordsEvent) {
         this.showCoords(((coordsEvent)e).getX(), ((coordsEvent)e).getY());
      } else if (e instanceof readUIEvent) {
         if (this.shown) {
            this.readUI(((readUIEvent)e).getData());
         }
      } else if (e instanceof setUIEvent) {
         this.setUI(((setUIEvent)e).getData());
      } else if (e instanceof GecSelectEvent) {
         try {
            if (this.glbControl.getSelectedGleis().typAllowesSWwertedit()) {
               this.swwertTF.requestFocus();
            }
         } catch (NullPointerException var3) {
         }
      }
   }

   @Override
   public void hidden(gecBase gec) {
      this.shown = false;
   }

   @Override
   public void shown(String n, gecBase gec) {
      this.shown = true;
      String t = "";
      if (n.equals("edit")) {
         t = "Gleiselement auswählen und Art verändern.";
         this.setmode = false;
      } else if (n.equals("set")) {
         t = "Art auswählen und auf Gleiselement via Klick setzen.";
         this.setmode = true;
         this.enrField.setText("0");
         this.swwertTF.setText("");
      } else {
         t = "";
         this.enrField.setText("0");
         this.swwertTF.setText("");
      }

      this.clearTypButton.setEnabled(!this.setmode);
      this.usageLabel.setText("<html>" + t + "</html>");
      this.glbControl.getMode().addChangeListener(this);
   }

   public void showCoords(int x, int y) {
      this.positionLabel.setText(String.format("%03d/%03d", x, y));
   }

   private void setUI(gleis.gleisUIcom gl) {
      if (this.lockSet == 0) {
         ++this.inSet;
         this.setTyp(gl.element.getTyp());
         this.setElement(gl.element);
         this.setRichtung(gl.richtung);
         this.enrField.setText(gl.enr + "");
         this.enrField.setEnabled(gl.typAllowesENRedit());
         this.swwertTF.setText(gl.swwert);
         this.swwertTF.setEnabled(gl.typAllowesSWwertedit());
         --this.inSet;
      }
   }

   private void readUI(gleis.gleisUIcom gl) {
      gl.element = this.getCurrentElement();
      gl.richtung = this.getCurrentRichtung();
      gl.enr = this.enrField.getInt();
      gl.swwert = this.swwertTF.getText();
      gl.changed = true;
   }

   private void setTyp(int typ) {
      ++this.inSet;

      try {
         ((JToggleButton)this.typButtons.get(typ)).setSelected(true);
      } catch (NullPointerException var3) {
      }

      --this.inSet;
   }

   private void setElement(element element) {
      ++this.inSet;
      Enumeration e = this.elementsButtons.elements();

      while(e.hasMoreElements()) {
         gleisElementPanel.elementEntry ee = (gleisElementPanel.elementEntry)e.nextElement();
         if (ee.element == element) {
            this.elementList.setSelectedValue(ee, true);
            break;
         }
      }

      --this.inSet;
   }

   private void setRichtung(gleisElements.RICHTUNG richtung) {
      ++this.inSet;

      try {
         ((JToggleButton)this.richtungButtons.get(richtung)).setSelected(true);
      } catch (NullPointerException var3) {
      }

      --this.inSet;
   }

   private int getCurrentTyp() {
      for(Entry<Integer, JToggleButton> e : this.typButtons.entrySet()) {
         if (((JToggleButton)e.getValue()).isSelected()) {
            return e.getKey();
         }
      }

      return 0;
   }

   private element getCurrentElement() {
      try {
         gleisElementPanel.elementEntry ee = (gleisElementPanel.elementEntry)this.elementList.getSelectedValue();
         return ee.element;
      } catch (NullPointerException var2) {
         return gleisHelper.findElement(this.getCurrentTyp(), -1);
      }
   }

   private gleisElements.RICHTUNG getCurrentRichtung() {
      for(Entry<gleisElements.RICHTUNG, JToggleButton> e : this.richtungButtons.entrySet()) {
         if (((JToggleButton)e.getValue()).isSelected()) {
            return (gleisElements.RICHTUNG)e.getKey();
         }
      }

      return gleisElements.RICHTUNG.left;
   }

   private void initMyComponents() {
      this.enrField = new NumberTextField(3);
      this.enrField.setFont(this.itemsFont);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridx = 3;
      gridBagConstraints.fill = 2;
      this.add(this.enrField, gridBagConstraints);
      SimpleToggleButtonRenderer renderer = new SimpleToggleButtonRenderer(130);
      this.elementList.setCellRenderer(renderer);
      ButtonGroup bg1 = new ButtonGroup();
      gleisTypContainer gtc = gleisTypContainer.getInstance();
      int i = 0;

      for(int t : gtc.getTypes()) {
         JToggleButton r = new SimpleToggleButton(gtc.getTypName(t));
         if (this.typButtons.isEmpty()) {
            r.setSelected(true);
         }

         r.setFocusable(false);
         r.setFont(this.itemsFont);
         r.addItemListener(new gleisElementPanel.typItemListener(t, r));

         try {
            r.setMnemonic(this.keyRow1[i]);
            ++i;
         } catch (ArrayIndexOutOfBoundsException var13) {
         }

         bg1.add(r);
         this.typPanel.add(r);
         this.typButtons.put(t, r);
      }

      i = 0;
      ButtonGroup bg2 = new ButtonGroup();
      Map<gleisElements.RICHTUNG, String> richtung = gtc.getRichtungen();

      for(Entry<gleisElements.RICHTUNG, String> e : richtung.entrySet()) {
         JToggleButton r = new SimpleToggleButton((String)e.getValue());
         if (this.richtungButtons.isEmpty()) {
            r.setSelected(true);
         }

         r.setFocusable(false);
         r.setFont(this.itemsFont);
         r.addItemListener(new gleisElementPanel.richtungItemListener(e, r));

         try {
            r.setMnemonic(this.keyRow2[i]);
            ++i;
         } catch (ArrayIndexOutOfBoundsException var12) {
         }

         bg2.add(r);
         this.richtungPanel.add(r);
         this.richtungButtons.put(e.getKey(), r);
      }

      this.enrField.getDocument().addDocumentListener(this.textFieldListener);
      this.swwertTF.getDocument().addDocumentListener(this.textFieldListener);
      this.enrField.addActionListener(this.textFieldAction);
      this.swwertTF.addActionListener(this.textFieldAction);
   }

   private void updateElements(int typ) {
      this.elementsButtons.removeAllElements();
      gleisTypContainer gtc = gleisTypContainer.getInstance();

      for(int e : gtc.getTypElements(typ)) {
         gleisElementPanel.elementEntry ee = new gleisElementPanel.elementEntry(gleisHelper.findElement(typ, e), gtc.getTypElementName(typ, e));
         this.elementsButtons.addElement(ee);
      }

      this.elementList.requestFocus();
   }

   private void typChanged(int typ, boolean selected) {
      if (selected) {
         this.updateElements(typ);
         this.setElement(this.getCurrentElement());
      }
   }

   private void richtungChanged(Entry<gleisElements.RICHTUNG, String> r, boolean selected) {
      if (selected) {
         this.updateGleisbild();
      }
   }

   private void updateGleisbild() {
      if (this.inSet == 0) {
         this.glbControl.getModel().ch_gleis_values();
      }
   }

   private void initComponents() {
      this.jFormattedTextField1 = new JFormattedTextField();
      this.usageLabel = new JLabel();
      this.typLabel = new JLabel();
      this.typPanel = new JPanel();
      this.clearTypButton = new JButton();
      this.elementLabel = new JLabel();
      this.jScrollPane1 = new JScrollPane();
      this.elementList = new JList();
      this.helpElementButton = new JButton();
      this.enrLabel = new JLabel();
      this.swLabel = new JLabel();
      this.richtungLabel = new JLabel();
      this.richtungPanel = new JPanel();
      this.swwertTF = new JTextField();
      this.clearENRButton = new JButton();
      this.clearSWwertButton = new JButton();
      this.clearRichtungButton = new JButton();
      this.positionLabel = new JLabel();
      this.jSeparator1 = new JSeparator();
      this.jFormattedTextField1.setText("jFormattedTextField1");
      this.setBorder(BorderFactory.createTitledBorder("Bauelemente"));
      this.setLayout(new GridBagLayout());
      this.usageLabel.setFont(this.itemsFont);
      this.usageLabel.setHorizontalAlignment(0);
      this.usageLabel.setText("Gleis Element Editor");
      this.usageLabel.setBorder(new DropShadowBorder(true, true, true, true));
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 14;
      this.add(this.usageLabel, gridBagConstraints);
      this.typLabel.setFont(this.itemsFont);
      this.typLabel.setForeground(SystemColor.windowBorder);
      this.typLabel.setText("E-Typ");
      this.typLabel.setToolTipText("Tastenkürzel: Alt+Q - Z");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.add(this.typLabel, gridBagConstraints);
      this.typPanel.setLayout(new GridLayout(1, 0));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      this.add(this.typPanel, gridBagConstraints);
      this.clearTypButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/clear16.png")));
      this.clearTypButton.setToolTipText("Element leeren");
      this.clearTypButton.setFocusPainted(false);
      this.clearTypButton.setFocusable(false);
      this.clearTypButton.setMargin(new Insets(0, 0, 0, 0));
      this.clearTypButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisElementPanel.this.clearTypButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 4;
      gridBagConstraints.gridy = 0;
      this.add(this.clearTypButton, gridBagConstraints);
      this.elementLabel.setDisplayedMnemonic('m');
      this.elementLabel.setFont(this.itemsFont);
      this.elementLabel.setForeground(SystemColor.windowBorder);
      this.elementLabel.setLabelFor(this.elementList);
      this.elementLabel.setText("E-Element");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.add(this.elementLabel, gridBagConstraints);
      this.jScrollPane1.setHorizontalScrollBarPolicy(31);
      this.jScrollPane1.setPreferredSize(new Dimension(258, 30));
      this.elementList.setFont(this.itemsFont);
      this.elementList.setModel(new SortedListModel(this.elementsButtons));
      this.elementList.setSelectionMode(0);
      this.elementList.setLayoutOrientation(2);
      this.elementList.setVisibleRowCount(0);
      this.elementList.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent evt) {
            gleisElementPanel.this.elementListValueChanged(evt);
         }
      });
      this.jScrollPane1.setViewportView(this.elementList);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridheight = 3;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 0.8;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.add(this.jScrollPane1, gridBagConstraints);
      this.helpElementButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/help16.png")));
      this.helpElementButton.setToolTipText("kurze Elementhilfe anzeigen");
      this.helpElementButton.setFocusPainted(false);
      this.helpElementButton.setFocusable(false);
      this.helpElementButton.setMargin(new Insets(0, 0, 0, 0));
      this.helpElementButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisElementPanel.this.helpElementButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.add(this.helpElementButton, gridBagConstraints);
      this.enrLabel.setFont(this.itemsFont);
      this.enrLabel.setForeground(SystemColor.windowBorder);
      this.enrLabel.setText("E-Nummer");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.add(this.enrLabel, gridBagConstraints);
      this.swLabel.setFont(this.itemsFont);
      this.swLabel.setForeground(SystemColor.windowBorder);
      this.swLabel.setText("SW-Wert");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.add(this.swLabel, gridBagConstraints);
      this.richtungLabel.setFont(this.itemsFont);
      this.richtungLabel.setForeground(SystemColor.windowBorder);
      this.richtungLabel.setText("Richtung");
      this.richtungLabel.setToolTipText("Tastenkürzel: Alt+A - F");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 5;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.add(this.richtungLabel, gridBagConstraints);
      this.richtungPanel.setLayout(new GridLayout(1, 0));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 5;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = 1;
      this.add(this.richtungPanel, gridBagConstraints);
      this.swwertTF.setFont(this.itemsFont);
      this.swwertTF.setText("SW-Wert");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 3;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.1;
      this.add(this.swwertTF, gridBagConstraints);
      this.clearENRButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/clear16.png")));
      this.clearENRButton.setToolTipText("E-Nummer leeren");
      this.clearENRButton.setFocusPainted(false);
      this.clearENRButton.setFocusable(false);
      this.clearENRButton.setMargin(new Insets(0, 0, 0, 0));
      this.clearENRButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisElementPanel.this.clearENRButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 4;
      gridBagConstraints.gridy = 2;
      this.add(this.clearENRButton, gridBagConstraints);
      this.clearSWwertButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/clear16.png")));
      this.clearSWwertButton.setToolTipText("SW-Wert leeren");
      this.clearSWwertButton.setFocusPainted(false);
      this.clearSWwertButton.setFocusable(false);
      this.clearSWwertButton.setMargin(new Insets(0, 0, 0, 0));
      this.clearSWwertButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisElementPanel.this.clearSWwertButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 4;
      gridBagConstraints.gridy = 3;
      this.add(this.clearSWwertButton, gridBagConstraints);
      this.clearRichtungButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/clear16.png")));
      this.clearRichtungButton.setEnabled(false);
      this.clearRichtungButton.setFocusPainted(false);
      this.clearRichtungButton.setFocusable(false);
      this.clearRichtungButton.setMargin(new Insets(0, 0, 0, 0));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 4;
      gridBagConstraints.gridy = 5;
      this.add(this.clearRichtungButton, gridBagConstraints);
      this.positionLabel.setFont(this.itemsFont);
      this.positionLabel.setText("0/0");
      this.positionLabel.setBorder(new DropShadowBorder(true, true, true, true));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 16;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.add(this.positionLabel, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 5;
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets = new Insets(2, 0, 10, 0);
      this.add(this.jSeparator1, gridBagConstraints);
   }

   private void elementListValueChanged(ListSelectionEvent evt) {
      if (this.setmode) {
         this.enrField.setEnabled(gleis.typAllowesENRedit(this.getCurrentElement()));
         this.swwertTF.setEnabled(gleis.typAllowesSWwertedit(this.getCurrentElement()));
      }

      if (this.inSet == 0) {
         this.updateGleisbild();
      }
   }

   private void clearSWwertButtonActionPerformed(ActionEvent evt) {
      this.swwertTF.setText("");
      this.updateGleisbild();
   }

   private void clearENRButtonActionPerformed(ActionEvent evt) {
      this.enrField.setInt(0);
      this.updateGleisbild();
   }

   private void helpElementButtonActionPerformed(ActionEvent evt) {
      if (this.elementHelp == null) {
         this.elementHelp = new gleisElementHelp(this);
      }

      this.elementHelp.setHelp(this.getCurrentElement());
      this.elementHelp.setVisible(true);
   }

   private void clearTypButtonActionPerformed(ActionEvent evt) {
      try {
         ((JToggleButton)this.typButtons.get(0)).setSelected(true);
      } catch (NullPointerException var3) {
      }
   }

   private class elementEntry implements Comparable {
      element element;
      String text;

      elementEntry(element e, String t) {
         super();
         this.element = e;
         this.text = t;
      }

      public String toString() {
         return this.text;
      }

      public int compareTo(Object o) {
         if (this.element == gleis.ELEMENT_STRECKE) {
            return -1;
         } else {
            gleisElementPanel.elementEntry oo = (gleisElementPanel.elementEntry)o;
            return oo.element == gleis.ELEMENT_STRECKE ? 1 : gleisElementPanel.this.stringComparator.compare(this.text, oo.text);
         }
      }
   }

   private class richtungItemListener implements ItemListener {
      private Entry<gleisElements.RICHTUNG, String> entry;
      private JToggleButton button;

      richtungItemListener(Entry<gleisElements.RICHTUNG, String> t, JToggleButton b) {
         super();
         this.entry = t;
         this.button = b;
      }

      public void itemStateChanged(ItemEvent e) {
         gleisElementPanel.this.richtungChanged(this.entry, this.button.isSelected());
      }
   }

   private class typItemListener implements ItemListener {
      private int typ;
      private JToggleButton button;

      typItemListener(int t, JToggleButton b) {
         super();
         this.typ = t;
         this.button = b;
      }

      public void itemStateChanged(ItemEvent e) {
         gleisElementPanel.this.typChanged(this.typ, this.button.isSelected());
      }
   }
}

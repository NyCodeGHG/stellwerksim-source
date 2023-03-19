package js.java.isolate.sim.panels;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisTypContainer;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.GecSelectEvent;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.panels.actionevents.coordsEvent;
import js.java.isolate.sim.panels.actionevents.readUIEvent;
import js.java.isolate.sim.panels.actionevents.setUIEvent;
import js.java.isolate.sim.panels.elementsPane.elementsDynPanel;
import js.java.isolate.sim.panels.elementsPane.elementsEmptyImage;
import js.java.isolate.sim.panels.elementsPane.elementsGleisImage;
import js.java.isolate.sim.panels.elementsPane.elementsQuickPanel;
import js.java.isolate.sim.panels.elementsPane.elementsView;
import js.java.tools.actions.AbstractEvent;
import js.java.tools.gui.BoundedPlainDocument;
import js.java.tools.gui.NumberTextField;
import js.java.tools.gui.SimpleToggleButton;
import js.java.tools.gui.border.DropShadowBorder;

public class gleisElementPanelV2 extends basePanel {
   private final DocumentListener textFieldListener = new DocumentListener() {
      public void insertUpdate(DocumentEvent e) {
         gleisElementPanelV2.this.lockSet++;
         gleisElementPanelV2.this.updateGleisbild();
         gleisElementPanelV2.this.lockSet--;
      }

      public void removeUpdate(DocumentEvent e) {
         gleisElementPanelV2.this.lockSet++;
         gleisElementPanelV2.this.updateGleisbild();
         gleisElementPanelV2.this.lockSet--;
      }

      public void changedUpdate(DocumentEvent e) {
         gleisElementPanelV2.this.lockSet++;
         gleisElementPanelV2.this.updateGleisbild();
         gleisElementPanelV2.this.lockSet--;
      }
   };
   private final ActionListener textFieldAction = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         gleisElementPanelV2.this.updateGleisbild();
      }
   };
   private boolean setmode = false;
   private final Font itemsFont;
   private boolean shown = false;
   private final ButtonGroup elementsGroup = new ButtonGroup();
   private elementsView elementsMain;
   private elementsQuickPanel elementsQuick;
   private final HashMap<element, JToggleButton> elementButtons = new HashMap();
   private final HashMap<gleisElements.RICHTUNG, JToggleButton> richtungButtons = new HashMap();
   private NumberTextField enrField;
   private int inSet = 0;
   private int lockSet = 0;
   private gleisElementHelp elementHelp = null;
   private final int[] keyRow2 = new int[]{65, 83, 68, 70};
   private JButton clearENRButton;
   private JButton clearElementButton;
   private JButton clearSWwertButton;
   private JPanel elementsPanel;
   private JScrollPane elementsScrollPane;
   private JLabel enrLabel;
   private JButton helpElementButton;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JSeparator jSeparator1;
   private JLabel positionLabel;
   private JLabel richtungLabel;
   private JPanel richtungPanel;
   private JLabel swLabel;
   private JTextField swwertTF;

   public gleisElementPanelV2(gleisbildEditorControl glb, stellwerk_editor e) {
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
         this.setmode = true;
         this.enrField.setText("0");
         this.swwertTF.setText("");
      }

      this.clearElementButton.setEnabled(!this.setmode);
      this.glbControl.getMode().addChangeListener(this);
   }

   public void showCoords(int x, int y) {
      this.positionLabel.setText(String.format("%03d/%03d", x, y));
   }

   private void setUI(gleis.gleisUIcom gl) {
      if (this.lockSet == 0) {
         ++this.inSet;
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

   private void setElement(element element) {
      ++this.inSet;
      JToggleButton b = (JToggleButton)this.elementButtons.get(element);
      if (b != null) {
         b.setSelected(true);
         if (!elementsDynPanel.mayNotScroll()) {
            if (b.getParent() instanceof elementsDynPanel) {
               ((elementsDynPanel)b.getParent()).focus();
            }

            b.scrollRectToVisible(new Rectangle(0, 0, b.getWidth(), b.getHeight()));
         }

         this.enableRichtung(element);
         this.enrField.setEnabled(gleis.typAllowesENRedit(element));
         this.swwertTF.setEnabled(gleis.typAllowesSWwertedit(element));
      }

      --this.inSet;
   }

   private void enableRichtung(element element) {
      for(JToggleButton b : this.richtungButtons.values()) {
         b.setEnabled(false);
      }

      for(gleisElements.RICHTUNG r : element.getAllowedRichtung()) {
         ((JToggleButton)this.richtungButtons.get(r)).setEnabled(true);
      }
   }

   private void setRichtung(gleisElements.RICHTUNG richtung) {
      ++this.inSet;

      try {
         ((JToggleButton)this.richtungButtons.get(richtung)).setSelected(true);
      } catch (NullPointerException var3) {
      }

      --this.inSet;
   }

   private element getCurrentElement() {
      for(Entry<element, JToggleButton> v : this.elementButtons.entrySet()) {
         if (((JToggleButton)v.getValue()).isSelected()) {
            return (element)v.getKey();
         }
      }

      return gleisElements.ELEMENT_LEER;
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
      gridBagConstraints.gridx = 1;
      gridBagConstraints.fill = 2;
      this.add(this.enrField, gridBagConstraints);
      this.elementsQuick = new elementsQuickPanel();
      this.elementsPanel.add(this.elementsQuick, "Center");
      this.elementsMain = new elementsView();
      JViewport sv = new JViewport();
      sv.add(this.elementsMain);
      this.elementsScrollPane.setViewport(sv);
      gleisTypContainer gtc = gleisTypContainer.getInstance();

      for(gleisTypContainer.block block : gtc.getBlocks()) {
         elementsDynPanel prevPanel = null;
         elementsDynPanel p = new elementsDynPanel(block.getTitle());
         if (block.isQuick()) {
            this.elementsQuick.add(block.getTitle(), p);
            prevPanel = p;
         } else {
            prevPanel = null;
         }

         p.setFont(this.itemsFont);
         int c = 0;

         for(element ee : block) {
            String n = gtc.getTypElementName(ee);
            if (n != null && !n.isEmpty()) {
               SimpleToggleButton b = new SimpleToggleButton(n);
               b.setFont(this.itemsFont);
               b.setFocusable(false);
               this.elementsGroup.add(b);
               b.addItemListener(new gleisElementPanelV2.elementItemListener(b, ee));
               if (ee.getTyp() == 1) {
                  b.setIcon(new elementsGleisImage());
               } else {
                  b.setIcon(new elementsEmptyImage());
               }

               b.setShortKey((char)(49 + c));
               p.add(b);
               if (c < 10 && prevPanel != null) {
                  prevPanel.getInputMap(0).put(KeyStroke.getKeyStroke(49 + c, 512), "componentKey" + c);
                  prevPanel.getActionMap().put("componentKey" + c, new gleisElementPanelV2.elementKeyAction(b));
               }

               this.elementButtons.put(ee, b);
               ++c;
            }
         }

         this.elementsMain.add(p);
      }

      int i = 0;
      ButtonGroup bg2 = new ButtonGroup();
      Map<gleisElements.RICHTUNG, String> richtung = gtc.getRichtungen();

      for(Entry<gleisElements.RICHTUNG, String> e : richtung.entrySet()) {
         JToggleButton r = new SimpleToggleButton((String)e.getValue());
         if (this.richtungButtons.isEmpty()) {
            r.setSelected(true);
         }

         r.setFocusable(false);
         r.setFont(this.itemsFont);
         r.addItemListener(new gleisElementPanelV2.richtungItemListener(e, r));

         try {
            r.setMnemonic(this.keyRow2[i]);
            r.setToolTipText("Schnelltaste: " + KeyEvent.getKeyText(this.keyRow2[i]));
            ++i;
         } catch (ArrayIndexOutOfBoundsException var13) {
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

   private void elementChanged(element e, boolean selected) {
      if (selected) {
         this.setElement(e);
         this.updateGleisbild();
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
      this.jPanel2 = new JPanel();
      this.elementsPanel = new JPanel();
      this.clearElementButton = new JButton();
      this.jPanel1 = new JPanel();
      this.helpElementButton = new JButton();
      this.positionLabel = new JLabel();
      this.elementsScrollPane = new JScrollPane();
      this.enrLabel = new JLabel();
      this.swLabel = new JLabel();
      this.richtungLabel = new JLabel();
      this.richtungPanel = new JPanel();
      this.swwertTF = new JTextField();
      this.clearENRButton = new JButton();
      this.clearSWwertButton = new JButton();
      this.jSeparator1 = new JSeparator();
      this.setBorder(BorderFactory.createTitledBorder("Bauelemente"));
      this.setLayout(new GridBagLayout());
      this.jPanel2.setLayout(new BorderLayout());
      this.elementsPanel.setLayout(new BorderLayout());
      this.clearElementButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/clear16.png")));
      this.clearElementButton.setToolTipText("Element leeren");
      this.clearElementButton.setFocusPainted(false);
      this.clearElementButton.setFocusable(false);
      this.clearElementButton.setMargin(new Insets(0, 0, 0, 0));
      this.clearElementButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisElementPanelV2.this.clearElementButtonActionPerformed(evt);
         }
      });
      this.elementsPanel.add(this.clearElementButton, "West");
      this.jPanel1.setLayout(new BorderLayout());
      this.helpElementButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/help16.png")));
      this.helpElementButton.setToolTipText("kurze Elementhilfe anzeigen");
      this.helpElementButton.setFocusPainted(false);
      this.helpElementButton.setFocusable(false);
      this.helpElementButton.setMargin(new Insets(0, 0, 0, 0));
      this.helpElementButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisElementPanelV2.this.helpElementButtonActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.helpElementButton, "West");
      this.positionLabel.setFont(this.itemsFont);
      this.positionLabel.setText("0/0");
      this.positionLabel.setBorder(new DropShadowBorder(true, true, true, true));
      this.jPanel1.add(this.positionLabel, "East");
      this.elementsPanel.add(this.jPanel1, "East");
      this.jPanel2.add(this.elementsPanel, "First");
      this.elementsScrollPane.setHorizontalScrollBarPolicy(32);
      this.elementsScrollPane.setVerticalScrollBarPolicy(21);
      this.jPanel2.add(this.elementsScrollPane, "Center");
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridwidth = 10;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new Insets(0, 0, 5, 0);
      this.add(this.jPanel2, gridBagConstraints);
      this.enrLabel.setFont(this.itemsFont);
      this.enrLabel.setForeground(SystemColor.windowBorder);
      this.enrLabel.setText("E-Nummer");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.insets = new Insets(0, 0, 0, 3);
      this.add(this.enrLabel, gridBagConstraints);
      this.swLabel.setFont(this.itemsFont);
      this.swLabel.setForeground(SystemColor.windowBorder);
      this.swLabel.setText("SW-Wert");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.insets = new Insets(0, 0, 0, 3);
      this.add(this.swLabel, gridBagConstraints);
      this.richtungLabel.setFont(this.itemsFont);
      this.richtungLabel.setForeground(SystemColor.windowBorder);
      this.richtungLabel.setText("Richtung");
      this.richtungLabel.setToolTipText("Tastenkürzel: Alt+A - F");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 8;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.insets = new Insets(0, 0, 0, 3);
      this.add(this.richtungLabel, gridBagConstraints);
      this.richtungPanel.setLayout(new GridLayout(2, 0));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 9;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridheight = 2;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 0.8;
      this.add(this.richtungPanel, gridBagConstraints);
      this.swwertTF.setDocument(new BoundedPlainDocument(30));
      this.swwertTF.setFont(this.itemsFont);
      this.swwertTF.setText("SW-Wert");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.add(this.swwertTF, gridBagConstraints);
      this.clearENRButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/clear16.png")));
      this.clearENRButton.setToolTipText("E-Nummer leeren");
      this.clearENRButton.setFocusPainted(false);
      this.clearENRButton.setFocusable(false);
      this.clearENRButton.setMargin(new Insets(0, 0, 0, 0));
      this.clearENRButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisElementPanelV2.this.clearENRButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 2;
      this.add(this.clearENRButton, gridBagConstraints);
      this.clearSWwertButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/clear16.png")));
      this.clearSWwertButton.setToolTipText("SW-Wert leeren");
      this.clearSWwertButton.setFocusPainted(false);
      this.clearSWwertButton.setFocusable(false);
      this.clearSWwertButton.setMargin(new Insets(0, 0, 0, 0));
      this.clearSWwertButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisElementPanelV2.this.clearSWwertButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 3;
      this.add(this.clearSWwertButton, gridBagConstraints);
      this.jSeparator1.setOrientation(1);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 3;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridheight = 2;
      gridBagConstraints.fill = 3;
      gridBagConstraints.insets = new Insets(0, 4, 0, 4);
      this.add(this.jSeparator1, gridBagConstraints);
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

   private void clearElementButtonActionPerformed(ActionEvent evt) {
      try {
         this.setElement(gleis.ELEMENT_LEER);
         this.updateGleisbild();
      } catch (NullPointerException var3) {
      }
   }

   private class elementItemListener implements ItemListener {
      private element e;
      private JToggleButton button;

      elementItemListener(JToggleButton b, element ee) {
         super();
         this.e = ee;
         this.button = b;
      }

      public void itemStateChanged(ItemEvent ie) {
         gleisElementPanelV2.this.elementChanged(this.e, this.button.isSelected());
      }
   }

   private class elementKeyAction extends AbstractAction {
      private final JToggleButton b;

      elementKeyAction(JToggleButton b) {
         super();
         this.b = b;
      }

      public void actionPerformed(ActionEvent e) {
         this.b.setSelected(true);
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
         gleisElementPanelV2.this.richtungChanged(this.entry, this.button.isSelected());
      }
   }
}

package js.java.isolate.sim.sim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import js.java.isolate.sim.zug.zug;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.WindowStateSaver.STORESTATES;
import js.java.tools.gui.jsuggestfield.SuggestDecorator;

public class VerspBegruendung extends JDialog implements SessionClose {
   private final zug my_zug;
   private final stellwerksim_main my_main;
   private final SuggestDecorator suggest;
   private final Preferences node;
   private JButton cancelButton;
   private JButton clearButton;
   private JTextField commentArea;
   private JLabel jLabel2;
   private JLabel jLabel4;
   private JPanel jPanel1;
   private JButton okButton;
   private JTextField outputField;
   private JLabel outputHeading;
   private ButtonGroup typGroup;

   public VerspBegruendung(stellwerksim_main a, zug z) {
      super(a, false);
      this.my_main = a;
      this.my_zug = z;
      a.uc.addCloseObject(this);
      Preferences root = Preferences.userNodeForPackage(this.getClass());
      this.node = root.node("texts");
      Vector<String> data = new Vector();

      try {
         List<String> keys = Arrays.asList(this.node.keys());
         Collections.sort(keys);

         for (String k : keys) {
            data.add(this.node.get(k, ""));
         }
      } catch (BackingStoreException var8) {
         Logger.getLogger(VerspBegruendung.class.getName()).log(Level.SEVERE, null, var8);
      }

      this.initComponents();
      this.commentArea.getDocument().addDocumentListener(new DocumentListener() {
         public void insertUpdate(DocumentEvent e) {
            VerspBegruendung.this.updateGui();
         }

         public void removeUpdate(DocumentEvent e) {
            VerspBegruendung.this.updateGui();
         }

         public void changedUpdate(DocumentEvent e) {
            VerspBegruendung.this.updateGui();
         }
      });
      this.suggest = new SuggestDecorator(this, this.commentArea, data);
      this.suggest.addSelectionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VerspBegruendung.this.updateGui();
         }
      });
      this.initAdditionals();
      this.setSize(450, 200);
      this.setName(this.getClass().getSimpleName());
      new WindowStateSaver(this, STORESTATES.LOCATION);
      this.setVisible(true);
   }

   protected JRootPane createRootPane() {
      ActionListener actionListener = new ActionListener() {
         public void actionPerformed(ActionEvent actionEvent) {
            VerspBegruendung.this.setVisible(false);
            VerspBegruendung.this.dispose();
         }
      };
      KeyStroke stroke = KeyStroke.getKeyStroke(27, 0);
      JRootPane rootPane = super.createRootPane();
      rootPane.registerKeyboardAction(actionListener, stroke, 2);
      return rootPane;
   }

   @Override
   public void close() {
      SwingUtilities.invokeLater(() -> this.dispose());
   }

   private void initAdditionals() {
      this.outputField.setText(this.my_zug.getSpezialName());
      this.commentArea.setText(this.my_zug.getUserText());
      this.commentArea.setEditable(this.my_zug.isMytrain());
      this.clearButton.setEnabled(!this.my_zug.getUserText().isEmpty() && this.my_zug.isMytrain());
   }

   private void updateGui() {
      this.okButton.setEnabled(this.commentArea.getText().length() > 5 && this.my_zug.isMytrain());
   }

   private void storeText(String txt) {
      LinkedHashSet<String> data = new LinkedHashSet();

      try {
         List<String> keys = Arrays.asList(this.node.keys());
         Collections.sort(keys);

         for (String k : keys) {
            data.add(this.node.get(k, ""));
         }

         data.add(txt);
         List<String> texts = (List<String>)data.stream().skip((long)Math.max(0, data.size() - 10)).collect(Collectors.toList());
         this.node.clear();
         int cnt = 0;

         for (String v : texts) {
            this.node.put("T" + cnt, v);
            cnt++;
         }
      } catch (BackingStoreException var8) {
         Logger.getLogger(VerspBegruendung.class.getName()).log(Level.SEVERE, null, var8);
      }
   }

   private void initComponents() {
      this.typGroup = new ButtonGroup();
      this.jLabel4 = new JLabel();
      this.outputHeading = new JLabel();
      this.outputField = new JTextField();
      this.jLabel2 = new JLabel();
      this.commentArea = new JTextField();
      this.jPanel1 = new JPanel();
      this.okButton = new JButton();
      this.clearButton = new JButton();
      this.cancelButton = new JButton();
      this.setDefaultCloseOperation(2);
      this.setTitle("Verspätungsmeldung für einen Zug");
      this.setLocationByPlatform(true);
      this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
      this.setMinimumSize(new Dimension(350, 200));
      this.setPreferredSize(new Dimension(450, 200));
      this.setResizable(false);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosed(WindowEvent evt) {
            VerspBegruendung.this.formWindowClosed(evt);
         }
      });
      this.getContentPane().setLayout(new GridBagLayout());
      this.jLabel4.setBackground(Color.black);
      this.jLabel4.setFont(this.jLabel4.getFont().deriveFont(this.jLabel4.getFont().getStyle() | 1));
      this.jLabel4.setForeground(Color.white);
      this.jLabel4
         .setText(
            "<html>Schreibe bzw. ergänze hier eine Verspätungsbegründung und Ursachen für Probleme im Zugablauf. Die Meldung wird, zusammen mit deinem Namen, an andere Stellwerke gemeldet.</html>"
         );
      this.jLabel4.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 10));
      this.jLabel4.setOpaque(true);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridwidth = 5;
      gridBagConstraints.fill = 2;
      this.getContentPane().add(this.jLabel4, gridBagConstraints);
      this.outputHeading.setText("Meldung für Zug");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(10, 10, 2, 5);
      this.getContentPane().add(this.outputHeading, gridBagConstraints);
      this.outputField.setEditable(false);
      this.outputField.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridwidth = 4;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(10, 2, 2, 10);
      this.getContentPane().add(this.outputField, gridBagConstraints);
      this.jLabel2.setText("Text");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 18;
      gridBagConstraints.insets = new Insets(2, 10, 2, 5);
      this.getContentPane().add(this.jLabel2, gridBagConstraints);
      this.commentArea.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            VerspBegruendung.this.commentAreaActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridwidth = 4;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 2, 2, 10);
      this.getContentPane().add(this.commentArea, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.weighty = 1.0;
      this.getContentPane().add(this.jPanel1, gridBagConstraints);
      this.okButton.setFont(this.okButton.getFont().deriveFont(this.okButton.getFont().getStyle() | 1));
      this.okButton.setText("Speichern");
      this.okButton.setEnabled(false);
      this.okButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            VerspBegruendung.this.okButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 10;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.insets = new Insets(5, 5, 10, 5);
      this.getContentPane().add(this.okButton, gridBagConstraints);
      this.clearButton.setText("Löschen");
      this.clearButton.setEnabled(false);
      this.clearButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            VerspBegruendung.this.clearButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 3;
      gridBagConstraints.gridy = 10;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.insets = new Insets(5, 5, 10, 5);
      this.getContentPane().add(this.clearButton, gridBagConstraints);
      this.cancelButton.setText("Abbrechen");
      this.cancelButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            VerspBegruendung.this.cancelButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 10;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.insets = new Insets(5, 5, 10, 10);
      this.getContentPane().add(this.cancelButton, gridBagConstraints);
      this.pack();
   }

   private void okButtonActionPerformed(ActionEvent evt) {
      this.setVisible(false);
      String txt = this.commentArea.getText().trim();
      if (!txt.equalsIgnoreCase(this.my_zug.getUserText())) {
         this.my_zug.setUserText(txt, this.my_main.getGleisbild().getAnlagenname() + ", " + this.my_main.uc.getUsername());
         this.my_main.sendZugUserText(this.my_zug, false);
         this.my_main.refreshZug();
         this.storeText(txt);
      }

      this.dispose();
   }

   private void cancelButtonActionPerformed(ActionEvent evt) {
      this.setVisible(false);
   }

   private void formWindowClosed(WindowEvent evt) {
      this.setVisible(false);
   }

   private void commentAreaActionPerformed(ActionEvent evt) {
      this.updateGui();
      if (this.okButton.isEnabled()) {
         this.okButtonActionPerformed(evt);
      }
   }

   private void clearButtonActionPerformed(ActionEvent evt) {
      this.setVisible(false);
      this.my_zug.setUserText("", "");
      this.my_main.sendZugUserText(this.my_zug, true);
      this.my_main.refreshZug();
      this.dispose();
   }
}

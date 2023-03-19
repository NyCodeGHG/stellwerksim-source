package js.java.isolate.sim.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gleisbildModelStore;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.panels.actionevents.warningEvent;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.ImageSelection;
import js.java.tools.actions.AbstractEvent;
import js.java.tools.gui.warningPopup.IconPopupButton;

public class savePanel extends basePanel implements SessionClose {
   private final IconPopupButton infoLabel;
   private JButton aboutButton;
   private JLabel buildLabel;
   private JPanel buttonPanel;
   private JProgressBar changeCbar;
   private JButton emitterButton;
   private JLabel jLabel1;
   private JPanel jPanel2;
   private JPanel jPanel3;
   private JButton reloadButton;
   private JButton saveButton;
   private JButton simPreviewButton;

   public savePanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
      this.buildLabel.setText("<html><i>Build: " + e.getBuild() + "</i></html>");
      this.infoLabel = new IconPopupButton();
      this.infoLabel.setLargeIcons(true);
      this.infoLabel.setBlinkEnabled(true);
      this.buttonPanel.add(this.infoLabel);
      if (this.my_main.getParameter("anlagenschreiben") == null) {
         this.saveButton.setText("<html>Screenshot ins Clipboard</html>");
      }

      if (this.my_main.getParameter("emittermode") == null || this.my_main.getParameter("emittermode").compareToIgnoreCase("true") != 0) {
         this.emitterButton.setEnabled(false);
      }

      if (this.my_main.getParameter("anlagenlesen") == null) {
         this.reloadButton.setEnabled(false);
      }

      e.registerListener(10, this);
   }

   @Override
   public void close() {
      this.infoLabel.setBlinkEnabled(false);
   }

   @Override
   public void action(AbstractEvent e) {
      if (e instanceof warningEvent) {
         warningEvent we = (warningEvent)e;
         this.setWarning(we.getWarning(), we.getRank());
      }
   }

   private void setWarning(String text, int rank) {
      this.infoLabel.clearWarning();
      if (text != null) {
         this.infoLabel.setBlinkEnabled(rank == 2);
         this.infoLabel.setWarning(text, null);
      }
   }

   @Override
   public void shown(String n, gecBase gec) {
      this.changeCbar.setValue((int)this.glbControl.getModel().getChangeC());
      this.changeCbar.setString(this.glbControl.getModel().getChangeC() + "");
   }

   private void initComponents() {
      this.buttonPanel = new JPanel();
      this.reloadButton = new JButton();
      this.simPreviewButton = new JButton();
      this.saveButton = new JButton();
      this.emitterButton = new JButton();
      this.aboutButton = new JButton();
      this.jPanel2 = new JPanel();
      this.jPanel3 = new JPanel();
      this.buildLabel = new JLabel();
      this.jLabel1 = new JLabel();
      this.changeCbar = new JProgressBar();
      this.setBorder(BorderFactory.createTitledBorder("Datei"));
      this.setLayout(new BorderLayout(0, 15));
      this.buttonPanel.setLayout(new GridLayout(2, 0, 8, 8));
      this.reloadButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/reload16.png")));
      this.reloadButton.setText("<html>gespeicherten Stand laden...</html>");
      this.reloadButton.setHorizontalAlignment(10);
      this.reloadButton.setIconTextGap(10);
      this.reloadButton.setMargin(new Insets(2, 4, 2, 4));
      this.reloadButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            savePanel.this.reloadButtonActionPerformed(evt);
         }
      });
      this.buttonPanel.add(this.reloadButton);
      this.simPreviewButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/preview32.png")));
      this.simPreviewButton.setText("<html>Simulatoransicht Vorschau</html>");
      this.simPreviewButton.setHorizontalAlignment(10);
      this.simPreviewButton.setMargin(new Insets(2, 4, 2, 4));
      this.simPreviewButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            savePanel.this.simPreviewButtonActionPerformed(evt);
         }
      });
      this.buttonPanel.add(this.simPreviewButton);
      this.saveButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/save22.png")));
      this.saveButton.setText("<html>Speichern</html>");
      this.saveButton.setHorizontalAlignment(10);
      this.saveButton.setMargin(new Insets(2, 4, 2, 4));
      this.saveButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            savePanel.this.saveButtonActionPerformed(evt);
         }
      });
      this.buttonPanel.add(this.saveButton);
      this.emitterButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/subway32.png")));
      this.emitterButton.setText("<html>Zugemitter starten...</html>");
      this.emitterButton.setHorizontalAlignment(10);
      this.emitterButton.setIconTextGap(10);
      this.emitterButton.setMargin(new Insets(2, 4, 2, 4));
      this.emitterButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            savePanel.this.emitterButtonActionPerformed(evt);
         }
      });
      this.buttonPanel.add(this.emitterButton);
      this.aboutButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/question22.png")));
      this.aboutButton.setText("Über...");
      this.aboutButton.setDefaultCapable(false);
      this.aboutButton.setHorizontalAlignment(10);
      this.aboutButton.setMargin(new Insets(2, 4, 2, 4));
      this.aboutButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            savePanel.this.aboutButtonActionPerformed(evt);
         }
      });
      this.buttonPanel.add(this.aboutButton);
      this.add(this.buttonPanel, "Center");
      this.jPanel2.setLayout(new GridLayout(1, 0, 10, 0));
      this.jPanel3.setLayout(new GridLayout(1, 0));
      this.jPanel3.add(this.buildLabel);
      this.jLabel1.setForeground(SystemColor.windowBorder);
      this.jLabel1.setHorizontalAlignment(4);
      this.jLabel1.setText("Änderungspunkte");
      this.jPanel3.add(this.jLabel1);
      this.jPanel2.add(this.jPanel3);
      this.changeCbar.setMaximum(350);
      this.changeCbar
         .setToolTipText(
            "<html>Die Änderungspunkte geben einen ungefähren Hinweis<br>über den Grad der Umbauten seit dem letzten Speichern.<br>Über einem Wert von 300 wird das (Zwischen-) Speichern empfohlen.</html>"
         );
      this.changeCbar.setStringPainted(true);
      this.jPanel2.add(this.changeCbar);
      this.add(this.jPanel2, "South");
   }

   private void saveButtonActionPerformed(ActionEvent evt) {
      try {
         ImageSelection.setClipboard(this.glbControl.getModel());
      } catch (Exception var5) {
      }

      boolean dosave = true;
      if (this.glbControl.getModel().gl_overmaxsize() > 0) {
         int r = JOptionPane.showConfirmDialog(
            this,
            "Achtung! Die Größe überschreitet "
               + 10000
               + " Felder um "
               + this.glbControl.getModel().gl_overmaxsize()
               + " Felder,\ndie Anlage wird sich dann mit hoher Wahrscheinlichkeit\nnicht mehr laden lassen.\n\nSoll  trotzdem gespeichert werden?",
            "Anlage zu groß!",
            0,
            2
         );
         if (r == 1) {
            dosave = false;
         }
      }

      if (dosave) {
         this.glbControl.getModel().clearStatus();
         if (this.my_main.getParameter("anlagenschreiben") != null) {
            this.my_main.setGUIEnable(false);
            this.my_main.setPanelInvisible(true);
            gleis.setAllowSmooth(true);
            this.glbControl.getModel().save(this.my_main.getParameter("anlagenschreiben"), new gleisbildModelStore.ioDoneMessage() {
               @Override
               public void done(boolean success) {
                  savePanel.this.my_main.setGUIEnable(true);
               }
            });
         } else {
            try {
               ImageSelection.setClipboard(this.glbControl.getModel());
            } catch (Exception var4) {
               JOptionPane.showMessageDialog(this.saveButton, "Kopieren ins Clipboard nicht möglich gewesen:\n" + var4.getMessage(), "Exception", 0);
            }
         }
      }
   }

   private void reloadButtonActionPerformed(ActionEvent evt) {
      int r = JOptionPane.showConfirmDialog(
         this, "Dies löscht alle nicht gespeicherten Änderungen,\nwirklich den letzten Stand laden?", "Alten Stand laden?", 2, 2
      );
      if (r == 0) {
         this.my_main.setGUIEnable(false);
         this.my_main.setPanelInvisible(true);
         this.glbControl.getModel().load(this.my_main.getParameter("anlagenlesen"), new gleisbildModelStore.ioDoneMessage() {
            @Override
            public void done(boolean success) {
               savePanel.this.my_main.setGUIEnable(true);
            }
         });
      }
   }

   private void emitterButtonActionPerformed(ActionEvent evt) {
      this.my_main.startEmitter();
   }

   private void aboutButtonActionPerformed(ActionEvent evt) {
      this.my_main.setPanelInvisible(true);
      this.my_main.getContext().showAbout();
      this.my_main.setPanelInvisible(false);
   }

   private void simPreviewButtonActionPerformed(ActionEvent evt) {
      this.my_main.showWaitPanel("previewsim", false);
      gleis.setAllowSmooth(true);
      this.glbControl.setSimViewStyle(true);
      this.glbControl.getModel().clearStatus();
      this.glbControl.repaint();
   }
}

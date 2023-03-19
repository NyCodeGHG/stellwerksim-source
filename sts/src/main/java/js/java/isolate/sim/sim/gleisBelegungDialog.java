package js.java.isolate.sim.sim;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.dialogs.htmlmessage1;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.WindowStateSaver.STORESTATES;

public class gleisBelegungDialog extends JDialog implements SessionClose {
   private String bstg = null;
   private stellwerksim_main my_main = null;
   private final zugUndPlanPanel fahrplanPanel;
   private Timer refreshTimer;
   private int refreshCnt = 0;
   private final HashMap<String, JCheckBox> boxen = new HashMap();
   private belegungsPlanPanel bpp;
   private belegungsPlanPanel.belegungsZeitPanel bzp;
   private JButton closeButton;
   private JPanel gleisPanel;
   private JScrollPane gleisScrollPane;
   private JButton helpButton;
   private JLabel jLabel1;
   private JPanel jPanel1;
   private JScrollPane scrollArea;

   public gleisBelegungDialog(stellwerksim_main a, String b, zugUndPlanPanel p) {
      super(a, false);
      this.my_main = a;
      this.bstg = b;
      this.fahrplanPanel = p;
      this.initComponents();
      this.setTitle("Belegung von Gleis " + b);
      this.remove(this.gleisScrollPane);
      this.initAdditionals();
      this.setSize(445, 400);
      this.setName(this.getClass().getSimpleName() + "G");
      new WindowStateSaver(this, STORESTATES.SIZE);
      this.setVisible(true);
   }

   public gleisBelegungDialog(stellwerksim_main a, zugUndPlanPanel p) {
      super(a, false);
      this.my_main = a;
      this.bstg = null;
      this.fahrplanPanel = p;
      this.initComponents();
      this.setTitle("Abfahrtsmonitor");
      this.initGleislist();
      this.initAdditionals();
      this.setSize(445, 400);
      this.setName(this.getClass().getSimpleName());
      new WindowStateSaver(this, STORESTATES.SIZE);
      this.setVisible(true);
   }

   @Override
   public void close() {
      SwingUtilities.invokeLater(() -> this.refreshTimer.stop());
   }

   private void initGleislist() {
      ItemListener ilts = new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            gleisBelegungDialog.this.initAdditionals();
         }
      };

      for(String bst : this.fahrplanPanel.getBahnsteige().getAlleBahnsteig()) {
         JCheckBox cb = new JCheckBox(bst);
         cb.addItemListener(ilts);
         this.gleisPanel.add(cb);
         this.boxen.put(bst, cb);
      }

      this.gleisPanel.invalidate();
      this.gleisScrollPane.invalidate();
      this.doLayout();
   }

   private void initAdditionals() {
      if (this.refreshTimer != null) {
         this.refreshTimer.stop();
      }

      this.bzp = new belegungsPlanPanel.belegungsZeitPanel(this.my_main);
      this.scrollArea.setRowHeaderView(this.bzp);
      this.bpp = new belegungsPlanPanel(this.my_main, this.bstg == null);
      if (this.bstg != null) {
         LinkedList<zugUndPlanPanel.gleisPlan> bplan = this.fahrplanPanel.getBelegungsPlan(this.bstg, 10, 60);
         this.bpp.updatePlan(bplan);
      } else {
         LinkedList<zugUndPlanPanel.gleisPlan> bplan = new LinkedList();
         String gl = "";

         for(Entry<String, JCheckBox> g : this.boxen.entrySet()) {
            if (((JCheckBox)g.getValue()).isSelected()) {
               bplan.addAll(this.fahrplanPanel.getBelegungsPlan(((String)g.getKey()).toString(), 10, 60));
               if (!gl.isEmpty()) {
                  gl = gl + ", ";
               } else {
                  gl = ": ";
               }

               gl = gl + ((String)g.getKey()).toString();
            }
         }

         Collections.sort(bplan);
         this.bpp.updatePlan(bplan);
         this.setTitle("Abfahrtsmonitor" + gl);
      }

      this.scrollArea.setViewportView(this.bpp);
      this.refreshTimer = new Timer(5000, new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            gleisBelegungDialog.this.refreshCnt++;
            if (gleisBelegungDialog.this.refreshCnt > 60) {
               gleisBelegungDialog.this.initAdditionals();
            } else {
               gleisBelegungDialog.this.bzp.repaint();
               if (gleisBelegungDialog.this.refreshCnt % 6 == 0) {
                  gleisBelegungDialog.this.bpp.repaint();
               }
            }
         }
      });
      this.refreshCnt = 0;
      this.refreshTimer.start();
   }

   private void showHelp() {
      String text = "";
      if (this.bstg == null) {
         text = "<html>Der Anfahrtsmonitor zeigt in beliebig wählbaren Bahnsteigen die kommenden Abfahrten. Es können beliebig viele Abfahrtsmonitore für unterschiedliche Anzeigen geöffnet werden.</html>";
      } else {
         text = "<html>Die Gleisbelegung zeigt die kommenden Abfahrten eines Bahnsteigs.</html>";
      }

      htmlmessage1 m = new htmlmessage1(this.my_main, false, "Was ist das?", text);
      m.show();
   }

   private void initComponents() {
      this.scrollArea = new JScrollPane();
      this.jPanel1 = new JPanel();
      this.helpButton = new JButton();
      this.closeButton = new JButton();
      this.gleisScrollPane = new JScrollPane();
      this.gleisPanel = new JPanel();
      this.jLabel1 = new JLabel();
      this.setDefaultCloseOperation(2);
      this.setCursor(new Cursor(0));
      this.setFocusable(false);
      this.setFocusableWindowState(false);
      this.setLocationByPlatform(true);
      this.setMinimumSize(new Dimension(500, 200));
      this.setType(Type.UTILITY);
      this.scrollArea.setMinimumSize(new Dimension(400, 300));
      this.getContentPane().add(this.scrollArea, "Center");
      this.jPanel1.setLayout(new GridLayout(1, 0, 50, 0));
      this.helpButton.setText("was ist das?");
      this.helpButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisBelegungDialog.this.helpButtonActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.helpButton);
      this.closeButton.setText("schließen");
      this.closeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisBelegungDialog.this.closeButtonActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.closeButton);
      this.getContentPane().add(this.jPanel1, "South");
      this.gleisScrollPane.setHorizontalScrollBarPolicy(32);
      this.gleisScrollPane.setVerticalScrollBarPolicy(21);
      this.gleisScrollPane.setMinimumSize(new Dimension(23, 50));
      this.gleisScrollPane.setPreferredSize(new Dimension(2, 50));
      this.gleisPanel.setLayout(new BoxLayout(this.gleisPanel, 2));
      this.jLabel1.setText("Bahnsteige");
      this.jLabel1.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
      this.gleisPanel.add(this.jLabel1);
      this.gleisScrollPane.setViewportView(this.gleisPanel);
      this.getContentPane().add(this.gleisScrollPane, "North");
      this.pack();
   }

   private void closeButtonActionPerformed(ActionEvent evt) {
      this.hide();
      this.dispose();
   }

   private void helpButtonActionPerformed(ActionEvent evt) {
      this.showHelp();
   }
}

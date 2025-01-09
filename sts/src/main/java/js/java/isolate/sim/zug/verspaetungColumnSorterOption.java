package js.java.isolate.sim.zug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.SoftBevelBorder;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.WindowStateSaver.STORESTATES;
import js.java.tools.gui.animCard.AnimComponent;

public class verspaetungColumnSorterOption extends JDialog {
   private final ActionListener up = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         verspaetungColumnSorterOption.this.upButton((verspaetungHandler.COMPARETYPES)Enum.valueOf(verspaetungHandler.COMPARETYPES.class, e.getActionCommand()));
      }
   };
   private final ActionListener down = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         verspaetungColumnSorterOption.this.downButton(
            (verspaetungHandler.COMPARETYPES)Enum.valueOf(verspaetungHandler.COMPARETYPES.class, e.getActionCommand())
         );
      }
   };
   private final HashMap<verspaetungHandler.COMPARETYPES, verspaetungColumnSorterOption.item> items = new HashMap();
   private JComponent lastMoved = null;
   private Color movedColor;
   private JButton cancelButton;
   private JButton defaultsButton;
   private JLabel jLabel1;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JScrollPane jScrollPane1;
   private JButton okButton;
   private JPanel optionPanel;

   public verspaetungColumnSorterOption(Frame parent) {
      super(parent, false);
      this.initComponents();
      ButtonGroup bg1 = new ButtonGroup();
      this.addItem(verspaetungHandler.COMPARETYPES.fertig, "Zug durchgefahren", "Durchgefahrene Züge ans Ende");
      this.addItem(verspaetungHandler.COMPARETYPES.visible, "Zug sichtbar", "Züge auf Gleistisch zu oberst");
      this.addItem(verspaetungHandler.COMPARETYPES.mytrain, "Zug zugeteilt", "Züge, die dem Stellwerksbereich schon zugeteilt sind weiter oben");
      bg1.add(this.addItem(verspaetungHandler.COMPARETYPES.anversp, "nächste Sollankunft + Verspätung"));
      bg1.add(this.addItem(verspaetungHandler.COMPARETYPES.an, "nächste Sollankunft"));
      this.addItem(verspaetungHandler.COMPARETYPES.marknum, "Markierung", "Numerische Markierung");
      this.showOrder();
      this.movedColor = this.optionPanel.getBackground();
      this.movedColor = new Color(this.movedColor.getRGB() - 4473856);
      this.setSize(400, 300);
      this.setLocationRelativeTo(parent);
      this.setName(this.getClass().getSimpleName());
      new WindowStateSaver(this, STORESTATES.LOCATION_AND_SIZE);
      this.setVisible(true);
   }

   private JCheckBox addItem(verspaetungHandler.COMPARETYPES key, String text) {
      return this.addItem(key, text, text);
   }

   private JCheckBox addItem(verspaetungHandler.COMPARETYPES key, String text, String tooltip) {
      verspaetungColumnSorterOption.item i = new verspaetungColumnSorterOption.item();
      i.key = key;
      i.text = text;
      i.pan = new Box(0);
      JButton b = new JButton(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/arrow_up.png")));
      b.setFocusPainted(false);
      b.setActionCommand(key + "");
      b.addActionListener(this.up);
      i.pan.add(b);
      i.cb = new JCheckBox(text);
      i.cb.setFocusPainted(false);
      i.cb.setSelected(verspaetungHandler.compareOrder.contains(key));
      i.cb.setToolTipText(tooltip);
      i.pan.add(i.cb);
      i.pan.add(Box.createHorizontalGlue());
      b = new JButton(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/arrow_down.png")));
      b.setFocusPainted(false);
      b.setActionCommand(key + "");
      b.addActionListener(this.down);
      i.pan.add(b);
      i.pan.setBorder(new SoftBevelBorder(1));
      this.optionPanel.add(i.pan);
      this.items.put(key, i);
      return i.cb;
   }

   private void showOrder() {
      try {
         int c = 0;

         for (verspaetungHandler.COMPARETYPES k : verspaetungHandler.compareOrder) {
            verspaetungColumnSorterOption.item i = (verspaetungColumnSorterOption.item)this.items.get(k);
            this.optionPanel.setComponentZOrder(i.pan, c);
            c++;
         }
      } catch (Exception var5) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "Caught", var5);
      }
   }

   private void upButton(verspaetungHandler.COMPARETYPES key) {
      if (this.lastMoved != null) {
         this.lastMoved.setBackground(this.optionPanel.getBackground());
      }

      this.lastMoved = null;
      verspaetungColumnSorterOption.item i = (verspaetungColumnSorterOption.item)this.items.get(key);
      int c = this.optionPanel.getComponentZOrder(i.pan);
      if (c > 0) {
         AnimComponent ac = new AnimComponent(this.optionPanel);
         this.optionPanel.setComponentZOrder(i.pan, c - 1);
         this.lastMoved = i.cb;
         this.lastMoved.setBackground(this.movedColor);
         this.optionPanel.revalidate();
         ac.paint2();
      }
   }

   private void downButton(verspaetungHandler.COMPARETYPES key) {
      if (this.lastMoved != null) {
         this.lastMoved.setBackground(this.optionPanel.getBackground());
      }

      this.lastMoved = null;
      verspaetungColumnSorterOption.item i = (verspaetungColumnSorterOption.item)this.items.get(key);
      int c = this.optionPanel.getComponentZOrder(i.pan);
      if (c < this.optionPanel.getComponentCount() - 1) {
         AnimComponent ac = new AnimComponent(this.optionPanel);
         this.optionPanel.setComponentZOrder(i.pan, c + 1);
         this.lastMoved = i.cb;
         this.lastMoved.setBackground(this.movedColor);
         this.optionPanel.revalidate();
         ac.paint2();
      }
   }

   private void initComponents() {
      this.jLabel1 = new JLabel();
      this.jPanel1 = new JPanel();
      this.okButton = new JButton();
      this.defaultsButton = new JButton();
      this.cancelButton = new JButton();
      this.jPanel2 = new JPanel();
      this.jScrollPane1 = new JScrollPane();
      this.optionPanel = new JPanel();
      this.setDefaultCloseOperation(2);
      this.setTitle("Spezifiziert die Sortierkriterien und Reihenfolge der Fahrplanspalte \"Verspätung\".");
      this.jLabel1.setText("<html>Spezifiziert die Sortierkriterien und Reihenfolge der Fahrplanspalte \"Verspätung\".</html>");
      this.jLabel1.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
      this.getContentPane().add(this.jLabel1, "North");
      this.jPanel1.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
      this.jPanel1.setLayout(new GridLayout(1, 0, 20, 0));
      this.okButton.setText("Ok");
      this.okButton.setFocusPainted(false);
      this.okButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            verspaetungColumnSorterOption.this.okButtonActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.okButton);
      this.defaultsButton.setText("auf Standard");
      this.defaultsButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            verspaetungColumnSorterOption.this.defaultsButtonActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.defaultsButton);
      this.cancelButton.setText("Abbruch");
      this.cancelButton.setFocusPainted(false);
      this.cancelButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            verspaetungColumnSorterOption.this.cancelButtonActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.cancelButton);
      this.getContentPane().add(this.jPanel1, "South");
      this.jPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      this.jPanel2.setLayout(new BorderLayout());
      this.optionPanel.setLayout(new BoxLayout(this.optionPanel, 1));
      this.jScrollPane1.setViewportView(this.optionPanel);
      this.jPanel2.add(this.jScrollPane1, "Center");
      this.getContentPane().add(this.jPanel2, "Center");
      this.pack();
   }

   private void defaultsButtonActionPerformed(ActionEvent evt) {
      verspaetungHandler.reset();
      this.setVisible(false);
      this.dispose();
   }

   private void cancelButtonActionPerformed(ActionEvent evt) {
      this.setVisible(false);
      this.dispose();
   }

   private void okButtonActionPerformed(ActionEvent evt) {
      ArrayList<verspaetungHandler.COMPARETYPES> ca = new ArrayList();

      for (int i = 0; i < this.optionPanel.getComponentCount(); i++) {
         for (verspaetungColumnSorterOption.item it : this.items.values()) {
            if (it.cb.isSelected() && it.pan == this.optionPanel.getComponent(i)) {
               ca.add(it.key);
               break;
            }
         }
      }

      verspaetungHandler.compareOrder = ca;
      this.setVisible(false);
      this.dispose();
   }

   public static void main(String[] args) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            verspaetungColumnSorterOption dialog = new verspaetungColumnSorterOption(new JFrame());
            dialog.addWindowListener(new WindowAdapter() {
               public void windowClosing(WindowEvent e) {
                  System.exit(0);
               }
            });
         }
      });
   }

   private static class item {
      verspaetungHandler.COMPARETYPES key;
      String text;
      JCheckBox cb;
      Box pan;

      private item() {
      }
   }
}

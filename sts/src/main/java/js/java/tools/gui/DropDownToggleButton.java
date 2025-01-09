package js.java.tools.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedHashMap;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;

public class DropDownToggleButton extends JToggleButton {
   private BasicButtonListener buttonListener;
   private JLabel arrowDownLabel;
   private JLabel textLabel = new JLabel();
   private JPopupMenu popup = new JPopupMenu();
   private JPanel addpanel = new JPanel();
   private JSeparator sep = new JSeparator(1);
   private boolean shouldDiscardRelease = false;
   private JMenuItem currentVisible = null;
   private ActionListener itemListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         DropDownToggleButton.this.currentVisible = (JMenuItem)e.getSource();
         DropDownToggleButton.this.updateObj();
         DropDownToggleButton.this.setSelected(true);
         ActionEvent e2 = new ActionEvent(e.getSource(), e.getID(), e.getActionCommand());
         DropDownToggleButton.this.fireActionPerformed(e2);
      }
   };
   private LinkedHashMap<String, String> items = new LinkedHashMap();
   private Dimension minsize = null;

   public DropDownToggleButton() {
      BasicButtonUI bbui = (BasicButtonUI)this.getUI();
      this.buttonListener = (BasicButtonListener)this.getClientProperty(bbui);
      this.removeMouseListener(this.buttonListener);
      this.addMouseListener(new DropDownToggleButton.DropDownButtonMouseListener());
      this.configureObject();
   }

   public DropDownToggleButton(Action a) {
      super(a);
      BasicButtonUI bbui = (BasicButtonUI)this.getUI();
      this.buttonListener = (BasicButtonListener)this.getClientProperty(bbui);
      this.removeMouseListener(this.buttonListener);
      this.addMouseListener(new DropDownToggleButton.DropDownButtonMouseListener());
      this.configureObject();
   }

   public void setFont(Font f) {
      super.setFont(f);
      if (this.textLabel != null) {
         this.textLabel.setFont(f);
      }
   }

   private void configureObject() {
      this.setLayout(new BorderLayout());
      this.setText(null);
      this.setIcon(null);
      this.setMargin(new Insets(2, 10, 2, 6));
      this.add(this.textLabel, "Center");
      this.addpanel.setLayout(new BorderLayout());
      this.addpanel.add(this.sep, "West");
      this.addpanel.setOpaque(false);
      ImageIcon arrowDownIcon = new ImageIcon(this.getClass().getResource("/js/java/tools/dropdown.gif"));
      this.arrowDownLabel = new JLabel(arrowDownIcon);
      this.addpanel.add(this.arrowDownLabel, "Center");
      this.add(this.addpanel, "East");
   }

   private void updateObj() {
      String t = this.currentVisible.getText();
      this.textLabel.setText(" " + t + "  ");
      this.setActionCommand(this.currentVisible.getActionCommand());
   }

   private JMenuItem add(String t, String a) {
      JMenuItem jm = new JMenuItem(t);
      jm.setActionCommand(a);
      jm.addActionListener(this.itemListener);
      this.popup.add(jm);
      if (this.popup.getComponentCount() == 1) {
         this.currentVisible = jm;
         this.updateObj();
         this.addpanel.setVisible(false);
      } else {
         this.addpanel.setVisible(true);
      }

      return jm;
   }

   public void setEnabled(boolean e) {
      super.setEnabled(e);
      this.textLabel.setEnabled(e);
      this.arrowDownLabel.setEnabled(e);
   }

   public void setItems(String[] items) {
      this.items.clear();

      for (int i = 0; i < items.length; i++) {
         try {
            String[] k = items[i].split(":");
            this.items.put(k[0], k[1]);
         } catch (Exception var4) {
         }
      }

      this.popup.removeAll();

      for (String a : this.items.keySet()) {
         this.add(a, (String)this.items.get(a));
      }

      this.calcMinsize();
   }

   public void addItem(String title, String action) {
      this.items.put(title, action);
      this.popup.removeAll();

      for (String a : this.items.keySet()) {
         this.add(a, (String)this.items.get(a));
      }

      this.calcMinsize();
   }

   private void calcMinsize() {
      Dimension nd = new Dimension(0, 0);
      if (this.minsize != null) {
         nd.width = Math.max(this.minsize.width, nd.width);
         nd.height = Math.max(this.minsize.height, nd.height);
      }

      for (String t : this.items.keySet()) {
         this.textLabel.setText(" " + t + "  ");
         this.repaint();
         Dimension d = super.getMinimumSize();
         nd.width = Math.max(d.width + 10, nd.width);
         nd.height = Math.max(d.height, nd.height);
      }

      this.minsize = nd;
      this.updateObj();
   }

   public Dimension getMinimumSize() {
      return this.minsize == null ? super.getMinimumSize() : this.minsize;
   }

   public Dimension getPreferredSize() {
      return this.minsize == null ? super.getMinimumSize() : this.minsize;
   }

   private class DropDownButtonMouseListener implements MouseListener {
      private DropDownButtonMouseListener() {
      }

      public void mouseClicked(MouseEvent e) {
         if (DropDownToggleButton.this.buttonListener != null) {
            DropDownToggleButton.this.buttonListener.mouseClicked(e);
         }
      }

      public void mouseEntered(MouseEvent e) {
         if (DropDownToggleButton.this.buttonListener != null) {
            DropDownToggleButton.this.buttonListener.mouseEntered(e);
         }
      }

      public void mouseExited(MouseEvent e) {
         if (DropDownToggleButton.this.buttonListener != null) {
            DropDownToggleButton.this.buttonListener.mouseExited(e);
         }
      }

      public void mousePressed(MouseEvent e) {
         if (DropDownToggleButton.this.buttonListener != null) {
            DropDownToggleButton.this.buttonListener.mousePressed(e);
         }

         if (DropDownToggleButton.this.isEnabled()
            && DropDownToggleButton.this.contains(e.getX(), e.getY())
            && e.getX() > DropDownToggleButton.this.addpanel.getBounds().x
            && DropDownToggleButton.this.popup.getComponentCount() > 1) {
            DropDownToggleButton.this.shouldDiscardRelease = true;
            DropDownToggleButton.this.popup.show(DropDownToggleButton.this, 0, DropDownToggleButton.this.getHeight() - 2);
            if (DropDownToggleButton.this.popup.getWidth() < DropDownToggleButton.this.getWidth()) {
               DropDownToggleButton.this.popup.setPopupSize(DropDownToggleButton.this.getWidth(), DropDownToggleButton.this.popup.getHeight());
            }
         }
      }

      public void mouseReleased(MouseEvent e) {
         if (DropDownToggleButton.this.shouldDiscardRelease) {
            DropDownToggleButton.this.getModel().setArmed(false);
            DropDownToggleButton.this.shouldDiscardRelease = false;
         }

         if (DropDownToggleButton.this.buttonListener != null) {
            DropDownToggleButton.this.buttonListener.mouseReleased(e);
         }
      }
   }
}

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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;

public class DropDownButton extends JButton {
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
         DropDownButton.this.currentVisible = (JMenuItem)e.getSource();
         DropDownButton.this.updateObj();
         ActionEvent e2 = new ActionEvent(e.getSource(), e.getID(), e.getActionCommand());
         DropDownButton.this.fireActionPerformed(e2);
      }
   };
   private LinkedHashMap<String, String> items = new LinkedHashMap();
   private Dimension minsize = null;

   public DropDownButton() {
      super();
      BasicButtonUI bbui = (BasicButtonUI)this.getUI();
      this.buttonListener = (BasicButtonListener)this.getClientProperty(bbui);
      this.removeMouseListener(this.buttonListener);
      this.addMouseListener(new DropDownButton.DropDownButtonMouseListener());
      this.configureObject();
   }

   public DropDownButton(Action a) {
      super(a);
      BasicButtonUI bbui = (BasicButtonUI)this.getUI();
      this.buttonListener = (BasicButtonListener)this.getClientProperty(bbui);
      this.removeMouseListener(this.buttonListener);
      this.addMouseListener(new DropDownButton.DropDownButtonMouseListener());
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
      this.setMargin(new Insets(0, 10, 0, 6));
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

   public void addItem(String title, String action) {
      this.items.put(title, action);
      this.popup.removeAll();

      for(String a : this.items.keySet()) {
         this.add(a, (String)this.items.get(a));
      }

      this.calcMinsize();
   }

   public void setItems(LinkedHashMap<String, String> items) {
      this.items = items;
      this.popup.removeAll();

      for(String a : this.items.keySet()) {
         this.add(a, (String)this.items.get(a));
      }

      this.calcMinsize();
   }

   public void setItems(String[] items) {
      this.items.clear();

      for(int i = 0; i < items.length; ++i) {
         try {
            String[] k = items[i].split(":");
            this.items.put(k[0], k[1]);
         } catch (Exception var4) {
         }
      }

      this.popup.removeAll();

      for(String a : this.items.keySet()) {
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

      for(String t : this.items.keySet()) {
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
         super();
      }

      public void mouseClicked(MouseEvent e) {
         if (DropDownButton.this.buttonListener != null) {
            DropDownButton.this.buttonListener.mouseClicked(e);
         }
      }

      public void mouseEntered(MouseEvent e) {
         if (DropDownButton.this.buttonListener != null) {
            DropDownButton.this.buttonListener.mouseEntered(e);
         }
      }

      public void mouseExited(MouseEvent e) {
         if (DropDownButton.this.buttonListener != null) {
            DropDownButton.this.buttonListener.mouseExited(e);
         }
      }

      public void mousePressed(MouseEvent e) {
         if (DropDownButton.this.buttonListener != null) {
            DropDownButton.this.buttonListener.mousePressed(e);
         }

         if (DropDownButton.this.isEnabled()
            && DropDownButton.this.contains(e.getX(), e.getY())
            && e.getX() > DropDownButton.this.addpanel.getBounds().x
            && DropDownButton.this.popup.getComponentCount() > 1) {
            DropDownButton.this.shouldDiscardRelease = true;
            DropDownButton.this.popup.show(DropDownButton.this, 0, DropDownButton.this.getHeight() - 2);
            if (DropDownButton.this.popup.getWidth() < DropDownButton.this.getWidth()) {
               DropDownButton.this.popup.setPopupSize(DropDownButton.this.getWidth(), DropDownButton.this.popup.getHeight());
            }
         }
      }

      public void mouseReleased(MouseEvent e) {
         if (DropDownButton.this.shouldDiscardRelease) {
            DropDownButton.this.getModel().setArmed(false);
            DropDownButton.this.shouldDiscardRelease = false;
         }

         if (DropDownButton.this.buttonListener != null) {
            DropDownButton.this.buttonListener.mouseReleased(e);
         }
      }
   }
}

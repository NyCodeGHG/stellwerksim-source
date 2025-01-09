package js.java.tools.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;

public class DropDownListButton extends JButton {
   private final BasicButtonListener buttonListener;
   private JPopupMenu popup = new JPopupMenu();
   private final JLabel textLabel = new JLabel();
   private final JPanel addpanel = new JPanel();
   private JLabel arrowDownLabel;
   private boolean shouldDiscardRelease = false;
   private JMenuItem currentVisible = null;
   private final ActionListener itemListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         JMenuItem newvis = (JMenuItem)e.getSource();
         if (newvis != DropDownListButton.this.currentVisible) {
            DropDownListButton.this.currentVisible = newvis;
            DropDownListButton.this.updateObj();
            ActionEvent e2 = new ActionEvent(e.getSource(), e.getID(), e.getActionCommand());
            DropDownListButton.this.fireActionPerformed(e2);
            ItemEvent ie2 = new ItemEvent(DropDownListButton.this.getModel(), 701, DropDownListButton.this.currentVisible, 1);
            DropDownListButton.this.fireItemStateChanged(ie2);
         }
      }
   };
   private Dimension minsize = null;
   private final HashMap<JMenuItem, String> items = new HashMap();

   protected void fireItemStateChanged(ItemEvent event) {
      Object[] listeners = this.listenerList.getListenerList();

      for (int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == ItemListener.class) {
            ((ItemListener)listeners[i + 1]).itemStateChanged(event);
         }
      }
   }

   public DropDownListButton() {
      BasicButtonUI bbui = (BasicButtonUI)this.getUI();
      this.buttonListener = (BasicButtonListener)this.getClientProperty(bbui);
      this.removeMouseListener(this.buttonListener);
      this.addMouseListener(new DropDownListButton.DropDownButtonMouseListener());
      this.configureObject();
   }

   public DropDownListButton(Action a) {
      super(a);
      BasicButtonUI bbui = (BasicButtonUI)this.getUI();
      this.buttonListener = (BasicButtonListener)this.getClientProperty(bbui);
      this.removeMouseListener(this.buttonListener);
      this.addMouseListener(new DropDownListButton.DropDownButtonMouseListener());
      this.configureObject();
   }

   private void configureObject() {
      this.setLayout(new BorderLayout());
      super.setText(null);
      super.setIcon(null);
      this.add(this.textLabel, "Center");
      this.addpanel.setLayout(new BorderLayout());
      this.addpanel.setOpaque(false);
      ImageIcon arrowDownIcon = new ImageIcon(this.getClass().getResource("/js/java/tools/dropdown.gif"));
      this.arrowDownLabel = new JLabel(arrowDownIcon);
      this.addpanel.add(this.arrowDownLabel, "Center");
      this.addpanel.add(new JSeparator(1), "East");
      this.add(this.addpanel, "West");
   }

   public void setFont(Font f) {
      super.setFont(f);
      if (this.popup != null) {
         this.popup.setFont(f);
      }

      if (this.textLabel != null) {
         this.textLabel.setFont(f);
      }
   }

   public void setText(String t) {
   }

   public void setCurrent(JMenuItem m) {
      this.currentVisible = m;
      this.updateObj();
   }

   public JMenuItem getCurrent() {
      return this.currentVisible;
   }

   private void updateObj() {
      if (this.currentVisible != null) {
         this.textLabel.setText(" " + this.currentVisible.getText());
         this.setActionCommand(this.currentVisible.getActionCommand());
         this.repaint();
      } else {
         this.textLabel.setText("");
         this.setActionCommand(null);
      }
   }

   public void setEnabled(boolean e) {
      super.setEnabled(e);
      this.arrowDownLabel.setEnabled(e);
   }

   public void setPopup(JPopupMenu p) {
      this.popup = p;
      this.popup.setFont(this.getFont());
      this.prepareMenu();
   }

   private void prepareMenu(Component c, Dimension nd) {
      if (c instanceof JMenuItem) {
         JMenuItem m = (JMenuItem)c;
         this.textLabel.setText(" " + m.getText() + " ");
         this.repaint();
         Dimension d = this.textLabel.getMinimumSize();
         nd.width = Math.max(d.width + 10, nd.width);
         nd.height = Math.max(d.height, nd.height);
         m.addActionListener(this.itemListener);
         this.items.put(m, m.getText());
         if (m instanceof JMenu) {
            JMenu jm = (JMenu)m;

            for (int i = 0; i < jm.getItemCount(); i++) {
               Component c2 = jm.getItem(i);
               this.prepareMenu(c2, nd);
            }
         }
      }
   }

   private void prepareMenu() {
      this.items.clear();
      Dimension nd = new Dimension(super.getMinimumSize());
      if (this.minsize != null) {
         nd.width = Math.max(this.minsize.width, nd.width);
         nd.height = Math.max(this.minsize.height, nd.height);
      }

      for (int i = 0; i < this.popup.getComponentCount(); i++) {
         Component c = this.popup.getComponent(i);
         this.prepareMenu(c, nd);
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

   public Dimension getMaximumSize() {
      return this.minsize == null ? super.getMinimumSize() : this.minsize;
   }

   private class DropDownButtonMouseListener implements MouseListener {
      private DropDownButtonMouseListener() {
      }

      public void mouseClicked(MouseEvent e) {
         if (DropDownListButton.this.buttonListener != null) {
            DropDownListButton.this.buttonListener.mouseClicked(e);
         }
      }

      public void mouseEntered(MouseEvent e) {
         if (DropDownListButton.this.buttonListener != null) {
            DropDownListButton.this.buttonListener.mouseEntered(e);
         }
      }

      public void mouseExited(MouseEvent e) {
         if (DropDownListButton.this.buttonListener != null) {
            DropDownListButton.this.buttonListener.mouseExited(e);
         }
      }

      public void mousePressed(MouseEvent e) {
         if (DropDownListButton.this.buttonListener != null) {
            DropDownListButton.this.buttonListener.mousePressed(e);
         }

         if (DropDownListButton.this.isEnabled()
            && DropDownListButton.this.contains(e.getX(), e.getY())
            && DropDownListButton.this.popup.getComponentCount() > 1) {
            DropDownListButton.this.shouldDiscardRelease = true;
            DropDownListButton.this.popup.show(DropDownListButton.this, 0, DropDownListButton.this.getHeight() - 2);
            if (DropDownListButton.this.popup.getWidth() < DropDownListButton.this.getWidth()) {
               DropDownListButton.this.popup.setPopupSize(DropDownListButton.this.getWidth(), DropDownListButton.this.popup.getHeight());
            }

            if (DropDownListButton.this.currentVisible != null) {
               LinkedList<MenuElement> elements = new LinkedList();
               this.showMenuRecursive(DropDownListButton.this.currentVisible, elements);
               MenuElement[] me = new MenuElement[elements.size()];
               elements.toArray(me);
               MenuSelectionManager.defaultManager().setSelectedPath(me);
            }
         }
      }

      private void showMenuRecursive(JMenuItem m, LinkedList<MenuElement> ll) {
         ll.addFirst(m);
         JPopupMenu p = (JPopupMenu)m.getParent();
         if (p != null) {
            ll.addFirst(p);

            try {
               JMenuItem mi = (JMenuItem)p.getInvoker();
               if (mi != null) {
                  this.showMenuRecursive(mi, ll);
               }
            } catch (ClassCastException var5) {
            }
         }
      }

      public void mouseReleased(MouseEvent e) {
         if (DropDownListButton.this.shouldDiscardRelease) {
            DropDownListButton.this.getModel().setArmed(false);
            DropDownListButton.this.shouldDiscardRelease = false;
         }

         if (DropDownListButton.this.buttonListener != null) {
            DropDownListButton.this.buttonListener.mouseReleased(e);
         }
      }
   }
}

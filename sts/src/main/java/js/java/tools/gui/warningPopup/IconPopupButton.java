package js.java.tools.gui.warningPopup;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.Timer;
import javax.swing.JPopupMenu.Separator;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;

public class IconPopupButton extends AbstractButton {
   private BasicButtonListener buttonListener;
   private JPopupMenu popup = new JPopupMenu();
   private JLabel textLabel = new JLabel();
   private boolean shouldDiscardRelease = false;
   private LinkedList<warningItems> wlist = null;
   private final Dimension minsize = new Dimension(7, 10);
   private warningItems lastSelected = null;
   private String headmessage = "";
   private boolean solutionsenabled = true;
   private boolean largeIcon = false;
   private boolean hasIcon = false;
   private Timer blinkTimer;
   private final ActionListener popupItemListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         JMenuItem currentVisible = (JMenuItem)e.getSource();
         if (IconPopupButton.this.wlist != null) {
            ActionEvent e2 = null;

            for(warningItems i : IconPopupButton.this.wlist) {
               if (currentVisible == i.getMenu()) {
                  IconPopupButton.this.lastSelected = i;
                  e2 = new ActionEvent(currentVisible, e.getID(), e.getActionCommand());
                  break;
               }
            }

            if (e2 != null) {
               IconPopupButton.this.fireActionPerformed(e2);
            }
         }
      }
   };
   private final ActionListener blinkListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         if (IconPopupButton.this.hasIcon) {
            if (IconPopupButton.this.textLabel.getIcon() == null) {
               IconPopupButton.this.setIcon();
            } else {
               IconPopupButton.this.textLabel.setIcon(null);
            }
         }
      }
   };

   public IconPopupButton() {
      super();
      BasicButtonUI bbui = (BasicButtonUI)this.getUI();
      this.buttonListener = (BasicButtonListener)this.getClientProperty(bbui);
      this.removeMouseListener(this.buttonListener);
      this.addMouseListener(new IconPopupButton.IconPopupButtonMouseListener());
      this.configureObject();
   }

   private void configureObject() {
      this.setLayout(new BorderLayout());
      this.add(this.textLabel, "Center");
      this.setText(null);
      this.setIcon(null);
      this.setFocusPainted(false);
      this.setFocusable(false);
      this.setMargin(new Insets(0, 0, 0, 0));
      this.setBorder(null);
      this.popup = new JPopupMenu();
   }

   public void setSolvingEnabled(boolean e) {
      this.solutionsenabled = e;
   }

   public boolean isSolvingEnabled() {
      return this.solutionsenabled;
   }

   public void setBlinkEnabled(boolean e) {
      if (!e && this.blinkTimer != null) {
         this.blinkTimer.stop();
         this.blinkTimer = null;
      } else if (e && this.blinkTimer == null) {
         this.blinkTimer = new Timer(1000, this.blinkListener);
         this.blinkTimer.start();
      }
   }

   public void setLargeIcons(boolean e) {
      this.largeIcon = e;
   }

   public Dimension getMinimumSize() {
      return this.minsize;
   }

   public Dimension getMaximumSize() {
      return this.minsize;
   }

   public Dimension getPreferredSize() {
      return this.minsize;
   }

   private void setIcon() {
      if (this.largeIcon) {
         this.textLabel.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/warningMessage.png")));
      } else {
         this.textLabel.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/warningIcon.png")));
      }
   }

   private void enableIcon() {
      this.setIcon();
      this.hasIcon = true;
   }

   public void setWarning(String headmessage, LinkedList<warningItems> w) {
      if (this.wlist != null) {
         this.wlist.clear();
      }

      this.wlist = w;
      this.headmessage = headmessage;
      this.popup.removeAll();
      JLabel head = new JLabel(headmessage);
      head.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/warningMessage.png")));
      this.popup.add(head);
      if (this.wlist != null) {
         if (!this.wlist.isEmpty()) {
            this.popup.add(new Separator());
         }

         ImageIcon ic = new ImageIcon(this.getClass().getResource("/js/java/tools/resources/warningSolution.png"));

         for(warningItems i : this.wlist) {
            JMenuItem jm = new JMenuItem(i.getMessage(), ic);
            jm.setEnabled(this.solutionsenabled);
            jm.addActionListener(this.popupItemListener);
            i.setMenu(jm);
            this.popup.add(jm);
         }
      }

      this.lastSelected = null;
      this.setToolTipText(headmessage);
      this.enableIcon();
   }

   public void addWarning(String headmessage, LinkedList<warningItems> w) {
      if (this.wlist == null) {
         this.wlist = new LinkedList();
      }

      if (w != null) {
         this.wlist.addAll(w);
      }

      if (this.headmessage.length() > 0) {
         this.headmessage = this.headmessage + "<br>";
         this.popup.add(new Separator());
      }

      this.headmessage = this.headmessage + headmessage;
      JLabel head = new JLabel(headmessage);
      head.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/warningMessage.png")));
      this.popup.add(head);
      if (w != null) {
         if (!w.isEmpty()) {
            this.popup.add(new Separator());
         }

         ImageIcon ic = new ImageIcon(this.getClass().getResource("/js/java/tools/resources/warningSolution.png"));

         for(warningItems i : w) {
            JMenuItem jm = new JMenuItem(i.getMessage(), ic);
            jm.setEnabled(this.solutionsenabled);
            jm.addActionListener(this.popupItemListener);
            i.setMenu(jm);
            this.popup.add(jm);
         }
      }

      this.lastSelected = null;
      this.setToolTipText("<html>" + this.headmessage + "</html>");
      this.enableIcon();
   }

   public void clearWarning() {
      if (this.wlist != null) {
         this.wlist.clear();
      }

      this.wlist = null;
      this.headmessage = "";
      this.popup.removeAll();
      this.lastSelected = null;
      this.hasIcon = false;
      this.textLabel.setIcon(null);
      this.setToolTipText("");
   }

   public warningItems getLastSelected() {
      return this.lastSelected;
   }

   private class IconPopupButtonMouseListener implements MouseListener {
      private IconPopupButtonMouseListener() {
         super();
      }

      public void mouseClicked(MouseEvent e) {
         if (IconPopupButton.this.buttonListener != null) {
            IconPopupButton.this.buttonListener.mouseClicked(e);
         }
      }

      public void mouseEntered(MouseEvent e) {
         if (IconPopupButton.this.buttonListener != null) {
            IconPopupButton.this.buttonListener.mouseEntered(e);
         }
      }

      public void mouseExited(MouseEvent e) {
         if (IconPopupButton.this.buttonListener != null) {
            IconPopupButton.this.buttonListener.mouseExited(e);
         }
      }

      public void mousePressed(MouseEvent e) {
         if (IconPopupButton.this.buttonListener != null) {
            IconPopupButton.this.buttonListener.mousePressed(e);
         }

         if (IconPopupButton.this.isEnabled()) {
            IconPopupButton.this.shouldDiscardRelease = true;
            if (IconPopupButton.this.popup.getComponentCount() > 0) {
               int w = IconPopupButton.this.getWidth();
               int h = IconPopupButton.this.getHeight();

               try {
                  h = IconPopupButton.this.textLabel.getIcon().getIconHeight();
               } catch (NullPointerException var6) {
               }

               try {
                  w = IconPopupButton.this.textLabel.getIcon().getIconWidth();
               } catch (NullPointerException var5) {
               }

               IconPopupButton.this.popup.show(IconPopupButton.this, w + 5, h);
            }
         }
      }

      public void mouseReleased(MouseEvent e) {
         if (IconPopupButton.this.shouldDiscardRelease) {
            IconPopupButton.this.shouldDiscardRelease = false;
         }

         if (IconPopupButton.this.buttonListener != null) {
            IconPopupButton.this.buttonListener.mouseReleased(e);
         }
      }
   }
}

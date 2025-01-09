package js.java.tools.balloontip;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class BalloonTip extends JPanel {
   private BalloonBorder border;
   private JLabel label = new JLabel();
   private Component attachedComponent;
   private JPanel closePanel;
   private JButton closeButton;
   private boolean closeAdded = false;
   private static final boolean DONOTUSE = false;
   private int hideDelay = 0;

   public BalloonTip(Component attachedComponent) {
      this(attachedComponent, new Color(255, 255, 225), 10, 20);
   }

   public BalloonTip(Component attachedComponent, Color fillColor, int borderWidth, int offset) {
      this.attachedComponent = attachedComponent;
      this.label.setBorder(new EmptyBorder(borderWidth, borderWidth, borderWidth, borderWidth));
      this.label.setBackground(fillColor);
      this.label.setOpaque(true);
      this.setBorder(this.border = new BalloonBorder(fillColor, offset));
      this.setOpaque(false);
      this.setLayout(new BorderLayout());
      this.add(this.label, "Center");
      this.closePanel = new JPanel();
      this.closePanel.setBackground(fillColor);
      this.closePanel.setLayout(new BoxLayout(this.closePanel, 3));
      this.closePanel.setBorder(BorderFactory.createEmptyBorder(2, 1, 1, 2));
      this.closeButton = new JButton();
      this.closeButton.setBackground(fillColor);
      this.closeButton.setFont(new Font("Dialog", 0, 10));
      this.closeButton.setText("X");
      this.closeButton.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
      this.closeButton.setMaximumSize(new Dimension(9, 9));
      this.closeButton.setMinimumSize(new Dimension(9, 9));
      this.closeButton.setPreferredSize(new Dimension(9, 9));
      this.closeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            BalloonTip.this.setVisible(false);
         }
      });
      this.closePanel.add(this.closeButton);
      this.setVisible(false);
      Container parent = attachedComponent.getParent();

      JLayeredPane layeredPane;
      while (true) {
         if (parent instanceof JFrame) {
            layeredPane = ((JFrame)parent).getLayeredPane();
            break;
         }

         if (parent instanceof JDialog) {
            layeredPane = ((JDialog)parent).getLayeredPane();
            break;
         }

         if (parent instanceof JInternalFrame) {
            layeredPane = ((JInternalFrame)parent).getLayeredPane();
            break;
         }

         if (parent instanceof JApplet) {
            layeredPane = ((JApplet)parent).getLayeredPane();
            break;
         }

         parent = parent.getParent();
      }

      layeredPane.add(this, JLayeredPane.POPUP_LAYER);
      attachedComponent.addComponentListener(new ComponentAdapter() {
         public void componentMoved(ComponentEvent e) {
            if (BalloonTip.this.isShowing()) {
               BalloonTip.this.determineAndSetLocation();
            }
         }
      });
   }

   private void determineAndSetLocation() {
      Point location = SwingUtilities.convertPoint(this.attachedComponent, this.getLocation(), this);
      this.setBounds(location.x, location.y - this.getPreferredSize().height, this.getPreferredSize().width, this.getPreferredSize().height);
   }

   public void setText(String text) {
      this.label.setText(text);
   }

   public void setIcon(Icon icon) {
      this.label.setIcon(icon);
   }

   public void setIconTextGap(int iconTextGap) {
      this.label.setIconTextGap(iconTextGap);
   }

   public void setHideDelay(int d) {
      this.hideDelay = d * 1000;
   }

   public void setCloseButton(boolean cb) {
      if (cb) {
         if (!this.closeAdded) {
            this.closeAdded = true;
            this.add(this.closePanel, "East");
         }
      } else if (this.closeAdded) {
         this.closeAdded = false;
         this.remove(this.closePanel);
      }
   }

   public void setVisible(boolean show) {
      if (show) {
         this.determineAndSetLocation();
      }

      super.setVisible(show);
      if (show && this.hideDelay > 0) {
         Timer t = new Timer(this.hideDelay, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               BalloonTip.this.setVisible(false);
            }
         });
         t.setRepeats(false);
         t.start();
      }
   }
}

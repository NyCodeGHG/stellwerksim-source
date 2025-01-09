package js.java.isolate.sim.sim;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.WindowStateSaver.STORESTATES;

public class externalPanel extends JPanel {
   public static final int TEXT_CONTROL = 1;
   private final stellwerksim_main my_main;
   private final ActionListener closeAction;
   private final externalPanel.EP_Interface my_window;
   private JButton closeButton;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JSeparator jSeparator1;

   public externalPanel(stellwerksim_main m, ActionListener closeAction, String title, boolean dialog) {
      this.my_main = m;
      this.closeAction = closeAction;
      if (dialog) {
         this.my_window = new externalPanel.EP_Dialog(this.my_main);
      } else {
         this.my_window = new externalPanel.EP_Frame(this.my_main);
      }

      this.my_window.setName(title);
      this.my_window.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent evt) {
            externalPanel.this.exitForm(evt);
         }
      });
      this.initComponents();
      this.my_window.setTitle(title + " - " + this.my_main.getGleisbild().getAnlagenname() + " - StellwerkSim");
      this.my_window.setLayout(new BorderLayout());
      this.my_window.add(this, "Center");
      this.my_window.setIconImage(Toolkit.getDefaultToolkit().createImage(this.getClass().getResource("/js/java/tools/resources/funk.gif")));
   }

   private void initComponents() {
      this.jPanel1 = new JPanel();
      this.jSeparator1 = new JSeparator();
      this.jPanel2 = new JPanel();
      this.closeButton = new JButton();
      this.setFont(new Font("Dialog", 0, 10));
      this.setLayout(new BorderLayout());
      this.jPanel1.setLayout(new BorderLayout());
      this.jSeparator1.setMaximumSize(new Dimension(32767, 2));
      this.jPanel1.add(this.jSeparator1, "North");
      this.jPanel2.setLayout(new FlowLayout(0, 0, 0));
      this.closeButton.setFont(new Font("Tahoma", 0, 8));
      this.closeButton.setText("X");
      this.closeButton.setToolTipText("zurück zur Einfensteransicht");
      this.closeButton.setContentAreaFilled(false);
      this.closeButton.setFocusPainted(false);
      this.closeButton.setFocusable(false);
      this.closeButton.setMargin(new Insets(0, 0, 0, 0));
      this.closeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            externalPanel.this.closeButtonActionPerformed(evt);
         }
      });
      this.jPanel2.add(this.closeButton);
      this.jPanel1.add(this.jPanel2, "South");
      this.add(this.jPanel1, "South");
   }

   private void exitForm(WindowEvent evt) {
      this.closeAction.actionPerformed(null);
   }

   private void closeButtonActionPerformed(ActionEvent evt) {
      this.exitForm(null);
   }

   public void setPanel(JPanel ic, int width, int height) {
      this.add(ic, "Center");
      this.my_window.setLocationRelativeTo(this.my_main);
      this.my_window.pack();
      this.my_window.setSize(width, height);
      this.my_window.setVisible(true);
   }

   public void setWindowPosition(int y, int height) {
      this.my_window.setLocation(this.my_window.getX(), y);
      this.my_window.setSize(this.my_window.getWidth(), height);
   }

   public void createStateSaver() {
      if (this.my_window instanceof JFrame) {
         new WindowStateSaver((JFrame)this.my_window, STORESTATES.LOCATION_AND_SIZE);
      } else {
         new WindowStateSaver((JDialog)this.my_window, STORESTATES.LOCATION_AND_SIZE);
      }
   }

   public void rmPanel() {
      this.my_window.setVisible(false);
      this.removeAll();
      this.my_window.dispose();
   }

   private static class EP_Dialog extends JDialog implements externalPanel.EP_Interface {
      EP_Dialog(JFrame parent) {
         super(parent);
         this.setDefaultCloseOperation(0);
         this.setType(Type.UTILITY);
      }
   }

   private static class EP_Frame extends JFrame implements externalPanel.EP_Interface {
      EP_Frame(JFrame parent) {
         this.setDefaultCloseOperation(0);
      }
   }

   interface EP_Interface {
      void setName(String var1);

      void addWindowListener(WindowListener var1);

      void setTitle(String var1);

      void setLayout(LayoutManager var1);

      void add(Component var1, Object var2);

      void setIconImage(Image var1);

      void setLocationRelativeTo(Component var1);

      void pack();

      void setSize(int var1, int var2);

      void setVisible(boolean var1);

      int getX();

      int getWidth();

      void dispose();

      void setLocation(int var1, int var2);
   }
}

package js.java.schaltungen.toplevelMessage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class TopLevelMessage extends JDialog implements ActionListener {
   private final String message;
   private final Timer timer;
   private States state = States.START;
   final int screenWidth;
   final int totalWidth;
   int x;
   int y;
   int waiting;
   final int delay;
   private JPanel contentPanel;
   private JLabel jLabel1;
   private JPanel jPanel1;
   private JLabel msgLabel;

   public TopLevelMessage(String message, int seconds) {
      super();
      this.message = message;
      this.delay = 20 * seconds;
      this.initComponents();
      this.timer = new Timer(50, this);
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      this.screenWidth = dim.width;
      this.totalWidth = this.getScreenMaxX();
   }

   public TopLevelMessage(JComponent message, int seconds) {
      super();
      this.message = "";
      this.delay = 20 * seconds;
      this.initComponents();
      this.contentPanel.removeAll();
      this.contentPanel.add(message, "Center");
      this.pack();
      this.timer = new Timer(50, this);
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      this.screenWidth = dim.width;
      this.totalWidth = this.getScreenMaxX();
   }

   private int getScreenMaxX() {
      Rectangle virtualBounds = new Rectangle();
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice[] gs = ge.getScreenDevices();

      for(int j = 0; j < gs.length; ++j) {
         GraphicsDevice gd = gs[j];
         GraphicsConfiguration[] gc = gd.getConfigurations();

         for(int i = 0; i < gc.length; ++i) {
            virtualBounds = virtualBounds.union(gc[i].getBounds());
         }
      }

      return virtualBounds.width + virtualBounds.x;
   }

   public void start() {
      this.state = States.START;
      this.timer.start();
   }

   public void stop() {
      if (this.state == States.MOVEIN || this.state == States.START || this.state == States.SHOWWAITING2) {
         this.waiting = 10;
         this.setState(States.SHOWWAITING);
      }
   }

   void xy(int _x, int _y) {
      this.x = _x;
      this.y = _y;
      this.setLocation(this.x, this.y);
   }

   public void setState(States s) {
      this.state = s;
   }

   void end() {
      this.setVisible(false);
      this.timer.stop();
      this.timer.removeActionListener(this);
      this.contentPanel.removeAll();
      this.dispose();
   }

   public void actionPerformed(ActionEvent e) {
      this.state.run(this);
   }

   private void initComponents() {
      this.jPanel1 = new JPanel();
      this.contentPanel = new JPanel();
      this.msgLabel = new JLabel();
      this.jLabel1 = new JLabel();
      this.setDefaultCloseOperation(2);
      this.setFocusable(false);
      this.setFocusableWindowState(false);
      this.setUndecorated(true);
      this.setResizable(false);
      this.setType(Type.POPUP);
      this.jPanel1.setBackground(Color.orange);
      this.jPanel1
         .setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)), BorderFactory.createEmptyBorder(2, 2, 2, 2)));
      this.jPanel1.setLayout(new BorderLayout());
      this.contentPanel
         .setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)), BorderFactory.createEmptyBorder(10, 10, 10, 200)));
      this.contentPanel.setOpaque(false);
      this.contentPanel.setLayout(new BorderLayout());
      this.msgLabel.setText(this.message);
      this.contentPanel.add(this.msgLabel, "Center");
      this.jPanel1.add(this.contentPanel, "Center");
      this.jLabel1.setBackground(Color.black);
      this.jLabel1.setFont(this.jLabel1.getFont().deriveFont(this.jLabel1.getFont().getStyle() | 1));
      this.jLabel1.setForeground(Color.orange);
      this.jLabel1.setText("StellwerkSim");
      this.jLabel1.setBorder(BorderFactory.createEmptyBorder(1, 10, 1, 10));
      this.jLabel1.setOpaque(true);
      this.jLabel1.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent evt) {
            TopLevelMessage.this.jLabel1MouseClicked(evt);
         }
      });
      this.jPanel1.add(this.jLabel1, "North");
      this.getContentPane().add(this.jPanel1, "Center");
      this.pack();
   }

   private void jLabel1MouseClicked(MouseEvent evt) {
      this.setState(States.MOVEOUT);
   }
}

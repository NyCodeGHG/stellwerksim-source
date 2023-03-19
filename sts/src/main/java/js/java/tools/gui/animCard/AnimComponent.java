package js.java.tools.gui.animCard;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class AnimComponent extends JComponent implements ActionListener {
   private Component oldpanel;
   private Component newpanel;
   private JLayeredPane layer;
   private BufferedImage oldimage;
   private BufferedImage newimage;
   private Timer timer;
   private int cnt = 0;
   private boolean newNeedPaint = true;
   private long last = 0L;

   public static JLayeredPane findLayer(Component c) {
      if (c == null) {
         return null;
      } else if (c instanceof JApplet) {
         return ((JApplet)c).getLayeredPane();
      } else if (c instanceof JWindow) {
         return ((JWindow)c).getLayeredPane();
      } else if (c instanceof JFrame) {
         return ((JFrame)c).getLayeredPane();
      } else {
         return c instanceof JRootPane ? ((JRootPane)c).getLayeredPane() : findLayer(c.getParent());
      }
   }

   public void locateAs(Component c) {
      Point p = SwingUtilities.convertPoint(c, c.getX(), c.getY(), this);
      this.setLocation(p);
      this.setSize(c.getSize());
   }

   public AnimComponent(Component _oldpanel, Component _newpanel) {
      super();
      long start = System.nanoTime();
      this.oldpanel = _oldpanel;
      this.newpanel = _newpanel;
      this.layer = findLayer(this.oldpanel);
      this.layer.add(this, 50);
      this.setOpaque(true);
      this.locateAs(this.oldpanel);
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice gs = ge.getDefaultScreenDevice();
      GraphicsConfiguration gc = gs.getDefaultConfiguration();
      this.oldimage = gc.createCompatibleImage(Math.max(1, this.oldpanel.getWidth()), Math.max(1, this.oldpanel.getHeight()), 3);
      this.newimage = gc.createCompatibleImage(Math.max(1, this.newpanel.getWidth()), Math.max(1, this.newpanel.getHeight()), 3);
      this.paintImage(this.oldimage, this.oldpanel, true);
      this.paintImage(this.newimage, this.newpanel, false);
      this.newNeedPaint = false;
      this.timer = new Timer(50, this);
      this.timer.setCoalesce(false);
      this.timer.start();
      this.actionPerformed(null);
      long stop = System.nanoTime();
      this.last = stop;
   }

   public AnimComponent(Component _oldpanel) {
      super();
      this.oldpanel = _oldpanel;
      this.newpanel = null;
      this.layer = findLayer(this.oldpanel);
      this.layer.add(this, 50);
      this.setOpaque(true);
      this.locateAs(this.oldpanel);
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice gs = ge.getDefaultScreenDevice();
      GraphicsConfiguration gc = gs.getDefaultConfiguration();
      this.oldimage = gc.createCompatibleImage(this.oldpanel.getWidth(), this.oldpanel.getHeight(), 3);
      this.newimage = gc.createCompatibleImage(this.oldpanel.getWidth(), this.oldpanel.getHeight(), 3);
      this.paintImage(this.oldimage, this.oldpanel, true);
      this.repaint();
   }

   public void paint2() {
      this.newpanel = this.oldpanel;
      this.paintImage(this.newimage, this.newpanel, false);
      this.newNeedPaint = false;
      this.timer = new Timer(50, this);
      this.timer.setCoalesce(false);
      this.timer.start();
   }

   private void paintImage(BufferedImage i, Component c, boolean left) {
      Graphics2D g2 = i.createGraphics();
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      c.paintAll(g2);
      g2.dispose();
   }

   public void paint(Graphics g) {
      long start = System.nanoTime();
      this.last = start;
      if (this.oldpanel.getParent() != null && !this.oldpanel.getParent().isVisible()) {
         this.timer.stop();
         this.layer.remove(this);
         this.layer.getParent().repaint();
      } else {
         Graphics2D g2 = (Graphics2D)g;
         g2.setBackground(this.oldpanel.getBackground());
         g2.clearRect(0, 0, this.getWidth(), this.getHeight());
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         float alpha = (float)this.cnt / 100.0F;
         g2.setComposite(AlphaComposite.SrcOver.derive(1.0F - alpha));
         g2.drawImage(this.oldimage, 0, 0, null);
         if (this.newNeedPaint) {
            this.paintImage(this.newimage, this.newpanel, false);
            this.newNeedPaint = false;
         } else {
            g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
            g2.drawImage(this.newimage, 0, 0, null);
         }

         g2.dispose();
      }
   }

   public void actionPerformed(ActionEvent e) {
      this.cnt += 10;
      this.repaint();
      if (this.cnt >= 100) {
         this.timer.stop();
         this.layer.remove(this);
         this.newpanel.repaint();
      }
   }
}

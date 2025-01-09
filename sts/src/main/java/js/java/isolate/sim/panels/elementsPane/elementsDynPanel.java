package js.java.isolate.sim.panels.elementsPane;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import js.java.isolate.sim.toolkit.menuBorderBoxed;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.gui.layout.AutoMultiColumnLayout;
import js.java.tools.gui.layout.ThinkingPanel;

public class elementsDynPanel extends ThinkingPanel implements ActionListener, SessionClose {
   private static final int MAXCOLORS = 10;
   private final String titel;
   private final Timer focusTimer = new Timer(120, this);
   private int focusLevel = 0;
   private int focusRotate = 0;
   private final Color[] focusColor = new Color[10];
   private final float[] dashes = new float[]{4.0F, 4.0F};
   private final int DASHSIZE = (int)(this.dashes[0] + this.dashes[1]);
   private static long lastScroller = 0L;

   public elementsDynPanel(String titel) {
      this.titel = titel;
      this.setBorder(new menuBorderBoxed(titel));
      this.setLayout(new AutoMultiColumnLayout());
      this.getInsets();
      this.setFocusable(true);
   }

   public void paintComponent(Graphics g) {
      if (this.focusColor[this.focusLevel] == null) {
         this.focusColor[this.focusLevel] = this.mixColor(this.focusLevel);
      }

      g.setColor(this.focusColor[this.focusLevel]);
      g.fillRect(0, 0, this.getWidth(), this.getHeight());
      if (this.isFocusOwner()) {
         Graphics2D g2 = (Graphics2D)g;
         g2.setColor(Color.BLACK);
         g2.setStroke(new BasicStroke(1.0F, 0, 2, 1.0F, this.dashes, (float)this.focusRotate));
         g2.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
         g2.setStroke(new BasicStroke(1.0F));
      }
   }

   public void focus() {
      this.scrollRectToVisible(new Rectangle(0, 0, this.getWidth(), this.getHeight()));
      this.requestFocusInWindow();
      this.flash();
      lastScroller = System.currentTimeMillis();
   }

   public static boolean mayNotScroll() {
      return false;
   }

   public void flash() {
      this.focusLevel = 9;
      this.focusTimer.start();
      this.repaint();
   }

   public void actionPerformed(ActionEvent e) {
      this.focusLevel--;
      if (this.focusLevel <= 0) {
         this.focusLevel = 0;
      }

      if (this.focusLevel <= 0 && !this.isFocusOwner()) {
         this.focusTimer.stop();
      }

      this.focusRotate--;
      if (this.focusRotate < 0) {
         this.focusRotate = this.DASHSIZE - 1;
      }

      this.repaint();
   }

   private Color mixColor(int focusLevel) {
      Color b = this.getBackground();
      return new Color(this.mixColor(b.getRed(), focusLevel), this.mixColor(b.getGreen(), focusLevel), this.mixColor(b.getBlue(), focusLevel));
   }

   private int mixColor(int c, int focusLevel) {
      return Math.min(255, c + focusLevel * 3);
   }

   @Override
   public void close() {
      this.focusTimer.stop();
   }
}

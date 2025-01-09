package js.java.isolate.gleisbelegung;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

public class pflagmark extends JComponent {
   private final zuggleis parent;
   private Dimension myd;

   public pflagmark(zuggleis parent) {
      this.parent = parent;
      this.setOpaque(true);
   }

   public Dimension getMaximumSize() {
      return this.getMinsize();
   }

   public Dimension getMinimumSize() {
      return this.getMinsize();
   }

   public Dimension getPreferredSize() {
      return this.getMinsize();
   }

   private Dimension getMinsize() {
      return this.myd;
   }

   private void calcDim() {
      int l = this.parent.calcPFlagLength();
      this.setToolTipText("<html>" + this.parent.getName() + "<br>P bis zu: " + l + " Minuten</html>");
      this.myd = new Dimension(timeline.MINUTEWIDTH * Math.max(0, l), 2);
   }

   public void render() {
      this.calcDim();
      this.setSize(this.myd);
      this.setLocation(this.parent.getX() - this.myd.width, this.parent.getY() + this.parent.getHeight() - this.myd.height);
   }

   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D)g;
      g2.setColor(Color.ORANGE);
      g2.fillRect(0, 0, this.getWidth(), this.getHeight());
   }
}

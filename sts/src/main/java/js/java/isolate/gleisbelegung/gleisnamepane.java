package js.java.isolate.gleisbelegung;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import js.java.tools.gui.GraphicTools;

class gleisnamepane extends JComponent {
   private gleislist parent;

   gleisnamepane(gleislist g) {
      super();
      this.parent = g;
      this.setBackground(timeline.bgcol);
      this.setToolTipText(this.parent.getGleis());
   }

   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D)g;
      GraphicTools.enableTextAA(g2);
      g2.setBackground(timeline.bgcol);
      g2.clearRect(0, 0, this.getWidth(), this.getHeight());
      g2.setColor(timeline.bgcol.darker());
      g2.drawLine(0, this.getHeight() - 1, this.getWidth(), this.getHeight() - 1);
      g2.setColor(timeline.bgcol.brighter());
      g2.drawLine(0, 0, this.getWidth(), 0);
      g2.setFont(timeline.font_bold);
      FontMetrics mfont = g2.getFontMetrics();
      g2.setColor(Color.BLACK);
      g2.drawString(this.parent.getGleis(), 10, mfont.getHeight());
   }

   public Dimension getMinimumSize() {
      return this.getMinsize();
   }

   public Dimension getMaximumSize() {
      return this.getMinsize();
   }

   public Dimension getPreferredSize() {
      return this.getMinsize();
   }

   private Dimension getMinsize() {
      Dimension d = this.parent.getPreferredSize();
      d.width = 150;
      return d;
   }
}

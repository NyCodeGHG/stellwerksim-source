package js.java.isolate.fahrplaneditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;

public class planPanel extends JPanel implements MouseListener, MouseMotionListener {
   private static final int SCROLLERHEIGHT = 15;
   private int xstart = 5;
   private int siderulerwidth;
   private int upperPos = 0;
   private int lowerPos = -1;
   private boolean upperClicked = false;
   private boolean lowerClicked = false;
   private planPanel.positionVerifier posVerfifier = new planPanel.simpleVerifier();
   private EventListenerList filterListener = new EventListenerList();

   public planPanel(int width) {
      super();
      this.siderulerwidth = width;
      this.addMouseListener(this);
      this.addMouseMotionListener(this);
   }

   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D)g;
      LayoutManager lm = this.getLayout();
      Dimension d = lm.preferredLayoutSize(this);
      g2.setColor(new Color(68, 68, 68));
      g2.fillRect(this.xstart, 0, this.siderulerwidth - this.xstart, d.height);
      int y0 = this.posVerfifier.getBestMatch(this.getUpperPos());
      int y1 = this.posVerfifier.getBestMatch(this.getLowerPos());
      GradientPaint gp = new GradientPaint(0.0F, 0.0F, new Color(153, 153, 153), 0.0F, (float)d.height, new Color(221, 221, 221), true);
      g2.setPaint(gp);
      g2.fillRect(this.xstart, y0, this.siderulerwidth - this.xstart, y1 - y0);
      g2.setColor(UIManager.getDefaults().getColor("Panel.background"));
      g2.draw3DRect(this.xstart, 0, this.siderulerwidth - 1 - this.xstart, d.height - 1, false);
      this.paintSlider(g2, this.getUpperPos(), this.upperClicked);
      this.paintSlider(g2, this.getLowerPos(), this.lowerClicked);
   }

   private void paintSlider(Graphics2D g2, int pos, boolean clicked) {
      if (clicked) {
         g2.setColor(new Color(204, 204, 255));
      } else {
         g2.setColor(UIManager.getDefaults().getColor("Button.background"));
      }

      g2.fillRect(this.xstart + 1, pos, this.siderulerwidth - 2 - this.xstart, 15);
      g2.setColor(UIManager.getDefaults().getColor("Button.background"));
      g2.draw3DRect(this.xstart + 1, pos, this.siderulerwidth - 3 - this.xstart, 14, true);
   }

   private int getUpperPos() {
      LayoutManager lm = this.getLayout();
      Dimension d = lm.preferredLayoutSize(this);
      if (this.upperPos >= this.lowerPos - 15 && this.lowerPos >= 0) {
         this.upperPos = this.lowerPos - 15;
      }

      if (this.upperPos < 0) {
         this.upperPos = 0;
      }

      if (this.upperPos > d.height - 15) {
         this.upperPos = d.height - 15;
      }

      return this.upperPos;
   }

   private int getLowerPos() {
      LayoutManager lm = this.getLayout();
      Dimension d = lm.preferredLayoutSize(this);
      if (this.upperPos + 15 > this.lowerPos && this.lowerPos >= 0) {
         this.lowerPos = this.upperPos + 15;
      }

      if (this.lowerPos < -1) {
         this.lowerPos = -1;
      }

      if (this.lowerPos >= d.height - 15) {
         this.lowerPos = -1;
      }

      return this.lowerPos == -1 ? d.height - 15 : this.lowerPos;
   }

   public void addFilterListener(planPanel.filterEventListener l) {
      this.filterListener.add(planPanel.filterEventListener.class, l);
   }

   public void removeFilterListener(planPanel.filterEventListener l) {
      this.filterListener.remove(planPanel.filterEventListener.class, l);
   }

   void resetPos() {
      this.upperPos = 0;
      this.lowerPos = -1;
      this.repaint();
      this.fireFilterChanged(false);
   }

   void noresetPos() {
      this.repaint();
      this.fireFilterChanged(false);
   }

   List<bahnhof> getSelected() {
      LinkedList<bahnhof> ret = new LinkedList();
      if (this.upperPos == 0 && this.lowerPos == -1) {
         return ret;
      } else {
         int nComps = this.getComponentCount();

         for(int i = 0; i < nComps; ++i) {
            Component c = this.getComponent(i);
            if (c.isVisible() && c instanceof bahnhof) {
               int y0 = c.getY();
               int y1 = y0 + c.getHeight();
               if (y0 >= this.getUpperPos() && y1 <= this.getLowerPos() + 15) {
                  ret.add((bahnhof)c);
               }
            }
         }

         return ret;
      }
   }

   protected void fireFilterChanged(boolean floating) {
      Object[] listeners = this.filterListener.getListenerList();

      for(int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == planPanel.filterEventListener.class) {
            ((planPanel.filterEventListener)listeners[i + 1]).changed(floating);
         }
      }
   }

   public void mouseClicked(MouseEvent e) {
      if (e.getClickCount() > 1) {
         this.resetPos();
      }
   }

   public void mousePressed(MouseEvent e) {
      if (e.getX() <= this.siderulerwidth) {
         this.upperClicked = e.getY() >= this.getUpperPos() && e.getY() <= this.getUpperPos() + 15;
         this.lowerClicked = e.getY() >= this.getLowerPos() && e.getY() <= this.getLowerPos() + 15;
         if (this.upperClicked || this.lowerClicked) {
            this.repaint();
         }
      }
   }

   public void mouseReleased(MouseEvent e) {
      if (!this.upperClicked && this.lowerClicked) {
      }

      this.upperPos = this.posVerfifier.getBestMatch(this.upperPos);
      this.lowerPos = this.posVerfifier.getBestMatch(this.lowerPos);
      this.upperClicked = false;
      this.lowerClicked = false;
      this.repaint();
      this.fireFilterChanged(false);
   }

   public void mouseEntered(MouseEvent e) {
   }

   public void mouseExited(MouseEvent e) {
   }

   public void mouseDragged(MouseEvent e) {
      if (!this.upperClicked && this.lowerClicked) {
      }

      if (this.upperClicked) {
         this.upperPos = e.getY() - 7;
      } else if (this.lowerClicked) {
         this.lowerPos = e.getY() - 7;
      }

      this.repaint();
      this.fireFilterChanged(true);
   }

   public void mouseMoved(MouseEvent e) {
   }

   public interface filterEventListener extends EventListener {
      void changed(boolean var1);
   }

   public interface positionVerifier {
      int getBestMatch(int var1);
   }

   private class simpleVerifier implements planPanel.positionVerifier {
      private simpleVerifier() {
         super();
      }

      @Override
      public int getBestMatch(int pos) {
         int nComps = planPanel.this.getComponentCount();

         for(int i = 0; i < nComps; ++i) {
            Component c = planPanel.this.getComponent(i);
            if (c.isVisible()) {
               int y0 = c.getY();
               int y1 = y0 + c.getHeight();
               if (pos >= y0 && pos <= y1) {
                  if (Math.abs(pos - y0) < Math.abs(pos - y1)) {
                     pos = y0;
                  } else {
                     pos = y1;
                  }
                  break;
               }
            }
         }

         return pos;
      }
   }
}

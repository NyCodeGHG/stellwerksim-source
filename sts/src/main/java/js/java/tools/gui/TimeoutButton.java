package js.java.tools.gui;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

public class TimeoutButton extends JButton {
   private final JLabel textLabel = new JLabel();
   private final JProgressBar pBar = new JProgressBar();
   private int timeout;
   private int ctime;
   private final Timer ttimer = new Timer(500, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         TimeoutButton.this.trigger();
      }
   });
   private boolean wastimeout = false;

   public TimeoutButton(String text, int timeout) {
      this.setLayout(new GridLayout(1, 0));
      this.textLabel.setText(text);
      this.textLabel.setHorizontalAlignment(0);
      this.add(this.pBar);
      this.add(this.textLabel);
      this.timeout = timeout * 2;
      this.ctime = this.timeout;
      this.pBar.setMinimum(0);
      this.pBar.setMaximum(this.timeout);
      this.pBar.setValue(this.timeout);
      this.addMouseListener(new MouseListener() {
         public void mouseClicked(MouseEvent e) {
         }

         public void mousePressed(MouseEvent e) {
         }

         public void mouseReleased(MouseEvent e) {
         }

         public void mouseEntered(MouseEvent e) {
            TimeoutButton.this.ctime = TimeoutButton.this.timeout;
         }

         public void mouseExited(MouseEvent e) {
            TimeoutButton.this.ctime = TimeoutButton.this.timeout * 2;
         }
      });
      this.wastimeout = false;
      this.ttimer.start();
   }

   public void stop() {
      this.ttimer.stop();
   }

   private void trigger() {
      this.ctime--;
      this.pBar.setValue(this.ctime);
      if (this.ctime <= 0) {
         this.ttimer.stop();
         this.wastimeout = true;
         this.doClick();
      }
   }

   public void setFont(Font f) {
      super.setFont(f);
      if (this.textLabel != null) {
         this.textLabel.setFont(f);
      }
   }

   public boolean wasTimeout() {
      return this.wastimeout;
   }
}

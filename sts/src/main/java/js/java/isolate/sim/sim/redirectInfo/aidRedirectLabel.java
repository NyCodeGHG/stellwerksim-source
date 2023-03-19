package js.java.isolate.sim.sim.redirectInfo;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import js.java.tools.gui.dataTransferDisplay.LedComponent;
import js.java.tools.gui.dataTransferDisplay.LedComponent.LEDCOLOR;
import js.java.tools.gui.layout.SimpleOneRowLayout;

public class aidRedirectLabel extends JPanel implements ActionListener {
   private final LedComponent led;
   private final int aid;
   private boolean blinkon = false;
   private boolean ledon = false;
   private boolean currentledstate = false;
   private final Timer blinker = new Timer(1200, this);
   private JLabel textField;

   public aidRedirectLabel(int aid, String test) {
      super();
      this.aid = aid;
      this.setBackground(UIManager.getDefaults().getColor("List.background"));
      this.setLayout(new SimpleOneRowLayout());
      this.led = new LedComponent(LEDCOLOR.YELLOW);
      this.add(this.led);
      this.textField = new JLabel();
      this.textField.setBackground(UIManager.getDefaults().getColor("List.background"));
      this.textField.setFont(this.textField.getFont().deriveFont((float)this.textField.getFont().getSize() - 2.0F));
      this.textField.setBounds(0, 0, 0, 0);
      this.add(this.textField);
      this.textField.setText(test);
   }

   public void setBlinkOn(boolean b) {
      this.ledon = false;
      if (b != this.blinkon) {
         this.led.setColor(LEDCOLOR.YELLOW);
         if (b) {
            this.currentledstate = true;
            this.led.setLed(this.currentledstate);
            this.blinker.start();
         } else {
            this.blinker.stop();
            this.currentledstate = false;
            this.led.setLed(this.currentledstate);
         }
      }

      this.blinkon = b;
   }

   public void setLedOn(boolean b) {
      if (b != this.ledon) {
         if (this.blinkon) {
            this.setBlinkOn(false);
         }

         if (b) {
            this.led.setColor(LEDCOLOR.GREEN);
         } else {
            this.led.setColor(LEDCOLOR.GREEN);
         }

         this.led.setLed(b);
      }

      this.ledon = b;
   }

   public void actionPerformed(ActionEvent e) {
      this.currentledstate = !this.currentledstate;
      this.led.setLed(this.currentledstate);
   }

   public int getAid() {
      return this.aid;
   }

   public String getText() {
      return this.textField.getText();
   }

   public Dimension getPreferredSize() {
      return super.getPreferredSize();
   }

   public Dimension getMaximumSize() {
      Dimension d = super.getMaximumSize();
      d.height = super.getPreferredSize().height;
      return d;
   }

   private void initComponents() {
      this.textField = new JLabel();
      this.setBackground(UIManager.getDefaults().getColor("List.background"));
      this.setLayout(null);
      this.textField.setBackground(UIManager.getDefaults().getColor("List.background"));
      this.textField.setFont(this.textField.getFont().deriveFont((float)this.textField.getFont().getSize() - 2.0F));
      this.add(this.textField);
      this.textField.setBounds(0, 0, 0, 0);
   }
}

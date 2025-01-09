package js.java.isolate.sim.sim.gruppentasten;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import js.java.schaltungen.moduleapi.SessionClose;

public class TasterButton extends JRadioButton implements SessionClose {
   private TasterButton.LIGHTMODE lmode = TasterButton.LIGHTMODE.OFF;
   private static final int TI_NORMAL = 0;
   private static final int TI_SELECTED = 1;
   private static final int TI_ROLLOVER = 2;
   private static final int TO_ROLLOVERSELECTED = 3;
   private final TasterImage[] images = new TasterImage[]{
      new TasterImage(false, false), new TasterImage(true, true), new TasterImage(false, true), new TasterImage(true, true)
   };
   private boolean blickOn = false;
   private final Timer blinkTimer = new Timer(400, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         TasterButton.this.gtHandler.verifyLight();
         TasterButton.this.blink();
      }
   });
   private final gtBase gtHandler;

   public TasterButton(boolean white, Color background, gtBase gtHandler) {
      this(white, background, gtHandler, "");
   }

   public TasterButton(boolean white, Color background, gtBase gtHandler, String fkey) {
      this.gtHandler = gtHandler;
      String text = gtHandler.getText();
      char key = gtHandler.getKey();
      this.setBackground(background);
      this.setFont(new Font("Dialog", 1, 9));
      if (white) {
         this.setForeground(new Color(255, 255, 255));
      } else {
         this.setForeground(new Color(1, 1, 1));
      }

      this.setText(text);
      this.setIcon(this.images[0]);
      this.setSelectedIcon(this.images[1]);
      this.setRolloverIcon(this.images[2]);
      this.setRolloverSelectedIcon(this.images[3]);
      this.setFocusPainted(false);
      this.setFocusable(false);
      this.setMargin(new Insets(0, 0, 0, 0));
      this.setMaximumSize(new Dimension(80, 15));
      this.setMinimumSize(new Dimension(60, 15));
      this.setPreferredSize(new Dimension(80, 15));
      this.setRequestFocusEnabled(false);
      this.addActionListener(gtHandler);
      if (key != ' ') {
         this.setMnemonic(key);
      }

      if (!fkey.isEmpty()) {
         this.setToolTipText(fkey);
         this.getInputMap(2).put(KeyStroke.getKeyStroke(fkey), "fpressed");
         this.getActionMap().put("fpressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
               ((JRadioButton)e.getSource()).doClick();
            }
         });
      }
   }

   public void setLight(TasterButton.LIGHTMODE l) {
      this.lmode = l;
      this.blinkTimer.start();
      this.blink();
   }

   private void blink() {
      this.blickOn = !this.blickOn;
      boolean b = this.lmode == TasterButton.LIGHTMODE.ON || this.blickOn && this.lmode == TasterButton.LIGHTMODE.BLINK;

      for (TasterImage ti : this.images) {
         ti.setLight(b);
      }

      if (!b && this.lmode == TasterButton.LIGHTMODE.OFF) {
         this.blinkTimer.stop();
      }

      this.repaint();
   }

   @Override
   public void close() {
      SwingUtilities.invokeLater(() -> this.blinkTimer.stop());
   }

   public static enum LIGHTMODE {
      OFF,
      ON,
      BLINK;
   }
}

package js.java.isolate.sim.panels.elementsPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.BoxLayout;
import javax.swing.DefaultButtonModel;
import javax.swing.JPanel;
import js.java.tools.gui.SimpleToggleButton;

public class elementsQuickPanel extends JPanel {
   private static final int[] keyRow = new int[]{81, 87, 69, 82, 84, 90, 85, 73, 79, 80};
   private int currentKeyPosition = 0;

   public elementsQuickPanel() {
      this.setLayout(new BoxLayout(this, 0));
   }

   public void add(String titel, elementsDynPanel panel) {
      SimpleToggleButton b = new SimpleToggleButton(titel);
      b.setModel(new DefaultButtonModel());
      b.setFocusPainted(false);
      b.setFont(this.getFont());
      b.addActionListener(new elementsQuickPanel.quickListener(panel));
      b.setBorderPainted(false);

      try {
         b.setMnemonic(keyRow[this.currentKeyPosition]);
         b.setToolTipText("Schnelltaste: " + KeyEvent.getKeyText(keyRow[this.currentKeyPosition]));
         this.currentKeyPosition++;
      } catch (ArrayIndexOutOfBoundsException var5) {
      }

      this.add(b);
   }

   private static class quickListener implements ActionListener {
      private final elementsDynPanel p;

      quickListener(elementsDynPanel p) {
         this.p = p;
      }

      public void actionPerformed(ActionEvent e) {
         this.p.focus();
      }
   }
}

package js.java.isolate.statusapplet.players;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.GroupLayout.Alignment;

public class playerListFrame extends JFrame {
   playerListFrame(JScrollPane outputscrollpane) {
      this.initComponents();
      this.setContentPane(outputscrollpane);
      this.pack();
   }

   private void initComponents() {
      this.setTitle("Spieler");
      this.setLocationByPlatform(true);
      GroupLayout layout = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGap(0, 400, 32767));
      layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGap(0, 300, 32767));
      this.pack();
   }
}

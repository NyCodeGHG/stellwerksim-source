package js.java.isolate.sim.sim;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import js.java.isolate.sim.sim.fahrplanRenderer.zugRenderer;
import js.java.isolate.sim.zug.zug;

public class zugFahrplanPanel extends JPanel {
   private final zugRenderer zugPanel;
   private final zugUndPlanPanel my_main;
   private JScrollPane scroller;

   public zugFahrplanPanel(zugUndPlanPanel my_main) {
      this.my_main = my_main;
      this.initComponents();
      this.zugPanel = new zugRenderer(my_main);
      this.scroller.setViewportView(this.zugPanel);
      this.scroller.getViewport().setBackground(Color.WHITE);
   }

   void showFahrplan(zug z) {
      this.zugPanel.showFahrplan(z);
   }

   void refresh() {
      this.zugPanel.refresh();
   }

   int getUnterzugAZid() {
      return this.zugPanel.getUnterzugAZid();
   }

   private void initComponents() {
      this.scroller = new JScrollPane();
      this.setBackground(new Color(255, 255, 255));
      this.setLayout(new BorderLayout());
      this.scroller.setBackground(new Color(255, 255, 255));
      this.scroller.setHorizontalScrollBarPolicy(31);
      this.add(this.scroller, "Center");
   }
}

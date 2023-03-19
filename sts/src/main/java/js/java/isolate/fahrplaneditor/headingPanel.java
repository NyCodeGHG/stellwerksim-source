package js.java.isolate.fahrplaneditor;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

class headingPanel extends JPanel {
   private JPanel bottomPanel;
   private JPanel topPanel;

   private void addLabel(String t) {
      JLabel l = new JLabel(t);
      l.setHorizontalAlignment(0);
      l.setOpaque(true);
      this.topPanel.add(l);
   }

   headingPanel(int xoffset) {
      super();
      this.initComponents();
      this.topPanel.setLayout(new planLayoutManager(true, xoffset));
      this.addLabel("");
      this.addLabel("");
      this.addLabel("");
      this.addLabel("Gleis");
      this.addLabel("");
      this.addLabel("An");
      this.addLabel("");
      this.addLabel("Ab");
      this.addLabel("");
      this.addLabel("Einfahrt");
      this.addLabel("");
      this.addLabel("Ausfahrt");
      this.addLabel("");
      this.addLabel("Flags");
      this.addLabel("Hinweistext");
      this.addLabel("");
      this.addLabel("Themenmarker");
      this.bottomPanel.setLayout(new planLayoutManager(false, xoffset));
      this.bottomPanel.add(new JLabel(""));
      this.bottomPanel.add(new JLabel(""));
      this.bottomPanel.add(new JLabel(""));
      this.bottomPanel.add(new JLabel(""));
      this.bottomPanel.add(new JLabel(""));
      this.bottomPanel.add(new JLabel(""));
      this.bottomPanel.add(new JLabel(""));
      this.bottomPanel.add(new JLabel(""));
      this.bottomPanel.add(new JLabel(""));
      this.bottomPanel.add(new JLabel(""));
      this.bottomPanel.add(new JLabel(""));
      this.bottomPanel.add(new JLabel(""));
      this.bottomPanel.add(new JLabel(""));
      this.bottomPanel.add(new JLabel(""));
      this.bottomPanel.add(new JLabel(""));
      Font f = new Font("SansSerif", 0, 8);

      for(char i = 'A'; i <= 'Z'; ++i) {
         JLabel l = new JLabel(i + "");
         l.setHorizontalAlignment(0);
         l.setFont(f);
         l.setOpaque(true);
         this.bottomPanel.add(new JLabel(""));
         this.bottomPanel.add(l);
      }

      this.bottomPanel.add(new JLabel(""));
   }

   private void initComponents() {
      this.topPanel = new JPanel();
      this.bottomPanel = new JPanel();
      this.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
      this.setLayout(new BoxLayout(this, 3));
      this.topPanel.setLayout(new BorderLayout());
      this.add(this.topPanel);
      this.bottomPanel.setLayout(new BorderLayout());
      this.add(this.bottomPanel);
   }
}

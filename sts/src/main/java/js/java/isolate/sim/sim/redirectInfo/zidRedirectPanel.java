package js.java.isolate.sim.sim.redirectInfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import js.java.isolate.sim.zug.zug;
import js.java.schaltungen.UserContext;
import js.java.tools.gui.ArrowBox;

public class zidRedirectPanel extends JPanel {
   private final UserContext uc;
   private TitledBorder tborder;
   private final RedirectStellwerkInfo my_main;
   private final JFrame parent;
   private final zug zug;
   private final LinkedList<zidRedirectPanel.routeItem> routeModel = new LinkedList();
   private final LinkedList<zidRedirectPanel.routeItem> skipModel = new LinkedList();
   private final LinkedList<aidRedirectLabel> routeLeds = new LinkedList();
   private final LinkedList<aidRedirectLabel> skipLeds = new LinkedList();
   private zidRedirectFrame frame = null;
   private JPanel dataPanel;
   private JLabel fromLabel;
   private JScrollPane jScrollPane1;
   private JScrollPane jScrollPane2;
   private JPanel middlePanel;
   private JPanel routePanel;
   private JPanel skipPanel;
   private JLabel toLabel;

   public zidRedirectPanel(UserContext uc, JFrame parent, RedirectStellwerkInfo m, zug z) {
      this.uc = uc;
      this.parent = parent;
      this.my_main = m;
      this.zug = z;
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            zidRedirectPanel.this.initMyComponent();
         }
      });
   }

   private void initMyComponent() {
      this.initComponents();
      this.dataPanel.setLayout(new zidRedirectPanel.zrLayout());
      this.tborder = new TitledBorder("Umleitung");
      this.setBorder(this.tborder);
   }

   public void close() {
      if (this.frame != null) {
         this.frame.setVisible(false);
         this.frame.dispose();
      }
   }

   public void update(int res, String[] tokens) {
      SwingUtilities.invokeLater(new zidRedirectPanel.zrRunnable(res, tokens));
   }

   private String getAnlage(int aid) {
      return this.my_main.getStellwerkName(aid);
   }

   private void fillModel(LinkedList<zidRedirectPanel.routeItem> model, int size) {
      while (model.size() < size) {
         model.add(new zidRedirectPanel.routeItem(0, "-"));
      }
   }

   private void awtUpdate(int res, String[] tokens) {
      if (res == 400) {
         this.close();
      } else {
         this.tborder.setTitle("Umleitung " + this.zug.getSpezialName());
         if (res != 250) {
            this.routeModel.clear();
            this.skipModel.clear();
            this.routePanel.removeAll();
            this.skipPanel.removeAll();
            this.routeLeds.clear();

            for (int i = 0; i < tokens.length; i++) {
               if (tokens[i].equals("AID")) {
                  int pos = Integer.parseInt(tokens[++i].trim());
                  int aid = Integer.parseInt(tokens[++i].trim());
                  if (pos == 0) {
                     this.fromLabel.setText(this.getAnlage(aid));
                  } else {
                     this.fillModel(this.routeModel, pos);
                     this.routeModel.set(pos - 1, new zidRedirectPanel.routeItem(aid, this.getAnlage(aid)));
                  }
               } else if (tokens[i].equals("SKIP")) {
                  int pos = Integer.parseInt(tokens[++i].trim()) + 1;
                  int aid = Integer.parseInt(tokens[++i].trim());
                  this.fillModel(this.skipModel, pos);
                  this.skipModel.set(pos - 1, new zidRedirectPanel.routeItem(0, this.getAnlage(aid)));
               }
            }

            if (res == 200 && !this.routeModel.isEmpty()) {
               this.toLabel.setText(((zidRedirectPanel.routeItem)this.routeModel.pollLast()).s);
            } else {
               this.toLabel.setText("");
            }

            for (zidRedirectPanel.routeItem s : this.routeModel) {
               aidRedirectLabel l = new aidRedirectLabel(s.aid, s.s);
               if (s.aid > 0) {
                  this.routeLeds.add(l);
               }

               this.routePanel.add(l);
            }

            for (zidRedirectPanel.routeItem s : this.skipModel) {
               aidRedirectLabel l = new aidRedirectLabel(s.aid, s.s);
               this.skipLeds.add(l);
               this.skipPanel.add(l);
               l.setLedOn(this.my_main.isStellwerkUsed(s.aid));
            }

            if (res == 300) {
               for (aidRedirectLabel l : this.routeLeds) {
                  l.setLedOn(true);
               }

               try {
                  ((aidRedirectLabel)this.routeLeds.getLast()).setBlinkOn(true);
               } catch (NoSuchElementException var8) {
               }
            }

            if (this.frame == null) {
               this.frame = new zidRedirectFrame(this.uc, this.parent, "Umleitung " + this.zug.getSpezialName(), this);
               this.frame.setVisible(true);
            }
         }

         if (res != 300) {
            int caid = 0;

            for (int ix = 0; ix < tokens.length; ix++) {
               if (tokens[ix].equals("CUR")) {
                  ix++;

                  try {
                     caid = Integer.parseInt(tokens[ix].trim());
                  } catch (NumberFormatException var7) {
                     System.out.println("T:" + tokens[ix].trim());
                     var7.printStackTrace();
                  }
               }
            }

            boolean found = false;
            Iterator<aidRedirectLabel> it = this.routeLeds.descendingIterator();

            while (it.hasNext()) {
               aidRedirectLabel l = (aidRedirectLabel)it.next();
               l.setLedOn(found);
               l.setBlinkOn(l.getAid() == caid);
               if (l.getAid() == caid) {
                  found = true;
               }
            }
         }

         this.dataPanel.revalidate();
         this.revalidate();
         this.dataPanel.repaint();
         this.repaint();
      }
   }

   private void initComponents() {
      this.fromLabel = new JLabel();
      this.toLabel = new JLabel();
      this.dataPanel = new JPanel();
      this.jScrollPane1 = new JScrollPane();
      this.routePanel = new JPanel();
      this.middlePanel = new JPanel();
      this.jScrollPane2 = new JScrollPane();
      this.skipPanel = new JPanel();
      this.setMinimumSize(new Dimension(100, 80));
      this.setPreferredSize(new Dimension(200, 180));
      this.setLayout(new BorderLayout());
      this.fromLabel.setBackground(UIManager.getDefaults().getColor("List.foreground"));
      this.fromLabel.setFont(this.fromLabel.getFont().deriveFont(this.fromLabel.getFont().getStyle() | 1));
      this.fromLabel.setForeground(UIManager.getDefaults().getColor("List.background"));
      this.fromLabel.setHorizontalAlignment(0);
      this.fromLabel.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
      this.fromLabel.setOpaque(true);
      this.add(this.fromLabel, "North");
      this.toLabel.setBackground(UIManager.getDefaults().getColor("List.foreground"));
      this.toLabel.setFont(this.toLabel.getFont().deriveFont(this.toLabel.getFont().getStyle() | 1));
      this.toLabel.setForeground(UIManager.getDefaults().getColor("List.background"));
      this.toLabel.setHorizontalAlignment(0);
      this.toLabel.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
      this.toLabel.setOpaque(true);
      this.add(this.toLabel, "South");
      this.dataPanel.setBackground(UIManager.getDefaults().getColor("List.background"));
      this.dataPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
      this.dataPanel.setLayout(new GridBagLayout());
      this.jScrollPane1.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 1));
      this.jScrollPane1.setHorizontalScrollBarPolicy(31);
      this.routePanel.setBackground(UIManager.getDefaults().getColor("List.background"));
      this.routePanel.setLayout(new BoxLayout(this.routePanel, 3));
      this.jScrollPane1.setViewportView(this.routePanel);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.dataPanel.add(this.jScrollPane1, gridBagConstraints);
      this.middlePanel.setBackground(UIManager.getDefaults().getColor("List.background"));
      this.middlePanel.setLayout(new BorderLayout());
      this.middlePanel.add(new ArrowBox(), "Center");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 1;
      gridBagConstraints.weighty = 1.0;
      this.dataPanel.add(this.middlePanel, gridBagConstraints);
      this.jScrollPane2.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
      this.jScrollPane2.setHorizontalScrollBarPolicy(31);
      this.skipPanel.setBackground(UIManager.getDefaults().getColor("List.background"));
      this.skipPanel.setLayout(new BoxLayout(this.skipPanel, 3));
      this.jScrollPane2.setViewportView(this.skipPanel);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.dataPanel.add(this.jScrollPane2, gridBagConstraints);
      this.add(this.dataPanel, "Center");
   }

   private static class routeItem {
      final String s;
      final int aid;

      routeItem(int aid, String s) {
         this.aid = aid;
         this.s = s;
      }
   }

   private class zrLayout implements LayoutManager {
      private zrLayout() {
      }

      public void addLayoutComponent(String name, Component comp) {
      }

      public void removeLayoutComponent(Component comp) {
      }

      public Dimension preferredLayoutSize(Container parent) {
         return new Dimension(10, 10);
      }

      public Dimension minimumLayoutSize(Container parent) {
         return new Dimension(10, 10);
      }

      public void layoutContainer(Container parent) {
         Insets insets = parent.getInsets();
         int nComps = parent.getComponentCount();
         int x = insets.left;
         int y = insets.top;
         int h = parent.getHeight() - insets.top - insets.bottom;
         int w = (parent.getWidth() - zidRedirectPanel.this.middlePanel.getMinimumSize().width - insets.left - insets.right) / 2;

         for (int i = 0; i < nComps; i++) {
            Component c = parent.getComponent(i);
            if (c == zidRedirectPanel.this.middlePanel) {
               c.setBounds(x, y, zidRedirectPanel.this.middlePanel.getMinimumSize().width, h);
               x += zidRedirectPanel.this.middlePanel.getMinimumSize().width;
            } else {
               c.setBounds(x, y, w, h);
               x += w;
            }
         }
      }
   }

   private class zrRunnable implements Runnable {
      private final int res;
      private final String[] tokens;

      private zrRunnable(int res, String[] tokens) {
         this.res = res;
         this.tokens = tokens;
      }

      public void run() {
         zidRedirectPanel.this.awtUpdate(this.res, this.tokens);
      }
   }
}

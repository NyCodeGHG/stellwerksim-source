package js.java.isolate.statusapplet.players;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import js.java.tools.gui.ArrowBox;

public class zidRedirect extends JPanel implements ircupdate {
   private TitledBorder tborder;
   private final int zid;
   private final oneInstance my_main;
   private LinkedList<zidRedirect.routeItem> routeModel = new LinkedList();
   private LinkedList<zidRedirect.routeItem> skipModel = new LinkedList();
   private LinkedList<routeLabel> routeLeds = new LinkedList();
   private LinkedList<routeLabel> skipLeds = new LinkedList();
   private JPanel dataPanel;
   private JLabel fromLabel;
   private JMenuItem jMenuItem1;
   private JPopupMenu jPopupMenu1;
   private JScrollPane jScrollPane1;
   private JScrollPane jScrollPane2;
   private JPanel middlePanel;
   private JPanel routePanel;
   private JPanel skipPanel;
   private JLabel toLabel;

   public zidRedirect(oneInstance m, int zid) {
      this.my_main = m;
      this.zid = zid;
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            zidRedirect.this.initMyComponent();
         }
      });
   }

   private void initMyComponent() {
      this.initComponents();
      this.dataPanel.setLayout(new zidRedirect.zrLayout());
      this.tborder = new TitledBorder("Umleitung");
      this.setBorder(this.tborder);
      this.my_main.addViewData(this);
      this.my_main.registerHook(this);
   }

   public void update(int res, String[] tokens) {
      SwingUtilities.invokeLater(new zidRedirect.zrRunnable(res, tokens));
   }

   private String getAnlage(int aid) {
      players_aid start = this.my_main.getAnlage(aid);
      String name = "AID " + aid;
      if (start != null) {
         name = start.name;
      }

      return name;
   }

   private void fillModel(LinkedList<zidRedirect.routeItem> model, int size) {
      while (model.size() < size) {
         model.add(new zidRedirect.routeItem(0, "-"));
      }
   }

   public boolean wirdAidUmfahren(players_aid a) {
      for (zidRedirect.routeItem ri : this.skipModel) {
         if (ri.aid == a.aid) {
            return true;
         }
      }

      return false;
   }

   public boolean hatAidUmleitung(players_aid a) {
      for (zidRedirect.routeItem ri : this.routeModel) {
         if (ri.aid == a.aid) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void updateAid(players_aid a) {
      for (routeLabel s : this.skipLeds) {
         players_aid d = this.my_main.getAnlage(s.getAid());
         s.setLedOn(d != null && d.spieler != null);
      }
   }

   @Override
   public void updateZug(players_zug z) {
   }

   private void awtUpdate(int res, String[] tokens) {
      if (res == 400) {
         this.my_main.removeViewData(this);
         this.my_main.unregisterHook(this);
      } else {
         players_zug z = this.my_main.getZug(this.zid);
         this.tborder.setTitle("Umleitung " + z.getSpezialName());
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
                     this.routeModel.set(pos - 1, new zidRedirect.routeItem(aid, this.getAnlage(aid)));
                  }
               } else if (tokens[i].equals("SKIP")) {
                  int pos = Integer.parseInt(tokens[++i].trim()) + 1;
                  int aid = Integer.parseInt(tokens[++i].trim());
                  this.fillModel(this.skipModel, pos);
                  this.skipModel.set(pos - 1, new zidRedirect.routeItem(0, this.getAnlage(aid)));
               }
            }

            if (res == 200 && !this.routeModel.isEmpty()) {
               this.toLabel.setText(((zidRedirect.routeItem)this.routeModel.pollLast()).s);
            } else {
               this.toLabel.setText("");
            }

            for (zidRedirect.routeItem s : this.routeModel) {
               routeLabel l = new routeLabel(s.aid, s.s);
               if (s.aid > 0) {
                  this.routeLeds.add(l);
               }

               this.routePanel.add(l);
            }

            for (zidRedirect.routeItem s : this.skipModel) {
               routeLabel l = new routeLabel(s.aid, s.s);
               this.skipLeds.add(l);
               this.skipPanel.add(l);
               players_aid d = this.my_main.getAnlage(s.aid);
               l.setLedOn(d != null && d.spieler != null);
            }

            if (res == 300) {
               for (routeLabel l : this.routeLeds) {
                  l.setLedOn(true);
               }

               try {
                  ((routeLabel)this.routeLeds.getLast()).setBlinkOn(true);
               } catch (NoSuchElementException var9) {
               }
            }
         }

         if (res != 300) {
            int caid = 0;

            for (int ix = 0; ix < tokens.length; ix++) {
               if (tokens[ix].equals("CUR")) {
                  ix++;

                  try {
                     caid = Integer.parseInt(tokens[ix].trim());
                  } catch (NumberFormatException var8) {
                     var8.printStackTrace();
                  }
               }
            }

            boolean found = false;
            Iterator<routeLabel> it = this.routeLeds.descendingIterator();

            while (it.hasNext()) {
               routeLabel l = (routeLabel)it.next();
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
      this.jPopupMenu1 = new JPopupMenu();
      this.jMenuItem1 = new JMenuItem();
      this.fromLabel = new JLabel();
      this.toLabel = new JLabel();
      this.dataPanel = new JPanel();
      this.jScrollPane1 = new JScrollPane();
      this.routePanel = new JPanel();
      this.middlePanel = new JPanel();
      this.jScrollPane2 = new JScrollPane();
      this.skipPanel = new JPanel();
      this.jMenuItem1.setText("entfernen");
      this.jMenuItem1.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            zidRedirect.this.jMenuItem1ActionPerformed(evt);
         }
      });
      this.jPopupMenu1.add(this.jMenuItem1);
      this.setComponentPopupMenu(this.jPopupMenu1);
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

   private void jMenuItem1ActionPerformed(ActionEvent evt) {
      this.my_main.removeViewData(this);
      this.my_main.unregisterHook(this);
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
         int w = (parent.getWidth() - zidRedirect.this.middlePanel.getMinimumSize().width - insets.left - insets.right) / 2;

         for (int i = 0; i < nComps; i++) {
            Component c = parent.getComponent(i);
            if (c == zidRedirect.this.middlePanel) {
               c.setBounds(x, y, zidRedirect.this.middlePanel.getMinimumSize().width, h);
               x += zidRedirect.this.middlePanel.getMinimumSize().width;
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
         zidRedirect.this.awtUpdate(this.res, this.tokens);
      }
   }
}

package js.java.isolate.fahrplaneditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;
import js.java.tools.TextHelper;
import js.java.tools.balloontip.BalloonTip;
import js.java.tools.gui.border.DropShadowBorder;

class bahnhof extends JPanel implements Comparable {
   private final LinkedList<bahnhofHalt> halte = new LinkedList();
   private FPEaidData aid;
   private fahrplaneditor my_main;
   private bahnhofHalt lastadded = null;
   public HashMap<Character, enritem> ausfahrtenTM = new HashMap();
   public HashMap<Character, enritem> einfahrtenTM = new HashMap();
   private boolean duringInit = false;
   private boolean inadd = false;
   private JMenuItem my_menu = null;
   private boolean inCBadd = false;
   private JComboBox bahnhofCB;
   private JPanel ctrlPanel;
   private JButton delButton;
   private JPanel haltePanel;
   private JComboBox regionCB;

   bahnhof(fahrplaneditor m, FPEaidData a) {
      this.my_main = m;
      this.aid = a;
      this.duringInit = true;
      this.initComponents();
      this.setBorder(new DropShadowBorder(true, true, true, true));
      this.haltePanel.setLayout(new lineLayoutManager(30));
      this.bahnhofCB.setSelectedItem(null);
      this.regionCB.setSelectedItem(null);

      for (String r : this.my_main.regionen) {
         this.regionCB.addItem(r);
      }

      if (this.aid != null) {
         this.regionCB.setSelectedItem(a.getRegion());
         this.bahnhofCB.setSelectedItem(this.aid);
         if (!this.aid.isErlaubt()) {
            this.delButton.setEnabled(false);
            this.bahnhofCB.setEnabled(false);
            this.regionCB.setEnabled(false);
         }
      }

      this.duringInit = false;
   }

   bahnhof(fahrplaneditor m) {
      this(m, null);
   }

   public boolean isErlaubt() {
      try {
         return this.aid.isErlaubt();
      } catch (NullPointerException var2) {
         return false;
      }
   }

   public boolean isEmpty() {
      int cmp = this.lastadded == null ? 0 : 1;
      return this.halte.size() <= cmp;
   }

   public void setEnabled(boolean b) {
      if (this.aid != null && !this.aid.isErlaubt()) {
         b = false;
      }

      this.delButton.setEnabled(b);
   }

   void addLine(planLine p) {
      bahnhofHalt b = new bahnhofHalt(this.my_main, this, p);
      synchronized (this.halte) {
         this.halte.add(b);
      }

      this.haltePanel.add(b.getThis());
      this.lastadded = null;
      this.revalidate();
      this.bahnhofCB.setEnabled(false);
      this.regionCB.setEnabled(false);
   }

   public void setMenu(JMenuItem m) {
      this.my_menu = m;
   }

   public JMenuItem getMenu() {
      return this.my_menu;
   }

   public void jump() {
      if (this.aid != null) {
         BalloonTip balloonTip = new BalloonTip(this.bahnhofCB);
         balloonTip.setText("Gesuchtes Stellwerk " + this.aid.getName());
         balloonTip.setCloseButton(true);
         balloonTip.setHideDelay(5);
         balloonTip.setVisible(true);
      }
   }

   public void sort() {
      TreeSet<bahnhofHalt> sort = new TreeSet();
      synchronized (this.halte) {
         for (bahnhofHalt b : this.halte) {
            if (b != this.lastadded) {
               sort.add(b);
            }
         }

         this.halte.clear();
         this.haltePanel.removeAll();

         for (bahnhofHalt bx : sort) {
            this.halte.add(bx);
            this.haltePanel.add(bx.getThis());
         }

         if (this.lastadded != null) {
            this.halte.add(this.lastadded);
            this.haltePanel.add(this.lastadded.getThis());
         }

         this.haltePanel.revalidate();
      }
   }

   public void addBhf(final boolean erstAufruf) {
      if (this.aid != null && !this.inadd && this.aid.isErlaubt()) {
         this.inadd = true;
         this.lastadded = null;
         SwingWorker w = new SwingWorker() {
            protected Object doInBackground() throws Exception {
               bahnhof.this.aid.makeValid();
               return null;
            }

            protected void done() {
               try {
                  bahnhofHalt b = new bahnhofHalt(bahnhof.this.my_main, bahnhof.this);
                  synchronized (bahnhof.this.halte) {
                     bahnhof.this.halte.add(b);
                  }

                  bahnhof.this.haltePanel.add(b.getThis());
                  bahnhof.this.lastadded = b;
                  bahnhof.this.revalidate();
                  bahnhof.this.my_main.entriesChanged();
                  if (!erstAufruf) {
                     SwingUtilities.invokeLater(() -> bahnhof.this.my_main.scrollZPanel(bahnhof.this.getBounds()));
                  }
               } catch (Exception var5) {
               }

               bahnhof.this.inadd = false;
            }
         };
         w.execute();
      }
   }

   public void entriesChanged(bahnhofHalt caller, boolean remove) {
      if (this.aid != null && this.aid.isErlaubt()) {
         if (remove) {
            synchronized (this.halte) {
               this.halte.remove(caller);
            }

            this.haltePanel.remove(caller.getThis());
            caller.removed();
            this.revalidate();
            this.repaint();
            if (this.halte.size() < 2) {
               this.bahnhofCB.setEnabled(true);
               this.regionCB.setEnabled(true);
               this.regionCBActionPerformed(null);
               if (this.halte.isEmpty()) {
                  this.aid = null;
                  bahnhofHalt var6 = null;
                  this.bahnhofCB.setSelectedItem(this.aid);
               }
            }
         } else {
            this.bahnhofCB.setEnabled(false);
            this.regionCB.setEnabled(false);
            if (caller == this.lastadded) {
               this.lastadded = null;
               this.addBhf(false);
            }
         }

         this.my_main.entriesChanged();
         this.my_main.runChecker();
      }
   }

   public FPEaidData getAidData() {
      return this.aid;
   }

   public void removed() {
      synchronized (this.halte) {
         for (bahnhofHalt b : this.halte) {
            b.removed();
         }
      }

      this.lastadded = null;
      this.my_main.entriesChanged();
   }

   public int saveData(StringBuffer data, int counter) {
      synchronized (this.halte) {
         int i = 0;

         for (bahnhofHalt b : this.halte) {
            i++;
            if (this.lastadded != b) {
               data.append(TextHelper.urlEncode("aaid[" + counter + "]"));
               data.append("=");
               data.append(TextHelper.urlEncode(this.aid.getAid() + ""));
               data.append("&");
               b.saveData(data, counter);
               counter++;
            } else {
               System.out.println("Ignored BHF, lastadded, line " + i);
            }
         }

         return counter;
      }
   }

   public int getMinAn() {
      return this.getMinAn(-1);
   }

   public int getMinAn(int marker) {
      int minan = Integer.MAX_VALUE;
      synchronized (this.halte) {
         for (bahnhofHalt b : this.halte) {
            if (b != this.lastadded && (marker == -1 || b.hasMarker(marker))) {
               int an = b.getAn();
               minan = Math.min(minan, an);
            }
         }

         return minan;
      }
   }

   public int getMaxAb() {
      return this.getMaxAb(-1);
   }

   public int getMaxAb(int marker) {
      int maxab = Integer.MIN_VALUE;
      synchronized (this.halte) {
         for (bahnhofHalt b : this.halte) {
            if (b != this.lastadded && (marker == -1 || b.hasMarker(marker))) {
               int ab = b.getAb();
               maxab = Math.max(maxab, ab);
            }
         }

         return maxab;
      }
   }

   public LinkedList<bahnhofHalt> getHalte() {
      synchronized (this.halte) {
         return (LinkedList<bahnhofHalt>)this.halte.stream().filter(b -> b != this.lastadded).collect(Collectors.toCollection(LinkedList::new));
      }
   }

   public int compareTo(Object o) {
      bahnhof b = (bahnhof)o;
      int ret = this.getMinAn() - b.getMinAn();
      if (ret == 0) {
         ret = this.getMaxAb() - b.getMaxAb();
      }

      if (ret == 0 && this.aid != null) {
         ret = this.getAidData().getName().compareTo(b.getAidData().getName());
      }

      if (ret == 0 && this.aid != null) {
         ret = this.getAidData().getAid() - b.getAidData().getAid();
      }

      return ret;
   }

   public void updateLayout() {
      synchronized (this.halte) {
         this.halte.stream().forEach(b -> b.doLayout());
      }

      this.doLayout();
      this.invalidate();
   }

   public void validate(LinkedList<dataFailures> l) {
      if (l == null) {
         this.ausfahrtenTM.clear();
         this.einfahrtenTM.clear();
      }

      TreeSet<bahnhofHalt> sort;
      synchronized (this.halte) {
         sort = (TreeSet<bahnhofHalt>)this.halte.stream().filter(bx -> bx != this.lastadded).collect(Collectors.toCollection(TreeSet::new));
      }

      boolean foundEK = false;
      boolean foundEnr0 = false;
      boolean lastEK = false;

      for (bahnhofHalt b : sort) {
         b.validate(l);
         lastEK = b.hasEK();
         foundEK |= lastEK;
         foundEnr0 |= b.getAusfahrtEnr() == 0;
      }

      if (l != null) {
         if (foundEK && !lastEK) {
            dataFailures ausf = ((bahnhofHalt)sort.last()).createAusfahrtFailure("Nach E/K Flag noch Fahrplanzeilen");
            l.add(ausf);
         }

         if (!foundEK && foundEnr0) {
            dataFailures ausf = ((bahnhofHalt)sort.last()).createAusfahrtFailure("keine Ausfahrt");
            l.add(ausf);
         }
      }
   }

   public void postValidate() {
      this.halte.stream().filter(b -> b != this.lastadded).forEach(b -> b.postValidate());
   }

   void cleanSaveStatus() {
      synchronized (this.halte) {
         this.halte.forEach(b -> b.cleanSaveStatus());
      }
   }

   void fadeOff(boolean off) {
      synchronized (this.halte) {
         this.halte.stream().forEach(b -> b.fadeOff(off));
      }
   }

   void fade2Off(boolean off) {
      synchronized (this.halte) {
         this.halte.stream().forEach(b -> b.fade2Off(off));
      }
   }

   void fade3Off(boolean off) {
      synchronized (this.halte) {
         this.halte.stream().forEach(b -> b.fade3Off(off));
      }
   }

   void shiftMinutes(int min) {
      synchronized (this.halte) {
         this.halte.stream().filter(b -> b != this.lastadded).forEach(b -> b.shiftMinutes(min));
      }
   }

   void filterMarker(char c) {
      synchronized (this.halte) {
         for (bahnhofHalt b : this.halte) {
            if (b != this.lastadded) {
               if (c == '*') {
                  b.fadeOff(false);
               } else if (b.hasMarker(c - 'A')) {
                  b.fadeOff(false);
               } else {
                  b.fadeOff(true);
               }
            }
         }
      }
   }

   private void initComponents() {
      this.ctrlPanel = new JPanel();
      this.delButton = new JButton();
      this.regionCB = new JComboBox();
      this.bahnhofCB = new JComboBox();
      this.haltePanel = new JPanel();
      this.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
      this.setLayout(new BorderLayout());
      this.ctrlPanel.setLayout(new FlowLayout(0));
      this.delButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/clear16.png")));
      this.delButton.setToolTipText("Kompletten Stellwerksplan löschen");
      this.delButton.setFocusPainted(false);
      this.delButton.setFocusable(false);
      this.delButton.setMargin(new Insets(1, 1, 1, 1));
      this.delButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            bahnhof.this.delButtonActionPerformed(evt);
         }
      });
      this.ctrlPanel.add(this.delButton);
      this.regionCB.setFocusable(false);
      this.regionCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            bahnhof.this.regionCBActionPerformed(evt);
         }
      });
      this.ctrlPanel.add(this.regionCB);
      this.bahnhofCB.setFocusable(false);
      this.bahnhofCB.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            bahnhof.this.bahnhofCBItemStateChanged(evt);
         }
      });
      this.ctrlPanel.add(this.bahnhofCB);
      this.add(this.ctrlPanel, "North");
      this.haltePanel.setLayout(new BorderLayout());
      this.add(this.haltePanel, "Center");
   }

   private void bahnhofCBItemStateChanged(ItemEvent evt) {
      if (!this.inCBadd) {
         if (this.aid != null) {
            System.out.println("aid vor : " + this.aid.getAid());
         }

         this.aid = (FPEaidData)this.bahnhofCB.getSelectedItem();
         if (this.aid != null) {
            System.out.println("aid nach: " + this.aid.getAid());
         }

         if (this.aid != null) {
            if (this.my_menu != null) {
               this.my_menu.setText(this.aid.getName());
            }

            if (!this.inCBadd && !this.duringInit) {
               if (!this.halte.isEmpty()) {
                  this.halte.remove();
                  this.haltePanel.removeAll();
                  this.revalidate();
               }

               this.addBhf(false);
            }
         }
      }
   }

   private void delButtonActionPerformed(ActionEvent evt) {
      boolean delOk;
      if (this.halte.size() > 1) {
         int r = JOptionPane.showConfirmDialog(
            this.delButton, "Wirklich das Stellwerk " + this.getAidData().getName() + " und alle Halte entfernen?", "Löschen bestätigen", 0, 3
         );
         delOk = r == 0;
      } else {
         delOk = true;
      }

      if (delOk) {
         this.my_main.deleteBhf(this);
      }
   }

   private void regionCBActionPerformed(ActionEvent evt) {
      FPEaidData oldaid = this.aid;
      this.inCBadd = true;
      this.bahnhofCB.removeAllItems();
      if (oldaid != null && !oldaid.isErlaubt()) {
         this.bahnhofCB.addItem(oldaid);
      } else {
         for (FPEaidData b : this.my_main.aids) {
            if (b.getRegion().equalsIgnoreCase((String)this.regionCB.getSelectedItem()) && b.isErlaubt()) {
               boolean canadd = true;

               for (bahnhof bb : this.my_main.getBhfs()) {
                  if (bb != this && bb.getAidData() != null) {
                     canadd &= bb.getAidData().getAid() != b.getAid();
                  }
               }

               if (canadd) {
                  this.bahnhofCB.addItem(b);
               }
            }
         }
      }

      this.inCBadd = false;
      this.bahnhofCB.setSelectedItem(oldaid);
   }
}

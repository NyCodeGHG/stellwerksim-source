package js.java.isolate.statusapplet.players;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.Timer;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.decor;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.paint2Base;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleis.gleisElements.gleisHelper;
import js.java.schaltungen.UserContext;

public class gleisbildLoadPanel extends JPanel implements ircupdate {
   private final GleisAdapter my_main;
   private final oneInstance my_instance;
   private final playersPanel kp;
   private final ConcurrentHashMap<Integer, gleisbildModelAdapter> added = new ConcurrentHashMap();
   private final ConcurrentHashMap<Integer, gleisbildFrame> frames = new ConcurrentHashMap();
   private Timer pingTimer = null;
   private boolean loaded = true;
   private final UserContext uc;
   private JButton addButton;
   private JComboBox aidCB;
   private JLabel jLabel1;
   private JSeparator jSeparator1;
   private JButton testAID2Button;
   private JButton testAID4Button;

   public gleisbildLoadPanel(UserContext uc, GleisAdapter m, oneInstance irc, playersPanel kp) {
      super();
      this.uc = uc;
      this.my_main = m;
      this.my_instance = irc;
      this.kp = kp;
      kp.registerHook(this);
      this.initComponents();
      this.initGleisDecor();
      this.updateCB();
   }

   Object deserialize(String msg) {
      return this.my_instance.getChat().deserialize(msg);
   }

   public void setProgress(int p) {
      if (p == 100) {
         for(gleisbildFrame f : this.frames.values()) {
            f.setClosable(true);
         }

         this.loaded = true;
      } else {
         this.loaded = false;
      }

      this.aidCBItemStateChanged(null);
   }

   private void initGleisDecor() {
      players_elementPainter painter = new players_elementPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_ANRUFÜBERGANG).clearAllPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_BAHNSTEIG).clearAllPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_BAHNÜBERGANG).clearAllPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_AUTOBAHNÜBERGANG).clearAllPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_DISPLAYKONTAKT).clearAllPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_HALTEPUNKT).clearAllPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_SETVMAX).clearAllPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_WEICHEOBEN).clearAllPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_WEICHEUNTEN).clearAllPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_WIEDERVMAX).clearAllPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_ANRUFÜBERGANG).clearAllPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_AUSFAHRT).clearLightPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_EINFAHRT).clearLightPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_KREUZUNG).clearLightPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_KREUZUNGBRUECKE).clearLightPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_SIGNAL).clearLightPainter().setDecorPainter(new players_paint_signal());
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_SIGNALKNOPF).clearLightPainter().setDecorPainter(new players_paint_signalknopf());
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_ZWERGSIGNAL).clearLightPainter().setDecorPainter(new players_paint_zwergsignal());
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_ZDECKUNGSSIGNAL).clearLightPainter().setDecorPainter(new players_paint_zdeckungssignal());
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_SPRUNG).clearLightPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_STRECKE).clearLightPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_ÜBERGABEPUNKT).clearLightPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_2ZDISPLAY).clearDecorPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_3ZDISPLAY).clearDecorPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_4ZDISPLAY).clearDecorPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_5ZDISPLAY).clearDecorPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_6ZDISPLAY).clearDecorPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_7ZDISPLAY).clearDecorPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_8ZDISPLAY).clearDecorPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_AIDDISPLAY).clearDecorPainter();
      new gleisbildLoadPanel.itemMediator(gleis.ELEMENT_ÜBERGABEAKZEPTOR).clearDecorPainter();
      decor d = decor.getDecor();

      for(element elm : gleisHelper.allElements()) {
         d.replaceElementPainter(elm, painter);
      }
   }

   private void updateCB() {
      TreeSet<players_aid> sort = this.kp.getSortedAids();
      players_aid selected = (players_aid)this.aidCB.getSelectedItem();
      this.aidCB.removeAllItems();

      for(players_aid pd : sort) {
         this.aidCB.addItem(pd);
      }

      this.aidCB.setSelectedItem(selected);
   }

   @Override
   public void updateAid(players_aid d) {
      synchronized(this) {
         if (!this.added.containsKey(d.aid)) {
            this.added.put(d.aid, new gleisbildModelAdapter(this.my_main, d));
            this.updateCB();
            if (this.added.size() == 1 && this.pingTimer == null) {
               this.pingTimer = new Timer(600000, new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     ((gleisbildModelAdapter)gleisbildLoadPanel.this.added.values().iterator().next()).ping();
                  }
               });
               this.pingTimer.start();
            }
         }

         if (this.frames.containsKey(d.aid)) {
            ((gleisbildFrame)this.frames.get(d.aid)).update(d);
         }
      }
   }

   @Override
   public void updateZug(players_zug z) {
      for(gleisbildFrame f : this.frames.values()) {
         f.update(z);
      }
   }

   void closingFrame(players_aid paid, gleisbildFrame frame) {
      this.frames.remove(paid.aid, frame);
      ((gleisbildModelAdapter)this.added.get(paid.aid)).freeGleisbild();
   }

   private void initComponents() {
      this.jLabel1 = new JLabel();
      this.aidCB = new JComboBox();
      this.addButton = new JButton();
      this.jSeparator1 = new JSeparator();
      this.testAID2Button = new JButton();
      this.testAID4Button = new JButton();
      this.setLayout(new FlowLayout(0, 5, 0));
      this.jLabel1.setText("Stellwerk");
      this.add(this.jLabel1);
      this.aidCB.setFont(this.aidCB.getFont().deriveFont((float)this.aidCB.getFont().getSize() - 1.0F));
      this.aidCB.setFocusable(false);
      this.aidCB.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            gleisbildLoadPanel.this.aidCBItemStateChanged(evt);
         }
      });
      this.add(this.aidCB);
      this.addButton.setFont(this.addButton.getFont().deriveFont((float)this.addButton.getFont().getSize() - 1.0F));
      this.addButton.setText("hinzufügen...");
      this.addButton.setEnabled(false);
      this.addButton.setFocusable(false);
      this.addButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisbildLoadPanel.this.addButtonActionPerformed(evt);
         }
      });
      this.add(this.addButton);
      this.jSeparator1.setOrientation(1);
      this.jSeparator1.setPreferredSize(new Dimension(2, 5));
      this.add(this.jSeparator1);
      this.testAID2Button.setFont(this.testAID2Button.getFont().deriveFont((float)this.testAID2Button.getFont().getSize() - 1.0F));
      this.testAID2Button.setText("Test AID2");
      this.testAID2Button.setFocusable(false);
      this.testAID2Button.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisbildLoadPanel.this.testAID2ButtonActionPerformed(evt);
         }
      });
      this.add(this.testAID2Button);
      this.testAID4Button.setFont(this.testAID4Button.getFont().deriveFont((float)this.testAID4Button.getFont().getSize() - 1.0F));
      this.testAID4Button.setText("Test AID4");
      this.testAID4Button.setFocusable(false);
      this.testAID4Button.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisbildLoadPanel.this.testAID4ButtonActionPerformed(evt);
         }
      });
      this.add(this.testAID4Button);
   }

   private void aidCBItemStateChanged(ItemEvent evt) {
      this.addButton.setEnabled(this.aidCB.getSelectedItem() != null && this.loaded);
   }

   private void addButtonActionPerformed(ActionEvent evt) {
      players_aid d = (players_aid)this.aidCB.getSelectedItem();
      if (d != null && !this.frames.containsKey(d.aid) && this.added.containsKey(d.aid)) {
         players_gleisbildModel glb = ((gleisbildModelAdapter)this.added.get(d.aid)).getGleisbild();
         gleisbildFrame gf = new gleisbildFrame(this.uc, this.my_main, this.kp, glb, d, this);
         this.frames.put(d.aid, gf);
         gf.setVisible(true);
         this.my_instance.addSubWindow(gf);
      }
   }

   private void testAID2ButtonActionPerformed(ActionEvent evt) {
      this.kp.handleIRCresult("USER", 200, "2:Test 2:50002:testman", true);
      this.kp.handleIRCresult("RUPDATE", 200, "1:2:5:1:8:", true);
      this.kp.handleIRCresult("INFO", 200, "1:10:00:00:0:2:1:Zug 1:1:0", true);
   }

   private void testAID4ButtonActionPerformed(ActionEvent evt) {
      this.kp.handleIRCresult("USER", 200, "4:Test 4:50004:testman", true);
      this.kp.handleIRCresult("RUPDATE", 200, "1:2:5:1:8:", true);
      this.kp.handleIRCresult("INFO", 200, "1:10:00:00:0:2:1:Zug 1:1:0", true);
   }

   private static class itemMediator {
      private final element elm;

      itemMediator(element element) {
         super();
         this.elm = element;
      }

      gleisbildLoadPanel.itemMediator clearAllPainter() {
         decor d = decor.getDecor();
         d.replaceDecorPainter(this.elm, null).replaceElementPainter(this.elm, 1);
         return this;
      }

      gleisbildLoadPanel.itemMediator clearDecorPainter() {
         decor d = decor.getDecor();
         d.replaceDecorPainter(this.elm, null);
         return this;
      }

      gleisbildLoadPanel.itemMediator clearLightPainter() {
         decor d = decor.getDecor();
         d.replaceElementPainter(this.elm, 1);
         return this;
      }

      gleisbildLoadPanel.itemMediator setDecorPainter(paint2Base painter) {
         decor d = decor.getDecor();
         d.replaceDecorPainter(this.elm, painter);
         return this;
      }
   }
}

package js.java.isolate.sim.inserts;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.GroupLayout.Alignment;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisTypContainer;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildModel;
import js.java.isolate.sim.inserts.inserttoken.enrgleistoken;
import js.java.isolate.sim.inserts.inserttoken.inserttoken;
import js.java.isolate.sim.inserts.inserttoken.newlinetoken;
import js.java.isolate.sim.inserts.inserttoken.streckengleistoken;

public class signalmitknopf extends insert {
   private gleisElements.RICHTUNG richtung = gleisElements.RICHTUNG.right;

   public signalmitknopf(GleisAdapter m, gleisbildModel glb) {
      super(m, glb);
   }

   @Override
   protected void initInterface() {
      super.initInterface();
      JPanel p = new JPanel();
      p.setLayout(new GridLayout(0, 2));
      gleisTypContainer gtc = gleisTypContainer.getInstance();
      ButtonGroup bg2 = new ButtonGroup();
      Map<gleisElements.RICHTUNG, String> richtungen = gtc.getRichtungen();

      for(Entry<gleisElements.RICHTUNG, String> e : richtungen.entrySet()) {
         JToggleButton r = new JRadioButton((String)e.getValue());
         r.setSelected(e.getKey() == this.richtung);
         r.setFocusable(false);
         r.addItemListener(new signalmitknopf.richtungItemListener(e, r));
         bg2.add(r);
         p.add(r);
      }

      this.add("Richtung", p);
      this.mklayout();
   }

   @Override
   protected int getXOffset() {
      return 0;
   }

   @Override
   protected int getYOffset() {
      return 0;
   }

   @Override
   protected boolean isleftright(gleisbildModel glb, int x) {
      return false;
   }

   private void mklayout() {
      LinkedList<inserttoken> l = new LinkedList();
      switch(this.richtung) {
         case left:
            l.add(new streckengleistoken());
            l.add(new streckengleistoken());
            l.add(new enrgleistoken(gleis.ELEMENT_SIGNAL, this.richtung, "enr"));
            l.add(new enrgleistoken(gleis.ELEMENT_SIGNALKNOPF, this.richtung, "enr"));
            l.add(new streckengleistoken());
            break;
         case right:
            l.add(new streckengleistoken());
            l.add(new enrgleistoken(gleis.ELEMENT_SIGNALKNOPF, this.richtung, "enr"));
            l.add(new enrgleistoken(gleis.ELEMENT_SIGNAL, this.richtung, "enr"));
            l.add(new streckengleistoken());
            l.add(new streckengleistoken());
            break;
         case down:
            l.add(new streckengleistoken());
            l.add(new newlinetoken());
            l.add(new streckengleistoken());
            l.add(new newlinetoken());
            l.add(new enrgleistoken(gleis.ELEMENT_SIGNALKNOPF, this.richtung, "enr"));
            l.add(new newlinetoken());
            l.add(new enrgleistoken(gleis.ELEMENT_SIGNAL, this.richtung, "enr"));
            l.add(new newlinetoken());
            l.add(new streckengleistoken());
            break;
         case up:
            l.add(new streckengleistoken());
            l.add(new newlinetoken());
            l.add(new enrgleistoken(gleis.ELEMENT_SIGNAL, this.richtung, "enr"));
            l.add(new newlinetoken());
            l.add(new enrgleistoken(gleis.ELEMENT_SIGNALKNOPF, this.richtung, "enr"));
            l.add(new newlinetoken());
            l.add(new streckengleistoken());
            l.add(new newlinetoken());
            l.add(new streckengleistoken());
      }

      this.setLayout(l);
   }

   @Override
   public String getName() {
      return "Signal mit Schutzsignalknopf";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5039 $";
   }

   private void richtungChanged(Entry<gleisElements.RICHTUNG, String> entry, boolean selected) {
      if (selected) {
         this.richtung = (gleisElements.RICHTUNG)entry.getKey();
         this.mklayout();
         this.refreshPreview();
      }
   }

   @Override
   protected void initVariables(boolean leftright) {
      BitSet bs = this.glbModel.getENRbitset();
      int e = bs.nextClearBit(1);
      bs.set(e);
      this.storage.put("enr", e + "");
   }

   private void initComponents() {
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGap(0, 117, 32767));
      layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGap(0, 83, 32767));
   }

   private class richtungItemListener implements ItemListener {
      private final Entry<gleisElements.RICHTUNG, String> entry;
      private final JToggleButton button;

      richtungItemListener(Entry<gleisElements.RICHTUNG, String> t, JToggleButton b) {
         super();
         this.entry = t;
         this.button = b;
      }

      public void itemStateChanged(ItemEvent e) {
         signalmitknopf.this.richtungChanged(this.entry, this.button.isSelected());
      }
   }
}

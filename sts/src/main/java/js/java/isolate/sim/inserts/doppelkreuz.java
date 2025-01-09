package js.java.isolate.sim.inserts;

import java.util.BitSet;
import java.util.LinkedList;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.GroupLayout.Alignment;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildModel;
import js.java.isolate.sim.inserts.inserttoken.emptytoken;
import js.java.isolate.sim.inserts.inserttoken.enrgleistoken;
import js.java.isolate.sim.inserts.inserttoken.inserttoken;
import js.java.isolate.sim.inserts.inserttoken.newlinetoken;
import js.java.isolate.sim.inserts.inserttoken.streckengleistoken;

public class doppelkreuz extends insert {
   private JCheckBox downup = null;

   public doppelkreuz(GleisAdapter m, gleisbildModel glb) {
      super(m, glb);
   }

   @Override
   protected void initInterface() {
      super.initInterface();
      this.downup = this.addBoolInput("nach unten", "downup", false);
      this.mklayout();
   }

   private void mklayout() {
      LinkedList<inserttoken> l = new LinkedList();
      l.add(new streckengleistoken());
      if (this.downup.isSelected()) {
         l.add(new streckengleistoken());
         l.add(new emptytoken());
         l.add(new emptytoken());
         l.add(new emptytoken());
         l.add(new emptytoken());
         l.add(new streckengleistoken());
      } else {
         l.add(new streckengleistoken());
         l.add(new streckengleistoken());
         l.add(new enrgleistoken(gleis.ELEMENT_WEICHEUNTEN, gleisElements.RICHTUNG.right, "enr0"));
         l.add(new enrgleistoken(gleis.ELEMENT_WEICHEUNTEN, gleisElements.RICHTUNG.right, "enr1"));
         l.add(new streckengleistoken());
         l.add(new streckengleistoken());
      }

      l.add(new streckengleistoken());
      l.add(new newlinetoken());
      l.add(new emptytoken());
      l.add(new emptytoken());
      l.add(new streckengleistoken());
      l.add(new emptytoken());
      l.add(new emptytoken());
      l.add(new streckengleistoken());
      l.add(new newlinetoken());
      l.add(new streckengleistoken());
      if (!this.downup.isSelected()) {
         l.add(new streckengleistoken());
         l.add(new emptytoken());
         l.add(new emptytoken());
         l.add(new emptytoken());
         l.add(new emptytoken());
         l.add(new streckengleistoken());
      } else {
         l.add(new streckengleistoken());
         l.add(new streckengleistoken());
         l.add(new enrgleistoken(gleis.ELEMENT_WEICHEOBEN, gleisElements.RICHTUNG.right, "enr2"));
         l.add(new enrgleistoken(gleis.ELEMENT_WEICHEOBEN, gleisElements.RICHTUNG.right, "enr3"));
         l.add(new streckengleistoken());
         l.add(new streckengleistoken());
      }

      l.add(new streckengleistoken());
      this.setLayout(l);
   }

   @Override
   public String getName() {
      return "Doppelkreuzung WW";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5039 $";
   }

   @Override
   public void action(String n, JComponent source) {
      if (n.equalsIgnoreCase("downup")) {
         this.mklayout();
      }

      this.refreshPreview();
   }

   @Override
   protected void initVariables(boolean leftright) {
      BitSet bs = this.glbModel.getENRbitset();

      for (int i = 0; i < 4; i++) {
         int e = bs.nextClearBit(1);
         bs.set(e);
         this.storage.put("enr" + i, e + "");
      }
   }

   private void initComponents() {
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGap(0, 117, 32767));
      layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGap(0, 83, 32767));
   }
}

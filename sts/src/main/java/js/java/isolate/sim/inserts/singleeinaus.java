package js.java.isolate.sim.inserts;

import java.util.BitSet;
import java.util.LinkedList;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisTypContainer;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildModel;
import js.java.isolate.sim.inserts.inserttoken.emptytoken;
import js.java.isolate.sim.inserts.inserttoken.enrgleistoken;
import js.java.isolate.sim.inserts.inserttoken.enrswgleistoken;
import js.java.isolate.sim.inserts.inserttoken.enrtoken;
import js.java.isolate.sim.inserts.inserttoken.inserttoken;
import js.java.isolate.sim.inserts.inserttoken.newlinetoken;
import js.java.isolate.sim.inserts.inserttoken.streckengleistoken;
import js.java.tools.ColorText;

public class singleeinaus extends insert {
   JCheckBox akzeptor = null;

   public singleeinaus(GleisAdapter m, gleisbildModel glb) {
      super(m, glb);
      this.storage.put("aus_name", "Testausfahrt");
      this.storage.put("bgcolor", "normal");
   }

   @Override
   protected void initInterface() {
      super.initInterface();
      this.addTextInput("Ein/Ausfahrt Name", "aus_name", (String)this.storage.get("aus_name"));
      this.akzeptor = this.addBoolInput(gleisTypContainer.getInstance().getTypElementName(gleis.ELEMENT_ÜBERGABEAKZEPTOR), "akzeptor", false);
      this.addColorInput(
         gleisTypContainer.getInstance().getTypElementName(gleis.ELEMENT_ÜBERGABEAKZEPTOR) + " Hintergrundfarbe",
         "bgcolor",
         (String)this.storage.get("bgcolor")
      );
      this.mklayout();
   }

   @Override
   protected int getXOffset() {
      return 0;
   }

   @Override
   protected int getYOffset() {
      return this.akzeptor.isSelected() ? 2 : 0;
   }

   private void mklayout() {
      String bgcolor = "normal";
      LinkedList<inserttoken> l = new LinkedList();
      if (this.akzeptor.isSelected()) {
         bgcolor = "bgcolor";
         emptytoken e = new emptytoken();
         l.add(e);
         l.add(e);
         l.add(e);
         l.add(new enrtoken(gleis.ELEMENT_ÜBERGABEAKZEPTOR, bgcolor, "ein_enr"));
         l.add(new newlinetoken());
         l.add(new newlinetoken());
      }

      l.add(new enrswgleistoken(gleis.ELEMENT_AUSFAHRT, gleisElements.RICHTUNG.left, bgcolor, "aus_name", "aus_enr"));
      l.add(new enrswgleistoken(gleis.ELEMENT_EINFAHRT, gleisElements.RICHTUNG.right, bgcolor, "aus_name", "ein_enr"));
      l.add(new streckengleistoken(bgcolor));
      l.add(new streckengleistoken(bgcolor));
      l.add(new streckengleistoken());
      l.add(new streckengleistoken());
      l.add(new streckengleistoken());
      l.add(new enrgleistoken(gleis.ELEMENT_ÜBERGABEPUNKT, gleisElements.RICHTUNG.left, "aus_enr"));
      l.add(new streckengleistoken());
      this.setLayout(l);
   }

   @Override
   public String getName() {
      return "eingleisige Ein-/Ausfahrt mit ÜP";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5039 $";
   }

   @Override
   public void action(String n, JComponent source) {
      if (n.equalsIgnoreCase("aus_name")) {
         JTextField f = (JTextField)source;
         this.storage.put("aus_name", f.getText());
         this.mklayout();
      } else if (n.equalsIgnoreCase("akzeptor")) {
         this.mklayout();
      } else if (n.equalsIgnoreCase("bgcolor")) {
         JComboBox f = (JComboBox)source;
         this.storage.put("bgcolor", ((ColorText)f.getSelectedItem()).getText());
         this.mklayout();
      }

      this.refreshPreview();
   }

   @Override
   public void focuslost(String n, JComponent source) {
      JTextField f = (JTextField)source;
      this.storage.put("aus_name", f.getText());
      this.mklayout();
      this.refreshPreview();
   }

   @Override
   public void textchange(String n, JComponent source) {
      this.focuslost(n, source);
   }

   @Override
   protected void initVariables(boolean leftright) {
      BitSet bs = this.glbModel.getENRbitset();
      int e = bs.nextClearBit(1);
      bs.set(e);
      this.storage.put("ein_enr", e + "");
      e = bs.nextClearBit(1);
      bs.set(e);
      this.storage.put("aus_enr", e + "");
   }

   private void initComponents() {
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGap(0, 117, 32767));
      layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGap(0, 83, 32767));
   }
}

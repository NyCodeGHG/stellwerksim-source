package js.java.isolate.sim.inserts;

import java.awt.BorderLayout;
import java.util.BitSet;
import java.util.LinkedList;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
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

public class dualeinaus extends insert {
   private JCheckBox akzeptor = null;

   public dualeinaus(GleisAdapter m, gleisbildModel glb) {
      super(m, glb);
      this.storage.put("aus_name", "Testausfahrt");
      this.storage.put("bgcolor1", "normal");
      this.storage.put("bgcolor2", "normal");
   }

   @Override
   protected void initInterface() {
      super.initInterface();
      this.addTextInput("Ein/Ausfahrt Name", "aus_name", (String)this.storage.get("aus_name"));
      this.akzeptor = this.addBoolInput(gleisTypContainer.getInstance().getTypElementName(gleis.ELEMENT_ÜBERGABEAKZEPTOR), "akzeptor", false);
      this.addColorInput(
         gleisTypContainer.getInstance().getTypElementName(gleis.ELEMENT_ÜBERGABEAKZEPTOR) + " 1 Hintergrundfarbe",
         "bgcolor1",
         (String)this.storage.get("bgcolor1")
      );
      this.addColorInput(
         gleisTypContainer.getInstance().getTypElementName(gleis.ELEMENT_ÜBERGABEAKZEPTOR) + " 2 Hintergrundfarbe",
         "bgcolor2",
         (String)this.storage.get("bgcolor2")
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
      String bgcolor1 = "normal";
      String bgcolor2 = "normal";
      LinkedList<inserttoken> l = new LinkedList();
      if (this.akzeptor.isSelected()) {
         bgcolor1 = "bgcolor1";
         bgcolor2 = "bgcolor2";
         emptytoken e = new emptytoken();
         l.add(e);
         l.add(e);
         l.add(e);
         l.add(new enrtoken(gleis.ELEMENT_ÜBERGABEAKZEPTOR, bgcolor1, "ein1_enr"));
         l.add(e);
         l.add(new enrtoken(gleis.ELEMENT_ÜBERGABEAKZEPTOR, bgcolor2, "ein2_enr"));
         l.add(new newlinetoken());
         l.add(new newlinetoken());
      }

      l.add(new enrswgleistoken(gleis.ELEMENT_AUSFAHRT, gleisElements.RICHTUNG.left, bgcolor1, "aus1_name", "aus1_enr"));
      l.add(new enrswgleistoken(gleis.ELEMENT_EINFAHRT, gleisElements.RICHTUNG.right, bgcolor1, "ein1_name", "ein1_enr"));
      l.add(new streckengleistoken(bgcolor1));
      l.add(new streckengleistoken(bgcolor1));
      l.add(new streckengleistoken());
      l.add(new streckengleistoken());
      l.add(new streckengleistoken());
      l.add(new enrgleistoken(gleis.ELEMENT_ÜBERGABEPUNKT, gleisElements.RICHTUNG.left, "aus1_enr"));
      l.add(new streckengleistoken());
      l.add(new newlinetoken());
      l.add(new newlinetoken());
      l.add(new enrswgleistoken(gleis.ELEMENT_AUSFAHRT, gleisElements.RICHTUNG.left, bgcolor2, "aus2_name", "aus2_enr"));
      l.add(new enrswgleistoken(gleis.ELEMENT_EINFAHRT, gleisElements.RICHTUNG.right, bgcolor2, "ein2_name", "ein2_enr"));
      l.add(new streckengleistoken(bgcolor2));
      l.add(new streckengleistoken(bgcolor2));
      l.add(new streckengleistoken());
      l.add(new streckengleistoken());
      l.add(new streckengleistoken());
      l.add(new enrgleistoken(gleis.ELEMENT_ÜBERGABEPUNKT, gleisElements.RICHTUNG.left, "aus2_enr"));
      l.add(new streckengleistoken());
      this.setLayout(l);
   }

   @Override
   public String getName() {
      return "zweigleisige Ein-/Ausfahrt mit ÜP";
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
      } else if (n.equalsIgnoreCase("bgcolor1")) {
         JComboBox f = (JComboBox)source;
         this.storage.put("bgcolor1", ((ColorText)f.getSelectedItem()).getText());
         this.mklayout();
      } else if (n.equalsIgnoreCase("bgcolor2")) {
         JComboBox f = (JComboBox)source;
         this.storage.put("bgcolor2", ((ColorText)f.getSelectedItem()).getText());
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
      this.storage.put("ein1_enr", e + "");
      e = bs.nextClearBit(1);
      bs.set(e);
      this.storage.put("aus1_enr", e + "");
      e = bs.nextClearBit(1);
      bs.set(e);
      this.storage.put("ein2_enr", e + "");
      e = bs.nextClearBit(1);
      bs.set(e);
      this.storage.put("aus2_enr", e + "");
      String links = leftright ? "%rechts" : "%links";
      String rechts = !leftright ? "%rechts" : "%links";
      this.storage.put("aus1_name", (String)this.storage.get("aus_name") + rechts);
      this.storage.put("aus2_name", (String)this.storage.get("aus_name") + links);
      this.storage.put("ein1_name", (String)this.storage.get("aus_name") + links);
      this.storage.put("ein2_name", (String)this.storage.get("aus_name") + rechts);
   }

   private void initComponents() {
      this.setLayout(new BorderLayout());
   }
}

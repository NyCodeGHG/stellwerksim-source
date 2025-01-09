package js.java.isolate.sim.inserts;

import java.awt.BorderLayout;
import java.util.BitSet;
import java.util.LinkedList;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildModel;
import js.java.isolate.sim.inserts.inserttoken.bgcolortoken;
import js.java.isolate.sim.inserts.inserttoken.emptytoken;
import js.java.isolate.sim.inserts.inserttoken.enrgleistoken;
import js.java.isolate.sim.inserts.inserttoken.gleistoken;
import js.java.isolate.sim.inserts.inserttoken.inserttoken;
import js.java.isolate.sim.inserts.inserttoken.newlinetoken;
import js.java.isolate.sim.inserts.inserttoken.streckengleistoken;
import js.java.tools.ColorText;
import js.java.tools.gui.NumberTextField;

public class mehrgleisbue extends insert {
   private int anzahl = 1;
   private int büart = 0;

   public mehrgleisbue(GleisAdapter m, gleisbildModel glb) {
      super(m, glb);
      this.storage.put("bgcolor", "normal");
   }

   @Override
   protected void initInterface() {
      super.initInterface();
      this.mklayout();
      this.addIntInput("Gleiszahl", "count", this.anzahl);
      this.addColorInput("Hintergrundfarbe", "bgcolor", (String)this.storage.get("bgcolor"));
      this.addMXInput(
         "BÜ-Art", this.büart, new String[]{"Ferngesteuerter", "fern", "Wärtergesteuert", "wärter", "Anrufschranke", "anruf", "Vollautomatisch", "auto"}
      );
   }

   @Override
   protected int getXOffset() {
      return 1;
   }

   @Override
   protected int getYOffset() {
      return 1;
   }

   private void mklayout() {
      LinkedList<inserttoken> l = new LinkedList();
      String bgcolor = "bgcolor";
      l.add(new emptytoken());
      bgcolortoken bt = new bgcolortoken(bgcolor);
      l.add(bt);
      l.add(new newlinetoken());

      for (int i = 0; i < this.anzahl; i++) {
         l.add(new streckengleistoken());
         element e = gleis.ELEMENT_BAHNÜBERGANG;
         if (this.büart == 1) {
            e = gleis.ELEMENT_WBAHNÜBERGANG;
         } else if (this.büart == 2) {
            e = gleis.ELEMENT_ANRUFÜBERGANG;
         } else if (this.büart == 3) {
            e = gleis.ELEMENT_AUTOBAHNÜBERGANG;
         }

         gleistoken gt = new enrgleistoken(e, gleisElements.RICHTUNG.left, "enr");
         gt.bgcolor = bgcolor;
         l.add(gt);
         l.add(new streckengleistoken());
         l.add(new newlinetoken());
         l.add(new emptytoken());
         l.add(bt);
         l.add(new newlinetoken());
      }

      this.setLayout(l);
   }

   @Override
   public String getName() {
      return "mehrgleisiger Bahnübergang";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5345 $";
   }

   @Override
   public void action(String n, JComponent source) {
      if (n.equalsIgnoreCase("count")) {
         NumberTextField f = (NumberTextField)source;
         this.anzahl = f.getInt();
         this.mklayout();
         this.refreshPreview();
      } else if (n.equalsIgnoreCase("bgcolor")) {
         JComboBox f = (JComboBox)source;
         this.storage.put("bgcolor", ((ColorText)f.getSelectedItem()).getText());
         this.mklayout();
         this.refreshPreview();
      } else if (n.equalsIgnoreCase("fern")) {
         this.büart = 0;
         this.mklayout();
         this.refreshPreview();
      } else if (n.equalsIgnoreCase("wärter")) {
         this.büart = 1;
         this.mklayout();
         this.refreshPreview();
      } else if (n.equalsIgnoreCase("anruf")) {
         this.büart = 2;
         this.mklayout();
         this.refreshPreview();
      } else if (n.equalsIgnoreCase("auto")) {
         this.büart = 3;
         this.mklayout();
         this.refreshPreview();
      }
   }

   @Override
   public void focuslost(String n, JComponent source) {
      if (n.equalsIgnoreCase("count")) {
         NumberTextField f = (NumberTextField)source;
         this.anzahl = f.getInt();
         this.mklayout();
         this.refreshPreview();
      }
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
      this.storage.put("enr", e + "");
   }

   private void initComponents() {
      this.setLayout(new BorderLayout());
   }
}

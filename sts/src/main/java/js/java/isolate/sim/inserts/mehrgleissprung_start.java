package js.java.isolate.sim.inserts;

import java.awt.BorderLayout;
import java.util.BitSet;
import java.util.LinkedList;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildModel;
import js.java.isolate.sim.inserts.inserttoken.bgcolortoken;
import js.java.isolate.sim.inserts.inserttoken.enrgleistoken;
import js.java.isolate.sim.inserts.inserttoken.gleistoken;
import js.java.isolate.sim.inserts.inserttoken.inserttoken;
import js.java.isolate.sim.inserts.inserttoken.newlinetoken;
import js.java.isolate.sim.inserts.inserttoken.streckengleistoken;
import js.java.tools.ColorText;
import js.java.tools.gui.NumberTextField;

public class mehrgleissprung_start extends insert {
   private int anzahl = 1;

   public mehrgleissprung_start(GleisAdapter m, gleisbildModel glb) {
      super(m, glb);
      this.storage.put("bgcolor", "normal");
   }

   @Override
   protected void initInterface() {
      super.initInterface();
      this.mklayout();
      this.addIntInput("Gleiszahl", "count", this.anzahl);
      this.addColorInput("Hintergrundfarbe", "bgcolor", (String)this.storage.get("bgcolor"));
   }

   @Override
   protected int getXOffset() {
      return 1;
   }

   private void mklayout() {
      LinkedList<inserttoken> l = new LinkedList();
      String bgcolor = "bgcolor";

      for(int i = 0; i < this.anzahl; ++i) {
         bgcolortoken bt = new bgcolortoken(bgcolor);
         l.add(bt);
         gleistoken gt = new enrgleistoken(gleis.ELEMENT_SPRUNG, gleisElements.RICHTUNG.left, "enr" + i);
         gt.bgcolor = bgcolor;
         l.add(gt);
         gt = new streckengleistoken();
         gt.bgcolor = bgcolor;
         l.add(gt);
         l.add(gt);
         gleistoken var7 = new streckengleistoken();
         var7.bgcolor = "normal";
         l.add(var7);
         if (i + 1 < this.anzahl && this.anzahl > 1) {
            l.add(new newlinetoken());
            bt = new bgcolortoken(bgcolor);
            l.add(bt);
            l.add(bt);
            l.add(bt);
            l.add(bt);
            l.add(new newlinetoken());
         }
      }

      this.setLayout(l);
   }

   @Override
   public String getName() {
      return "mehrgleisiger Sprung Start";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5039 $";
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

      for(int i = 0; i < this.anzahl; ++i) {
         int e = bs.nextClearBit(1);
         bs.set(e);
         this.storage.put("enr" + i, e + "");
      }
   }

   private void initComponents() {
      this.setLayout(new BorderLayout());
   }
}

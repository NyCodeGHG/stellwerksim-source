package js.java.isolate.sim.inserts;

import java.awt.BorderLayout;
import java.util.BitSet;
import java.util.LinkedList;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleis.gleisElements.gleisHelper;
import js.java.isolate.sim.gleisbild.gleisbildModel;
import js.java.isolate.sim.inserts.inserttoken.bgcolortoken;
import js.java.isolate.sim.inserts.inserttoken.enrgleistoken;
import js.java.isolate.sim.inserts.inserttoken.gleistoken;
import js.java.isolate.sim.inserts.inserttoken.inserttoken;
import js.java.isolate.sim.inserts.inserttoken.streckengleistoken;
import js.java.isolate.sim.inserts.inserttoken.swgleistoken;

public class gleisdisplay extends insert {
   private int size = 2;

   public gleisdisplay(GleisAdapter m, gleisbildModel glb) {
      super(m, glb);
   }

   @Override
   protected void initInterface() {
      super.initInterface();
      this.addTextInput("Displayname", "display", "Dsply");
      this.addTextInput("Kontaktname", "kontakt", "FSKtk");
      this.addMXInput(
         "Displaygröße", this.size - 2, new String[]{"2", "size2", "3", "size3", "4", "size4", "5", "size5", "6", "size6", "7", "size7", "8", "size8"}
      );
      this.add("Achtung!", new JLabel("Es wird keine Displayverdrahtung eingerichtet!"));
      this.storage.put("kontakt", "FSKtk");
      this.storage.put("display", "Dsply");
      this.mklayout();
   }

   @Override
   protected int getXOffset() {
      return 1;
   }

   @Override
   protected boolean isleftright(gleisbildModel glb, int x) {
      return false;
   }

   private void mklayout() {
      LinkedList<inserttoken> l = new LinkedList();
      gleistoken gt = new streckengleistoken();
      gt.bgcolor = "normal";
      l.add(gt);
      gt = new swgleistoken(gleis.ELEMENT_DISPLAYKONTAKT, gleisElements.RICHTUNG.left, "kontakt");
      gt.bgcolor = "schwarz";
      l.add(gt);
      gleistoken var7 = new enrgleistoken(gleis.ELEMENT_SPRUNG, gleisElements.RICHTUNG.right, "enr");
      var7.bgcolor = "schwarz";
      l.add(var7);
      element elm = gleisHelper.calcSizeDisplay(this.size);
      gleistoken var8 = new swgleistoken(elm, gleisElements.RICHTUNG.right, "display");
      var8.bgcolor = "schwarz";
      l.add(var8);
      bgcolortoken bt = new bgcolortoken("schwarz");

      for(int i = 1; i < this.size; ++i) {
         l.add(bt);
      }

      gleistoken var9 = new enrgleistoken(gleis.ELEMENT_SPRUNG, gleisElements.RICHTUNG.left, "enr");
      var9.bgcolor = "schwarz";
      l.add(var9);
      gleistoken var10 = new streckengleistoken();
      var10.bgcolor = "schwarz";
      l.add(var10);
      gleistoken var11 = new streckengleistoken();
      var11.bgcolor = "normal";
      l.add(var11);
      this.setLayout(l);
   }

   @Override
   public String getName() {
      return "Gleis mit Display";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5039 $";
   }

   @Override
   public void action(String n, JComponent source) {
      if (n.startsWith("size")) {
         this.size = Integer.parseInt(n.substring(4));
      } else if (n.equalsIgnoreCase("display")) {
         JTextField f = (JTextField)source;
         this.storage.put("display", f.getText());
      } else if (n.equalsIgnoreCase("kontakt")) {
         JTextField f = (JTextField)source;
         this.storage.put("kontakt", f.getText());
      }

      this.mklayout();
      this.refreshPreview();
   }

   @Override
   public void focuslost(String n, JComponent source) {
      this.action(n, source);
   }

   @Override
   public void textchange(String n, JComponent source) {
      if (!n.equalsIgnoreCase("size")) {
         this.focuslost(n, source);
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
      this.setLayout(new BorderLayout());
   }
}

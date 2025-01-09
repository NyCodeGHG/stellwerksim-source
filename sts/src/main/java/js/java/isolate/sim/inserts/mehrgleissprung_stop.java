package js.java.isolate.sim.inserts;

import java.awt.BorderLayout;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
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

public class mehrgleissprung_stop extends insert {
   private int enr = 0;
   private TreeMap<Integer, LinkedList<gleis>> allenrs;
   private LinkedList<gleis> allowedenrs;

   public mehrgleissprung_stop(GleisAdapter m, gleisbildModel glb) {
      super(m, glb);
      this.enr = 0;
   }

   @Override
   protected void initInterface() {
      super.initInterface();
      this.mklayout();
      this.addENRInput("Sprung-ENR", "enr", this.allowedenrs);
   }

   @Override
   protected int getXOffset() {
      return 1;
   }

   private void mklayout() {
      LinkedList<inserttoken> l = new LinkedList();
      LinkedList<gleis> ll = null;
      this.findUsefullEnr();
      String bgcolor = "normal";
      int anzahl = 1;
      if (this.enr == 0 && !this.allenrs.isEmpty()) {
         this.enr = (Integer)this.allenrs.firstKey();
      }

      if (this.enr != 0) {
         ll = (LinkedList<gleis>)this.allenrs.get(this.enr);
         if (ll == null && !this.allenrs.isEmpty()) {
            this.enr = (Integer)this.allenrs.firstKey();
            ll = (LinkedList<gleis>)this.allenrs.get(this.enr);
         }
      }

      if (ll != null) {
         anzahl = ll.size();
         bgcolor = ((gleis)ll.getFirst()).getExtendFarbe();
      }

      for (int i = 0; i < anzahl; i++) {
         bgcolortoken bt = new bgcolortoken(bgcolor);
         l.add(bt);
         gleistoken gt = new enrgleistoken(gleis.ELEMENT_SPRUNG, gleisElements.RICHTUNG.left, "enr" + i);
         gt.bgcolor = bgcolor;
         l.add(gt);
         gt = new streckengleistoken();
         gt.bgcolor = bgcolor;
         l.add(gt);
         l.add(gt);
         gleistoken var9 = new streckengleistoken();
         var9.bgcolor = "normal";
         l.add(var9);
         if (i + 1 < anzahl && anzahl > 1) {
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
      return "mehrgleisiger Sprung Gegenstück";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5039 $";
   }

   private LinkedList<gleis> sprungBelow(gleis gl) {
      int y = gl.getRow();
      int x = gl.getCol();
      LinkedList<gleis> n = new LinkedList();
      n.add(gl);
      gleis gl2 = null;

      do {
         gl2 = this.glbModel.getXY_null(x, y + n.size() * 2);
         if (gl2 != null
            && gl2.getElement() == gleis.ELEMENT_SPRUNG
            && gl2.getENR() != gl.getENR()
            && gl.getExtendFarbe().equalsIgnoreCase(gl2.getExtendFarbe())) {
            n.add(gl2);
         } else {
            gl2 = null;
         }
      } while (gl2 != null);

      return n;
   }

   private void findUsefullEnr() {
      this.allenrs = new TreeMap();
      this.allowedenrs = new LinkedList();
      HashSet<Integer> seenenrs = new HashSet();
      Iterator<gleis> it = this.glbModel.findIterator(gleis.ELEMENT_SPRUNG);

      while (it.hasNext()) {
         gleis gl = (gleis)it.next();
         if (!seenenrs.contains(gl.getENR())) {
            seenenrs.add(gl.getENR());
            if (this.glbModel.findFirst(gl, gl.getENR(), gleis.ELEMENT_SPRUNG) == null) {
               LinkedList<gleis> b = this.sprungBelow(gl);
               this.allenrs.put(gl.getENR(), b);
               this.allowedenrs.add(gl);

               for (gleis g : b) {
                  seenenrs.add(g.getENR());
               }
            }
         }
      }
   }

   @Override
   public void action(String n, JComponent source) {
      if (n.equalsIgnoreCase("enr")) {
         JComboBox f = (JComboBox)source;
         gleis gl = (gleis)f.getSelectedItem();
         this.enr = gl.getENR();
         this.mklayout();
         this.refreshPreview();
      }
   }

   @Override
   protected void initVariables(boolean leftright) {
      LinkedList<gleis> ll = null;
      BitSet bs = this.glbModel.getENRbitset();
      if (this.enr == 0 && !this.allenrs.isEmpty()) {
         this.enr = (Integer)this.allenrs.firstKey();
      }

      if (this.enr != 0) {
         ll = (LinkedList<gleis>)this.allenrs.get(this.enr);
         if (ll == null && !this.allenrs.isEmpty()) {
            this.enr = (Integer)this.allenrs.firstKey();
            ll = (LinkedList<gleis>)this.allenrs.get(this.enr);
         }
      }

      if (ll != null) {
         int i = 0;

         for (gleis g : ll) {
            this.storage.put("enr" + i, g.getENR() + "");
            i++;
         }
      } else {
         int e = bs.nextClearBit(1);
         bs.set(e);
         this.storage.put("enr0", e + "");
      }
   }

   private void initComponents() {
      this.setLayout(new BorderLayout());
   }
}

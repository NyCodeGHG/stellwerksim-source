package js.java.isolate.sim.inserts;

import java.awt.BorderLayout;
import java.util.LinkedList;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleisbild.gleisbildModel;
import js.java.isolate.sim.inserts.inserttoken.inserttoken;
import js.java.isolate.sim.inserts.inserttoken.newlinetoken;
import js.java.isolate.sim.inserts.inserttoken.streckengleistoken;

public class testinsert extends insert {
   public testinsert(GleisAdapter m, gleisbildModel glb) {
      super(m, glb);
      this.initComponents();
      LinkedList<inserttoken> l = new LinkedList();
      l.add(new streckengleistoken());
      l.add(new streckengleistoken());
      l.add(new streckengleistoken());
      l.add(new newlinetoken());
      l.add(new newlinetoken());
      l.add(new streckengleistoken());
      l.add(new streckengleistoken());
      l.add(new streckengleistoken());
      this.setLayout(l);
   }

   @Override
   public String getName() {
      return "testinsert";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5039 $";
   }

   @Override
   protected void initVariables(boolean demo) {
   }

   private void initComponents() {
      this.setLayout(new BorderLayout());
   }
}

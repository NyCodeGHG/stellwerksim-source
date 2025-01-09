package js.java.isolate.fahrplaneditor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;
import javax.swing.SwingWorker;
import js.java.tools.gui.warningPopup.IconPopupButton;

class dataValidator extends SwingWorker<LinkedList<dataFailures>, Object> {
   private fahrplaneditor my_main;

   dataValidator(fahrplaneditor a) {
      this.my_main = a;
   }

   protected LinkedList<dataFailures> doInBackground() throws Exception {
      System.out.println("validator start");
      LinkedList<dataFailures> l = new LinkedList();
      LinkedList<bahnhof> bhfs = this.my_main.getBhfs();
      TreeSet<bahnhof> sort = new TreeSet();

      for (bahnhof b : bhfs) {
         sort.add(b);
      }

      this.my_main.hasEKTM.clear();
      this.my_main.hadEKF.clear();

      for (bahnhof b : sort) {
         if (this.my_main.checker_stop) {
            return null;
         }

         b.validate(null);
      }

      this.my_main.hasEKTM.clear();

      for (bahnhof b : sort) {
         if (this.my_main.checker_stop) {
            return null;
         }

         b.validate(l);
      }

      return l;
   }

   protected void done() {
      this.my_main.errorcnt = this.my_main.getBhfs().isEmpty() ? 1 : 0;

      try {
         for (bahnhof b : this.my_main.getBhfs()) {
            b.postValidate();
         }

         LinkedList<dataFailures> l = (LinkedList<dataFailures>)this.get();
         if (l != null) {
            HashMap<IconPopupButton, LinkedList<dataFailures>> list = new HashMap();

            for (dataFailures d : l) {
               if (!list.containsKey(d.getButton())) {
                  list.put(d.getButton(), new LinkedList());
               }

               ((LinkedList)list.get(d.getButton())).add(d);
               this.my_main.errorcnt++;
            }

            if (this.my_main.errorcnt > 0) {
            }

            for (IconPopupButton b : list.keySet()) {
               HashMap<String, LinkedList<solutionInterface>> sols = new HashMap();

               for (dataFailures d : (LinkedList)list.get(b)) {
                  if (!sols.containsKey(d.getMessage())) {
                     sols.put(d.getMessage(), new LinkedList());
                  }

                  ((LinkedList)sols.get(d.getMessage())).addAll(d.getSolutions());
               }

               b.clearWarning();

               for (String m : sols.keySet()) {
                  b.addWarning(m, (LinkedList)sols.get(m));
                  if (b.getWidth() == 0) {
                     this.my_main.showFullLine(true);
                  }
               }
            }
         }

         System.out.println("validator done");
      } catch (Exception var9) {
         var9.printStackTrace();
      }

      this.my_main.checker_running = false;
      this.my_main.startChecker();
   }
}

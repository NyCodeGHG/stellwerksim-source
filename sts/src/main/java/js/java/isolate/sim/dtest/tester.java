package js.java.isolate.sim.dtest;

import java.util.LinkedList;
import java.util.TreeSet;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class tester {
   private dtest[] tests = null;
   private final gleisbildModelSts glbModel;
   private final tester.callback my_main;

   private void construct() {
      if (this.tests == null) {
         this.tests = new dtest[]{
            new ausfahrttest1(),
            new ausfahrttest2(),
            new einfahrttest1(),
            new einfahrttest2(),
            new sprungfarbetest(),
            new sprung1elementtest(),
            new sprung2elementtest(),
            new randtest(),
            new bahnsteigtest1(),
            new bahnsteigtest2(),
            new bahnsteigtest3(),
            new bahnsteigtest4(),
            new bahnsteigtest5(),
            new buetest1(),
            new buetest2(),
            new signaltest1(),
            new signaltest2(),
            new signaltest3(),
            new signaltest4(),
            new signaltest5(),
            new signaltest6(),
            new signaltest7(),
            new signalkopfgleistest1(),
            new signalkopfgleistest2(),
            new displaytest1(),
            new displaytest2(),
            new displaytest3(),
            new displaytest4(),
            new farbtest1(),
            new texttest1(),
            new texttest2(),
            new enr0test(),
            new namedoppelttest(),
            new enrausfahrttest1(),
            new enreinfahrttest1(),
            new ausfahrtueptest1(),
            new enrueptest1(),
            new ueptest1(),
            new ueptest2(),
            new akzeptortest1(),
            new enrsprungtest1(),
            new fahrstrassetest1(),
            new fahrstrassetest2(),
            new fahrstrassetest3(),
            new fahrstrassetest4(),
            new fahrstrassetest5(),
            new fahrstrassetest6(),
            new fahrstrassetest7(),
            new fahrstrassetest8(),
            new fahrstrassetest9(),
            new fahrstrassetest10(),
            new nolighttest1(),
            new nolighttest2(),
            new sizetest(),
            new enrduptest(),
            new uepdisplaytest(),
            new massbigtest(),
            new bvtest1(),
            new bvtest2(),
            new swwerttest1(),
            new swwertVmaxtest(),
            new zwergtest1(),
            new zwergtest2(),
            new zwergtest3(),
            new zwergtest4(),
            new zwergtest5(),
            new vsignaltest2(),
            new vsignaltest3(),
            new vsignaltest4(),
            new signalkeinefstest(),
            new zwergbstgtest(),
            new zwergzdstest(),
            new signalgesichttest(),
            new zielknopftest1(),
            new zielknopftest2(),
            new zielknopftest3()
         };
      }
   }

   public tester(tester.callback ed, gleisbildModelSts glb) {
      super();
      this.glbModel = glb;
      this.my_main = ed;
      this.construct();
   }

   public int testList() {
      return this.tests.length;
   }

   public TreeSet<dtestresult> runTest() {
      TreeSet<dtestresult> results = new TreeSet();
      this.my_main.setGUIEnable(false);
      this.my_main.setProgress(0);
      this.my_main.showStatus("Test gestartet.", 0);
      this.glbModel.purgeFahrwege();
      int i = 0;

      for(dtest t : this.tests) {
         this.my_main.setProgress(i * 100 / this.tests.length);
         this.my_main.showStatus("Test " + t.getName() + " (" + t.getVersion() + ").", 0);

         try {
            LinkedList<dtestresult> r = t.runTest(this.glbModel);
            results.addAll(r);
         } catch (Exception var9) {
            System.out.println(var9.getMessage());
            var9.printStackTrace();
            dtestresult d = new dtestresult(1, "Test " + t.getName() + " ist mit einem Programmfehler ausgestiegen! (" + var9.getMessage() + ")");
            results.add(d);
         }

         ++i;
      }

      this.my_main.setProgress(100);
      this.my_main.showStatus("Test abgeschlossen.", 0);
      if (results.isEmpty()) {
         dtestresult d = new dtestresult(0, "Die Tests haben keine Auffälligkeiten gefunden.");
         results.add(d);
      } else {
         this.my_main.showStatus("Die Ergebnisse stehen im Editor.", 0);
      }

      this.my_main.setGUIEnable(true);
      return results;
   }

   public interface callback {
      void setGUIEnable(boolean var1);

      void setProgress(int var1);

      void showStatus(String var1, int var2);
   }
}

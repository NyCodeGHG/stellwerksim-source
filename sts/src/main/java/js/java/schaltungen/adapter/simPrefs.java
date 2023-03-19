package js.java.schaltungen.adapter;

import js.java.tools.prefs;

public class simPrefs extends prefs {
   public simPrefs() {
      super("/org/js-home/stellwerksim/main");
   }

   public simPrefs(String node) {
      super("/org/js-home/stellwerksim/" + node);
   }
}

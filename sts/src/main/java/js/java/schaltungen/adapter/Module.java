package js.java.schaltungen.adapter;

import js.java.isolate.fahrplaneditor.fahrplaneditor;
import js.java.isolate.gleisbelegung.belegung;
import js.java.isolate.landkarteneditor.landkarteneditor;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.stellwerksim;
import js.java.isolate.statusapplet.stellwerk_karte;
import js.java.isolate.statusapplet.stellwerk_players;

public enum Module {
   SIM(stellwerksim.class, "Simulator", "/js/java/isolate/sim/stellwerksim.html", false, true),
   GLEISEDITOR(stellwerk_editor.class, "Gleiseditor", "/js/java/isolate/sim/stellwerk_editor.html", true, false),
   FAHRPLANEDITOR(fahrplaneditor.class, "Fahrplaneditor", "/js/java/isolate/fahrplaneditor/fahrplaneditor.html", true, false),
   KARTENEDITOR(landkarteneditor.class, "Landkarteneditor", "/js/java/isolate/landkarteneditor/landkarteneditor.html", true, false),
   GLEISBELEGUNG(belegung.class, "Gleisbelegung", "/js/java/isolate/gleisbelegung/belegung.html", true, false),
   LIVEKARTE(stellwerk_karte.class, "Live Karte", "/js/java/isolate/statusapplet/stellwerk_karte.html", false, true),
   STATUS(stellwerk_players.class, "Spielerstatus", "/js/java/isolate/statusapplet/stellwerk_players.html", false, true);

   public final String launch;
   public final String title;
   public final String testfile;
   public final boolean multiInstances;
   public final boolean singleModule;

   private Module(Class launch, String title, String testfile, boolean multi, boolean single) {
      this.launch = launch.getCanonicalName();
      this.title = title;
      this.testfile = testfile;
      this.singleModule = single;
      this.multiInstances = !this.singleModule & multi;
   }
}

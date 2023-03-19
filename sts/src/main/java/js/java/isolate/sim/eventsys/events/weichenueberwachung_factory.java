package js.java.isolate.sim.eventsys.events;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventFactory;
import js.java.isolate.sim.gleisbild.gleisbildModelEventsys;

public class weichenueberwachung_factory extends eventFactory {
   private JSpinner dauer;

   public weichenueberwachung_factory() {
      super();
   }

   @Override
   public String getName() {
      return "Weichenzungenüberwachung";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return weichenueberwachung.class;
   }

   @Override
   protected void initGui() {
      this.dauer = new JSpinner(new SpinnerNumberModel(10, 3, 30, 1));
      this.dauer.setToolTipText("<html>Dauer</html>");
      this.add("Dauer", "Dauer (Minuten)", this.dauer, false);
   }

   @Override
   public void showContainer(eventContainer ev, boolean editmode) {
      super.showContainer(ev, editmode);
      this.dauer.setValue(ev.getIntValue("dauer", 10));
      this.dauer.setEnabled(editmode);
   }

   @Override
   public void readContainer(eventContainer ev) {
      super.readContainer(ev);
      ev.setIntValue("dauer", Math.max(this.dauer.getValue(), 3));
   }

   @Override
   public String getDescription() {
      return "Weichenzungenüberwachung für eine einstellbare Zeit gestört.";
   }

   @Override
   public boolean serverEvent(eventContainer ev, gleisbildModelEventsys glb, String parameter) {
      ev.setIntValue("dauer", random(5, 20));
      if (parameter.equalsIgnoreCase("EXTRA")) {
         ev.setIntValue("dauer", random(15, 30));
      }

      return true;
   }

   @Override
   public boolean isStopFollowing() {
      return true;
   }
}

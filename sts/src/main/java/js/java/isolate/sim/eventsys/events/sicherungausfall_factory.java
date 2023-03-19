package js.java.isolate.sim.eventsys.events;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventFactory;
import js.java.isolate.sim.gleisbild.gleisbildModelEventsys;

public class sicherungausfall_factory extends eventFactory {
   private JSpinner dauer;

   public sicherungausfall_factory() {
      super();
   }

   @Override
   public String getName() {
      return "Sicherung geflogen";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return sicherungausfall.class;
   }

   @Override
   protected void initGui() {
      this.dauer = new JSpinner(new SpinnerNumberModel(3, 3, 10, 1));
      this.add("Dauer", "Dauer (Minuten)", this.dauer, false);
   }

   @Override
   public void showContainer(eventContainer ev, boolean editmode) {
      super.showContainer(ev, editmode);
      this.dauer.setValue(Math.max(ev.getIntValue("dauer", 3), 3));
      this.dauer.setEnabled(editmode);
   }

   @Override
   public void readContainer(eventContainer ev) {
      super.readContainer(ev);
      ev.setIntValue("dauer", Math.max(this.dauer.getValue(), 3));
   }

   @Override
   public String getDescription() {
      return "Sicherung für ein Teil des Stelltisches geflogen für eine einstellbare Zeit.";
   }

   @Override
   public boolean serverEvent(eventContainer ev, gleisbildModelEventsys glb, String parameter) {
      ev.setIntValue("dauer", random(4, 8));
      return true;
   }
}

package js.java.isolate.sim.eventsys.events;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventFactory;
import js.java.isolate.sim.gleisbild.gleisbildModelEventsys;

public class fsspeicherstoerung_factory extends eventFactory {
   private JSpinner num;

   public fsspeicherstoerung_factory() {
      super();
   }

   @Override
   public String getName() {
      return "Fahrstraßenspeicher-Störung";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return fsspeicherstoerung.class;
   }

   @Override
   protected void initGui() {
      this.num = new JSpinner(new SpinnerNumberModel(10, 1, 60, 1));
      this.add("Details", "Dauer (Minuten)", this.num, false);
   }

   @Override
   public void showContainer(eventContainer ev, boolean editmode) {
      super.showContainer(ev, editmode);
      this.num.setValue(Math.max(ev.getIntValue("dauer", 10), 1));
      this.num.setEnabled(editmode);
   }

   @Override
   public void readContainer(eventContainer ev) {
      super.readContainer(ev);
      ev.setIntValue("dauer", Math.max(this.num.getValue(), 1));
   }

   @Override
   public String getDescription() {
      return "Ausfall des Fahrstraßenspeichers für eine einstellbare Zeit.";
   }

   @Override
   public boolean serverEvent(eventContainer ev, gleisbildModelEventsys glb, String parameter) {
      ev.setIntValue("dauer", random(8, 30));
      return true;
   }

   @Override
   public boolean isStopFollowing() {
      return true;
   }
}

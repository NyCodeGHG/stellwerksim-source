package js.java.isolate.sim.eventsys.events;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventFactory;

public class bahnuebergangstoerung_factory extends eventFactory {
   private JSpinner num;

   @Override
   public String getName() {
      return "Bahnübergangsstörung";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return bahnuebergangstoerung.class;
   }

   @Override
   protected void initGui() {
      this.num = new JSpinner(new SpinnerNumberModel(5, 1, 60, 1));
      this.add("Details", "Dauer (Minuten)", this.num, false);
   }

   @Override
   public void showContainer(eventContainer ev, boolean editmode) {
      super.showContainer(ev, editmode);
      this.num.setValue(Math.max(ev.getIntValue("dauer", 5), 1));
      this.num.setEnabled(editmode);
   }

   @Override
   public void readContainer(eventContainer ev) {
      super.readContainer(ev);
      ev.setIntValue("dauer", Math.max((Integer)this.num.getValue(), 1));
   }

   @Override
   public String getDescription() {
      return "Ausfall eines Bahnübergangs für eine einstellbare Zeit beim Anlegen einer Fahrstraße.";
   }
}

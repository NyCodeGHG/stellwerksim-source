package js.java.isolate.sim.eventsys.events;

import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventFactory;
import js.java.isolate.sim.gleisbild.gleisbildModelEventsys;

public class relaisgruppestoerung_factory extends eventFactory {
   private JSpinner num;
   private JCheckBox stark;

   @Override
   public String getName() {
      return "Relaisgruppenstörung";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return relaisgruppestoerung.class;
   }

   @Override
   protected void initGui() {
      this.num = new JSpinner(new SpinnerNumberModel(5, 5, 12, 5));
      this.add("Details", "Maximaldauer (Minuten)", this.num, false);
      this.stark = new JCheckBox("stark frequentierte bevorzugen");
      this.stark.setToolTipText("es werden bevorzugt stark befahrere Signale gestört");
      this.add("Details", null, this.stark, false);
   }

   @Override
   public void showContainer(eventContainer ev, boolean editmode) {
      super.showContainer(ev, editmode);
      this.num.setValue(Math.max(ev.getIntValue("dauer", 5), 5));
      this.num.setEnabled(editmode);
      this.stark.setSelected(ev.getBoolValue("stark", true));
      this.stark.setEnabled(editmode);
   }

   @Override
   public void readContainer(eventContainer ev) {
      super.readContainer(ev);
      ev.setIntValue("dauer", Math.max((Integer)this.num.getValue(), 5));
      ev.setValue("stark", this.stark.isSelected());
   }

   @Override
   public String getDescription() {
      return "Ausfall mehrerer Elemente.";
   }

   @Override
   public boolean serverEvent(eventContainer ev, gleisbildModelEventsys glb, String parameter) {
      ev.setIntValue("dauer", random(5, 12));
      ev.setValue("stark", true);
      return true;
   }

   @Override
   public boolean isStopFollowing() {
      return true;
   }
}

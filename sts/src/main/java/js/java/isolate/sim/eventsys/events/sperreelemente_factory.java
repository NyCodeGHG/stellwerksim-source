package js.java.isolate.sim.eventsys.events;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.NumberEditor;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;

public class sperreelemente_factory extends sperrungBaseFactory {
   private JSpinner starttimefield;
   private JSpinner stoptimefield;
   private SpinnerNumberModel starttime;
   private SpinnerNumberModel stoptime;

   public sperreelemente_factory() {
      super();
   }

   @Override
   public String getName() {
      return "Sperre Elemente permanent";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return sperreelemente.class;
   }

   @Override
   public boolean isRandom() {
      return false;
   }

   @Override
   protected void initGui() {
      super.initGui();
      this.starttime = new SpinnerNumberModel(0, 0, 24, 1);
      this.starttimefield = new JSpinner(this.starttime);
      this.starttimefield.setEditor(new NumberEditor(this.starttimefield, "00"));
      this.starttimefield.setToolTipText("<html>Greift nur, wenn Spieler danach Sim betritt.<br>Kein Störungsbeginn bei laufendem Spiel.</html>");
      this.add("Zeitraum", "Startzeit (Stunde)", this.starttimefield, false);
      this.stoptime = new SpinnerNumberModel(24, 0, 24, 1);
      this.stoptimefield = new JSpinner(this.stoptime);
      this.stoptimefield.setEditor(new NumberEditor(this.stoptimefield, "00"));
      this.starttimefield.setToolTipText("<html>Greift nur, wenn Spieler danach Sim betritt.<br>Störung endet nur beim Verlassen des Sims.</html>");
      this.add("Zeitraum", "Stopzeit (Stunde)", this.stoptimefield, false);
   }

   @Override
   public void showContainer(eventContainer ev, boolean editmode) {
      super.showContainer(ev, editmode);
      this.starttimefield.setEnabled(editmode);
      this.starttime.setValue(ev.getLongValue("starthour"));
      this.stoptimefield.setEnabled(editmode);
      if (ev.isValue("stophour")) {
         this.stoptime.setValue(ev.getLongValue("stophour"));
      } else {
         this.stoptime.setValue(23);
      }
   }

   @Override
   public void readContainer(eventContainer ev) {
      super.readContainer(ev);
      ev.setLongValue("starthour", (long)this.starttime.getNumber().intValue());
      ev.setLongValue("stophour", (long)this.stoptime.getNumber().intValue());
   }

   @Override
   public String getDescription() {
      return "Ausfall mehrerer Elemente ab Sim-Start.";
   }

   @Override
   public boolean isIndependantEvent() {
      return true;
   }
}

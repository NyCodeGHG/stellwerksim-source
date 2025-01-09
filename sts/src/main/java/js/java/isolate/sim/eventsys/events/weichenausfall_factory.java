package js.java.isolate.sim.eventsys.events;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventFactory;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelEventsys;

public class weichenausfall_factory extends eventFactory {
   private JSpinner num;

   @Override
   public String getName() {
      return "Weichenausfall";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return weichenausfall.class;
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
      return "Ausfall einer Weiche für eine einstellbare Zeit beim Anlegen einer Fahrstraße.";
   }

   @Override
   public boolean serverEvent(eventContainer ev, gleisbildModelEventsys glb, String parameter) {
      try {
         if (!parameter.isEmpty()) {
            String[] p = parameter.split(",");
            int enr = Integer.parseInt(p[0]);
            gleis gl = glb.findFirst(new Object[]{enr, gleis.ALLE_WEICHEN});
            if (gl != null) {
               int dauer = random(10, 15);
               if (p.length > 1) {
                  dauer = Integer.parseInt(p[1]);
               }

               ev.setGleis(gl);
               ev.setIntValue("dauer", dauer);
               return true;
            }
         }
      } catch (Exception var8) {
      }

      return false;
   }

   @Override
   public boolean isStopFollowing() {
      return true;
   }
}

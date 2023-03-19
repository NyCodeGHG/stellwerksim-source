package js.java.isolate.sim.eventsys.events;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelEventsys;

public class sperreelementeaufzeit_factory extends sperrungBaseFactory {
   private JSpinner num;

   public sperreelementeaufzeit_factory() {
      super();
   }

   @Override
   public String getName() {
      return "Sperre Elemente dynamisch";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return sperreelementeaufzeit.class;
   }

   @Override
   public boolean isRandom() {
      return true;
   }

   @Override
   protected void initGui() {
      super.initGui();
      this.num = new JSpinner(new SpinnerNumberModel(5, 5, 120, 5));
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
      ev.setIntValue("dauer", Math.max(this.num.getValue(), 1));
   }

   @Override
   public String getDescription() {
      return "Ausfall mehrerer Elemente ab einem Zeitpunkt.";
   }

   @Override
   public boolean isIndependantEvent() {
      return true;
   }

   @Override
   public boolean serverEvent(eventContainer ev, gleisbildModelEventsys glb, String parameter) {
      try {
         if (!parameter.isEmpty()) {
            Matcher m = Pattern.compile("\"(.*)\",(.+)").matcher(parameter);
            if (m.matches() && m.groupCount() == 2) {
               String[] p = m.group(2).split(",");
               ev.setValue("text", m.group(1).replace('_', ' '));
               ev.setName("Sperrung " + (glb.events.size() + 1));
               HashSet<gleis> set = new HashSet();

               for(int i = 0; i < p.length; ++i) {
                  String t = p[i];
                  if (t.startsWith("DAUER=")) {
                     t = t.substring(6);
                     int dauer = Integer.parseInt(t);
                     ev.setValue("dauer", dauer);
                  } else if (t.startsWith("FAST")) {
                     ev.setValue("schnell", true);
                  } else {
                     int enr = Integer.parseInt(t);
                     gleis gl = glb.findFirst(
                        new Object[]{
                           enr, gleis.ELEMENT_SIGNAL, gleis.ELEMENT_WEICHEOBEN, gleis.ELEMENT_WEICHEUNTEN, gleis.ELEMENT_AUSFAHRT, gleis.ELEMENT_EINFAHRT
                        }
                     );
                     if (gl != null) {
                        set.add(gl);
                     }
                  }
               }

               if (!set.isEmpty()) {
                  ev.setGleisList(set);
                  return true;
               }
            }
         } else {
            HashSet<gleis> set = new HashSet();
            gleis gl = glb.findFirst(new Object[]{gleis.ELEMENT_SIGNAL});
            if (gl != null) {
               set.add(gl);
            }

            if (!set.isEmpty()) {
               ev.setGleisList(set);
               return true;
            }
         }
      } catch (Exception var11) {
         var11.printStackTrace();
      }

      return false;
   }
}

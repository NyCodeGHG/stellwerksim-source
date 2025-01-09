package js.java.isolate.sim.sim.funk;

import java.util.Iterator;
import javax.swing.JOptionPane;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.events.hotlineDefektEvent;
import js.java.isolate.sim.gleisbild.gleisbildModelEventsys;
import js.java.isolate.sim.sim.zugUndPlanPanel;
import js.java.isolate.sim.zug.zug;

public class funk_hotline extends funk_zugBase {
   private event runningE = null;
   private final stCodeFrame codewin;

   public funk_hotline(zugUndPlanPanel.funkAdapter a, zug z, int unterzug) {
      super("Hotline", a, z, unterzug);
      this.codewin = new stCodeFrame(a);
      if (!this.my_main.isCaller()) {
         this.addValueItem(new funk_hotline.funk_cmd("Störungsannahme via Funk") {
            @Override
            void called(zug z) {
               funk_hotline.this.code();
            }
         });
      }

      this.addValueItem(new funk_hotline.funk_cmd("Telefon gestört") {
         @Override
         void called(zug z) {
            funk_hotline.this.gestoert();
         }
      });
      if (z != null) {
         if (this.my_main.isRedirectAllowedMode() && z.isMytrain() && this.my_main.isOnline() && this.match(z)) {
            this.addValueItem(new funk_hotline.funk_cmd("Zugumleitung veranlassen") {
               @Override
               void called(zug z) {
                  funk_hotline.this.redirect(z);
               }
            });
         }

         if (this.my_main.isOnline() && this.match(z)) {
            this.addValueItem(new funk_hotline.funk_cmd("Zugfahrweg anfragen") {
               @Override
               void called(zug z) {
                  funk_hotline.this.fahrweg(z);
               }
            });
         }
      }
   }

   @Override
   public void selected(funkAuftragBase.funkValueItem sel) {
      funk_hotline.funk_cmd c = (funk_hotline.funk_cmd)sel;
      c.called(this.z);
   }

   private void gestoert() {
      if (this.runningE == null || this.runningE.isEventDone()) {
         eventContainer ev = new eventContainer((gleisbildModelEventsys)this.my_main.my_gleisbild(), hotlineDefektEvent.class);
         this.runningE = event.createEvent(ev, (gleisbildModelEventsys)this.my_main.my_gleisbild(), this.my_main.getSimulator());
      }
   }

   private void code() {
      this.codewin.showDialog(this.my_main.funkReferenceComponent());
   }

   private void redirect(zug z) {
      if (JOptionPane.showConfirmDialog(
            this.my_main.funkReferenceComponent(), "Dies hat auch Auswirkungen auf andere Spieler!\nWirklich ausführen?", "Umleitung bestätigen", 2, 2
         )
         == 0) {
         this.my_main.requestZugRedirect(z);
      }
   }

   private void fahrweg(zug z) {
      this.my_main.requestZugFahrweg(z);
   }

   private boolean match(zug z) {
      boolean match = z.getAusEnr() != 0;
      if (match) {
         Iterator<zug> it = z.getAllUnseenFahrplanzeilen();

         while (it.hasNext() && match) {
            zug zz = (zug)it.next();
            match &= !this.hasMatchingFlag(zz);
         }
      }

      return match;
   }

   private boolean hasMatchingFlag(zug z) {
      return z.getFlags().hasFlag('E') || z.getFlags().hasFlag('K');
   }

   private abstract class funk_cmd extends funkAuftragBase.funkValueItem {
      funk_cmd(String text) {
         super(text, 0);
      }

      public String toString() {
         return this.text;
      }

      abstract void called(zug var1);
   }
}

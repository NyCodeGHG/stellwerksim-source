package js.java.schaltungen.adapter;

import de.deltaga.eb.EventBusService;
import java.awt.Component;
import js.java.schaltungen.UserContextMini;
import js.java.schaltungen.settings.PrefsChangedEvent;
import js.java.tools.prefs;

public class closePrefs extends prefs {
   public closePrefs() {
      super("/org/js-home/stellwerksim/close");
   }

   public closePrefs(String node) {
      super("/org/js-home/stellwerksim/close/" + node);
   }

   public boolean is(closePrefs.Parts p) {
      return this.getBoolean(p.name(), true);
   }

   public void set(closePrefs.Parts p, boolean enable) {
      this.putBoolean(p.name(), enable);
   }

   public static enum Parts {
      COMMUNICATOR,
      SIM,
      GLEISEDITOR,
      FAHRPLANEDITOR,
      LANDKARTE;

      public boolean is() {
         return new closePrefs().is(this);
      }

      public void clear() {
         new closePrefs().set(this, false);
         EventBusService.getInstance().publish(new PrefsChangedEvent(this));
      }

      public boolean ask(UserContextMini uc, Component parent, String title) {
         return this.is() ? new CloseConfirmDialog(uc, parent, this, title).confirm() : true;
      }
   }
}

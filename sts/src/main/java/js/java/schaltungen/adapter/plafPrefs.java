package js.java.schaltungen.adapter;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import de.deltaga.eb.EventBusService;
import java.awt.Color;
import java.awt.Insets;
import javax.swing.UIManager;
import js.java.schaltungen.settings.PrefsChangedEvent;
import js.java.tools.prefs;

public class plafPrefs extends prefs {
   public plafPrefs() {
      super("/org/js-home/stellwerksim/plaf");
   }

   public plafPrefs(String node) {
      super("/org/js-home/stellwerksim/plaf/" + node);
   }

   public boolean is(plafPrefs.Parts p) {
      return this.getBoolean(p.name(), false);
   }

   public void set(plafPrefs.Parts p, boolean enable) {
      this.putBoolean(p.name(), enable);
   }

   public static enum Parts {
      EMBEDD_MENU {
         @Override
         public void configure() {
            UIManager.put("TitlePane.menuBarEmbedded", !this.is());
            FlatLaf.revalidateAndRepaintAllFramesAndDialogs();
         }
      },
      SCROLL_BUTTONS {
         @Override
         public void configure() {
            UIManager.put("ScrollBar.showButtons", this.is());
            FlatLaf.revalidateAndRepaintAllFramesAndDialogs();
         }
      },
      LARGE_SCROLLERS {
         @Override
         public void configure() {
            UIManager.put("ScrollBar.width", this.is() ? 16 : 10);
            UIManager.put("ScrollBar.trackInsets", new Insets(2, 4, 2, 4));
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
            UIManager.put("ScrollBar.track", new Color(14737632));
            FlatLaf.revalidateAndRepaintAllFramesAndDialogs();
         }
      };

      private Parts() {
      }

      public boolean is() {
         return new plafPrefs().is(this);
      }

      public void clear() {
         new plafPrefs().set(this, false);
         EventBusService.getInstance().publish(new PrefsChangedEvent(this));
      }

      public abstract void configure();
   }

   public static enum Style {
      NORMAL {
         @Override
         public FlatLaf get() {
            return new FlatIntelliJLaf();
         }
      };

      private Style() {
      }

      public abstract FlatLaf get();
   }
}

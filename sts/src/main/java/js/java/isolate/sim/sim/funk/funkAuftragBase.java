package js.java.isolate.sim.sim.funk;

import java.util.LinkedList;
import java.util.List;
import js.java.isolate.sim.sim.zugUndPlanPanel;

public abstract class funkAuftragBase {
   private final String titel;
   protected final zugUndPlanPanel.funkAdapter my_main;
   private final LinkedList<funkAuftragBase.funkValueItem> items = new LinkedList();

   protected funkAuftragBase(String t, zugUndPlanPanel.funkAdapter a) {
      this.titel = t;
      this.my_main = a;
   }

   public String getTitel() {
      return this.titel;
   }

   protected void addValueItem(funkAuftragBase.funkValueItem fvi) {
      this.items.add(fvi);
   }

   public abstract void selected(funkAuftragBase.funkValueItem var1);

   public List<funkAuftragBase.funkValueItem> getValues() {
      return this.items;
   }

   public static class dataFunkValueItem<T> extends funkAuftragBase.funkValueItem {
      public final T e;

      dataFunkValueItem(String text, int id, T e) {
         super(text, id);
         this.e = e;
      }

      dataFunkValueItem(String text, int id, String iconName, T e) {
         super(text, id, iconName);
         this.e = e;
      }
   }

   public static class funkValueItem {
      public final String text;
      public final int id;
      public final String iconName;

      funkValueItem(String text, int id) {
         this.text = text;
         this.id = id;
         this.iconName = null;
      }

      funkValueItem(String text, int id, String iconName) {
         this.text = text;
         this.id = id;
         this.iconName = iconName;
      }
   }
}

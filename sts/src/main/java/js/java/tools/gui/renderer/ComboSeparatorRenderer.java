package js.java.tools.gui.renderer;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;

public class ComboSeparatorRenderer implements ListCellRenderer {
   private ListCellRenderer delegate;
   private JPanel separatorPanel = new JPanel(new BorderLayout());
   private JSeparator separator = new JSeparator();

   public ComboSeparatorRenderer(ListCellRenderer delegate) {
      this.delegate = delegate;
   }

   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      Component comp = this.delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if (index != -1 && this.addSeparatorAfter(list, value, index)) {
         this.separatorPanel.removeAll();
         this.separatorPanel.add(comp, "Center");
         this.separatorPanel.add(this.separator, "South");
         return this.separatorPanel;
      } else {
         return comp;
      }
   }

   protected boolean addSeparatorAfter(JList list, Object value, int index) {
      return value instanceof ComboSeparatorRenderer.ComboSeparatorIdentifier
         && ((ComboSeparatorRenderer.ComboSeparatorIdentifier)value).addSeparatorAfter(index);
   }

   public interface ComboSeparatorIdentifier {
      boolean addSeparatorAfter(int var1);
   }
}

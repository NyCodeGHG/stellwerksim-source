package js.java.tools.gui.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.io.Serializable;
import java.security.AccessControlException;
import java.util.Enumeration;
import java.util.LinkedList;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.UIDefaults;
import javax.swing.UIDefaults.LazyInputMap;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.IconUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.plaf.metal.MetalIconFactory;
import sun.swing.SwingUtilities2;
import sun.swing.SwingUtilities2.AATextInfo;

public class nightvisiontheme extends BasicLookAndFeel {
   private static final ColorUIResource WHITE = new ColorUIResource(Color.WHITE);
   private static final ColorUIResource BLACK = new ColorUIResource(Color.BLACK);
   private static final ColorUIResource BACKGROUND = new ColorUIResource(8947848);
   private static final ColorUIResource DARKERBACKGROUND = new ColorUIResource(BACKGROUND.darker());
   private static final ColorUIResource DARKERDARKERBACKGROUND = new ColorUIResource(BACKGROUND.darker().darker());
   private static final ColorUIResource BRIGHTERBACKGROUND = new ColorUIResource(BACKGROUND.brighter());
   private static final ColorUIResource BRIGHTERBRIGHTERBACKGROUND = new ColorUIResource(BACKGROUND.brighter().brighter());
   private static final ColorUIResource FOREGROUND = new ColorUIResource(0);
   private static final ColorUIResource ORANGE = new ColorUIResource(16758528);

   public nightvisiontheme() {
      super();
   }

   public String getName() {
      return "STS/nightvision";
   }

   protected void initComponentDefaults(UIDefaults table) {
      Object[] defaults = new Object[]{
         "control",
         BACKGROUND,
         "controlShadow",
         DARKERDARKERBACKGROUND,
         "controlHighlight",
         BRIGHTERBRIGHTERBACKGROUND,
         "controlDkShadow",
         DARKERBACKGROUND,
         "controlLtHighlight",
         BRIGHTERBACKGROUND,
         "menu",
         BACKGROUND
      };
      table.putDefaults(defaults);
      super.initComponentDefaults(table);
      float fsize = 11.0F;

      try {
         String fsizeStr = System.getProperty("nightvision.fontsize");

         try {
            fsize = Float.parseFloat(fsizeStr);
            System.out.println("Fontsize override: " + fsize);
         } catch (NumberFormatException | NullPointerException var9) {
         }
      } catch (AccessControlException var10) {
         System.out.println("Zugriffsbeschränkung, dieses optional in die Policies hinzufügen:");
         System.out.println("permission java.util.PropertyPermission \"nightvision.*\", \"read\";");
         System.out.println("Damit dann als Startoption \"-Dnightvision.fontsize=12\" möglich!");
      } catch (Exception var11) {
         var11.printStackTrace();
      }

      LinkedList changes = new LinkedList();
      Enumeration newKeys = table.keys();

      while(newKeys.hasMoreElements()) {
         Object key = newKeys.nextElement();
         Font f = table.getFont(key);
         if (f != null) {
            changes.add(key);
            changes.add(new FontUIResource(f.deriveFont(fsize)));
         }
      }

      this.setColor(table, changes, "selectionBackground", ORANGE);
      this.setColor(table, changes, "selectionForeground", BLACK);
      this.setColor(table, changes, "acceleratorForeground", BRIGHTERBRIGHTERBACKGROUND);
      this.setColor(table, changes, "acceleratorSelectionForeground", BLACK);
      this.setFor(
         table, changes, "border", new BorderUIResource(new nightvisiontheme.RollOverBorder()), "MenuItem", "CheckBoxMenuItem", "Menu", "RadioButtonMenuItem"
      );
      this.setFor(table, changes, "borderPainted", true, "MenuItem", "CheckBoxMenuItem", "Menu", "RadioButtonMenuItem");
      this.installInputMaps(table, changes);
      table.putDefaults(changes.toArray());
      Object[] overide = new Object[]{
         "ProgressBar.foreground",
         WHITE,
         "ProgressBar.foregroundHighlight",
         BLACK,
         "TextField.background",
         WHITE,
         "TextField.foreground",
         FOREGROUND,
         "ComboBox.background",
         DARKERBACKGROUND,
         "Tree.expandedIcon",
         new IconUIResource(MetalIconFactory.getTreeControlIcon(false)),
         "Tree.collapsedIcon",
         new IconUIResource(MetalIconFactory.getTreeControlIcon(true)),
         "ToolTip.background",
         new ColorUIResource(Color.YELLOW),
         "ToolTip.border",
         new BorderUIResource(new CompoundBorder(new LineBorder(Color.BLACK, 2), new EmptyBorder(5, 10, 5, 10))),
         "ToolTip.font",
         new FontUIResource("SansSerif", 1, 13),
         "ToolTip.foreground",
         BLACK,
         "CheckBox.icon",
         new nightvisiontheme.CheckBoxIcon(),
         "RadioButton.icon",
         new nightvisiontheme.RadioButtonIcon()
      };
      table.putDefaults(overide);

      try {
         table.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, AATextInfo.getAATextInfo(true));
      } catch (Exception var8) {
         var8.printStackTrace();
         System.out.println("Zugriffsbeschränkung, dieses optional in die Policies hinzufügen:");
         System.out.println("permission \"java.lang.RuntimePermission\" \"accessClassInPackage.sun.swing\";");
      }
   }

   private void showResources(UIDefaults table) {
      System.out.println("----");
      Enumeration newKeys = table.keys();

      while(newKeys.hasMoreElements()) {
         Object obj = newKeys.nextElement();
         System.out.printf("%50s : %s\n", obj, table.get(obj));
      }

      System.out.println("----");
   }

   private void setColor(UIDefaults table, LinkedList changes, String prop, ColorUIResource col) {
      Enumeration newKeys = table.keys();

      while(newKeys.hasMoreElements()) {
         Object key = newKeys.nextElement();
         if (key.toString().endsWith("." + prop)) {
            Color c = table.getColor(key);
            if (c != null) {
               changes.add(key);
               changes.add(col);
            }
         }
      }
   }

   private void setFor(UIDefaults table, LinkedList changes, String prop, Object res, String... destination) {
      Enumeration newKeys = table.keys();

      while(newKeys.hasMoreElements()) {
         Object key = newKeys.nextElement();

         for(String d : destination) {
            if (key.toString().equalsIgnoreCase(d + "." + prop)) {
               changes.add(key);
               changes.add(res);
            }
         }
      }
   }

   private void installInputMaps(UIDefaults table, LinkedList changes) {
      Object fieldInputMap = new LazyInputMap(
         new Object[]{
            "ctrl C",
            "copy-to-clipboard",
            "ctrl V",
            "paste-from-clipboard",
            "ctrl X",
            "cut-to-clipboard",
            "COPY",
            "copy-to-clipboard",
            "PASTE",
            "paste-from-clipboard",
            "CUT",
            "cut-to-clipboard",
            "control INSERT",
            "copy-to-clipboard",
            "shift INSERT",
            "paste-from-clipboard",
            "shift DELETE",
            "cut-to-clipboard",
            "shift LEFT",
            "selection-backward",
            "shift KP_LEFT",
            "selection-backward",
            "shift RIGHT",
            "selection-forward",
            "shift KP_RIGHT",
            "selection-forward",
            "ctrl LEFT",
            "caret-previous-word",
            "ctrl KP_LEFT",
            "caret-previous-word",
            "ctrl RIGHT",
            "caret-next-word",
            "ctrl KP_RIGHT",
            "caret-next-word",
            "ctrl shift LEFT",
            "selection-previous-word",
            "ctrl shift KP_LEFT",
            "selection-previous-word",
            "ctrl shift RIGHT",
            "selection-next-word",
            "ctrl shift KP_RIGHT",
            "selection-next-word",
            "ctrl A",
            "select-all",
            "HOME",
            "caret-begin-line",
            "END",
            "caret-end-line",
            "shift HOME",
            "selection-begin-line",
            "shift END",
            "selection-end-line",
            "BACK_SPACE",
            "delete-previous",
            "shift BACK_SPACE",
            "delete-previous",
            "ctrl H",
            "delete-previous",
            "DELETE",
            "delete-next",
            "ctrl DELETE",
            "delete-next-word",
            "ctrl BACK_SPACE",
            "delete-previous-word",
            "RIGHT",
            "caret-forward",
            "LEFT",
            "caret-backward",
            "KP_RIGHT",
            "caret-forward",
            "KP_LEFT",
            "caret-backward",
            "ENTER",
            "notify-field-accept",
            "ctrl BACK_SLASH",
            "unselect",
            "control shift O",
            "toggle-componentOrientation"
         }
      );
      Object passwordInputMap = new LazyInputMap(
         new Object[]{
            "ctrl C",
            "copy-to-clipboard",
            "ctrl V",
            "paste-from-clipboard",
            "ctrl X",
            "cut-to-clipboard",
            "COPY",
            "copy-to-clipboard",
            "PASTE",
            "paste-from-clipboard",
            "CUT",
            "cut-to-clipboard",
            "control INSERT",
            "copy-to-clipboard",
            "shift INSERT",
            "paste-from-clipboard",
            "shift DELETE",
            "cut-to-clipboard",
            "shift LEFT",
            "selection-backward",
            "shift KP_LEFT",
            "selection-backward",
            "shift RIGHT",
            "selection-forward",
            "shift KP_RIGHT",
            "selection-forward",
            "ctrl LEFT",
            "caret-begin-line",
            "ctrl KP_LEFT",
            "caret-begin-line",
            "ctrl RIGHT",
            "caret-end-line",
            "ctrl KP_RIGHT",
            "caret-end-line",
            "ctrl shift LEFT",
            "selection-begin-line",
            "ctrl shift KP_LEFT",
            "selection-begin-line",
            "ctrl shift RIGHT",
            "selection-end-line",
            "ctrl shift KP_RIGHT",
            "selection-end-line",
            "ctrl A",
            "select-all",
            "HOME",
            "caret-begin-line",
            "END",
            "caret-end-line",
            "shift HOME",
            "selection-begin-line",
            "shift END",
            "selection-end-line",
            "BACK_SPACE",
            "delete-previous",
            "shift BACK_SPACE",
            "delete-previous",
            "ctrl H",
            "delete-previous",
            "DELETE",
            "delete-next",
            "RIGHT",
            "caret-forward",
            "LEFT",
            "caret-backward",
            "KP_RIGHT",
            "caret-forward",
            "KP_LEFT",
            "caret-backward",
            "ENTER",
            "notify-field-accept",
            "ctrl BACK_SLASH",
            "unselect",
            "control shift O",
            "toggle-componentOrientation"
         }
      );
      Object multilineInputMap = new LazyInputMap(
         new Object[]{
            "ctrl C",
            "copy-to-clipboard",
            "ctrl V",
            "paste-from-clipboard",
            "ctrl X",
            "cut-to-clipboard",
            "COPY",
            "copy-to-clipboard",
            "PASTE",
            "paste-from-clipboard",
            "CUT",
            "cut-to-clipboard",
            "control INSERT",
            "copy-to-clipboard",
            "shift INSERT",
            "paste-from-clipboard",
            "shift DELETE",
            "cut-to-clipboard",
            "shift LEFT",
            "selection-backward",
            "shift KP_LEFT",
            "selection-backward",
            "shift RIGHT",
            "selection-forward",
            "shift KP_RIGHT",
            "selection-forward",
            "ctrl LEFT",
            "caret-previous-word",
            "ctrl KP_LEFT",
            "caret-previous-word",
            "ctrl RIGHT",
            "caret-next-word",
            "ctrl KP_RIGHT",
            "caret-next-word",
            "ctrl shift LEFT",
            "selection-previous-word",
            "ctrl shift KP_LEFT",
            "selection-previous-word",
            "ctrl shift RIGHT",
            "selection-next-word",
            "ctrl shift KP_RIGHT",
            "selection-next-word",
            "ctrl A",
            "select-all",
            "HOME",
            "caret-begin-line",
            "END",
            "caret-end-line",
            "shift HOME",
            "selection-begin-line",
            "shift END",
            "selection-end-line",
            "UP",
            "caret-up",
            "KP_UP",
            "caret-up",
            "DOWN",
            "caret-down",
            "KP_DOWN",
            "caret-down",
            "PAGE_UP",
            "page-up",
            "PAGE_DOWN",
            "page-down",
            "shift PAGE_UP",
            "selection-page-up",
            "shift PAGE_DOWN",
            "selection-page-down",
            "ctrl shift PAGE_UP",
            "selection-page-left",
            "ctrl shift PAGE_DOWN",
            "selection-page-right",
            "shift UP",
            "selection-up",
            "shift KP_UP",
            "selection-up",
            "shift DOWN",
            "selection-down",
            "shift KP_DOWN",
            "selection-down",
            "ENTER",
            "insert-break",
            "BACK_SPACE",
            "delete-previous",
            "shift BACK_SPACE",
            "delete-previous",
            "ctrl H",
            "delete-previous",
            "DELETE",
            "delete-next",
            "ctrl DELETE",
            "delete-next-word",
            "ctrl BACK_SPACE",
            "delete-previous-word",
            "RIGHT",
            "caret-forward",
            "LEFT",
            "caret-backward",
            "KP_RIGHT",
            "caret-forward",
            "KP_LEFT",
            "caret-backward",
            "TAB",
            "insert-tab",
            "ctrl BACK_SLASH",
            "unselect",
            "ctrl HOME",
            "caret-begin",
            "ctrl END",
            "caret-end",
            "ctrl shift HOME",
            "selection-begin",
            "ctrl shift END",
            "selection-end",
            "ctrl T",
            "next-link-action",
            "ctrl shift T",
            "previous-link-action",
            "ctrl SPACE",
            "activate-link-action",
            "control shift O",
            "toggle-componentOrientation"
         }
      );
      this.addValue(changes, "TextField.focusInputMap", fieldInputMap);
      this.addValue(changes, "TextArea.focusInputMap", multilineInputMap);
      this.addValue(changes, "TextPane.focusInputMap", multilineInputMap);
      this.addValue(changes, "EditorPane.focusInputMap", multilineInputMap);
      this.addValue(changes, "PasswordField.focusInputMap", passwordInputMap);
   }

   private void addValue(LinkedList changes, String key, Object value) {
      changes.add(key);
      changes.add(value);
   }

   public String getID() {
      return "nightvision";
   }

   public String getDescription() {
      return "STS Nachtansicht";
   }

   public boolean isNativeLookAndFeel() {
      return false;
   }

   public boolean isSupportedLookAndFeel() {
      return true;
   }

   private static class CheckBoxIcon implements Icon, UIResource, Serializable {
      private CheckBoxIcon() {
         super();
      }

      protected int getControlSize() {
         return 13;
      }

      public void paintIcon(Component c, Graphics g, int x, int y) {
         ButtonModel model = ((JCheckBox)c).getModel();
         int controlSize = this.getControlSize();
         if (model.isEnabled()) {
            g.setColor(c.getForeground());
            g.drawRect(x, y, controlSize - 2, controlSize - 2);
         }

         if (model.isSelected()) {
            this.drawCheck(c, g, x, y);
         }
      }

      protected void drawCheck(Component c, Graphics g, int x, int y) {
         int controlSize = this.getControlSize();
         g.fillRect(x + 3, y + 5, 2, controlSize - 8);
         g.drawLine(x + (controlSize - 4), y + 3, x + 5, y + (controlSize - 6));
         g.drawLine(x + (controlSize - 4), y + 4, x + 5, y + (controlSize - 5));
      }

      public int getIconWidth() {
         return this.getControlSize();
      }

      public int getIconHeight() {
         return this.getControlSize();
      }
   }

   private static class RadioButtonIcon implements Icon, UIResource, Serializable {
      private RadioButtonIcon() {
         super();
      }

      protected int getControlSize() {
         return 13;
      }

      public void paintIcon(Component c, Graphics g, int x, int y) {
         ButtonModel model = ((JRadioButton)c).getModel();
         int controlSize = this.getControlSize();
         if (model.isEnabled()) {
            g.setColor(c.getForeground());
            g.drawArc(x + 1, y + 1, controlSize - 2, controlSize - 2, 0, 360);
            if (model.isSelected() || model.isArmed()) {
               this.drawCheck(c, g, x, y);
            }
         }
      }

      protected void drawCheck(Component c, Graphics g, int x, int y) {
         int controlSize = this.getControlSize();
         g.fillArc(x + 3, y + 3, controlSize - 6, controlSize - 6, 0, 360);
      }

      public int getIconWidth() {
         return this.getControlSize();
      }

      public int getIconHeight() {
         return this.getControlSize();
      }
   }

   private static class RollOverBorder extends LineBorder {
      RollOverBorder() {
         super(nightvisiontheme.ORANGE.brighter(), 1);
      }

      public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
         if (c instanceof JMenuItem) {
            JMenuItem mi = (JMenuItem)c;
            ButtonModel bm = mi.getModel();
            if (bm.isArmed() || bm.isSelected() && mi instanceof JMenu && !((JMenu)mi).isTopLevelMenu()) {
               super.paintBorder(c, g, x, y, width, height);
            }
         }
      }
   }
}

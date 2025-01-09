package com.ezware.dialog.task;

import com.ezware.common.EmptyIcon;
import com.ezware.common.Strings;
import com.ezware.common.SwingBean;
import com.ezware.dialog.task.design.TaskDialogContent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class TaskDialog extends SwingBean {
   private static final String INSTANCE_PROPERTY = "task.dialog.instance";
   private static final String DEBUG_PROPERTY = "task.dialog.debug";
   static final String I18N_PREFIX = "@@";
   private static final String LOCALE_BUNDLE_NAME = "task-dialog";
   private static IContentDesign design = ContentDesignFactory.getDesignByOperatingSystem();
   private static final KeyStroke KS_ESCAPE = KeyStroke.getKeyStroke(27, 0);
   private static final KeyStroke KS_ENTER = KeyStroke.getKeyStroke(10, 0);
   private static final KeyStroke KS_F1 = KeyStroke.getKeyStroke(112, 0);
   private Locale resourceBundleLocale = null;
   private ResourceBundle resourceBundle = null;
   private TaskDialog.Command result = TaskDialog.StandardCommand.CANCEL;
   private final JDialog dlg;
   private final TaskDialogContent content;
   private Set<TaskDialog.Command> commands = new LinkedHashSet(Arrays.asList(TaskDialog.StandardCommand.OK));
   private final List<TaskDialog.ValidationListener> validationListeners = new ArrayList();

   static final IContentDesign getDesign() {
      return design;
   }

   public static final String makeKey(String text) {
      return "@@" + text;
   }

   public static final TaskDialog getInstance(Component source) {
      Window w = SwingUtilities.getWindowAncestor(source);
      if (w instanceof JDialog) {
         JComponent c = (JComponent)((JDialog)w).getContentPane();
         return (TaskDialog)c.getClientProperty("task.dialog.instance");
      } else {
         return null;
      }
   }

   public static final void setDebugMode(boolean debug) {
      if (debug) {
         System.setProperty("task.dialog.debug", "true");
      } else {
         System.clearProperty("task.dialog.debug");
      }
   }

   public static final boolean isDebugMode() {
      return System.getProperty("task.dialog.debug") != null;
   }

   public TaskDialog(Component parent, String title) {
      this(parent != null ? SwingUtilities.getWindowAncestor(parent) : (Window)null, title);
   }

   public TaskDialog(Window parent, String title) {
      Window pWnd = parent;
      if (parent == null) {
         pWnd = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();

         while (pWnd != null && !pWnd.isVisible() && pWnd.getParent() != null) {
            pWnd = (Window)pWnd.getParent();
         }
      }

      this.dlg = new JDialog(pWnd);
      this.dlg.setMinimumSize(new Dimension(300, 150));
      this.setResizable(false);
      this.setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
      this.dlg.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            TaskDialog.this.result = TaskDialog.StandardCommand.CANCEL;
         }
      });
      this.setTitle(title);
      this.content = design.buildContent();
      this.dlg.setContentPane(this.content);
      JComponent c = (JComponent)this.dlg.getContentPane();
      c.putClientProperty("task.dialog.instance", this);
   }

   public ModalityType getModalityType() {
      return this.dlg.getModalityType();
   }

   public void setModalityType(ModalityType modalityType) {
      this.dlg.setModalityType(modalityType);
   }

   public boolean isResizable() {
      return this.dlg.isResizable();
   }

   public void setResizable(boolean resizable) {
      this.dlg.setResizable(resizable);
   }

   public final void addValidationListener(TaskDialog.ValidationListener listener) {
      if (listener != null) {
         this.validationListeners.add(listener);
      }
   }

   public final void removeValidationListener(TaskDialog.ValidationListener listener) {
      if (listener != null) {
         this.validationListeners.remove(listener);
      }
   }

   public final void fireValidationFinished(boolean validationResult) {
      ListIterator<TaskDialog.ValidationListener> iter = this.validationListeners.listIterator();

      while (iter.hasPrevious()) {
         ((TaskDialog.ValidationListener)iter.previous()).validationFinished(validationResult);
      }
   }

   public Locale getLocale() {
      return this.dlg.getLocale();
   }

   public void setLocale(Locale locale) {
      this.dlg.setLocale(locale);
   }

   private synchronized ResourceBundle getLocaleBundle() {
      Locale currentLocale = this.getLocale();
      if (!currentLocale.equals(this.resourceBundleLocale)) {
         this.resourceBundleLocale = currentLocale;
         this.resourceBundle = ResourceBundle.getBundle("task-dialog", this.resourceBundleLocale, this.getClass().getClassLoader());
      }

      return this.resourceBundle;
   }

   public String getLocalizedString(String key) {
      try {
         return this.getLocaleBundle().getString(key);
      } catch (MissingResourceException var3) {
         return String.format("<%s>", key);
      }
   }

   public String getString(String text) {
      return text.startsWith("@@") ? this.getLocalizedString(text.substring("@@".length())) : text;
   }

   public void setVisible(boolean visible) {
      if (visible) {
         this.content.setCommands(this.commands, getDesign().isCommandButtonSizeLocked());
      }

      if (this.firePropertyChange("visible", Boolean.valueOf(this.isVisible()), Boolean.valueOf(visible))) {
         if (visible) {
            this.dlg.pack();
            this.dlg.setLocationRelativeTo(KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow());
            this.dlg.setVisible(true);
         } else {
            this.dlg.dispose();
         }
      }
   }

   public boolean isVisible() {
      return this.dlg.isVisible();
   }

   public TaskDialog.Command getResult() {
      return this.result;
   }

   public void setResult(TaskDialog.Command result) {
      this.result = result;
      this.firePropertyChange("result", null, result);
   }

   public TaskDialog.Command show() {
      this.setVisible(true);
      return this.getResult();
   }

   public String toString() {
      return this.getTitle();
   }

   public String getTitle() {
      return this.dlg.getTitle();
   }

   public void setTitle(String title) {
      this.dlg.setTitle(this.getString(title));
   }

   public void setIcon(Icon icon) {
      if (this.firePropertyChange("icon", this.getIcon(), icon)) {
         this.content.setMainIcon(icon);
      }
   }

   public Icon getIcon() {
      return this.content.getMainIcon();
   }

   public void setInstruction(String instruction) {
      if (this.firePropertyChange("instruction", this.getInstruction(), instruction)) {
         this.content.setInstruction(instruction);
      }
   }

   public String getInstruction() {
      return this.content.getInstruction();
   }

   public void setText(String text) {
      if (this.firePropertyChange("text", this.getText(), text)) {
         this.content.setMainText(text);
      }
   }

   public String getText() {
      return this.content.getMainText();
   }

   public void setFixedComponent(JComponent c) {
      this.content.setComponent(c);
   }

   public JComponent getFixedComponent() {
      return this.content.getComponent();
   }

   public TaskDialog.Details getDetails() {
      return this.content;
   }

   public TaskDialog.Footer getFooter() {
      return this.content;
   }

   public void setCommands(Collection<TaskDialog.Command> commands) {
      this.commands = new LinkedHashSet(commands);
   }

   public void setCommands(TaskDialog.Command... commands) {
      this.setCommands(Arrays.asList(commands));
   }

   public Collection<TaskDialog.Command> getCommands() {
      return this.commands;
   }

   public boolean isCommandsVisible() {
      return this.content.isCommandsVisible();
   }

   public void setCommandsVisible(boolean visible) {
      this.content.setCommandsVisible(visible);
   }

   static {
      getDesign().updateUIDefaults();
   }

   public interface Command {
      String getTitle();

      TaskDialog.CommandTag getTag();

      String getDescription();

      boolean isClosing();

      int getWaitInterval();

      boolean isEnabled(boolean var1);

      KeyStroke getKeyStroke();
   }

   public static enum CommandTag {
      OK("ok", TaskDialog.KS_ENTER, true, true),
      CANCEL("cancel", TaskDialog.KS_ESCAPE, true),
      HELP("help", TaskDialog.KS_F1, false),
      HELP2("help2", "Help", TaskDialog.KS_F1, false),
      YES("yes", TaskDialog.KS_ENTER, true, true),
      NO("yes", TaskDialog.KS_ESCAPE, true),
      APPLY("apply"),
      NEXT("next", true, false),
      BACK("back"),
      FINISH("finish", TaskDialog.KS_ENTER, true, true),
      LEFT("left"),
      RIGHT("right");

      private String tag;
      private final String defaultTitle;
      private boolean useValidationResult = false;
      private boolean closing;
      private KeyStroke defaultKeyStroke;

      private CommandTag(String tag, String defaultTitle, KeyStroke defaultKeyStroke, boolean useValidationResult, boolean closing) {
         this.tag = "tag " + tag;
         this.defaultTitle = TaskDialog.makeKey(Strings.isEmpty(defaultTitle) ? Strings.capitalize(tag) : defaultTitle);
         this.useValidationResult = useValidationResult;
         this.closing = closing;
         this.defaultKeyStroke = defaultKeyStroke;
      }

      private CommandTag(String tag, String defaultTitle, KeyStroke defaultKeyStroke, boolean closing) {
         this(tag, defaultTitle, defaultKeyStroke, false, closing);
      }

      private CommandTag(String tag, KeyStroke defaultKeyStroke, boolean closing) {
         this(tag, null, defaultKeyStroke, false, closing);
      }

      private CommandTag(String tag, boolean closing) {
         this(tag, null, closing);
      }

      private CommandTag(String tag) {
         this(tag, false);
      }

      private CommandTag(String tag, KeyStroke defaultKeyStroke, boolean useValidationResult, boolean closing) {
         this(tag, null, defaultKeyStroke, useValidationResult, closing);
      }

      private CommandTag(String tag, boolean useValidationResult, boolean closing) {
         this(tag, null, useValidationResult, closing);
      }

      public String getDefaultTitle() {
         return this.defaultTitle;
      }

      public String toString() {
         return this.tag;
      }

      public boolean isEnabled(boolean validationResult) {
         return this.useValidationResult ? validationResult : true;
      }

      public boolean isClosing() {
         return this.closing;
      }

      public KeyStroke getDefaultKeyStroke() {
         return this.defaultKeyStroke;
      }
   }

   public abstract static class CustomCommand implements TaskDialog.Command {
      private final TaskDialog.StandardCommand command;

      public CustomCommand(TaskDialog.StandardCommand command) {
         if (command == null) {
            throw new IllegalArgumentException("Command should not be null");
         } else {
            this.command = command;
         }
      }

      @Override
      public String getDescription() {
         return this.command.getDescription();
      }

      @Override
      public TaskDialog.CommandTag getTag() {
         return this.command.getTag();
      }

      @Override
      public String getTitle() {
         return this.command.getTitle();
      }

      @Override
      public boolean isClosing() {
         return this.command.isClosing();
      }

      @Override
      public int getWaitInterval() {
         return this.command.getWaitInterval();
      }

      @Override
      public boolean isEnabled(boolean validationResult) {
         return this.command.isEnabled(validationResult);
      }

      public boolean equals(Object obj) {
         return this.command.equals(obj);
      }

      public int hashCode() {
         return this.command.hashCode();
      }

      @Override
      public KeyStroke getKeyStroke() {
         return this.command.getKeyStroke();
      }
   }

   public interface Details {
      String getCollapsedLabel();

      void setCollapsedLabel(String var1);

      String getExpandedLabel();

      void setExpandedLabel(String var1);

      boolean isExpanded();

      void setExpanded(boolean var1);

      void setAlwaysExpanded(boolean var1);

      boolean isAlwaysExpanded();

      JComponent getExpandableComponent();

      void setExpandableComponent(JComponent var1);
   }

   public interface Footer {
      boolean isCheckBoxSelected();

      void setCheckBoxSelected(boolean var1);

      String getCheckBoxText();

      void setCheckBoxText(String var1);

      Icon getIcon();

      void setIcon(Icon var1);

      String getText();

      void setText(String var1);
   }

   public static enum StandardCommand implements TaskDialog.Command {
      OK(TaskDialog.CommandTag.OK),
      CANCEL(TaskDialog.CommandTag.CANCEL);

      private final TaskDialog.CommandTag tag;

      private StandardCommand(TaskDialog.CommandTag tag) {
         this.tag = tag;
      }

      @Override
      public String getDescription() {
         return null;
      }

      @Override
      public TaskDialog.CommandTag getTag() {
         return this.tag;
      }

      @Override
      public String getTitle() {
         return this.tag.getDefaultTitle();
      }

      @Override
      public boolean isClosing() {
         return this.tag.isClosing();
      }

      @Override
      public int getWaitInterval() {
         return 0;
      }

      @Override
      public boolean isEnabled(boolean validationResult) {
         return this.tag.isEnabled(validationResult);
      }

      @Override
      public KeyStroke getKeyStroke() {
         return this.tag.getDefaultKeyStroke();
      }

      public TaskDialog.Command derive(final String title, final int waitInterval) {
         return new TaskDialog.CustomCommand(this) {
            @Override
            public String getTitle() {
               return title;
            }

            @Override
            public int getWaitInterval() {
               return waitInterval;
            }
         };
      }

      public TaskDialog.Command derive(final String title) {
         return new TaskDialog.CustomCommand(this) {
            @Override
            public String getTitle() {
               return title;
            }
         };
      }
   }

   public static enum StandardIcon implements Icon {
      INFO("OptionPane.informationIcon"),
      QUESTION("OptionPane.questionIcon"),
      WARNING("OptionPane.warningIcon"),
      ERROR("OptionPane.errorIcon");

      private final String key;
      private Icon emptyIcon;

      private StandardIcon(String key) {
         this.key = key;
      }

      public int getIconHeight() {
         return this.getIcon().getIconHeight();
      }

      public int getIconWidth() {
         return this.getIcon().getIconWidth();
      }

      public void paintIcon(Component c, Graphics g, int x, int y) {
         this.getIcon().paintIcon(c, g, x, y);
      }

      private synchronized Icon getIcon() {
         Icon icon = UIManager.getIcon(this.key);
         return icon == null ? this.getEmptyIcon() : icon;
      }

      private synchronized Icon getEmptyIcon() {
         if (this.emptyIcon == null) {
            this.emptyIcon = EmptyIcon.hidden();
         }

         return this.emptyIcon;
      }
   }

   public interface ValidationListener {
      void validationFinished(boolean var1);
   }
}

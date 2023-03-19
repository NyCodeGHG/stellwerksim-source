package com.ezware.dialog.task;

import com.ezware.common.AncestorAdapter;
import com.ezware.common.Strings;
import com.ezware.dialog.task.design.CommandLinkButton;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.event.AncestorEvent;
import net.miginfocom.swing.MigLayout;

public final class TaskDialogs {
   private TaskDialogs() {
      super();
   }

   public static TaskDialogs.TaskDialogBuilder build() {
      return new TaskDialogs.TaskDialogBuilder();
   }

   public static TaskDialogs.TaskDialogBuilder build(Window parent, String instruction, String text) {
      TaskDialogs.TaskDialogBuilder builder = new TaskDialogs.TaskDialogBuilder();
      builder.parent(parent);
      builder.instruction(instruction);
      builder.text(text);
      return builder;
   }

   public static void inform(Window parent, String instruction, String text) {
      build(parent, instruction, text).inform();
   }

   public static void error(Window parent, String instruction, String text) {
      build(parent, instruction, text).error();
   }

   public static boolean ask(Window parent, String instruction, String text) {
      return build(parent, instruction, text).ask();
   }

   @Deprecated
   public static boolean warn(Window parent, String instruction, String text) {
      return build(parent, instruction, text).warn();
   }

   public static boolean isConfirmed(Window parent, String instruction, String text) {
      return build(parent, instruction, text).isConfirmed();
   }

   public static void showException(Throwable ex) {
      build().showException(ex);
   }

   public static final int radioChoice(Window parent, String instruction, String text, int defaultChoice, List<String> choices) {
      return build(parent, instruction, text).radioChoice(defaultChoice, choices);
   }

   public static final int radioChoice(Window parent, String instruction, String text, int defaultChoice, String... choices) {
      return build(parent, instruction, text).radioChoice(defaultChoice, choices);
   }

   public static final int choice(Window parent, String instruction, String text, int defaultChoice, List<CommandLink> choices) {
      return build(parent, instruction, text).choice(defaultChoice, choices);
   }

   public static final int choice(Window parent, String instruction, String text, int defaultChoice, CommandLink... choices) {
      return build(parent, instruction, text).choice(defaultChoice, choices);
   }

   public static final <T> T input(Window parent, String instruction, String text, T defaultValue) {
      return build(parent, instruction, text).inputColumns(25).input(defaultValue);
   }

   private static TaskDialog messageDialog(Window parent, String title, Icon icon, String instruction, String text) {
      TaskDialogs.TextWithWaitInterval twi = new TaskDialogs.TextWithWaitInterval(instruction);
      TaskDialog dlg = new TaskDialog(parent, title);
      dlg.setInstruction(twi.getText());
      dlg.setText(text);
      dlg.setIcon(icon);
      dlg.setCommands(TaskDialog.StandardCommand.CANCEL.derive(TaskDialog.makeKey("Close"), twi.getWaitInterval()));
      return dlg;
   }

   private static TaskDialog questionDialog(Window parent, String title, Icon icon, String instruction, String text) {
      TaskDialogs.TextWithWaitInterval twi = new TaskDialogs.TextWithWaitInterval(instruction);
      TaskDialog dlg = new TaskDialog(parent, title);
      dlg.setInstruction(twi.getText());
      dlg.setText(text);
      dlg.setIcon(icon);
      dlg.setCommands(
         TaskDialog.StandardCommand.OK.derive(TaskDialog.makeKey("Yes"), twi.getWaitInterval()),
         TaskDialog.StandardCommand.CANCEL.derive(TaskDialog.makeKey("No"))
      );
      return dlg;
   }

   public static final class TaskDialogBuilder {
      private Window parent = null;
      private String title = null;
      private Icon icon = null;
      private String instruction = null;
      private String text = null;
      private Integer inputColumns = null;

      private TaskDialogBuilder() {
         super();
      }

      public TaskDialogs.TaskDialogBuilder parent(Window parent) {
         this.parent = parent;
         return this;
      }

      public TaskDialogs.TaskDialogBuilder title(String title) {
         this.title = title;
         return this;
      }

      public TaskDialogs.TaskDialogBuilder icon(Icon icon) {
         this.icon = icon;
         return this;
      }

      public TaskDialogs.TaskDialogBuilder instruction(String instruction) {
         this.instruction = instruction;
         return this;
      }

      public TaskDialogs.TaskDialogBuilder text(String text) {
         this.text = text;
         return this;
      }

      public TaskDialogs.TaskDialogBuilder inputColumns(int columns) {
         this.inputColumns = columns;
         return this;
      }

      private String getTitle(String defaultTitle) {
         return this.title == null ? defaultTitle : this.title;
      }

      private Icon getIcon(Icon defaultIcon) {
         return this.icon == null ? defaultIcon : this.icon;
      }

      public void message() {
         TaskDialogs.messageDialog(this.parent, this.title, this.icon, this.instruction, this.text).setVisible(true);
      }

      public void inform() {
         TaskDialogs.messageDialog(
               this.parent, this.getTitle(TaskDialog.makeKey("Information")), this.getIcon(TaskDialog.StandardIcon.INFO), this.instruction, this.text
            )
            .setVisible(true);
      }

      public void error() {
         TaskDialogs.messageDialog(
               this.parent, this.getTitle(TaskDialog.makeKey("Error")), this.getIcon(TaskDialog.StandardIcon.ERROR), this.instruction, this.text
            )
            .setVisible(true);
      }

      public boolean ask() {
         return TaskDialogs.questionDialog(
               this.parent, this.getTitle(TaskDialog.makeKey("Question")), this.getIcon(TaskDialog.StandardIcon.QUESTION), this.instruction, this.text
            )
            .show()
            .equals(TaskDialog.StandardCommand.OK);
      }

      @Deprecated
      public boolean warn() {
         return TaskDialogs.questionDialog(
               this.parent, this.getTitle(TaskDialog.makeKey("Warning")), this.getIcon(TaskDialog.StandardIcon.WARNING), this.instruction, this.text
            )
            .show()
            .equals(TaskDialog.StandardCommand.OK);
      }

      public boolean isConfirmed() {
         return TaskDialogs.questionDialog(
               this.parent, this.getTitle(TaskDialog.makeKey("Warning")), this.getIcon(TaskDialog.StandardIcon.WARNING), this.instruction, this.text
            )
            .show()
            .equals(TaskDialog.StandardCommand.OK);
      }

      public void showException(Throwable ex) {
         TaskDialog dlg = new TaskDialog(this.parent, this.getTitle(TaskDialog.makeKey("Exception")));
         String msg = ex.getMessage();
         String className = ex.getClass().getName();
         boolean noMessage = Strings.isEmpty(msg);
         dlg.setInstruction(noMessage ? className : msg);
         dlg.setText(noMessage ? "" : className);
         dlg.setIcon(this.getIcon(TaskDialog.StandardIcon.ERROR));
         dlg.setCommands(TaskDialog.StandardCommand.CANCEL.derive(TaskDialog.makeKey("Close")));
         JTextArea text = new JTextArea();
         text.setEditable(false);
         text.setFont(UIManager.getFont("Label.font"));
         text.setText(Strings.stackStraceAsString(ex));
         text.setCaretPosition(0);
         JScrollPane scroller = new JScrollPane(text);
         scroller.setPreferredSize(new Dimension(400, 200));
         dlg.getDetails().setExpandableComponent(scroller);
         dlg.getDetails().setExpanded(noMessage);
         dlg.setResizable(true);
         dlg.setVisible(true);
      }

      public int radioChoice(int defaultChoice, List<String> choices) {
         TaskDialog dlg = TaskDialogs.questionDialog(this.parent, this.getTitle(TaskDialog.makeKey("Choice")), null, this.instruction, this.text);
         ButtonGroup bGroup = new ButtonGroup();
         List<ButtonModel> models = new ArrayList();
         JPanel p = new JPanel(new MigLayout(""));

         for(String c : choices) {
            JRadioButton btn = new JRadioButton(c);
            btn.setOpaque(false);
            models.add(btn.getModel());
            bGroup.add(btn);
            p.add(btn, "dock north");
         }

         if (defaultChoice >= 0 && defaultChoice < choices.size()) {
            bGroup.setSelected((ButtonModel)models.get(defaultChoice), true);
         }

         p.setOpaque(false);
         dlg.setIcon(this.getIcon(TaskDialog.StandardIcon.QUESTION));
         dlg.setFixedComponent(p);
         TaskDialogs.TextWithWaitInterval twi = new TaskDialogs.TextWithWaitInterval(this.instruction);
         dlg.setCommands(TaskDialog.StandardCommand.OK.derive(TaskDialog.makeKey("Select"), twi.getWaitInterval()), TaskDialog.StandardCommand.CANCEL);
         return dlg.show().equals(TaskDialog.StandardCommand.OK) ? models.indexOf(bGroup.getSelection()) : -1;
      }

      public int radioChoice(int defaultChoice, String... choices) {
         return this.radioChoice(defaultChoice, Arrays.asList(choices));
      }

      public int choice(final int defaultChoice, List<CommandLink> choices) {
         TaskDialog dlg = TaskDialogs.questionDialog(this.parent, this.getTitle(TaskDialog.makeKey("Choice")), null, this.instruction, this.text);
         final ButtonGroup bGroup = new ButtonGroup();
         List<ButtonModel> models = new ArrayList();
         final List<CommandLinkButton> buttons = new ArrayList();
         JPanel p = new JPanel(new MigLayout(""));

         for(CommandLink link : choices) {
            CommandLinkButton btn = new CommandLinkButton(link, TaskDialog.getDesign().getCommandLinkPainter());
            models.add(btn.getModel());
            buttons.add(btn);
            bGroup.add(btn);
            p.add(btn, "dock north");
            btn.addFocusListener(new FocusAdapter() {
               public void focusGained(FocusEvent e) {
                  bGroup.setSelected(((CommandLinkButton)e.getSource()).getModel(), true);
               }
            });
         }

         if (defaultChoice >= 0 && defaultChoice < choices.size()) {
            bGroup.setSelected((ButtonModel)models.get(defaultChoice), true);
            p.addAncestorListener(new AncestorAdapter() {
               @Override
               public void ancestorAdded(AncestorEvent event) {
                  ((CommandLinkButton)buttons.get(defaultChoice)).requestFocusInWindow();
               }
            });
         }

         p.setOpaque(false);
         dlg.setIcon(this.getIcon(TaskDialog.StandardIcon.QUESTION));
         dlg.setFixedComponent(p);
         dlg.setCommandsVisible(false);
         return dlg.show().equals(TaskDialog.StandardCommand.CANCEL) ? -1 : models.indexOf(bGroup.getSelection());
      }

      public int choice(int defaultChoice, CommandLink... choices) {
         return this.choice(defaultChoice, Arrays.asList(choices));
      }

      public <T> T input(T defaultValue) {
         TaskDialog dlg = TaskDialogs.questionDialog(this.parent, this.getTitle(TaskDialog.makeKey("Input")), null, this.instruction, this.text);
         dlg.setIcon(this.getIcon(TaskDialog.StandardIcon.INFO));
         JFormattedTextField tfInput = new JFormattedTextField();
         tfInput.setColumns(this.inputColumns == null ? 25 : this.inputColumns);
         tfInput.setValue(defaultValue);
         dlg.setFixedComponent(tfInput);
         dlg.setCommands(TaskDialog.StandardCommand.OK, TaskDialog.StandardCommand.CANCEL);
         return (T)(dlg.show().equals(TaskDialog.StandardCommand.OK) ? tfInput.getValue() : null);
      }
   }

   private static class TextWithWaitInterval {
      String text;
      int waitInterval = 0;

      TextWithWaitInterval(String text) {
         super();
         this.text = text;
         int prefixPos = text.indexOf("@@");
         if (prefixPos >= 0) {
            try {
               this.waitInterval = Integer.valueOf(text.substring(prefixPos + "@@".length()));
            } catch (Throwable var4) {
               this.waitInterval = 0;
            }

            this.text = text.substring(0, prefixPos);
         }
      }

      public String getText() {
         return this.text;
      }

      public int getWaitInterval() {
         return this.waitInterval;
      }
   }
}

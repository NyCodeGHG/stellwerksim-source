package com.ezware.dialog.task.design;

import com.ezware.common.Icons;
import com.ezware.common.Markup;
import com.ezware.common.Strings;
import com.ezware.dialog.task.TaskDialog;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;
import java.util.UUID;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import net.miginfocom.swing.MigLayout;

public class TaskDialogContent extends JPanel implements TaskDialog.Details, TaskDialog.Footer {
   private static final long serialVersionUID = 1L;
   final JLabel lbIcon = hidden(new JLabel());
   final JLabel lbInstruction = hidden(new JLabel());
   final JLabel lbText = hidden(new JLabel());
   final JPanel pExpandable = hidden(new JPanel(new BorderLayout()));
   final JPanel pComponent = hidden(new JPanel(new BorderLayout()));
   final DetailsToggleButton cbDetails = hidden(new DetailsToggleButton());
   final JCheckBox cbFooterCheck = hidden(new JCheckBox());
   final JLabel lbFooter = hidden(new JLabel());
   final JPanel pCommands = new JPanel(new MigLayout("ins 0, nogrid, fillx, aligny 100%, gapy unrel"));
   final JPanel pFooter = hidden(new JPanel(new MigLayout()));
   final JPanel pCommandPane = new JPanel(new MigLayout());
   private final String[] detailsText = new String[2];
   private String instruction = null;
   private String text;
   private boolean alwaysExpanded;
   private Icon icon;

   private static <T extends JComponent> T hidden(T c) {
      c.setVisible(false);
      return c;
   }

   public TaskDialogContent() {
      this.pExpandable.setOpaque(false);
      this.pComponent.setOpaque(false);
      this.cbDetails.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            final boolean selected = e.getStateChange() == 1;
            TaskDialogContent.this.cbDetails.setText(selected ? TaskDialogContent.this.getExpandedLabel() : TaskDialogContent.this.getCollapsedLabel());
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  TaskDialogContent.this.pExpandable.setVisible(selected);
                  SwingUtilities.getWindowAncestor(TaskDialogContent.this).pack();
               }
            });
         }
      });
   }

   public void setInstruction(String instruction) {
      this.instruction = instruction;
      boolean visible = instruction != null && instruction.trim().length() > 0;
      this.lbInstruction.setVisible(visible);
      if (visible) {
         this.lbInstruction.setText(Markup.toHTML(instruction));
      }
   }

   public String getInstruction() {
      return this.instruction;
   }

   public void setCommands(Set<? extends TaskDialog.Command> commands, boolean lockButtonSize) {
      this.pCommands.removeAll();
      String group = lockButtonSize ? "sgx commands, " : "";
      TaskDialog owner = this.getOwner();

      for (TaskDialog.Command c : commands) {
         String tag = c.getTag() == null ? "" : c.getTag().toString();
         this.pCommands.add(new JButton(new TaskDialogContent.CommandAction(c, owner)), group + "aligny top, " + tag);
      }
   }

   public boolean isCommandsVisible() {
      return this.pCommandPane.isVisible();
   }

   public void setCommandsVisible(boolean visible) {
      this.pCommandPane.setVisible(visible);
   }

   public void setMainText(String text) {
      this.text = text;
      boolean isEmtpy = Strings.isEmpty(text);
      this.lbText.setText(Markup.toHTML(text));
      this.lbText.setVisible(!isEmtpy);
   }

   public String getMainText() {
      return this.text;
   }

   private TaskDialog getOwner() {
      return TaskDialog.getInstance(this);
   }

   @Override
   public String getCollapsedLabel() {
      return Strings.isEmpty(this.detailsText[0]) ? this.getOwner().getString(UIManager.getString("TaskDialog.moreDetailsText")) : this.detailsText[0];
   }

   @Override
   public void setCollapsedLabel(String collapsedLabel) {
      this.detailsText[0] = collapsedLabel;
   }

   @Override
   public String getExpandedLabel() {
      return Strings.isEmpty(this.detailsText[1]) ? this.getOwner().getString(UIManager.getString("TaskDialog.fewerDetailsText")) : this.detailsText[1];
   }

   @Override
   public void setExpandedLabel(String expandedLabel) {
      this.detailsText[1] = expandedLabel;
   }

   @Override
   public JComponent getExpandableComponent() {
      return this.pExpandable.getComponentCount() == 0 ? null : (JComponent)this.pExpandable.getComponent(0);
   }

   @Override
   public void setExpandableComponent(JComponent c) {
      this.pExpandable.removeAll();
      if (c != null) {
         this.pExpandable.add(c);
      }

      this.cbDetails.setVisible(c != null && !this.alwaysExpanded);
   }

   @Override
   public boolean isExpanded() {
      return this.cbDetails.isSelected();
   }

   @Override
   public void setExpanded(boolean expanded) {
      this.cbDetails.setSelected(!expanded);
      this.cbDetails.setSelected(expanded);
      this.pExpandable.setVisible(expanded);
   }

   @Override
   public void setAlwaysExpanded(boolean alwaysExpanded) {
      if (alwaysExpanded) {
         this.setExpanded(true);
      }

      this.cbDetails.setVisible(this.getExpandableComponent() != null && !alwaysExpanded);
      this.alwaysExpanded = alwaysExpanded;
   }

   @Override
   public boolean isAlwaysExpanded() {
      return this.alwaysExpanded;
   }

   @Override
   public String getCheckBoxText() {
      return this.cbFooterCheck.getText();
   }

   public void setMainIcon(Icon icon) {
      this.lbIcon.setVisible(icon != null);
      this.lbIcon.setIcon(icon);
   }

   public Icon getMainIcon() {
      return this.lbIcon.getIcon();
   }

   @Override
   public Icon getIcon() {
      return this.icon;
   }

   @Override
   public void setIcon(Icon icon) {
      this.icon = icon;
      this.lbFooter.setIcon(Icons.scale(icon, 16, 16));
   }

   @Override
   public String getText() {
      return this.lbFooter.getText();
   }

   @Override
   public void setText(String text) {
      boolean footerLabelVisible = !Strings.isEmpty(text);
      this.pFooter.setVisible(footerLabelVisible);
      this.lbFooter.setVisible(footerLabelVisible);
      this.lbFooter.setText(Markup.toHTML(text));
   }

   public void setComponent(JComponent c) {
      this.pComponent.removeAll();
      if (c != null) {
         this.pComponent.add(c);
      }

      this.pComponent.setVisible(c != null);
   }

   public JComponent getComponent() {
      return this.pComponent.getComponentCount() == 0 ? null : (JComponent)this.pComponent.getComponent(0);
   }

   @Override
   public boolean isCheckBoxSelected() {
      return this.cbFooterCheck.isVisible() && this.cbFooterCheck.isSelected();
   }

   @Override
   public void setCheckBoxSelected(boolean selected) {
      this.cbFooterCheck.setSelected(selected);
   }

   @Override
   public void setCheckBoxText(String text) {
      this.cbFooterCheck.setVisible(!Strings.isEmpty(text));
      this.cbFooterCheck.setText(text == null ? "" : text);
   }

   class CommandAction extends AbstractAction implements TaskDialog.ValidationListener {
      private static final long serialVersionUID = 1L;
      private final TaskDialog.Command command;
      private final TaskDialog dlg;
      private Timer timer;
      private int counter;

      CommandAction(TaskDialog.Command command, TaskDialog dlg) {
         super(dlg.getString(command.getTitle()));
         this.command = command;
         this.dlg = dlg;
         this.counter = command.getWaitInterval();
         KeyStroke keyStroke = command.getKeyStroke();
         if (keyStroke != null) {
            String actionID = "TaskDialog.Command." + UUID.randomUUID().toString();
            TaskDialogContent.this.getInputMap(2).put(keyStroke, actionID);
            TaskDialogContent.this.getActionMap().put(actionID, this);
         }

         dlg.addValidationListener(this);
         this.putValue("Name", this.getTitle());
         if (this.counter > 0) {
            this.setEnabled(false);
            this.timer = new Timer(1000, new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  CommandAction.this.tick();
               }
            });
            dlg.addPropertyListener("visible", new PropertyChangeListener() {
               public void propertyChange(PropertyChangeEvent e) {
                  if (Boolean.TRUE.equals(e.getNewValue())) {
                     CommandAction.this.timer.start();
                  }
               }
            });
         }
      }

      @Override
      public void validationFinished(boolean validationResult) {
         this.setEnabled(this.command.isEnabled(validationResult));
      }

      public void actionPerformed(ActionEvent e) {
         this.dlg.setResult(this.command);
         if (this.command.isClosing()) {
            this.dlg.setVisible(false);
         }
      }

      private String getTitle() {
         String title = this.dlg.getString(this.command.getTitle());
         return this.counter > 0 ? String.format("%s (%d)", title, this.counter) : title;
      }

      private void tick() {
         if (--this.counter <= 0) {
            this.timer.stop();
         }

         this.putValue("Name", this.getTitle());
         this.setEnabled(this.counter <= 0);
      }
   }
}

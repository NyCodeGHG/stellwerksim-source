package com.ezware.dialog.task.design;

import com.ezware.dialog.task.ICommandLinkPainter;
import com.ezware.dialog.task.TaskDialog;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.UIManager;
import net.miginfocom.swing.MigLayout;

public class MacOsContentDesign extends DefaultContentDesign {
   private ICommandLinkPainter commandButtonPainter;

   public MacOsContentDesign() {
      super();
   }

   @Override
   public void updateUIDefaults() {
      super.updateUIDefaults();
      UIManager.put("TaskDialog.moreDetailsIcon", createResourceIcon("moreDetailsMac.png"));
      UIManager.put("TaskDialog.fewerDetailsIcon", createResourceIcon("fewerDetailsMac.png"));
      UIManager.put("TaskDialog.messageBackground", 17);
      UIManager.put("TaskDialog.instructionForeground", 13);
      UIManager.put("TaskDialog.instructionFont", this.deriveFont("Label.font", Integer.valueOf(1), 1.0F));
      UIManager.put("TaskDialog.textFont", this.deriveFont("Label.font", Integer.valueOf(0), 0.85F));
      UIManager.put("TaskDialog.moreDetailsText", TaskDialog.makeKey("Details"));
      UIManager.put("TaskDialog.fewerDetailsText", TaskDialog.makeKey("Details"));
   }

   @Override
   public TaskDialogContent buildContent() {
      TaskDialogContent content = new TaskDialogContent();
      content.setMinimumSize(new Dimension(200, 70));
      content.lbInstruction.setFont(UIManager.getFont("TaskDialog.instructionFont"));
      content.lbInstruction.setForeground(UIManager.getColor("TaskDialog.instructionForeground"));
      content.lbText.setFont(UIManager.getFont("TaskDialog.textFont"));
      content.pComponent.setOpaque(false);
      content.removeAll();
      content.setLayout(this.createMigLayout("ins dialog, hidemode 3, fill", "[]", "[][][]"));
      JPanel pMessage = new JPanel(this.createMigLayout("ins 0, gapx 7, hidemode 3", "[][grow]", "[][][]"));
      pMessage.setBackground(UIManager.getColor("TaskDialog.messageBackground"));
      pMessage.add(content.lbIcon, "cell 0 0 0 2, aligny top");
      pMessage.add(content.lbInstruction, "cell 1 0, growx, aligny top");
      pMessage.add(content.lbText, "cell 1 1, growx, aligny top");
      pMessage.add(content.pComponent, "cell 1 3, grow");
      content.setBackground(pMessage.getBackground());
      content.add(pMessage, "cell 0 0");
      content.pFooter.setLayout(new MigLayout("ins 0"));
      content.pFooter.add(content.lbFooter, "dock center");
      content.add(content.pFooter, "cell 0 2");
      content.pExpandable
         .setBorder(BorderFactory.createCompoundBorder(UIManager.getBorder("InsetBorder.aquaVariant"), BorderFactory.createEmptyBorder(7, 7, 7, 7)));
      content.pCommandPane.setLayout(this.createMigLayout("ins 0, hidemode 3", "[pref!][grow]", "[pref!][pref!,grow][pref!][pref!]"));
      content.pCommandPane.add(content.cbDetails, "cell 0 0");
      content.pCommandPane.add(content.pExpandable, "cell 0 1 3 1, grow");
      content.pCommandPane.add(content.pCommands, "cell 2 2, alignx right");
      content.pCommandPane.add(content.cbFooterCheck, "cell 0 2");
      content.add(content.pCommandPane, "cell 0 1, grow");
      return content;
   }

   @Override
   public ICommandLinkPainter getCommandLinkPainter() {
      if (this.commandButtonPainter == null) {
         this.commandButtonPainter = new MacOsCommandLinkPainter();
      }

      return this.commandButtonPainter;
   }

   @Override
   public boolean isCommandButtonSizeLocked() {
      return false;
   }
}

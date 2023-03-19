package com.ezware.dialog.task.design;

import com.ezware.dialog.task.ICommandLinkPainter;
import java.awt.SystemColor;
import javax.swing.UIManager;

public class LinuxContentDesign extends DefaultContentDesign {
   ICommandLinkPainter commandButtonPainter;

   public LinuxContentDesign() {
      super();
   }

   @Override
   public void updateUIDefaults() {
      super.updateUIDefaults();
      if (UIManager.getIcon("OptionPane.errorIcon") == null) {
         UIManager.put("OptionPane.errorIcon", createResourceIcon("linux_error.png"));
         UIManager.put("OptionPane.informationIcon", createResourceIcon("linux_info.png"));
         UIManager.put("OptionPane.questionIcon", createResourceIcon("linux_question.png"));
         UIManager.put("OptionPane.warningIcon", createResourceIcon("linux_warning.png"));
      }

      UIManager.put("TaskDialog.messageBackground", 17);
      UIManager.put("TaskDialog.instructionForeground", SystemColor.textHighlight);
      UIManager.put("TaskDialog.instructionFont", this.deriveFont("Label.font", Integer.valueOf(1), 1.07F));
      UIManager.put("TaskDialog.textFont", this.deriveFont("Label.font", Integer.valueOf(0), 0.85F));
   }

   @Override
   public ICommandLinkPainter getCommandLinkPainter() {
      if (this.commandButtonPainter == null) {
         this.commandButtonPainter = new MacOsCommandLinkPainter();
      }

      return this.commandButtonPainter;
   }
}

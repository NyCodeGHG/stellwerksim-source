package com.ezware.dialog.task;

import com.ezware.dialog.task.design.TaskDialogContent;

public interface IContentDesign {
   String ICON_INFO = "OptionPane.informationIcon";
   String ICON_QUESTION = "OptionPane.questionIcon";
   String ICON_WARNING = "OptionPane.warningIcon";
   String ICON_ERROR = "OptionPane.errorIcon";
   String ICON_FEWER_DETAILS = "TaskDialog.fewerDetailsIcon";
   String ICON_MORE_DETAILS = "TaskDialog.moreDetailsIcon";
   String ICON_COMMAND_LINK = "TaskDialog.commandLinkIcon";
   String COLOR_MESSAGE_BACKGROUND = "TaskDialog.messageBackground";
   String COLOR_INSTRUCTION_FOREGROUND = "TaskDialog.instructionForeground";
   String FONT_INSTRUCTION = "TaskDialog.instructionFont";
   String FONT_TEXT = "TaskDialog.textFont";
   String TEXT_MORE_DETAILS = "TaskDialog.moreDetailsText";
   String TEXT_FEWER_DETAILS = "TaskDialog.fewerDetailsText";

   void updateUIDefaults();

   TaskDialogContent buildContent();

   boolean isCommandButtonSizeLocked();

   ICommandLinkPainter getCommandLinkPainter();
}

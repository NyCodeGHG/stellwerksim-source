package com.ezware.dialog.task;

import javax.swing.Icon;

public class CommandLink {
   private final String instruction;
   private final String text;
   private final Icon icon;

   public CommandLink(Icon icon, String instruction, String text) {
      this.instruction = instruction;
      this.text = text;
      this.icon = icon;
   }

   public CommandLink(String instruction, String text) {
      this(null, instruction, text);
   }

   public String getInstruction() {
      return this.instruction;
   }

   public String getText() {
      return this.text;
   }

   public Icon getIcon() {
      return this.icon;
   }
}

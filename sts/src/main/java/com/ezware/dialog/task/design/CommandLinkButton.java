package com.ezware.dialog.task.design;

import com.ezware.common.Markup;
import com.ezware.dialog.task.CommandLink;
import com.ezware.dialog.task.ICommandLinkPainter;
import com.ezware.dialog.task.TaskDialog;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

public class CommandLinkButton extends JToggleButton {
   private static final long serialVersionUID = 1L;
   private final CommandLink link;
   private final ICommandLinkPainter painter;

   public CommandLinkButton(CommandLink link, ICommandLinkPainter painter) {
      this.link = link;
      this.painter = painter;
      this.setHorizontalAlignment(2);
      this.setHorizontalTextPosition(4);
      this.setVerticalAlignment(1);
      this.setVerticalTextPosition(1);
      this.setIconTextGap(7);
      Icon icon = link.getIcon();
      this.setIcon(icon == null ? UIManager.getIcon("TaskDialog.commandLinkIcon") : icon);
      this.setText(this.buildText());
      this.setMargin(new Insets(7, 7, 7, 7));
      if (painter != null) {
         painter.intialize(this);
      }

      this.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TaskDialog dlg = TaskDialog.getInstance((Component)e.getSource());
            if (dlg != null) {
               dlg.setResult(TaskDialog.StandardCommand.OK);
               dlg.setVisible(false);
            }
         }
      });
   }

   private String buildText() {
      Font fontInstr = UIManager.getFont("TaskDialog.instructionFont");
      Font fontText = UIManager.getFont("TaskDialog.textFont");
      Color colorInstr = UIManager.getColor("TaskDialog.instructionForeground");
      StringBuilder txt = new StringBuilder();
      txt.append("<html><head><style type='text/css'>");
      txt.append("p { " + Markup.toCSS(fontInstr) + Markup.toCSS(colorInstr) + " };");
      txt.append(String.format("div { " + Markup.toSizeCSS(fontText) + " }"));
      txt.append("</style></head>");
      txt.append("<p>" + Markup.toHTML(this.link.getInstruction(), false) + "</p>");
      txt.append("<div>" + Markup.toHTML(this.link.getText(), false) + "</div>");
      txt.append("</html>");
      return txt.toString();
   }

   protected void paintComponent(Graphics g) {
      if (this.painter != null) {
         this.painter.paint(g, this);
      }

      super.paintComponent(g);
   }
}

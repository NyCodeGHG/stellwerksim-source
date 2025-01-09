package com.ezware.dialog.task.design;

import com.ezware.dialog.task.ICommandLinkPainter;
import com.ezware.dialog.task.IContentDesign;
import com.ezware.dialog.task.TaskDialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIDefaults.ActiveValue;
import net.miginfocom.swing.MigLayout;

public class DefaultContentDesign implements IContentDesign {
   private ICommandLinkPainter commandButtonPainter;

   @Override
   public void updateUIDefaults() {
      UIManager.put("TaskDialog.moreDetailsIcon", createResourceIcon("moreDetails.png"));
      UIManager.put("TaskDialog.fewerDetailsIcon", createResourceIcon("fewerDetails.png"));
      UIManager.put("TaskDialog.commandLinkIcon", createResourceIcon("arrowGreenRight.png"));
      UIManager.put("TaskDialog.messageBackground", SystemColor.window);
      UIManager.put("TaskDialog.instructionForeground", SystemColor.textHighlight.darker());
      UIManager.put("TaskDialog.instructionFont", this.deriveFont("Label.font", null, 1.4F));
      UIManager.put("TaskDialog.textFont", this.deriveFont("Label.font", null, 1.0F));
      UIManager.put("TaskDialog.moreDetailsText", TaskDialog.makeKey("MoreDetails"));
      UIManager.put("TaskDialog.fewerDetailsText", TaskDialog.makeKey("FewerDetails"));
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
      content.setLayout(this.createMigLayout("hidemode 3, fill"));
      JPanel pMessage = new JPanel(this.createMigLayout("ins dialog, gapx 7, hidemode 3", "[][grow]", "[][]10[grow][]"));
      pMessage.setBackground(UIManager.getColor("TaskDialog.messageBackground"));
      pMessage.add(content.lbIcon, "cell 0 0 0 2, aligny top");
      pMessage.add(content.lbInstruction, "cell 1 0, growx, aligny top");
      pMessage.add(content.lbText, "cell 1 1, growx, aligny top");
      pMessage.add(content.pExpandable, "cell 1 2, grow");
      pMessage.add(content.pComponent, "cell 1 3, grow");
      content.setBackground(pMessage.getBackground());
      content.add(pMessage, "dock center");
      content.pFooter.setLayout(this.createMigLayout("ins dialog"));
      content.pFooter.add(new JSeparator(), "dock north");
      content.pFooter.add(content.lbFooter, "dock center");
      content.add(content.pFooter, "dock south");
      content.pCommandPane.setLayout(this.createMigLayout("ins dialog, gapy 2, hidemode 3", "[pref!][grow]", "[pref][pref]"));
      content.pCommandPane.add(content.pCommands, "cell 1 0, alignx right");
      content.pCommandPane.add(content.cbDetails, "cell 0 0");
      content.pCommandPane.add(content.cbFooterCheck, "cell 0 1");
      content.pCommandPane.add(new JSeparator(), "dock north");
      content.add(content.pCommandPane, "dock south");
      return content;
   }

   @Override
   public boolean isCommandButtonSizeLocked() {
      return true;
   }

   private static String fixDebug(String lc) {
      if (!TaskDialog.isDebugMode()) {
         return lc;
      } else {
         return lc.toLowerCase().indexOf("debug") < 0 ? "debug," + lc : lc;
      }
   }

   protected MigLayout createMigLayout(String layoutConstraints) {
      return new MigLayout(fixDebug(layoutConstraints));
   }

   protected MigLayout createMigLayout(String layoutConstraints, String colConstraints, String rowConstraints) {
      return new MigLayout(fixDebug(layoutConstraints), colConstraints, rowConstraints);
   }

   @Override
   public ICommandLinkPainter getCommandLinkPainter() {
      if (this.commandButtonPainter == null) {
         this.commandButtonPainter = new DefaultCommandLinkPainter();
      }

      return this.commandButtonPainter;
   }

   protected static final Object createResourceIcon(final String name) {
      return new ActiveValue() {
         public Object createValue(UIDefaults table) {
            return new ImageIcon(TaskDialog.class.getResource(name));
         }
      };
   }

   protected final Object deriveFont(final String name, final Integer style, final float sizeFactor) {
      return new ActiveValue() {
         public Object createValue(UIDefaults table) {
            Font font = UIManager.getFont(name);
            float factor = sizeFactor == 0.0F ? 1.0F : sizeFactor;
            return style == null && factor == 1.0F ? font : font.deriveFont(style == null ? font.getStyle() : style, font.getSize2D() * factor);
         }
      };
   }
}

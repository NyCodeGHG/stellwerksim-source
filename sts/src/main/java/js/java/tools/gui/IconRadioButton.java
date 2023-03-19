package js.java.tools.gui;

import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

public class IconRadioButton extends JRadioButton {
   private Icon icon = null;
   private JLabel subLabel;
   private JPanel radioPanel;
   private int iconWidth;

   public IconRadioButton(int iconwidth) {
      super();
      this.iconWidth = iconwidth;
      this.configureObject();
   }

   private void configureObject() {
      this.setLayout(new BoxLayout(this, 2));
      this.radioPanel = new JPanel();
      this.radioPanel.setOpaque(false);
      this.resizePanel();
      this.add(this.radioPanel);
      this.subLabel = new JLabel();
      this.subLabel.setOpaque(false);
      this.add(this.subLabel);
   }

   private void resizePanel() {
      try {
         UIDefaults d = UIManager.getDefaults();
         Icon i = (Icon)d.get("RadioButton.icon");
         int x = i.getIconWidth() + this.getIconTextGap();
         if (this.icon == null) {
            x += this.iconWidth + this.subLabel.getIconTextGap();
         }

         Dimension dim = new Dimension(x, i.getIconHeight());
         this.radioPanel.setMinimumSize(dim);
         this.radioPanel.setMaximumSize(dim);
         this.radioPanel.setPreferredSize(dim);
         this.radioPanel.revalidate();
      } catch (NullPointerException var5) {
      }
   }

   public void setIcon(Icon i) {
      super.setIcon(i);
      this.resizePanel();
   }

   public void setEnabled(boolean e) {
      super.setEnabled(e);
      this.resizePanel();
      this.subLabel.setEnabled(e);
   }

   public void setExtraIcon(Icon imageIcon) {
      this.icon = imageIcon;
      this.subLabel.setIcon(this.icon);
   }

   public void setText(String text) {
      this.subLabel.setText(text);
   }
}

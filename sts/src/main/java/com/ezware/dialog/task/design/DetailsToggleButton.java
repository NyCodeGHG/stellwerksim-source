package com.ezware.dialog.task.design;

import javax.swing.JCheckBox;
import javax.swing.UIManager;

class DetailsToggleButton extends JCheckBox {
   private static final long serialVersionUID = 1L;

   DetailsToggleButton() {
      super();
      this.setIcon(UIManager.getIcon("TaskDialog.moreDetailsIcon"));
      this.setSelectedIcon(UIManager.getIcon("TaskDialog.fewerDetailsIcon"));
   }
}

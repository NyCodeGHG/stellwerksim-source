package js.java.isolate.sim.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.TreeMap;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleis.colorSystem.gleisColor;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.panels.actionevents.colorModifiedEvent;
import js.java.tools.ColorText;
import js.java.tools.ColorTextIcon;
import js.java.tools.actions.AbstractEvent;
import js.java.tools.gui.layout.ColumnLayout;

public class colorPanel extends basePanel {
   protected ButtonGroup colbg = new ButtonGroup();
   private final LinkedList<AbstractButton> blist = new LinkedList();

   protected void init() {
      this.initComponents();
      this.my_main.registerListener(10, this);
      this.setLayout(new ColumnLayout(2));
      TreeMap<String, Color> cols = gleisColor.getInstance().getBGcolors();

      for (String k : cols.keySet()) {
         ColorText c = new ColorText(k, (Color)cols.get(k));
         AbstractButton b = this.createButton(c);
         b.setIcon(new ColorTextIcon(c));
         b.setText(c.getText());
         b.setHorizontalAlignment(2);
         b.setFocusable(false);
         b.setFocusPainted(false);
         b.setActionCommand(k);
         this.add(b);
         this.blist.add(b);
      }
   }

   protected AbstractButton createButton(ColorText c) {
      JToggleButton b = new JToggleButton();
      b.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            String a = ((JToggleButton)e.getSource()).getActionCommand();
            colorPanel.this.glbControl.setNextColor(a);
         }
      });
      if (c.getText().equals("normal")) {
         b.setSelected(true);
      }

      this.colbg.add(b);
      return b;
   }

   public colorPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.init();
   }

   @Override
   public void action(AbstractEvent e) {
      if (e instanceof colorModifiedEvent) {
         TreeMap<String, Color> cols = gleisColor.getInstance().getBGcolors();

         for (AbstractButton b : this.blist) {
            String k = b.getActionCommand();
            ColorText c = new ColorText(k, (Color)cols.get(k));
            b.setIcon(new ColorTextIcon(c));
         }
      }
   }

   @Override
   public void shown(String n, gecBase gec) {
   }

   private void initComponents() {
      this.setBorder(BorderFactory.createTitledBorder("Hintergrundfarben"));
      this.setLayout(null);
   }
}

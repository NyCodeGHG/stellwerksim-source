package js.java.isolate.sim.panels;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.dtest.dtestresult;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.panels.actionevents.dtestresultEvent;
import js.java.isolate.sim.toolkit.DesignTestResultRenderer;
import js.java.tools.actions.AbstractEvent;

public class designtestResultPanel extends basePanel {
   DefaultListModel listm = new DefaultListModel();
   private JScrollPane jScrollPane1;
   private JList resultList;

   public designtestResultPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
      this.resultList.setModel(this.listm);
      e.registerListener(10, this);
   }

   @Override
   public void action(AbstractEvent e) {
      if (e instanceof dtestresultEvent) {
         dtestresultEvent ee = (dtestresultEvent)e;
         this.listm.removeAllElements();

         for(dtestresult d : ee.getResults()) {
            this.listm.addElement(d);
         }
      }
   }

   @Override
   public void shown(String n, gecBase gec) {
   }

   @Override
   public void hidden(gecBase gec) {
   }

   private void initComponents() {
      this.jScrollPane1 = new JScrollPane();
      this.resultList = new JList();
      this.setBorder(BorderFactory.createTitledBorder("Designtest Ergebnis"));
      this.setLayout(new BorderLayout());
      this.resultList.setSelectionMode(0);
      this.resultList.setCellRenderer(new DesignTestResultRenderer());
      this.resultList.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent evt) {
            designtestResultPanel.this.resultListValueChanged(evt);
         }
      });
      this.jScrollPane1.setViewportView(this.resultList);
      this.add(this.jScrollPane1, "Center");
   }

   private void resultListValueChanged(ListSelectionEvent evt) {
      dtestresult s = (dtestresult)this.resultList.getSelectedValue();
      if (s != null) {
         s.paintResultsInGleisbild(this.glbControl.getModel());
      } else {
         this.glbControl.getModel().allOff();
      }
   }
}

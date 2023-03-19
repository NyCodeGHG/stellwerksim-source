package js.java.isolate.sim.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.gleisbild.gecWorker.gecInsert;
import js.java.isolate.sim.inserts.doppelkreuz;
import js.java.isolate.sim.inserts.dualeinaus;
import js.java.isolate.sim.inserts.gleisdisplay;
import js.java.isolate.sim.inserts.insert;
import js.java.isolate.sim.inserts.kreuzweichenstrasse;
import js.java.isolate.sim.inserts.mehrgleisbue;
import js.java.isolate.sim.inserts.mehrgleissprung_start;
import js.java.isolate.sim.inserts.mehrgleissprung_stop;
import js.java.isolate.sim.inserts.signalmitknopf;
import js.java.isolate.sim.inserts.singleeinaus;
import js.java.isolate.sim.inserts.weichenstrasse;
import js.java.isolate.sim.panels.actionevents.insertPanelPreviewShowEvent;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.gui.ViewTooltips;

public class insertPanel extends basePanel implements SessionClose {
   private final DefaultListModel model;
   private JScrollPane dataPanel;
   private JList insertsList;
   private JPanel jPanel1;
   private JScrollPane jScrollPane2;

   public insertPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
      ViewTooltips.register(this.insertsList);
      this.model = new DefaultListModel();
      String p = e.getParameter("insertpanels");
      StringTokenizer pst = new StringTokenizer(p, ",");

      while(pst.hasMoreTokens()) {
         String tk1 = pst.nextToken();
         if (tk1.equalsIgnoreCase("singleeinaus")) {
            this.add(new singleeinaus(this.my_main, this.glbControl.getModel()));
         } else if (tk1.equalsIgnoreCase("dualeinaus")) {
            this.add(new dualeinaus(this.my_main, this.glbControl.getModel()));
         } else if (tk1.equalsIgnoreCase("mehrgleissprung_start")) {
            this.add(new mehrgleissprung_start(this.my_main, this.glbControl.getModel()));
         } else if (tk1.equalsIgnoreCase("mehrgleissprung_stop")) {
            this.add(new mehrgleissprung_stop(this.my_main, this.glbControl.getModel()));
         } else if (tk1.equalsIgnoreCase("weichenstrasse")) {
            this.add(new weichenstrasse(this.my_main, this.glbControl.getModel()));
         } else if (tk1.equalsIgnoreCase("kreuzweichenstrasse")) {
            this.add(new kreuzweichenstrasse(this.my_main, this.glbControl.getModel()));
         } else if (tk1.equalsIgnoreCase("mehrgleisbue")) {
            this.add(new mehrgleisbue(this.my_main, this.glbControl.getModel()));
         } else if (tk1.equalsIgnoreCase("doppelkreuz")) {
            this.add(new doppelkreuz(this.my_main, this.glbControl.getModel()));
         } else if (tk1.equalsIgnoreCase("gleisdisplay")) {
            this.add(new gleisdisplay(this.my_main, this.glbControl.getModel()));
         } else if (tk1.equalsIgnoreCase("signalmitknopf")) {
            this.add(new signalmitknopf(this.my_main, this.glbControl.getModel()));
         }
      }

      this.insertsList.setModel(this.model);
   }

   @Override
   public void close() {
      this.model.clear();
   }

   private void add(insert i) {
      this.model.addElement(i);
      this.my_main.getContext().addCloseObject(i);
   }

   @Override
   public void shown(String n, gecBase gec) {
      this.insertsListValueChanged(null);
   }

   private void initComponents() {
      this.jPanel1 = new JPanel();
      this.jScrollPane2 = new JScrollPane();
      this.insertsList = new JList();
      this.dataPanel = new JScrollPane();
      this.setBorder(BorderFactory.createTitledBorder("Baugruppe einfügen"));
      this.setLayout(new GridLayout(0, 2));
      this.jPanel1.setBorder(BorderFactory.createTitledBorder("verfügbare Baugruppen"));
      this.jPanel1.setLayout(new BorderLayout());
      this.insertsList.setSelectionMode(0);
      this.insertsList.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent evt) {
            insertPanel.this.insertsListValueChanged(evt);
         }
      });
      this.jScrollPane2.setViewportView(this.insertsList);
      this.jPanel1.add(this.jScrollPane2, "Center");
      this.add(this.jPanel1);
      this.dataPanel.setBorder(BorderFactory.createTitledBorder("Parameter"));
      this.add(this.dataPanel);
   }

   private void insertsListValueChanged(ListSelectionEvent evt) {
      insert in = (insert)this.insertsList.getSelectedValue();
      if (in != null) {
         ((gecInsert)this.glbControl.getMode()).setInsert(in);
         this.dataPanel.setViewportView(in);
         ((TitledBorder)this.dataPanel.getBorder()).setTitle(in.getName());
         Dimension d2 = this.dataPanel.getViewport().getSize();
         in.setViewWidth(d2.width);
         in.revalidate();
         this.my_main.interPanelCom(new insertPanelPreviewShowEvent(in));
         this.dataPanel.repaint();
      }
   }
}

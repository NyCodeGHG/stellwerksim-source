package js.java.isolate.sim.panels;

import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleisbild.gleisbildControl;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gleisbildModel;
import js.java.isolate.sim.gleisbild.gleisbildViewPanel;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.inserts.insert;
import js.java.isolate.sim.panels.actionevents.insertPanelPreviewShowEvent;
import js.java.isolate.sim.panels.actionevents.insertPanelPreviewUpdateEvent;
import js.java.schaltungen.UserContext;
import js.java.tools.actions.AbstractEvent;

public class insertPreviewPanel extends basePanel {
   private final gleisbildViewPanel sub_editor_gleisbild;
   private final gleisbildViewPanel sub_sim_gleisbild;
   private insert currentlyshow = null;
   private JScrollPane editor_gleisbildscroller;
   private JScrollPane sim_gleisbildscroller;

   public insertPreviewPanel(gleisbildEditorControl glb, stellwerk_editor e, UserContext uc) {
      super(glb, e);
      this.initComponents();
      this.sub_editor_gleisbild = new gleisbildViewPanel(uc, new insertPreviewPanel.gbcInsert(uc, true));
      this.sub_sim_gleisbild = new gleisbildViewPanel(uc, new insertPreviewPanel.gbcInsert(uc, false));
      this.editor_gleisbildscroller.setViewportView(this.sub_editor_gleisbild);
      this.sim_gleisbildscroller.setViewportView(this.sub_sim_gleisbild);
      e.registerListener(10, this);
   }

   @Override
   public void action(AbstractEvent e) {
      if (e instanceof insertPanelPreviewShowEvent) {
         this.currentlyshow = ((insertPanelPreviewShowEvent)e).getInsert();
         this.sub_editor_gleisbild.setModel(this.currentlyshow.getModel());
         this.sub_sim_gleisbild.setModel(this.currentlyshow.getModel());
         this.sub_editor_gleisbild.paintBuffer();
         this.sub_editor_gleisbild.repaint();
         this.sub_sim_gleisbild.paintBuffer();
         this.sub_sim_gleisbild.repaint();
      } else if (e instanceof insertPanelPreviewUpdateEvent) {
         this.sub_editor_gleisbild.paintBuffer();
         this.sub_editor_gleisbild.repaint();
         this.sub_sim_gleisbild.paintBuffer();
         this.sub_sim_gleisbild.repaint();
      }
   }

   @Override
   public void shown(String n, gecBase gec) {
   }

   private void initComponents() {
      this.editor_gleisbildscroller = new JScrollPane();
      this.sim_gleisbildscroller = new JScrollPane();
      this.setBorder(BorderFactory.createTitledBorder("Baugruppe Vorschau"));
      this.setLayout(new GridLayout(1, 0));
      this.editor_gleisbildscroller.setBorder(BorderFactory.createTitledBorder("Editor"));
      this.add(this.editor_gleisbildscroller);
      this.sim_gleisbildscroller.setBorder(BorderFactory.createTitledBorder("Simulator"));
      this.add(this.sim_gleisbildscroller);
   }

   private class gbcInsert extends gleisbildControl<gleisbildModel> {
      private final boolean editorView;
      private BufferedImage img = null;

      gbcInsert(UserContext uc, boolean editorView) {
         super(uc);
         this.editorView = editorView;
         this.setScalePreset("115");
      }

      @Override
      public boolean isEditorView() {
         return this.editorView;
      }

      @Override
      protected void structureChanged(boolean fullChange) {
         if (this.model != null && (this.img == null || this.storedWidth != this.model.getGleisWidth() || this.storedHeight != this.model.getGleisHeight())) {
            this.storedWidth = this.model.getGleisWidth();
            this.storedHeight = this.model.getGleisHeight();
            this.img = this.createCompatibleImage();
         }
      }

      @Override
      public BufferedImage getPaintingImage() {
         return this.img;
      }

      @Override
      public BufferedImage getVisibleImage() {
         return this.img;
      }
   }
}

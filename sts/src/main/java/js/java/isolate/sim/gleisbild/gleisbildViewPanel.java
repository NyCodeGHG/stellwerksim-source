package js.java.isolate.sim.gleisbild;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.LinkedList;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import js.java.isolate.sim.gleis.gleis;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.actions.AbstractListener;
import js.java.tools.actions.ListenerList;
import js.java.tools.gui.border.DropShadowBorder;

public class gleisbildViewPanel extends JComponent implements PaintSaveInterface, SessionClose {
   private boolean lockOut = false;
   private final DropShadowBorder dsborder = new DropShadowBorder();
   private final gleisbildPainter painter = new gleisbildPainter();
   private Dimension preferredSize = new Dimension(10, 10);
   private final LinkedList<gleisbildViewPanel.glbOverlayPainter> overlayPainter = new LinkedList();
   private final ListenerList<ModelChangeEvent> modelChangeListeners = new ListenerList();
   private BufferedImageOp externalFilter = null;
   protected gleisbildModel model = null;
   protected gleisbildControl control = null;
   private final AbstractListener<StructureChangeEvent> modelListener = new AbstractListener<StructureChangeEvent>() {
      public void action(StructureChangeEvent e) {
         gleisbildViewPanel.this.updateSize();
         gleisbildViewPanel.this.repaint();
      }
   };
   private final UserContext uc;

   public void addModelChangeListener(AbstractListener<ModelChangeEvent> l) {
      this.modelChangeListeners.addListener(l);
   }

   public void removeModelChangeListener(AbstractListener<ModelChangeEvent> l) {
      this.modelChangeListeners.removeListener(l);
   }

   public gleisbildViewPanel(UserContext uc) {
      super();
      this.uc = uc;
      this.setDoubleBuffered(false);
      this.setOpaque(true);
      uc.addCloseObject(this.painter);
   }

   public gleisbildViewPanel(UserContext uc, gleisbildControl control) {
      super();
      this.uc = uc;
      this.setDoubleBuffered(false);
      this.setOpaque(true);
      this.setControl(control);
      uc.addCloseObject(this.painter);
      uc.addCloseObject(this);
   }

   public gleisbildViewPanel(UserContext uc, gleisbildModel model) {
      super();
      this.uc = uc;
      this.setDoubleBuffered(false);
      this.setOpaque(true);
      this.setModel(model);
      uc.addCloseObject(this.painter);
      uc.addCloseObject(this);
   }

   public gleisbildViewPanel(UserContext uc, gleisbildModel model, gleisbildControl control) {
      super();
      this.uc = uc;
      this.setDoubleBuffered(false);
      this.setOpaque(true);
      this.setModel(model);
      this.setControl(control);
      uc.addCloseObject(this.painter);
      uc.addCloseObject(this);
   }

   public gleisbildViewPanel(UserContext uc, gleisbildControl control, gleisbildModel model) {
      super();
      this.uc = uc;
      this.setDoubleBuffered(false);
      this.setOpaque(true);
      this.setModel(model);
      this.setControl(control);
      uc.addCloseObject(this.painter);
   }

   @Override
   public void close() {
      this.modelChangeListeners.clear();
      this.setModel(null);
      this.setControl(null);
      this.externalFilter = null;
      this.overlayPainter.clear();
   }

   public void setModel(gleisbildModel m) {
      gleisbildModel oldmodel = this.model;
      if (this.model != null) {
         this.model.removeStructureChangeListener(this.modelListener);
      }

      this.model = m;
      if (this.model != null) {
         this.model.addStructureChangeListener(this.modelListener);
      }

      this.modelChangeListeners.fireEvent(new ModelChangeEvent(oldmodel, this.model));
      this.updateSize();
      this.repaint();
   }

   @Override
   public gleisbildModel getModel() {
      return this.model;
   }

   public void setControl(gleisbildControl control) {
      if (this.control != null) {
         this.control.unregister(this);
      }

      this.control = control;
      if (this.control != null) {
         this.control.register(this);
         this.updateSize();
      }
   }

   @Override
   public boolean isEditorView() {
      return this.control.isEditorView();
   }

   @Override
   public boolean isMasstabView() {
      return this.control.isMasstabView();
   }

   public void setLockMode(boolean enabled) {
      this.lockOut = enabled;
      this.setSmoothOn();
   }

   void updateSize() {
      if (SwingUtilities.isEventDispatchThread()) {
         Dimension csize;
         if (this.model != null && this.control != null) {
            csize = new Dimension(
               (int)((double)this.model.getGleisWidth() * this.control.getScaler().getXScale()),
               (int)((double)this.model.getGleisHeight() * this.control.getScaler().getYScale())
            );
         } else {
            csize = this.preferredSize;
         }

         if (csize.width != this.preferredSize.width || csize.height != this.preferredSize.height) {
            this.preferredSize = csize;
            this.revalidate();
            this.validate();
            this.setSize(this.preferredSize);
         }
      } else {
         SwingUtilities.invokeLater(() -> this.updateSize());
      }
   }

   public void paintBuffer() {
      if (this.control != null && this.model != null) {
         Graphics2D g = this.control.getPaintingImage().createGraphics();
         this.painter.paintComponent(this, g, this.control.getScaler());
         g.dispose();
      }
   }

   public void setExtenalFilterOp(BufferedImageOp externalFilter) {
      this.externalFilter = externalFilter;
      this.repaint();
   }

   private void paintImage(Graphics2D g) {
      BufferedImage img = this.control.getVisibleImage();
      if (img != null) {
         BufferedImageOp filterOp = this.externalFilter;
         if (this.lockOut) {
            int lockOutPos = 10;
            float[] brightKernel = new float[100];
            float v = 0.01F;

            for(int i = 0; i < brightKernel.length; ++i) {
               brightKernel[i] = 0.01F;
            }

            filterOp = new ConvolveOp(new Kernel(10, 10, brightKernel));
         }

         if (this.control.isEditorView()) {
            g.setColor(Color.BLACK);
            this.dsborder.paintBorder(this, g, 0, 0, img.getWidth() + 1 + 5, img.getHeight() + 1 + 5);
         }

         if (filterOp != null) {
            try {
               g.drawImage(img, filterOp, 0, 0);
            } catch (Exception var8) {
               var8.printStackTrace();
               g.drawImage(img, 0, 0, this);
            }
         } else {
            g.drawImage(img, 0, 0, this);
         }
      }
   }

   public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D)g.create();
      g2.setColor(gleis.colors.col_stellwerk_back);
      g2.fillRect(0, 0, this.getWidth(), this.getHeight());
      if (this.model != null && this.control != null && this.uc.getDataSwitch().awtpaint) {
         this.paintImage(g2);
         if (!this.lockOut) {
            if (this.overlayPainter != null) {
               for(gleisbildViewPanel.glbOverlayPainter p : this.overlayPainter) {
                  p.paint(this, g2, this.control.getScaler());
               }
            }

            this.painter.paintOverlay(this, g2, this.control.getScaler());
         }
      }

      g2.dispose();
   }

   public void addOverlayPainer(gleisbildViewPanel.glbOverlayPainter p) {
      if (!this.overlayPainter.contains(p)) {
         this.overlayPainter.add(p);
      }
   }

   public void removeOverlayPainer(gleisbildViewPanel.glbOverlayPainter p) {
      this.overlayPainter.remove(p);
   }

   public Dimension getPreferredSize() {
      return this.preferredSize;
   }

   public Dimension getMaximumSize() {
      return this.preferredSize;
   }

   public Dimension getMinimumSize() {
      return this.preferredSize;
   }

   public void setSmoothOff() {
      gleis.forceSmoothOff();
   }

   public void setSmoothOn() {
      gleis.forceSmoothOn();
   }

   public void setSmoothOneOff() {
      gleis.setAllowSmooth(false);
   }

   public interface glbOverlayPainter {
      void paint(gleisbildViewPanel var1, Graphics var2, scaleHolder var3);
   }
}

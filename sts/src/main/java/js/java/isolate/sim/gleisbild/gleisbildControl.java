package js.java.isolate.sim.gleisbild;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.gleis.gleis;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.schaltungen.timesystem.timedelivery;
import js.java.tools.actions.AbstractListener;
import js.java.tools.actions.ListenerList;

public abstract class gleisbildControl<T extends gleisbildModel> implements SessionClose {
   protected T model = (T)null;
   protected gleisbildViewPanel panel = null;
   protected int storedWidth = 0;
   protected int storedHeight = 0;
   protected boolean enabled = true;
   private timedelivery thetime = null;
   protected final scaleHolder scaler = new scaleHolder();
   protected final UserContext uc;
   private final ListenerList<CoordinatesEvent> coordsListeners = new ListenerList();
   private final AbstractListener<StructureChangeEvent> sce = new AbstractListener<StructureChangeEvent>() {
      public void action(StructureChangeEvent e) {
         gleisbildControl.this.structureChanged(e.dataChanged());
      }
   };
   private final AbstractListener<ModelChangeEvent> mcl = new AbstractListener<ModelChangeEvent>() {
      public void action(ModelChangeEvent e) {
         gleisbildControl.this.unregisterModel(e.getOldModel());
         gleisbildControl.this.registerModel(e.getNewModel());
         gleisbildControl.this.structureChanged(true);
      }
   };

   protected gleisbildControl(UserContext uc) {
      this.uc = uc;
      uc.addCloseObject(this);
   }

   @Override
   public void close() {
      if (this.panel != null) {
         this.unregister(this.panel);
      }
   }

   public void addCoordinatesListener(AbstractListener<CoordinatesEvent> l) {
      this.coordsListeners.addListener(l);
   }

   public void removeCoordinatesListener(AbstractListener<CoordinatesEvent> l) {
      this.coordsListeners.removeListener(l);
   }

   protected void emitCoordinateSignal(gleis gl, MouseEvent e) {
      this.coordsListeners.fireEvent(new CoordinatesEvent(gl, e));
   }

   protected BufferedImage createCompatibleImage() {
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice gs = ge.getDefaultScreenDevice();
      GraphicsConfiguration gc = gs.getDefaultConfiguration();

      try {
         return gc.createCompatibleImage(
            Math.max((int)((double)this.model.getGleisWidth() * this.scaler.getXScale()), 1),
            Math.max((int)((double)this.model.getGleisHeight() * this.scaler.getYScale()), 1),
            3
         );
      } catch (OutOfMemoryError var5) {
         System.out.println("Out of Memory: " + var5.getMessage());
         System.gc();
         Logger.getLogger("stslogger").log(Level.SEVERE, "Out of memory", var5);
         return null;
      }
   }

   private void registerModel(gleisbildModel m) {
      this.model = (T)m;

      try {
         m.addStructureChangeListener(this.sce);
      } catch (NullPointerException var3) {
      }
   }

   private void unregisterModel(gleisbildModel m) {
      try {
         m.removeStructureChangeListener(this.sce);
      } catch (NullPointerException var3) {
      }
   }

   public void register(gleisbildViewPanel panel) {
      panel.addModelChangeListener(this.mcl);
      this.registerModel(panel.getModel());
      this.panel = panel;
   }

   public void unregister(gleisbildViewPanel panel) {
      this.unregisterModel(panel.getModel());
      panel.removeModelChangeListener(this.mcl);
      this.panel = null;
   }

   public T getModel() {
      return this.model;
   }

   public gleisbildViewPanel getPanel() {
      return this.panel;
   }

   protected abstract void structureChanged(boolean var1);

   public abstract BufferedImage getPaintingImage();

   public abstract BufferedImage getVisibleImage();

   public void setScalePreset(String newS) {
      this.scaler.setScalePreset(newS);
      if (this.panel != null) {
         this.panel.updateSize();
      }

      this.storedWidth = this.storedHeight = 0;
      this.structureChanged(true);
   }

   public void setScale(double x, double y) {
      this.scaler.setScale(x, y);
      if (this.panel != null) {
         this.panel.updateSize();
      }

      this.storedWidth = this.storedHeight = 0;
      this.structureChanged(true);
   }

   public scaleHolder getScaler() {
      return this.scaler;
   }

   public void setGUIEnable(boolean enabled) {
      this.enabled = enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public boolean isEditorView() {
      return false;
   }

   public boolean isMasstabView() {
      return false;
   }

   public timedelivery getSimTime() {
      return this.thetime;
   }

   public void setSimTime(timedelivery t) {
      this.thetime = t;
   }

   public gleis gleisUnderMouse(MouseEvent e) {
      return this.gleisUnderMouse(e.getX(), e.getY());
   }

   public gleis gleisUnderMouse(int x, int y) {
      int sx = this.scaler.getGleisColOfMouseX(x);
      int sy = this.scaler.getGleisRowOfMouseY(y);
      return this.model.getXY_null(sx, sy);
   }
}

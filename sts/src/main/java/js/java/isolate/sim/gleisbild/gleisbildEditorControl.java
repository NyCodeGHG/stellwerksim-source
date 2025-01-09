package js.java.isolate.sim.gleisbild;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.colorSystem.gleisColor;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.gleisbild.gecWorker.gecNowork;
import js.java.isolate.sim.gleisbild.gleisbildWorker.fahrwegCalculator;
import js.java.isolate.sim.gleisbild.gleisbildWorker.minimizeGB;
import js.java.isolate.sim.gleisbild.gleisbildWorker.renewEnr;
import js.java.schaltungen.UserContext;

public class gleisbildEditorControl extends gleisbildControl<gleisbildModelSts> implements MouseListener, MouseMotionListener, Runnable {
   private final BufferedImage[] img = new BufferedImage[]{null, null};
   private int currentVisibleBuffer = 0;
   private final int maxFlipCount = 1;
   private boolean pausePainterThread = false;
   private final stellwerk_editor editor;
   private boolean editorView = true;
   private boolean masstabView = false;
   private boolean masstabColor = false;
   private volatile boolean threadRunning = true;
   private gecBase mouseEditor = new gecNowork();
   private final Thread paintThread = new Thread(this);
   private final ArrayBlockingQueue paintCommand = new ArrayBlockingQueue(2);
   private fahrwegCalculator fwCalc = null;
   private String nextColor = "normal";

   public gleisbildEditorControl(stellwerk_editor editor, UserContext uc) {
      super(uc);
      this.editor = editor;
      this.paintThread.start();
   }

   @Override
   public void close() {
      this.threadRunning = false;
      this.paintThread.interrupt();
      this.mouseEditor = null;
      this.paintCommand.clear();
      super.close();
   }

   @Override
   public void register(gleisbildViewPanel panel) {
      super.register(panel);
      panel.getModel().getDisplayBar().cleanup();
      panel.getModel().getDisplayBar().handleLegacy();
      this.registerMouse(panel);
   }

   @Override
   public void unregister(gleisbildViewPanel panel) {
      this.unregisterMouse(panel);
      super.unregister(panel);
      this.img[0] = null;
      this.img[1] = null;
   }

   private void registerMouse(gleisbildViewPanel panel) {
      panel.addMouseListener(this);
      panel.addMouseMotionListener(this);
   }

   private void unregisterMouse(gleisbildViewPanel panel) {
      panel.removeMouseListener(this);
      panel.removeMouseMotionListener(this);
   }

   @Override
   public BufferedImage getPaintingImage() {
      return this.img[Math.min(1 - this.currentVisibleBuffer, 1)];
   }

   @Override
   public BufferedImage getVisibleImage() {
      return this.img[Math.min(this.currentVisibleBuffer, 1)];
   }

   @Override
   protected void structureChanged(boolean fullChange) {
      if (this.img[0] == null || this.img[1] == null || this.storedWidth != this.model.getGleisWidth() || this.storedHeight != this.model.getGleisHeight()) {
         this.storedWidth = this.model.getGleisWidth();
         this.storedHeight = this.model.getGleisHeight();
         this.img[0] = this.createCompatibleImage();
         this.img[1] = this.createCompatibleImage();
         this.currentVisibleBuffer = 0;
         fullChange = true;
      }

      if (fullChange) {
         this.paintBufferDelayed();
      } else {
         this.panel.repaint();
      }
   }

   public void repaint() {
      this.paintBufferDelayed();
   }

   public void run() {
      while (this.threadRunning) {
         try {
            this.paintCommand.poll(1L, TimeUnit.DAYS);
            this.paintCommand.clear();
            if (!this.pausePainterThread && this.panel != null && this.img[0] != null && this.img[1] != null) {
               this.panel.paintBuffer();
               this.flipImage();
               this.panel.repaint();
            }
         } catch (InterruptedException var2) {
            return;
         } catch (NullPointerException var3) {
         } catch (Exception var4) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "run()", var4);
         }
      }
   }

   public void setPausePainter(boolean pause) {
      this.pausePainterThread = pause;
   }

   public void paintBufferDelayed() {
      this.paintCommand.offer(this);
   }

   protected void flipImage() {
      this.currentVisibleBuffer = 1 - this.currentVisibleBuffer;
   }

   @Override
   public void setGUIEnable(boolean enabled) {
      this.editor.setGUIEnable(enabled);
   }

   public void renewAllENR() {
      this.setGUIEnable(false);
      renewEnr r = new renewEnr(this.model, this.editor);
      r.fullRenew();
      this.setGUIEnable(true);
   }

   void renewENR() {
      this.setGUIEnable(false);
      renewEnr r = new renewEnr(this.model, this.editor);
      r.renew();
      this.setGUIEnable(true);
   }

   public void minimize() {
      minimizeGB r = new minimizeGB(this.model, this.editor);
      r.minimize();
   }

   public void calcFahrwege() {
      this.fwCalc = new fahrwegCalculator(this.model, this.editor, new fahrwegCalculator.callHook() {
         @Override
         public void showPanel(int p, int v) {
            gleisbildEditorControl.this.editor.showPanel(p, v);
         }

         @Override
         public fw_doppelt_interface new_fw_doppelt_class(ArrayList<fahrstrasse> old) {
            return gleisbildEditorControl.this.editor.new_fw_doppelt_class(old);
         }
      });
      this.fwCalc.startThread();
   }

   public void stopCalcFahrwege() {
      this.fwCalc.stopCalcFahrwege();
   }

   @Override
   public boolean isEditorView() {
      return this.editorView;
   }

   @Override
   public boolean isMasstabView() {
      return this.masstabView;
   }

   public void setSimViewStyle(boolean simmode) {
      this.editorView = !simmode;
   }

   public gleis getSelectedGleis() {
      return this.model.getSelectedGleis();
   }

   public void setSelectedGleis(gleis gl) {
      this.model.setSelectedGleis(gl, false);
   }

   public void setFocus(gleis gl) {
      this.model.setFocus(gl);
   }

   public void mouseClicked(MouseEvent e) {
      this.mouseEditor.mouseClicked(e);
   }

   public void mousePressed(MouseEvent e) {
      gleis gl = this.gleisUnderMouse(e);
      this.emitCoordinateSignal(gl, e);
      this.mouseEditor.mousePressed(e);
   }

   public void mouseReleased(MouseEvent e) {
      this.mouseEditor.mouseReleased(e);
   }

   public void mouseEntered(MouseEvent e) {
      this.mouseEditor.mouseEntered(e);
   }

   public void mouseExited(MouseEvent e) {
      this.mouseEditor.mouseExited(e);
   }

   public void mouseDragged(MouseEvent e) {
      gleis gl = this.gleisUnderMouse(e);
      this.emitCoordinateSignal(gl, e);
      this.mouseEditor.mouseDragged(e);
   }

   public void mouseMoved(MouseEvent e) {
      gleis gl = this.gleisUnderMouse(e);
      this.emitCoordinateSignal(gl, e);
      this.mouseEditor.mouseMoved(e);
   }

   public void setMode(gecBase e) {
      if (e == null) {
         e = new gecNowork();
      }

      this.mouseEditor.deinit(e);
      e.init(this, this.mouseEditor);
      this.mouseEditor = e;
   }

   public gecBase getMode() {
      return this.mouseEditor;
   }

   public void setNextColor(String a) {
      this.nextColor = a;
   }

   public String getNextColor() {
      return this.nextColor;
   }

   public void setMassVisible(boolean b) {
      if (this.masstabView != b) {
         this.setMassColor(b);
         this.masstabView = b;
         this.repaint();
      }
   }

   public void setMassColor(boolean b) {
      if (this.masstabColor != b) {
         if (b) {
            gleisColor.getInstance().setMasstabColor();
         } else {
            gleisColor.getInstance().setNormalColor();
         }

         this.masstabColor = b;
         this.repaint();
      }
   }
}

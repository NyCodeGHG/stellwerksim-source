package js.java.isolate.sim.gleisbild;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.EventListener;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import js.java.isolate.sim.trigger;
import js.java.isolate.sim.triggerjobmanager;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventHaeufigkeiten;
import js.java.isolate.sim.eventsys.thema;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasseSelection;
import js.java.isolate.sim.gleisbild.fahrstrassen.fsAllocs;
import js.java.isolate.sim.structServ.structinfo;
import js.java.schaltungen.UserContext;

public class gleisbildSimControl extends gleisbildControl<gleisbildModelSts> implements MouseListener, MouseMotionListener, structinfo {
   private final Runnable painterThread = () -> {
      if (this.uc.getDataSwitch().painterThread) {
         this.painterThread_run();
      }
   };
   private final BufferedImage[] img = new BufferedImage[]{null, null};
   private int currentVisibleBuffer = 0;
   private triggerjobmanager manager = null;
   private boolean running = true;
   private long lastClick = 0L;
   private int maxFlipCount = 1;
   private gleisbildSimControl.clickClick clicker = new gleisbildSimControl.clickClick() {
      @Override
      public void clicked(int n) {
      }
   };
   private Thread pThread = null;
   private final LinkedBlockingQueue<gleisbildSimControl.PAINTCOMMAND> threadCommands = new LinkedBlockingQueue();
   private final EventListenerList painterListener = new EventListenerList();
   private gleisbildSimControl.TIMERMODE timerMode;
   private long timerMaxTime = 0L;
   private static final int DUAL_KEY_TIME_DEFAULT = 5;
   private static final int DUAL_KEY_TIME_LONG = 60;
   private int DUAL_KEY_TIME = 5;
   private final gleisbildSimControl.gleisSelection currentSelection = new gleisbildSimControl.gleisSelection();

   public gleisbildSimControl(UserContext uc) {
      super(uc);
      this.setDualKeyTimeLong(false);
   }

   @Override
   public void register(gleisbildViewPanel panel) {
      super.register(panel);
      panel.getModel().getDisplayBar().handleLegacy();
      this.manager = new triggerjobmanager(this);
      this.uc.addCloseObject(this.manager);
      this.running = true;
      this.threadCommands.clear();
      if (this.pThread == null) {
         this.pThread = new Thread(this.painterThread);
         this.pThread.setName("GSC_painter");
         this.pThread.start();
      }

      this.structureChanged(true);
      this.registerMouse(panel);
   }

   @Override
   public void unregister(gleisbildViewPanel panel) {
      this.unregisterMouse(panel);
      super.unregister(panel);
      this.img[0] = null;
      this.img[1] = null;
      this.currentVisibleBuffer = 0;
      if (this.manager != null) {
         this.manager.tjm_stop();
      }

      this.manager = null;
      this.stop();
   }

   private void registerMouse(gleisbildViewPanel panel) {
      panel.addMouseListener(this);
      panel.addMouseMotionListener(this);
   }

   private void unregisterMouse(gleisbildViewPanel panel) {
      panel.removeMouseListener(this);
      panel.removeMouseMotionListener(this);
   }

   public void stop() {
      this.running = false;
      this.threadCommands.offer(gleisbildSimControl.PAINTCOMMAND.EXITTHREAD);
      if (this.pThread != null) {
         this.pThread.interrupt();
         this.pThread = null;
      }
   }

   private void addRepaintTrigger() {
      this.manager.add(new gleisbildSimControl.repaintTrigger());
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
         this.fastPaint();
      }
   }

   @Override
   public BufferedImage getPaintingImage() {
      return this.img[this.maxFlipCount - this.currentVisibleBuffer];
   }

   @Override
   public BufferedImage getVisibleImage() {
      return this.img[this.currentVisibleBuffer];
   }

   protected void flipImage() {
      this.currentVisibleBuffer = 1 - this.currentVisibleBuffer;
   }

   public boolean paint2mini(Graphics2D g, int x, int y, int width, int height) {
      if (this.getVisibleImage() != null) {
         g.drawImage(this.getVisibleImage(), x, y, x + width, y + height, 0, 0, this.getVisibleImage().getWidth(), this.getVisibleImage().getHeight(), null);
         return true;
      } else {
         return false;
      }
   }

   public void setUseOffscreen(boolean on) {
      this.maxFlipCount = on ? 1 : 0;
   }

   public boolean isUseOffscreen() {
      return this.maxFlipCount == 1;
   }

   private void painterThread_run() {
      while(this.running) {
         try {
            gleisbildSimControl.PAINTCOMMAND cmd = (gleisbildSimControl.PAINTCOMMAND)this.threadCommands.take();
            switch(cmd) {
               case PAINTBUFFER:
                  if (this.uc.getDataSwitch().paintbuffer) {
                     this.panel.paintBuffer();
                  }
                  break;
               case FLIPBUFFER:
                  if (this.uc.getDataSwitch().flipImage) {
                     this.flipImage();
                  }
                  break;
               case SHOWBUFFER:
                  if (this.uc.getDataSwitch().panelrepaint) {
                     this.panel.repaint();
                  }

                  if (this.uc.getDataSwitch().firepainter) {
                     this.firePainter();
                  }
                  break;
               case EXITTHREAD:
                  return;
            }
         } catch (NullPointerException | InterruptedException var2) {
         } catch (Exception var3) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "run()", var3);
         }
      }
   }

   public void addPainterListener(gleisbildSimControl.paintEventListener l) {
      this.painterListener.add(gleisbildSimControl.paintEventListener.class, l);
   }

   public void removePainterListener(gleisbildSimControl.paintEventListener l) {
      this.painterListener.remove(gleisbildSimControl.paintEventListener.class, l);
   }

   protected void firePainter() {
      Object[] listeners = this.painterListener.getListenerList();

      for(int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == gleisbildSimControl.paintEventListener.class) {
            ((gleisbildSimControl.paintEventListener)listeners[i + 1]).paintEvent();
         }
      }
   }

   protected void fastPaint(gleis gl) {
      this.paintBuffer();
   }

   public void fastPaint() {
      this.paintBuffer();
   }

   public synchronized void paintBuffer() {
      this.threadCommands.offer(gleisbildSimControl.PAINTCOMMAND.PAINTBUFFER);
      this.threadCommands.offer(gleisbildSimControl.PAINTCOMMAND.FLIPBUFFER);
      this.threadCommands.offer(gleisbildSimControl.PAINTCOMMAND.SHOWBUFFER);
   }

   public void initEventTJM() {
      for(eventContainer ec : this.model.events) {
         if (eventContainer.isDebug()) {
            eventContainer.getDebug().writeln("ev: " + ec.getName());
         }

         boolean addevent = true;

         for(String t : ec.getThemeList(true)) {
            if (thema.isThema(t)) {
               addevent = false;
               if (eventContainer.isDebug()) {
                  eventContainer.getDebug().writeln("ev removed by remove list: " + ec.getName());
               }
               break;
            }
         }

         if (addevent) {
            Set<String> in = ec.getThemeList(false);
            if (!in.isEmpty()) {
               addevent = false;

               for(String t : in) {
                  if (thema.isThema(t)) {
                     addevent = true;
                     if (eventContainer.isDebug()) {
                        eventContainer.getDebug().writeln("ev added by add list: " + ec.getName());
                     }
                     break;
                  }
               }
            }
         }

         ec.setAllowUse(addevent);
         if (addevent && eventContainer.isDebug()) {
            eventContainer.getDebug().writeln("allowuse ev: " + ec.getName());
         }
      }

      eventHaeufigkeiten.create(this.model).initTJM(this);
   }

   public void setDualKeyTimeLong(boolean e) {
      this.DUAL_KEY_TIME = e ? 60 : 5;
   }

   public boolean isDualKeyTimeLong() {
      return this.DUAL_KEY_TIME == 60;
   }

   public void timerOff() {
      this.timerMaxTime = 0L;
   }

   public void setUFGTon() {
      this.timerMaxTime = this.getSimTime().getSimutime() + 1000L * (long)this.DUAL_KEY_TIME;
      this.timerMode = gleisbildSimControl.TIMERMODE.UFGT;
   }

   public boolean isUFGTon() {
      return this.timerMode == gleisbildSimControl.TIMERMODE.UFGT && this.timerMaxTime >= this.getSimTime().getSimutime();
   }

   public void setManualAutoFSon() {
      this.timerMaxTime = this.getSimTime().getSimutime() + 1000L * (long)this.DUAL_KEY_TIME;
      this.timerMode = gleisbildSimControl.TIMERMODE.AUTOFS;
   }

   public boolean isManualAutoFSon() {
      return this.timerMode == gleisbildSimControl.TIMERMODE.AUTOFS && this.timerMaxTime >= this.getSimTime().getSimutime();
   }

   public gleisbildSimControl.gleisSelection getSelection() {
      return this.currentSelection;
   }

   public void clearSelection() {
      try {
         this.currentSelection.signal1.getFluentData().setPressed(false);
         this.currentSelection.signal1 = null;
      } catch (Exception var4) {
      }

      try {
         this.currentSelection.signal2.getFluentData().setPressed(false);
         this.currentSelection.signal2 = null;
      } catch (Exception var3) {
      }

      try {
         this.currentSelection.gl_object1.getFluentData().setPressed(false);
         this.currentSelection.gl_object1 = null;
      } catch (Exception var2) {
      }

      this.fastPaint();
      this.addRepaintTrigger();
   }

   public long getLastClick() {
      return this.lastClick;
   }

   public void simulateClick() {
      this.lastClick = System.currentTimeMillis();
   }

   public void mouseClicked(MouseEvent e) {
   }

   public void mousePressed(MouseEvent e) {
      this.lastClick = System.currentTimeMillis();
      gleis gl = this.gleisUnderMouse(e);
      this.emitCoordinateSignal(gl, e);
      if (gl != null) {
         this.handleMouse(gl, e);
      }
   }

   public void mouseReleased(MouseEvent e) {
   }

   public void mouseEntered(MouseEvent e) {
   }

   public void mouseExited(MouseEvent e) {
   }

   public void mouseDragged(MouseEvent e) {
   }

   public void mouseMoved(MouseEvent e) {
      gleis gl = this.gleisUnderMouse(e);
      this.emitCoordinateSignal(gl, e);
   }

   public void registerClicker(gleisbildSimControl.clickClick c) {
      this.clicker = c;
   }

   protected void handleMouse(gleis gl, MouseEvent me) {
      if (gl != null && !gl.getFluentData().isGesperrt() && this.enabled) {
         if (gl.getElement() == gleis.ELEMENT_SIGNAL
            || gl.getElement() == gleis.ELEMENT_AUSFAHRT
            || gl.getElement() == gleis.ELEMENT_ZWERGSIGNAL
            || gl.getElement() == gleis.ELEMENT_SIGNALKNOPF
            || gl.getElement() == gleis.ELEMENT_SIGNAL_ZIELKNOPF
            || gl.getElement() == gleis.ELEMENT_AUSFAHRT_ZIELKNOPF) {
            try {
               this.currentSelection.gl_object1.getFluentData().setPressed(false);
               this.currentSelection.gl_object1 = null;
            } catch (Exception var12) {
            }

            if (this.currentSelection.signal1 == null) {
               this.currentSelection.signal1 = gl;
               gl.getFluentData().setPressed(true);
               this.fastPaint(gl);
               this.clicker.clicked(1);
            } else {
               this.currentSelection.signal2 = gl;
               gl.getFluentData().setPressed(true);
               this.fastPaint(gl);
               this.clicker.clicked(2);
               fahrstrasseSelection fs = this.model.findFahrweg(this.currentSelection.signal1, this.currentSelection.signal2, this.isUFGTon());
               if (fs != null) {
                  if (this.isManualAutoFSon()) {
                     fs.getStart().setTriggeredAutoFW(fs);
                  } else if (this.model.gleisbildextend.getSignalversion() > 0) {
                     this.model.getAdapter().getFSallocator().getFS(fs, fsAllocs.ALLOCM_USER_GETORSTORE);
                  } else {
                     this.model.getAdapter().getFSallocator().getFS(fs, fsAllocs.ALLOCM_USER_GET);
                  }
               }

               this.timerOff();
               this.currentSelection.signal1.getFluentData().setPressed(false);
               this.currentSelection.signal2.getFluentData().setPressed(false);
               this.currentSelection.signal1 = null;
               this.currentSelection.signal2 = null;
               this.fastPaint();
            }
         } else if (gl.getElement() == gleis.ELEMENT_WEICHEOBEN
            || gl.getElement() == gleis.ELEMENT_WEICHEUNTEN
            || gl.getElement() == gleis.ELEMENT_BAHNÜBERGANG
            || gl.getElement() == gleis.ELEMENT_ANRUFÜBERGANG
            || gl.getElement() == gleis.ELEMENT_ÜBERGABEAKZEPTOR
            || gl.getElement() == gleis.ELEMENT_ZDECKUNGSSIGNAL) {
            try {
               this.currentSelection.signal1.getFluentData().setPressed(false);
               this.currentSelection.signal1 = null;
               this.currentSelection.signal2.getFluentData().setPressed(false);
               this.currentSelection.signal2 = null;
            } catch (Exception var11) {
            }

            try {
               this.currentSelection.gl_object1.getFluentData().setPressed(false);
               this.currentSelection.gl_object1 = null;
            } catch (Exception var10) {
            }

            this.currentSelection.gl_object1 = gl;
            gl.getFluentData().setPressed(true);
            this.fastPaint();
         } else if (gleis.ALLE_DISPLAYS.matches(gl.getElement())) {
            try {
               this.currentSelection.signal1.getFluentData().setPressed(false);
               this.currentSelection.signal1 = null;
               this.currentSelection.signal2.getFluentData().setPressed(false);
               this.currentSelection.signal2 = null;
            } catch (Exception var9) {
            }

            try {
               this.currentSelection.gl_object1.getFluentData().setPressed(false);
               this.currentSelection.gl_object1 = null;
            } catch (Exception var8) {
            }

            this.currentSelection.gl_object1 = gl;
            gl.getFluentData().setPressed(true);
            this.fastPaint();
         } else if (this.model.canCallPhone() && me.getClickCount() == 2) {
            int x = gl.getCol();
            int y = gl.getRow();

            for(int i = 0; i < 5; ++i) {
               gleis g2 = this.model.getXY_null(x - i, y);
               if (g2 == null) {
                  break;
               }

               if (g2.getElement() == gleis.ELEMENT_AIDDISPLAY) {
                  String v = g2.getFluentData().displayGetValue();
                  if (v != null && !v.isEmpty()) {
                     this.model.callPhone(v);
                  }
               }
            }
         }
      }
   }

   public Collection getStructInfo() {
      Vector ret = new Vector();
      Vector v = new Vector();
      v.addElement("Control");
      v.addElement("TJM");
      v.addElement(this.manager);
      ret.add(v);
      v = new Vector();
      v.addElement("Control");
      v.addElement("SimControl");
      v.addElement(this);
      ret.add(v);
      return ret;
   }

   @Override
   public Vector getStructure() {
      Vector v = new Vector();
      v.addElement("currentVisibleBuffer");
      v.addElement(this.currentVisibleBuffer + "");
      v.addElement("running");
      v.addElement(this.running + "");
      v.addElement("lastClick");
      v.addElement(this.lastClick + "");
      return v;
   }

   @Override
   public String getStructName() {
      return "SimControl";
   }

   @Override
   public void setEnabled(boolean e) {
      super.setEnabled(e);
      this.panel.setLockMode(!e);
      this.panel.repaint();
   }

   private static enum PAINTCOMMAND {
      PAINTBUFFER,
      FLIPBUFFER,
      SHOWBUFFER,
      EXITTHREAD;
   }

   private static enum TIMERMODE {
      UFGT,
      AUTOFS;
   }

   public interface clickClick {
      void clicked(int var1);
   }

   public static class gleisSelection {
      public gleis signal1 = null;
      public gleis signal2 = null;
      public gleis gl_object1 = null;

      public gleisSelection() {
         super();
      }
   }

   public interface paintEventListener extends EventListener {
      void paintEvent();
   }

   private class repaintTrigger extends trigger {
      private repaintTrigger() {
         super();
      }

      @Override
      public boolean ping() {
         return true;
      }
   }
}

package js.java.isolate.sim;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventHaeufigkeiten;
import js.java.isolate.sim.eventsys.thema;
import js.java.isolate.sim.gleis.decor;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelStore;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.sim.GleisAdapterRouter;
import js.java.isolate.sim.sim.fsallocator;
import js.java.isolate.sim.sim.stellwerksim_main;
import js.java.isolate.sim.zug.zug;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.audio.AudioController;
import js.java.schaltungen.moduleapi.ModuleObject;
import js.java.schaltungen.moduleapi.SessionExit;
import js.java.schaltungen.timesystem.timedelivery;
import js.java.schaltungen.toplevelMessage.TopLevelMessage;
import js.java.tools.prefs;
import js.java.tools.actions.AbstractEvent;
import js.java.tools.gui.SwingTools;

public class stellwerksim extends JPanel implements GleisAdapter, SessionExit, ModuleObject {
   private stellwerksim_main simulator = null;
   private final UserContext uc;
   private final gleisbildModelSts glbModel;
   private final TopLevelMessage tlm;
   private final GleisAdapterRouter gadapter;
   private final fsallocator fsalloc;
   private JLabel msgLabel;
   private JProgressBar queueFill;

   public stellwerksim(UserContext uc) {
      super();
      this.uc = uc;
      zug.clear();
      event.clear();
      thema.clear();
      fahrstrasse.fserror = false;
      decor.createDecor(uc);
      this.gadapter = new GleisAdapterRouter(this);
      this.glbModel = new gleisbildModelSts(this.gadapter);
      this.fsalloc = new fsallocator(this.gadapter);
      uc.addCloseObject(this.glbModel);
      uc.addCloseObject(this.gadapter);
      this.initComponents();
      this.tlm = new TopLevelMessage(this, 0);
      this.initMyComponents();
      this.initValues();
      this.tlm.start();
   }

   private void initValues() {
      if (this.uc.getParameter("stoerungen") != null && this.uc.getParameter("stoerungen").equalsIgnoreCase("false")) {
         eventHaeufigkeiten.stoerungenein = false;
      } else {
         eventHaeufigkeiten.stoerungenein = true;
      }

      if (this.uc.getParameter("develop") == null || this.uc.getParameter("develop").compareTo("true") != 0) {
         String offlinemd = "";
         if (this.uc.getParameter("offline") != null && Integer.parseInt(this.uc.getParameter("offline")) > 0) {
            offlinemd = "&offline=" + Integer.parseInt(this.uc.getParameter("offline"));
         }

         if (this.uc.getParameter("develop") != null && this.uc.getParameter("develop").equals("local")) {
            offlinemd = "";
         }

         this.glbModel
            .load(
               this.uc.getParameter("anlagenlesen") + offlinemd,
               new gleisbildModelStore.ioDoneMessage() {
                  @Override
                  public void done(boolean success) {
                     stellwerksim.this.tlm.stop();
                     if (success) {
                        stellwerksim.this.uc.overrideModuleClose(stellwerksim.this);
                        stellwerksim.this.uc.getAudio().playSimStart();
                        stellwerksim.this.simulator = new stellwerksim_main(
                           stellwerksim.this.uc, stellwerksim.this.glbModel, stellwerksim.this.uc.getParameter("running")
                        );
                        stellwerksim.this.gadapter.add(stellwerksim.this.simulator);
                        stellwerksim.this.simulator.setVisible(true);
                        SwingTools.toFront(stellwerksim.this.simulator);
                     } else {
                        stellwerksim.this.uc.showTopLevelMessage("Beim Start ist ein Fehler aufgetreten, Details siehe Console", 10);
                        stellwerksim.this.uc.moduleClosed();
                     }
                  }
               }
            );
      }
   }

   private void initMyComponents() {
      this.glbModel.gl_resize(10, 10);
   }

   @Override
   public void setProgress(final int p) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            if (p > 0) {
               stellwerksim.this.queueFill.setIndeterminate(false);
            }

            stellwerksim.this.queueFill.setValue(p);
         }
      });
   }

   @Override
   public void showStatus(final String s, int type) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            stellwerksim.this.msgLabel.setText(s);
         }
      });
   }

   @Override
   public void showStatus(String s) {
      this.showStatus(s, 0);
   }

   @Override
   public AudioController getAudio() {
      return this.uc.getAudio();
   }

   @Override
   public Simulator getSim() {
      return this.simulator;
   }

   @Override
   public prefs getSimPrefs() {
      return this.getSim().getSimPrefs();
   }

   @Override
   public gleisbildModelSts getGleisbild() {
      return this.glbModel;
   }

   private void initComponents() {
      this.queueFill = new JProgressBar();
      this.msgLabel = new JLabel();
      this.setOpaque(false);
      this.setLayout(new GridLayout(0, 1));
      this.queueFill.setFocusable(false);
      this.queueFill.setIndeterminate(true);
      this.queueFill.setMaximumSize(new Dimension(32767, 30));
      this.queueFill.setMinimumSize(new Dimension(10, 22));
      this.queueFill.setPreferredSize(new Dimension(148, 22));
      this.queueFill.setStringPainted(true);
      this.add(this.queueFill);
      this.msgLabel.setText("Das Gleisbild wird jetzt vom Server geladen, danach entpackt und steht dann bereit!");
      this.add(this.msgLabel);
   }

   @Override
   public void exit() {
      this.uc.moduleClosed();
      this.simulator = null;
   }

   @Override
   public void terminate() {
      if (this.simulator != null) {
         this.simulator.exit();
      } else {
         this.exit();
      }
   }

   @Override
   public String getParameter(String typ) {
      return this.uc.getParameter(typ);
   }

   @Override
   public void setUI(gleis.gleisUIcom gl) {
      throw new UnsupportedOperationException("Not supported.");
   }

   @Override
   public void readUI(gleis.gleisUIcom gl) {
      throw new UnsupportedOperationException("Not supported.");
   }

   @Override
   public void repaintGleisbild() {
      throw new UnsupportedOperationException("Not supported.");
   }

   @Override
   public void incZählwert() {
      throw new UnsupportedOperationException("Not supported.");
   }

   @Override
   public void interPanelCom(AbstractEvent e) {
      throw new UnsupportedOperationException("Not supported.");
   }

   @Override
   public void setGUIEnable(boolean e) {
      throw new UnsupportedOperationException("Not supported.");
   }

   @Override
   public int getBuild() {
      return this.uc.getBuild();
   }

   @Override
   public timedelivery getTimeSystem() {
      throw new UnsupportedOperationException("Not supported.");
   }

   @Override
   public fsallocator getFSallocator() {
      return this.fsalloc;
   }
}

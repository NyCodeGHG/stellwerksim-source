package js.java.isolate.statusapplet.players;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.event.InternalFrameEvent;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.sim.botcom.events.BPosChange;
import js.java.isolate.sim.sim.botcom.events.EPosChange;
import js.java.isolate.sim.sim.botcom.events.ElementOccurance;
import js.java.isolate.sim.sim.botcom.events.SBldChange;
import js.java.isolate.sim.sim.botcom.events.XPosChange;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.chatcomng.ChatNG;
import js.java.schaltungen.chatcomng.ICFactory;
import js.java.schaltungen.chatcomng.IrcChannel;
import js.java.schaltungen.chatcomng.JoinChannelEvent;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.gui.dataTransferDisplay.DataTransferDisplayComponent;
import org.relayirc.chatengine.ChannelEvent;

public class gleisbildFrame extends JFrame implements SessionClose {
   private final GleisAdapter my_main;
   private final playersPanel kp;
   private final players_gleisbildModel glbModel;
   private final players_gleisbildPanel glbPanel;
   private final players_gleisbildControl glbControl;
   private final players_aid paid;
   private final gleisbildLoadPanel my_parent;
   private DataTransferDisplayComponent dataMonitor = null;
   private boolean noClosing = false;
   private gleisbildFrame.ChannelInput my_input = null;
   private JLabel enrLabel;
   private JButton fourButton;
   private JButton fullButton;
   private JButton halfButton;
   private JButton halthalflButton;
   private JButton reloadButton;
   private JToolBar toolBar;
   private JPanel topPanel;
   private JLabel userName;

   gleisbildFrame(UserContext uc, GleisAdapter m, playersPanel kp, players_gleisbildModel glb, players_aid aid, gleisbildLoadPanel parent) {
      super();
      this.my_main = m;
      this.kp = kp;
      this.glbModel = glb;
      this.paid = aid;
      this.my_parent = parent;
      this.setIconImage(uc.getWindowIcon());
      this.dataMonitor = new DataTransferDisplayComponent();
      this.initComponents();
      this.setTitle(this.paid.name);
      this.update(this.paid);
      boolean loaded = this.glbModel.loadIfNeeded(this.dataMonitor);
      this.glbControl = new players_gleisbildControl(uc, this);
      this.glbPanel = new players_gleisbildPanel(uc, this, this.glbControl, this.glbModel);
      this.add(this.glbPanel, "Center");
      String channelname = String.format("#m%x%04x", kp.getInstanz(), aid.aid);
      uc.busPublish(new JoinChannelEvent(channelname, new gleisbildFrame.CIFactory(), true));
      System.out.println("join " + channelname);
      this.pack();
      this.setSize(500, 300);
      this.setClosable(loaded);
      if (loaded) {
         this.glbControl.createImages();
         this.glbPanel.forceRepaint();
      }

      uc.addCloseObject(this);
   }

   @Override
   public void close() {
      this.dispose();
      if (this.my_input != null) {
         this.my_input.sendPart();
         this.my_input = null;
      }
   }

   public void reload() {
      this.setClosable(false);
      this.glbModel.reload();
   }

   public void setClosable(boolean b) {
      this.noClosing = !b;
      this.reloadButton.setEnabled(b);
   }

   void setENR(String enr) {
      this.enrLabel.setText(enr);
   }

   private void channelMessage(ChannelEvent event) {
      String line = (String)event.getValue();
      this.dataMonitor.gotData();
      Object devent = this.my_parent.deserialize(line);
      if (devent != null) {
         Method[] methods = this.getClass().getDeclaredMethods();

         for(Method m : methods) {
            if (m.getName().equals("handle")) {
               Parameter[] params = m.getParameters();
               Parameter p = params[0];
               if (p.getType().isAssignableFrom(devent.getClass())) {
                  try {
                     m.invoke(this, devent);
                     break;
                  } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var12) {
                     Logger.getLogger(gleisbildFrame.class.getName()).log(Level.SEVERE, null, var12);
                  }
               }
            }
         }
      }
   }

   private void handle(SBldChange event) {
      try {
         int enr = event.enr;
         int v = event.st;
         fahrstrasse fs = null;
         if (event.fsname != null) {
            fs = this.glbModel.findFahrwegByName(event.fsname);
         }

         gleis signal = this.glbModel.findFirst(new Object[]{enr, gleis.ELEMENT_SIGNAL, gleis.ELEMENT_ZWERGSIGNAL});
         fahrstrasse ofs = ((players_fluentData)signal.getFluentData()).getFS();
         if (ofs != null) {
            this.glbControl.unmarkFS(ofs);
         }

         gleisElements.Stellungen st = gleis.ST_SIGNAL_ROT;
         if (v == gleis.ST_SIGNAL_GRÜN.ordinal()) {
            st = gleis.ST_SIGNAL_GRÜN;
         } else if (v == gleis.ST_SIGNAL_ZS1.ordinal()) {
            st = gleis.ST_SIGNAL_ZS1;
         } else if (v == gleis.ST_SIGNAL_RF.ordinal()) {
            st = gleis.ST_SIGNAL_RF;
         }

         if (st != gleis.ST_SIGNAL_ROT) {
            signal.getFluentData().setStellung(st, fs);
            if (fs != null) {
               this.glbControl.markFS(fs);
            }
         } else {
            signal.getFluentData().setStellung(gleis.ST_SIGNAL_ROT);
         }

         this.glbPanel.forceRepaint();
      } catch (Exception var8) {
         Logger.getLogger(gleisbildFrame.class.getName()).log(Level.SEVERE, null, var8);
      }
   }

   private void handle(EPosChange event) {
      players_zug z = this.findZid(event.zid);
      if (z != null) {
         int enr1 = 0;
         int enr2 = 0;
         enr1 = event.enr1;
         enr2 = event.enr2;
         if (enr2 > 0) {
            this.glbControl.setZugOn(z, enr2, false);
         } else {
            gleis s1 = this.glbModel.firstSignalAfterEinfahrt(enr1);
            if (s1 != null) {
               this.glbControl.setZugOn(z, s1.getENR(), false);
            } else {
               this.glbControl.setZugOn(z, enr1, true);
            }
         }
      }
   }

   private void handle(BPosChange event) {
      players_zug z = this.findZid(event.zid);
      if (z != null) {
         int x = event.x;
         int y = event.y;
         this.glbControl.setZugOn(z, event.bstg, x, y);
      }
   }

   private void handle(XPosChange event) {
      players_zug z = this.findZid(event.zid);
      if (z != null) {
         this.glbControl.setZugOff(z);
      }
   }

   private void handle(ElementOccurance event) {
      this.glbControl.setSt(event.enr, event.kind);
      this.glbPanel.forceRepaint();
   }

   private players_zug findZid(String p) {
      int z = Integer.parseInt(p);
      return z == 0 ? null : this.kp.findOrAddZug(z);
   }

   private players_zug findZid(int z) {
      return z == 0 ? null : this.kp.findOrAddZug(z);
   }

   public void update(players_aid uaid) {
      if (this.paid.spieler != null) {
         this.userName.setText("Fdl: " + this.paid.spieler);
      } else {
         this.userName.setText("unbedient");
         this.glbControl.setZugOffAll();
      }
   }

   public void update(players_zug z) {
      try {
         if (z.currentaid.aid != this.paid.aid) {
            this.glbControl.setZugOff(z);
         }
      } catch (NullPointerException var3) {
      }
   }

   private void setScale(double f) {
      int w = (int)((double)this.glbModel.getGleisWidth() * this.glbControl.getScaler().getXScale());
      int h = (int)((double)this.glbModel.getGleisHeight() * this.glbControl.getScaler().getYScale());
      w = (int)((double)w / f);
      h = (int)((double)h / f);
      if (w < 100) {
         w = 100;
      }

      if (h < 80) {
         h = 80;
      }

      Insets insets = this.getInsets();
      Dimension p = this.topPanel.getSize();
      this.setSize(new Dimension(w + insets.left + insets.right, h + insets.bottom + insets.top + p.height));
   }

   void closeMe() {
      this.formWindowClosing(null);
   }

   private void initComponents() {
      this.topPanel = new JPanel();
      this.userName = new JLabel();
      this.enrLabel = new JLabel();
      this.toolBar = new JToolBar();
      this.fourButton = new JButton();
      this.halfButton = new JButton();
      this.halthalflButton = new JButton();
      this.fullButton = new JButton();
      this.reloadButton = new JButton();
      this.setDefaultCloseOperation(0);
      this.setLocationByPlatform(true);
      this.setMinimumSize(new Dimension(150, 60));
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent evt) {
            gleisbildFrame.this.formWindowClosing(evt);
         }
      });
      this.topPanel.setLayout(new BorderLayout());
      this.userName.setText("Spieler");
      this.userName.setMinimumSize(new Dimension(50, 15));
      this.topPanel.add(this.userName, "West");
      this.enrLabel.setMaximumSize(new Dimension(80, 20));
      this.topPanel.add(this.enrLabel, "Center");
      this.toolBar.setFloatable(false);
      this.toolBar.setRollover(true);
      this.toolBar.add(this.dataMonitor);
      this.fourButton.setFont(this.fourButton.getFont().deriveFont((float)this.fourButton.getFont().getSize() - 4.0F));
      this.fourButton.setText("1:4");
      this.fourButton.setFocusable(false);
      this.fourButton.setHorizontalTextPosition(0);
      this.fourButton.setVerticalTextPosition(3);
      this.fourButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisbildFrame.this.fourButtonActionPerformed(evt);
         }
      });
      this.toolBar.add(this.fourButton);
      this.halfButton.setFont(this.halfButton.getFont().deriveFont((float)this.halfButton.getFont().getSize() - 4.0F));
      this.halfButton.setText("1:2");
      this.halfButton.setFocusable(false);
      this.halfButton.setHorizontalTextPosition(0);
      this.halfButton.setVerticalTextPosition(3);
      this.halfButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisbildFrame.this.halfButtonActionPerformed(evt);
         }
      });
      this.toolBar.add(this.halfButton);
      this.halthalflButton.setFont(this.halthalflButton.getFont().deriveFont((float)this.halthalflButton.getFont().getSize() - 4.0F));
      this.halthalflButton.setText("1:1,5");
      this.halthalflButton.setFocusable(false);
      this.halthalflButton.setHorizontalTextPosition(0);
      this.halthalflButton.setVerticalTextPosition(3);
      this.halthalflButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisbildFrame.this.halthalflButtonActionPerformed(evt);
         }
      });
      this.toolBar.add(this.halthalflButton);
      this.fullButton.setFont(this.fullButton.getFont().deriveFont((float)this.fullButton.getFont().getSize() - 4.0F));
      this.fullButton.setText("1:1");
      this.fullButton.setFocusable(false);
      this.fullButton.setHorizontalTextPosition(0);
      this.fullButton.setVerticalTextPosition(3);
      this.fullButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisbildFrame.this.fullButtonActionPerformed(evt);
         }
      });
      this.toolBar.add(this.fullButton);
      this.reloadButton.setFont(this.reloadButton.getFont().deriveFont((float)this.reloadButton.getFont().getSize() - 4.0F));
      this.reloadButton.setText("R");
      this.reloadButton.setToolTipText("Reload");
      this.reloadButton.setEnabled(false);
      this.reloadButton.setFocusable(false);
      this.reloadButton.setHorizontalTextPosition(0);
      this.reloadButton.setVerticalTextPosition(3);
      this.reloadButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisbildFrame.this.reloadButtonActionPerformed(evt);
         }
      });
      this.toolBar.add(this.reloadButton);
      this.topPanel.add(this.toolBar, "East");
      this.getContentPane().add(this.topPanel, "North");
      this.pack();
   }

   private void formInternalFrameClosing(InternalFrameEvent evt) {
   }

   private void formInternalFrameClosed(InternalFrameEvent evt) {
      System.out.println("closed");
      this.glbPanel.closingFrame();
   }

   private void fourButtonActionPerformed(ActionEvent evt) {
      this.setScale(4.0);
   }

   private void halfButtonActionPerformed(ActionEvent evt) {
      this.setScale(2.0);
   }

   private void fullButtonActionPerformed(ActionEvent evt) {
      this.setScale(1.0);
   }

   private void halthalflButtonActionPerformed(ActionEvent evt) {
      this.setScale(1.5);
   }

   private void reloadButtonActionPerformed(ActionEvent evt) {
      this.reload();
   }

   private void formWindowClosing(WindowEvent evt) {
      if (!this.noClosing) {
         this.setVisible(false);
         System.out.println("closing");
         if (this.my_input != null) {
            this.my_input.sendPart();
            this.my_input = null;
         }

         this.dispose();
         this.my_parent.closingFrame(this.paid, this);
      }
   }

   public class CIFactory implements ICFactory<gleisbildFrame.ChannelInput> {
      public CIFactory() {
         super();
      }

      public gleisbildFrame.ChannelInput newInstance(ChatNG chat, String name) {
         if (gleisbildFrame.this.my_input == null) {
            gleisbildFrame.this.my_input = gleisbildFrame.this.new ChannelInput(chat, name);
         }

         return gleisbildFrame.this.my_input;
      }
   }

   public class ChannelInput extends IrcChannel {
      public ChannelInput(ChatNG chat, String name) {
         super(chat, name, name, false);
      }

      @Override
      public void onMessage(ChannelEvent event) {
         try {
            gleisbildFrame.this.channelMessage(event);
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }
   }
}

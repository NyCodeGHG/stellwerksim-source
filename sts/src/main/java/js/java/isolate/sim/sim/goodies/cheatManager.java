package js.java.isolate.sim.sim.goodies;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.sim.stellwerksim_main;
import js.java.schaltungen.chatcomng.BOTCOMMAND;
import js.java.schaltungen.moduleapi.SessionClose;

public class cheatManager implements ActionListener, SessionClose {
   private final stellwerksim_main my_main;
   private final JMenuItem cheatMenu;
   private final HashSet<String> usedCodes = new HashSet();
   private long waitTime;
   private cheatWin openWin = null;
   private GAction action = null;
   private String code = null;
   private Timer chTimer = null;

   public cheatManager(stellwerksim_main my_main, JMenuItem cheatMenu) {
      super();
      this.my_main = my_main;
      this.cheatMenu = cheatMenu;
      this.waitTime = System.currentTimeMillis() + 2400000L;
      this.initCheat();
   }

   @Override
   public void close() {
      if (this.openWin != null) {
         this.openWin.setVisible(false);
         this.openWin.dispose();
      }

      if (this.chTimer != null) {
         this.chTimer.stop();
      }
   }

   private void initCheat() {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            cheatManager.this.chTimer = new Timer(300000, cheatManager.this);
            cheatManager.this.chTimer.start();
            cheatManager.this.cheatMenu.setEnabled(true);
         }
      });
   }

   private void proveCheatMenu() {
      if (this.openWin != null) {
         this.openWin.setEnabled(System.currentTimeMillis() > this.waitTime || this.my_main.isExtraMode());
      }
   }

   public void actionPerformed(ActionEvent e) {
      this.proveCheatMenu();
   }

   public void menuAction() {
      if (this.openWin != null) {
         this.openWin.setVisible(false);
         this.openWin.dispose();
      }

      this.action = null;
      this.openWin = new cheatWin(this, this.my_main);
      this.openWin.setVisible(true);
      this.proveCheatMenu();
   }

   private String createAction(String kind) {
      String ret = "Code unbekannt";
      switch(kind) {
         case "randstoerung":
            ret = "Zufällige Störung";
            this.action = new GAction() {
               @Override
               public void run() {
                  double r = Math.random();
                  if (r < 0.25) {
                     cheatManager.this.my_main.getGleisbild().IRCeventTrigger("relaisgruppestoerung");
                  } else if (r < 0.5) {
                     cheatManager.this.my_main.getGleisbild().IRCeventTrigger("sicherungausfall");
                  } else if (r < 0.75) {
                     cheatManager.this.my_main.getGleisbild().IRCeventTrigger("fsspeicherstoerung");
                  } else {
                     cheatManager.this.my_main.getGleisbild().IRCeventTrigger("weichenfsstoerung");
                  }
               }
            };
            break;
         case "signalstoerung":
            ret = "Zufällige Signalstörung";
            this.action = new GAction() {
               @Override
               public void run() {
                  cheatManager.this.my_main.getGleisbild().IRCeventTrigger("randomsignalstoerung");
               }
            };
            break;
         case "stromausfall":
            ret = "Stromausfall";
            this.action = new GAction() {
               @Override
               public void run() {
                  cheatManager.this.my_main.getGleisbild().IRCeventTrigger("stellwerkausfall");
               }
            };
            break;
         case "heilung1":
            ret = "Für 30 Minuten keine neuen Störungen";
            this.action = new GAction() {
               @Override
               public void run() {
                  cheatManager.this.my_main.getGleisbild().IRCeventTrigger("pause(30)");
               }
            };
            break;
         case "weichenstoerung":
            ret = "Zufällige Weichenstörung";
            this.action = new GAction() {
               @Override
               public void run() {
                  cheatManager.this.my_main.getGleisbild().IRCeventTrigger("randomweichestoerung");
               }
            };
            break;
         case "umleitung1":
            ret = "Eine Umleitung, bitte via Funkmenü veranlassen";
            this.action = new GAction() {
               @Override
               public void run() {
                  cheatManager.this.my_main.getGleisbild().IRCeventTrigger("oneredirect");
               }
            };
            break;
         case "autobue":
            ret = "Alle BÜs schließen automatisch bei Fahrstraße";
            this.action = new GAction() {
               @Override
               public void run() {
                  gleis.michNervenBüs = true;
               }
            };
      }

      return ret;
   }

   public void result(final int res, String kind) {
      if (this.openWin != null) {
         final String ktext;
         switch(res) {
            case 200:
               ktext = this.createAction(kind);
               break;
            case 300:
               ktext = "Code wurde in dem Spiel schon genutzt";
               break;
            case 400:
            default:
               ktext = "Code ungültig oder abgelaufen";
               break;
            case 401:
               ktext = "Code wurde erst kürzlich genutzt";
         }

         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               cheatManager.this.openWin.result(res, ktext);
            }
         });
      }
   }

   void check(String code) {
      if (this.usedCodes.contains(code)) {
         this.result(300, null);
      } else {
         this.my_main.send2Bot(BOTCOMMAND.CCODE, code);
         this.code = code;
      }
   }

   void start() {
      this.usedCodes.add(this.code);
      this.my_main.send2Bot(BOTCOMMAND.UCODE, this.code);
      this.action.run();
      this.waitTime = System.currentTimeMillis() + 600000L;
      this.action = null;
      this.code = null;
   }
}
